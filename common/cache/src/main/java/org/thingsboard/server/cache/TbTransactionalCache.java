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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Core transactional cache contract used throughout ThingsBoard for entity caching.
 *
 * <p>Implementations include {@link CaffeineTbTransactionalCache} (in-process, single node)
 * and {@link RedisTbTransactionalCache} (distributed, cluster-wide). Versioned entities use
 * {@link VersionedTbCache} which extends this interface with optimistic concurrency.
 *
 * <p>Typical usage pattern (cache-aside):
 * <ol>
 *   <li>{@link #get(K)} — return cached value or {@code null} wrapper on miss</li>
 *   <li>{@link #getAndPutInTransaction(K, Supplier, boolean)} — load from DB inside a
 *       {@link TbCacheTransaction} to avoid stampedes</li>
 *   <li>{@link #evict(K)} / {@link #evictOrPut(K, Serializable)} — invalidate on entity update</li>
 * </ol>
 *
 * <p>Cache names correspond to constants in {@code CacheConstants} and are configured via
 * {@link CacheSpecsMap} ({@code cache.specs.<name>.maxSize} and
 * {@code cache.specs.<name>.timeToLiveInMinutes}).
 *
 * @param <K> serializable cache key type (e.g. {@link org.thingsboard.server.cache.user.UserCacheKey})
 * @param <V> serializable cache value type (entity or DTO)
 * @see TbCacheTransaction
 * @see TbCacheValueWrapper
 */
public interface TbTransactionalCache<K extends Serializable, V extends Serializable> {

    /**
     * Returns the logical cache name matching {@link CacheSpecsMap} configuration keys.
     *
     * @return cache name constant (e.g. {@code "users"})
     */
    String getCacheName();

    /**
     * Retrieves a value from the cache, distinguishing misses from negative-cache hits.
     *
     * @param key cache key
     * @return wrapper with value, empty wrapper for cached {@code null}, or {@code null} on miss
     */
    TbCacheValueWrapper<V> get(K key);

    /**
     * Stores a value, invalidating any in-flight transactions on the same key (Caffeine)
     * or overwriting the Redis key (Redis).
     *
     * @param key   cache key
     * @param value value to store; {@code null} may be stored as a tombstone depending on implementation
     */
    void put(K key, V value);

    /**
     * Stores a value only when the key is absent.
     *
     * @param key   cache key
     * @param value value to store when absent
     */
    void putIfAbsent(K key, V value);

    /**
     * Removes a single cache entry.
     *
     * @param key cache key to evict
     */
    void evict(K key);

    /**
     * Removes multiple cache entries in one operation.
     *
     * @param keys collection of keys to evict; no-op when empty
     */
    void evict(Collection<K> keys);

    /**
     * Attempts eviction; if the key was absent, stores a short-lived tombstone instead.
     *
     * <p>Redis uses this to prevent concurrent {@link #getAndPutInTransaction} calls from
     * repopulating stale data after an eviction. Caffeine delegates to {@link #evict(K)}.
     *
     * @param key   cache key
     * @param value tombstone value written when eviction finds no existing entry (Redis only)
     */
    void evictOrPut(K key, V value);

    /**
     * Opens a transaction scoped to a single key for atomic read-then-write.
     *
     * @param key the key to watch (Redis) or lock (Caffeine)
     * @return transaction handle; must be {@link TbCacheTransaction#commit()} or {@link TbCacheTransaction#rollback()}
     */
    TbCacheTransaction<K, V> newTransactionForKey(K key);

    /**
     * Opens a transaction scoped to multiple keys.
     *
     * @param keys keys participating in the transaction
     * @return transaction handle
     */
    TbCacheTransaction<K, V> newTransactionForKeys(List<K> keys);

    /**
     * Loads from the database with optional transactional cache population.
     *
     * @param key             cache key
     * @param dbCall          supplier that fetches the value from persistent storage
     * @param cacheNullValue  when {@code true}, caches {@code null} results as negative hits
     * @param putToCache      when {@code false}, skips cache write and only reads from cache or DB
     * @return the loaded value, or {@code null}
     */
    default V getOrFetchFromDB(K key, Supplier<V> dbCall, boolean cacheNullValue, boolean putToCache) {
        if (putToCache) {
            return getAndPutInTransaction(key, dbCall, cacheNullValue);
        } else {
            TbCacheValueWrapper<V> cacheValueWrapper = get(key);
            if (cacheValueWrapper != null) {
                return cacheValueWrapper.get();
            }
            return dbCall.get();
        }
    }

    /**
     * Cache-aside load using identity mapping between cache and result types.
     *
     * @param key            cache key
     * @param dbCall         database fetch supplier
     * @param cacheNullValue whether to cache {@code null} database results
     * @return loaded value
     */
    default V getAndPutInTransaction(K key, Supplier<V> dbCall, boolean cacheNullValue) {
        return getAndPutInTransaction(key, dbCall, Function.identity(), Function.identity(), cacheNullValue);
    }

    /**
     * Cache-aside load inside a transaction to prevent duplicate database fetches under contention.
     *
     * <p>On cache miss, opens {@link #newTransactionForKey(K)}, calls {@code dbCall}, stages the
     * mapped value, and commits. Rolls back on failure or when {@code dbValue} is {@code null}
     * and {@code cacheNullValue} is {@code false}.
     *
     * @param key                 cache key
     * @param dbCall              supplier fetching the result from the database
     * @param cacheValueToResult  maps cached entity to the caller's return type
     * @param dbValueToCacheValue maps database result to the type stored in cache
     * @param cacheNullValue      when {@code true}, commits even when the database returns {@code null}
     * @param <R>                 caller result type
     * @return mapped result from cache or database
     * @throws Throwable re-thrown after transaction rollback when {@code dbCall} fails
     */
    default <R> R getAndPutInTransaction(K key, Supplier<R> dbCall, Function<V, R> cacheValueToResult, Function<R, V> dbValueToCacheValue, boolean cacheNullValue) {
        TbCacheValueWrapper<V> cacheValueWrapper = get(key);
        if (cacheValueWrapper != null) {
            V cacheValue = cacheValueWrapper.get();
            return cacheValue != null ? cacheValueToResult.apply(cacheValue) : null;
        }
        var cacheTransaction = newTransactionForKey(key);
        try {
            R dbValue = dbCall.get();
            if (dbValue != null || cacheNullValue) {
                cacheTransaction.put(key, dbValueToCacheValue.apply(dbValue));
                cacheTransaction.commit();
                return dbValue;
            } else {
                cacheTransaction.rollback();
                return null;
            }
        } catch (Throwable e) {
            cacheTransaction.rollback();
            throw e;
        }
    }

    /**
     * Loads with type mapping and optional cache write control.
     *
     * @param key                 cache key
     * @param dbCall              database fetch supplier
     * @param cacheValueToResult  maps cached value to return type
     * @param dbValueToCacheValue maps database result to cache value type
     * @param cacheNullValue      whether to cache {@code null} results
     * @param putToCache          whether to write through to cache on miss
     * @param <R>                 caller result type
     * @return mapped result
     */
    default <R> R getOrFetchFromDB(K key, Supplier<R> dbCall, Function<V, R> cacheValueToResult, Function<R, V> dbValueToCacheValue, boolean cacheNullValue, boolean putToCache) {
        if (putToCache) {
            return getAndPutInTransaction(key, dbCall, cacheValueToResult, dbValueToCacheValue, cacheNullValue);
        } else {
            TbCacheValueWrapper<V> cacheValueWrapper = get(key);
            if (cacheValueWrapper != null) {
                var cacheValue = cacheValueWrapper.get();
                return cacheValue == null ? null : cacheValueToResult.apply(cacheValue);
            }
            return dbCall.get();
        }
    }

}
