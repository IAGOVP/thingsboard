/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.thingsboard.server.common.data.FstStatsService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.JedisClusterCRC16;

/**
 * Abstract Redis-backed implementation of {@link TbTransactionalCache}.
 *
 * <p>Activated when {@code cache.type=redis}. Each entity cache (e.g. {@link org.thingsboard.server.cache.user.UserRedisCache})
 * extends this class with a typed {@link TbRedisSerializer} and cache name constant.
 *
 * <p>Key design points:
 * <ul>
 *   <li>Keys are stored as {@code <cacheName><key.toString()>} UTF-8 strings</li>
 *   <li>Values are serialized via {@link TbRedisSerializer}; {@code null} is stored as Spring's
 *       {@link NullValue} sentinel for negative caching</li>
 *   <li>TTL comes from {@link CacheSpecsMap} ({@code timeToLiveInMinutes}); {@code 0} means persistent</li>
 *   <li>Cache is disabled when {@link CacheSpecs#getMaxSize()} is {@code 0} or specs are missing</li>
 *   <li>Eviction tombstones use {@link TBRedisCacheConfiguration#getEvictTtlInMs()} via {@link #evictOrPut}</li>
 *   <li>Cluster mode routes transactions to the correct slot via {@link JedisClusterCRC16}</li>
 * </ul>
 *
 * <p>Versioned entities extend {@link VersionedRedisTbCache} which prepends an 8-byte version
 * prefix and uses Lua scripts for compare-and-set writes.
 *
 * @param <K> serializable key type
 * @param <V> serializable value type
 * @see RedisTbCacheTransaction
 * @see TBRedisCacheConfiguration
 * @see VersionedRedisTbCache
 */
@Slf4j
public abstract class RedisTbTransactionalCache<K extends Serializable, V extends Serializable> implements TbTransactionalCache<K, V> {

    /** Serialized Spring {@link NullValue} marker for negative cache entries. */
    static final byte[] BINARY_NULL_VALUE = RedisSerializer.java().serialize(NullValue.INSTANCE);

    /** Non-null pool placeholder required by {@link JedisConnection} for proper connection closing. */
    static final JedisPool MOCK_POOL = new JedisPool();

    @Autowired
    private FstStatsService fstStatsService;

    @Getter
    private final String cacheName;

    @Getter
    private final JedisConnectionFactory connectionFactory;

    private final RedisSerializer<String> keySerializer = StringRedisSerializer.UTF_8;

    private final TbRedisSerializer<K, V> valueSerializer;

    /** Expiration applied to eviction tombstones written by {@link #evictOrPut}. */
    protected final Expiration evictExpiration;

    /** TTL for normal cache entries; persistent when {@link CacheSpecs#getTimeToLiveInMinutes()} is 0. */
    protected final Expiration cacheTtl;

    /** When {@code false}, all operations are no-ops (cache disabled via {@code maxSize=0}). */
    protected final boolean cacheEnabled;

    /**
     * Initializes the Redis cache with connection factory, serialization, and TTL from configuration.
     *
     * @param cacheName         logical name matching {@link CacheSpecsMap} keys
     * @param cacheSpecsMap     per-cache size and TTL settings; may be {@code null} to disable caching
     * @param connectionFactory   Redis connection factory from {@link TBRedisCacheConfiguration}
     * @param configuration       Redis global settings (evict TTL, pool, SSL)
     * @param valueSerializer     serializer for cache values (JSON, Java, or typed JSON)
     */
    public RedisTbTransactionalCache(String cacheName,
                                     CacheSpecsMap cacheSpecsMap,
                                     RedisConnectionFactory connectionFactory,
                                     TBRedisCacheConfiguration configuration,
                                     TbRedisSerializer<K, V> valueSerializer) {
        this.cacheName = cacheName;
        this.connectionFactory = (JedisConnectionFactory) connectionFactory;
        this.valueSerializer = valueSerializer;
        this.evictExpiration = Expiration.from(configuration.getEvictTtlInMs(), TimeUnit.MILLISECONDS);
        this.cacheTtl = Optional.ofNullable(cacheSpecsMap)
                .map(CacheSpecsMap::getSpecs)
                .map(specs -> specs.get(cacheName))
                .map(CacheSpecs::getTimeToLiveInMinutes)
                .filter(ttl -> !ttl.equals(0))
                .map(ttl -> Expiration.from(ttl, TimeUnit.MINUTES))
                .orElseGet(Expiration::persistent);
        this.cacheEnabled = Optional.ofNullable(cacheSpecsMap)
                .map(CacheSpecsMap::getSpecs)
                .map(x -> x.get(cacheName))
                .map(CacheSpecs::getMaxSize)
                .map(size -> size > 0)
                .orElse(false);
    }

