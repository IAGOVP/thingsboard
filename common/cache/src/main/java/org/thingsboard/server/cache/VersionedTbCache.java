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

import org.thingsboard.server.common.data.HasVersion;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Extension of {@link TbTransactionalCache} for entities implementing {@link org.thingsboard.server.common.data.HasVersion}.
 *
 * <p>Writes include the entity version so stale updates from out-of-order events are rejected.
 * Adds version-aware {@link #evict(K, Long)} that writes a tombstone at the given version.
 *
 * <p>Implementations: {@link VersionedCaffeineTbCache}, {@link VersionedRedisTbCache}.
 *
 * @param <K> versioned cache key
 * @param <V> versioned entity type
 * @see VersionedCacheKey
 */
public interface VersionedTbCache<K extends VersionedCacheKey, V extends Serializable & HasVersion> extends TbTransactionalCache<K, V> {

    TbCacheValueWrapper<V> get(K key);

/**
         * Cache-aside get with automatic put on miss.
         *
         * @param key      cache key
         * @param supplier database fallback
         * @return cached or loaded value
         */
    default V get(K key, Supplier<V> supplier) {
        return get(key, supplier, true);
    }

/**
         * Cache-aside get with optional write-through.
         *
         * @param key         cache key
         * @param supplier    database fallback
         * @param putToCache  whether to store the loaded value
         * @return cached or loaded value
         */
    default V get(K key, Supplier<V> supplier, boolean putToCache) {
        return Optional.ofNullable(get(key))
                .map(TbCacheValueWrapper::get)
                .orElseGet(() -> {
                    V value = supplier.get();
                    if (putToCache) {
                        put(key, value);
                    }
                    return value;
                });
    }

    void put(K key, V value);

    void evict(K key);

    void evict(Collection<K> keys);

/**
         * Version-aware eviction writing a versioned tombstone.
         *
         * @param key     cache key
         * @param version entity version for optimistic invalidation
         */
    void evict(K key, Long version);

/**
         * Extracts the version stamp from an entity for compare-and-set logic.
         *
         * @param value entity; {@code null} maps to {@code 0L}
         * @return version number, {@code 0L} for null, or {@code null} when version is unset
         */
    default Long getVersion(V value) {
        if (value == null) {
            return 0L;
        } else if (value.getVersion() != null) {
            return value.getVersion();
        } else {
            return null;
        }
    }

}
