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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Abstract in-process Caffeine implementation of {@link TbTransactionalCache}.
 *
 * <p>Default cache backend when {@code cache.type=caffeine} (or property is absent).
 * Wraps a Spring {@link Cache} instance registered by {@link TbCaffeineCacheConfiguration}
 * using per-cache {@link CacheSpecs} from {@link CacheSpecsMap}.
 *
 * <p>Concurrency model:
 * <ul>
 *   <li>A global {@link #lock} serializes transaction lifecycle and mutating operations</li>
 *   <li>{@link #objectTransactions} maps each key to active transaction IDs watching that key</li>
 *   <li>{@link #transactions} holds open {@link CaffeineTbCacheTransaction} instances by UUID</li>
 *   <li>Direct {@link #put}, {@link #evict}, etc. mark overlapping transactions as failed</li>
 * </ul>
 *
 * <p>Unlike Redis, {@link #evictOrPut} simply evicts because Caffeine transaction failure
 * already prevents stale read-through repopulation.
 *
 * <p>Entity-specific subclasses (e.g. {@link org.thingsboard.server.cache.user.UserCaffeineCache})
 * only supply the cache name constant. Versioned entities use {@link VersionedCaffeineTbCache}.
 *
 * @param <K> serializable key type
 * @param <V> serializable value type
 * @see CaffeineTbCacheTransaction
 * @see TbCaffeineCacheConfiguration
 */
@RequiredArgsConstructor
public abstract class CaffeineTbTransactionalCache<K extends Serializable, V extends Serializable> implements TbTransactionalCache<K, V> {

    @Getter
    protected final String cacheName;

    /** Underlying Caffeine cache from Spring {@link CacheManager}. */
    protected final Cache cache;

    /** Guards transaction registry and coordinates with direct cache mutations. */
    protected final Lock lock = new ReentrantLock();

    /** Maps cache keys to the set of transaction UUIDs currently watching each key. */
    private final Map<K, Set<UUID>> objectTransactions = new HashMap<>();

    /** Active transactions indexed by their unique identifier. */
    private final Map<UUID, CaffeineTbCacheTransaction<K, V>> transactions = new HashMap<>();

    /**
     * Resolves the named Caffeine cache from the Spring {@link CacheManager}.
     *
     * @param cacheManager Spring cache manager built by {@link TbCaffeineCacheConfiguration}
     * @param cacheName    logical cache name (e.g. {@link org.thingsboard.server.common.data.CacheConstants#USER_CACHE})
     * @throws IllegalArgumentException when the cache name is not registered
     */
    public CaffeineTbTransactionalCache(CacheManager cacheManager, String cacheName) {
        this.cacheName = cacheName;
        this.cache = Optional.ofNullable(cacheManager.getCache(cacheName))
                .orElseThrow(() -> new IllegalArgumentException("Cache '" + cacheName + "' is not configured"));
    }

    /**
     * {@inheritDoc}
     *
     * @param key cache key
     * @return value wrapper, or {@code null} on miss
     */
    @Override
    public TbCacheValueWrapper<V> get(K key) {
        return SimpleTbCacheValueWrapper.wrap(cache.get(key));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Fails any in-flight transactions on the key before writing.
     *
     * @param key   cache key
     * @param value value to store
     */
    @Override
    public void put(K key, V value) {
        lock.lock();
        try {
            failAllTransactionsByKey(key);
            cache.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param key   cache key
     * @param value value to store when absent
     */
    @Override
    public void putIfAbsent(K key, V value) {
        lock.lock();
        try {
            failAllTransactionsByKey(key);
            doPutIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param key cache key to remove
     */
    @Override
    public void evict(K key) {
        lock.lock();
        try {
            failAllTransactionsByKey(key);
            doEvict(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param keys keys to evict
     */
    @Override
    public void evict(Collection<K> keys) {
        lock.lock();
        try {
            keys.forEach(key -> {
                failAllTransactionsByKey(key);
                doEvict(key);
            });
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #evict(K)} — Caffeine does not need eviction tombstones.
     *
     * @param key   cache key
     * @param value ignored for Caffeine
     */
    @Override
    public void evictOrPut(K key, V value) {
        //No need to put the value in case of Caffeine, because evict will cancel concurrent transaction used to "get" the missing value from cache.
        evict(key);
    }

    /**
     * {@inheritDoc}
     *
     * @param key single key for the transaction
     * @return new {@link CaffeineTbCacheTransaction}
     */
    @Override
    public TbCacheTransaction<K, V> newTransactionForKey(K key) {
        return newTransaction(Collections.singletonList(key));
    }

    /**
     * {@inheritDoc}
     *
     * @param keys keys watched by the transaction
     * @return new {@link CaffeineTbCacheTransaction}
     */
    @Override
    public TbCacheTransaction<K, V> newTransactionForKeys(List<K> keys) {
        return newTransaction(keys);
    }

    /**
     * Unsynchronized put-if-absent delegated to the underlying Caffeine cache.
     *
     * @param key   cache key
     * @param value value to store when absent
     */
    void doPutIfAbsent(K key, V value) {
        cache.putIfAbsent(key, value);
    }

    /**
     * Unsynchronized eviction delegated to the underlying Caffeine cache.
     *
     * @param key cache key
     */
    void doEvict(K key) {
        cache.evict(key);
    }

    /**
     * Registers a new transaction and indexes it by key for conflict detection.
     *
     * @param keys keys participating in the transaction
     * @return newly created transaction (not yet committed)
     */
    TbCacheTransaction<K, V> newTransaction(List<K> keys) {
        lock.lock();
        try {
            var transaction = new CaffeineTbCacheTransaction<>(this, keys);
            var transactionId = transaction.getId();
            for (K key : keys) {
                objectTransactions.computeIfAbsent(key, k -> new HashSet<>()).add(transactionId);
            }
            transactions.put(transactionId, transaction);
            return transaction;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Commits a transaction when not marked failed, applying pending puts and invalidating
     * overlapping transactions on the same keys.
     *
     * @param trId       transaction UUID
     * @param pendingPuts staged key-value pairs from {@link CaffeineTbCacheTransaction}
     * @return {@code true} when commit succeeded; {@code false} when the transaction was failed
     */
    public boolean commit(UUID trId, Map<K, V> pendingPuts) {
        lock.lock();
        try {
            var tr = transactions.get(trId);
            var success = !tr.isFailed();
            if (success) {
                for (K key : tr.getKeys()) {
                    Set<UUID> otherTransactions = objectTransactions.get(key);
                    if (otherTransactions != null) {
                        for (UUID otherTrId : otherTransactions) {
                            if (trId == null || !trId.equals(otherTrId)) {
                                transactions.get(otherTrId).setFailed(true);
                            }
                        }
                    }
                }
                pendingPuts.forEach(this::doPutIfAbsent);
            }
            removeTransaction(trId);
            return success;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Rolls back a transaction by removing it from the registry without applying pending puts.
     *
     * @param id transaction UUID
     */
    void rollback(UUID id) {
        lock.lock();
        try {
            removeTransaction(id);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a transaction from both {@link #transactions} and {@link #objectTransactions}.
     *
     * @param id transaction UUID to remove
     */
    private void removeTransaction(UUID id) {
        CaffeineTbCacheTransaction<K, V> transaction = transactions.remove(id);
        if (transaction != null) {
            for (var key : transaction.getKeys()) {
                Set<UUID> transactions = objectTransactions.get(key);
                if (transactions != null) {
                    transactions.remove(id);
                    if (transactions.isEmpty()) {
                        objectTransactions.remove(key);
                    }
                }
            }
        }
    }

    /**
     * Marks all in-flight transactions touching the given key as failed.
     *
     * @param key cache key whose watchers should be invalidated
     */
    protected void failAllTransactionsByKey(K key) {
        Set<UUID> transactionsIds = objectTransactions.get(key);
        if (transactionsIds != null) {
            for (UUID otherTrId : transactionsIds) {
                transactions.get(otherTrId).setFailed(true);
            }
        }
    }

}