    /**
     * {@inheritDoc}
     *
     * @param key cache key
     * @return value wrapper, empty wrapper for negative hit, or {@code null} on miss/disabled cache
     */
    @Override
    public TbCacheValueWrapper<V> get(K key) {
        if (!cacheEnabled) {
            return null;
        }
        try (var connection = connectionFactory.getConnection()) {
            byte[] rawValue = doGet(key, connection);
            if (rawValue == null || rawValue.length == 0) {
                return null;
            } else if (Arrays.equals(rawValue, BINARY_NULL_VALUE)) {
                return SimpleTbCacheValueWrapper.empty();
            } else {
                long startTime = System.nanoTime();
                V value = valueSerializer.deserialize(key, rawValue);
                if (value != null) {
                    fstStatsService.recordDecodeTime(value.getClass(), startTime);
                    fstStatsService.incrementDecode(value.getClass());
                }
                return SimpleTbCacheValueWrapper.wrap(value);
            }
        }
    }

    /**
     * Performs the low-level Redis GET for a key. Overridden by {@link VersionedRedisTbCache}
     * to strip the version prefix.
     *
     * @param key        cache key
     * @param connection open Redis connection
     * @return raw serialized bytes, or {@code null} on miss
     */
    protected byte[] doGet(K key, RedisConnection connection) {
        return connection.stringCommands().get(getRawKey(key));
    }

    /**
     * {@inheritDoc}
     *
     * @param key   cache key
     * @param value value to store
     */
    @Override
    public void put(K key, V value) {
        if (!cacheEnabled) {
            return;
        }
        try (var connection = connectionFactory.getConnection()) {
            put(key, value, connection);
        }
    }

    /**
     * Stores a value using an existing Redis connection (used inside transactions).
     *
     * @param key        cache key
     * @param value      value to store
     * @param connection open Redis connection participating in a MULTI/EXEC block
     */
    public void put(K key, V value, RedisConnection connection) {
        put(connection, key, value, RedisStringCommands.SetOption.UPSERT);
    }

    /**
     * {@inheritDoc}
     *
     * @param key   cache key
     * @param value value to store when absent
     */
    @Override
    public void putIfAbsent(K key, V value) {
        if (!cacheEnabled) {
            return;
        }
        try (var connection = connectionFactory.getConnection()) {
            put(connection, key, value, RedisStringCommands.SetOption.SET_IF_ABSENT);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param key cache key to delete
     */
    @Override
    public void evict(K key) {
        if (!cacheEnabled) {
            return;
        }
        try (var connection = connectionFactory.getConnection()) {
            connection.keyCommands().del(getRawKey(key));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param keys keys to delete; no-op when empty (Redis DEL requires at least one key)
     */
    @Override
    public void evict(Collection<K> keys) {
        if (!cacheEnabled) {
            return;
        }
        //Redis expects at least 1 key to delete. Otherwise - ERR wrong number of arguments for 'del' command
        if (keys.isEmpty()) {
            return;
        }
        try (var connection = connectionFactory.getConnection()) {
            connection.keyCommands().del(keys.stream().map(this::getRawKey).toArray(byte[][]::new));
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Deletes the key; if absent, writes a short-lived tombstone so concurrent
     * {@link #getAndPutInTransaction} calls do not repopulate stale data.
     *
     * @param key   cache key
     * @param value tombstone value when delete finds no existing entry
     */
    @Override
    public void evictOrPut(K key, V value) {
        if (!cacheEnabled) {
            return;
        }
        try (var connection = connectionFactory.getConnection()) {
            var rawKey = getRawKey(key);
            var records = connection.keyCommands().del(rawKey);
            if (records == null || records == 0) {
                //We need to put the value in case of Redis, because evict will NOT cancel concurrent transaction used to "get" the missing value from cache.
                connection.stringCommands().set(rawKey, getRawValue(value), evictExpiration, RedisStringCommands.SetOption.UPSERT);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param key key to watch in a Redis MULTI/EXEC transaction
     * @return {@link RedisTbCacheTransaction} bound to a watched connection
     */
    @Override
    public TbCacheTransaction<K, V> newTransactionForKey(K key) {
        byte[][] rawKey = new byte[][]{getRawKey(key)};
        RedisConnection connection = watch(rawKey);
        return new RedisTbCacheTransaction<>(this, connection);
    }

    /**
     * {@inheritDoc}
     *
     * @param keys keys to watch atomically
     * @return {@link RedisTbCacheTransaction} bound to a watched connection
     */
    @Override
    public TbCacheTransaction<K, V> newTransactionForKeys(List<K> keys) {
        RedisConnection connection = watch(keys.stream().map(this::getRawKey).toArray(byte[][]::new));
        return new RedisTbCacheTransaction<>(this, connection);
    }

    /**
     * {@inheritDoc}
     *
     * @param key                 cache key
     * @param dbCall              database supplier
     * @param cacheValueToResult  result mapper
     * @param dbValueToCacheValue cache value mapper
     * @param cacheNullValue      negative-cache flag
     * @param <R>                 result type
     * @return loaded value, or direct DB result when cache is disabled
     */
    @Override
    public <R> R getAndPutInTransaction(K key, Supplier<R> dbCall, Function<V, R> cacheValueToResult, Function<R, V> dbValueToCacheValue, boolean cacheNullValue) {
        if (!cacheEnabled) {
            return dbCall.get();
        }
        return TbTransactionalCache.super.getAndPutInTransaction(key, dbCall, cacheValueToResult, dbValueToCacheValue, cacheNullValue);
    }

    /**
     * Obtains a Redis connection pinned to the correct cluster slot for the given raw key.
     *
     * @param rawKey serialized Redis key bytes
     * @return standalone or slot-specific cluster connection
     */
    protected RedisConnection getConnection(byte[] rawKey) {
        if (!connectionFactory.isRedisClusterAware()) {
            return connectionFactory.getConnection();
        }
        RedisConnection connection = connectionFactory.getClusterConnection();

        int slotNum = JedisClusterCRC16.getSlot(rawKey);
        Jedis jedis = new Jedis((((JedisClusterConnection) connection).getNativeConnection().getConnectionFromSlot(slotNum)));

        JedisConnection jedisConnection = new JedisConnection(jedis, MOCK_POOL, jedis.getDB());
        jedisConnection.setConvertPipelineAndTxResults(connectionFactory.getConvertPipelineAndTxResults());

        return jedisConnection;
    }

    /**
     * Starts a Redis WATCH/MULTI transaction on the connection for the given keys.
     *
     * @param rawKeysList serialized key bytes to watch
     * @return open connection in MULTI state; caller must commit or discard via {@link RedisTbCacheTransaction}
     * @throws RuntimeException connection is closed and re-thrown on watch failure
     */
    protected RedisConnection watch(byte[][] rawKeysList) {
        RedisConnection connection = getConnection(rawKeysList[0]);
        try {
            connection.watch(rawKeysList);
            connection.multi();
        } catch (Exception e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    /**
     * Serializes the cache key to Redis bytes with the cache name prefix.
     *
     * @param key logical cache key
     * @return UTF-8 bytes of {@code cacheName + key.toString()}
     * @throws RuntimeException         when serialization throws
     * @throws IllegalArgumentException when serialization returns {@code null}
     */
    protected byte[] getRawKey(K key) {
        String keyString = cacheName + key.toString();
        byte[] rawKey;
        try {
            rawKey = keySerializer.serialize(keyString);
        } catch (Exception e) {
            log.warn("Failed to serialize the cache key: {}", key, e);
            throw new RuntimeException(e);
        }
        if (rawKey == null) {
            log.warn("Failed to serialize the cache key: {}", key);
            throw new IllegalArgumentException("Failed to serialize the cache key!");
        }
        return rawKey;
    }

    /**
     * Serializes a cache value, mapping {@code null} to the {@link #BINARY_NULL_VALUE} sentinel.
     *
     * @param value value to serialize
     * @return serialized bytes
     * @throws RuntimeException when serialization fails
     */
    protected byte[] getRawValue(V value) {
        if (value == null) {
            return BINARY_NULL_VALUE;
        } else {
            try {
                long startTime = System.nanoTime();
                var bytes = valueSerializer.serialize(value);
                fstStatsService.recordEncodeTime(value.getClass(), startTime);
                fstStatsService.incrementEncode(value.getClass());
                return bytes;
            } catch (Exception e) {
                log.warn("Failed to serialize the cache value: {}", value, e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Stages a SET inside an open Redis transaction.
     *
     * @param connection open MULTI connection
     * @param key        cache key
     * @param value      value to set
     * @param setOption  Redis SET option (UPSERT or SET_IF_ABSENT)
     */
    public void put(RedisConnection connection, K key, V value, RedisStringCommands.SetOption setOption) {
        if (!cacheEnabled) {
            return;
        }
        byte[] rawKey = getRawKey(key);
        put(connection, rawKey, value, setOption);
    }

    /**
     * Stages a SET with a pre-serialized key inside an open Redis transaction.
     *
     * @param connection open MULTI connection
     * @param rawKey     serialized Redis key
     * @param value      value to set
     * @param setOption  Redis SET option
     */
    public void put(RedisConnection connection, byte[] rawKey, V value, RedisStringCommands.SetOption setOption) {
        byte[] rawValue = getRawValue(value);
        connection.stringCommands().set(rawKey, rawValue, this.cacheTtl, setOption);
    }

    /**
     * Executes a Lua script by SHA with automatic script loading and fallback to {@code EVAL}.
     *
     * @param connection  Redis connection (must support scripting)
     * @param scriptSha   expected SHA-1 digest of {@code luaScript}
     * @param luaScript   Lua source text
     * @param returnType  Redis return type for the script result
     * @param numKeys     number of KEYS arguments
     * @param keysAndArgs KEYS followed by ARGV bytes
     * @throws IllegalStateException when loaded script SHA does not match expected SHA
     */
    protected void executeScript(RedisConnection connection, byte[] scriptSha, byte[] luaScript, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        try {
            connection.scriptingCommands().evalSha(scriptSha, returnType, numKeys, keysAndArgs);
        } catch (InvalidDataAccessApiUsageException ignored) {
            log.debug("Loading LUA with expected SHA [{}], connection [{}]", new String(scriptSha), connection.getNativeConnection());
            String actualSha = connection.scriptingCommands().scriptLoad(luaScript);
            if (!Arrays.equals(scriptSha, StringRedisSerializer.UTF_8.serialize(actualSha))) {
                String message = String.format("SHA for LUA script wrong! Expected [%s], but actual [%s], connection [%s]",
                        new String(scriptSha), actualSha, connection.getNativeConnection());
                throw new IllegalStateException(message);
            }
            try {
                connection.scriptingCommands().evalSha(scriptSha, returnType, numKeys, keysAndArgs);
            } catch (InvalidDataAccessApiUsageException exception) {
                log.warn("Slowly executing eval instead of fast evalSha", exception);
                connection.scriptingCommands().eval(luaScript, returnType, numKeys, keysAndArgs);
            }
        }
    }

}
