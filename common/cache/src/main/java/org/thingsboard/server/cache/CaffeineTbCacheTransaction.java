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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * In-process transaction that stages puts before atomic commit to a {@link CaffeineTbTransactionalCache}.
 *
 * <p>Pending entries accumulate in {@link #pendingPuts}. On {@link #commit()}, the parent cache
 * applies puts only if this transaction was not marked failed by a concurrent mutation on
 * a watched key. {@link #rollback()} drops the transaction without writing.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see CaffeineTbTransactionalCache#commit
 */
@Slf4j
@RequiredArgsConstructor

public class CaffeineTbCacheTransaction<K extends Serializable, V extends Serializable> implements TbCacheTransaction<K, V> {
    @Getter
    /** Unique transaction identifier for registry tracking. */
    private final UUID id = UUID.randomUUID();
    /** Parent cache coordinating commit/rollback. */
    private final CaffeineTbTransactionalCache<K, V> cache;
    @Getter
    /** Keys watched by this transaction for conflict detection. */
    private final List<K> keys;
    @Getter
    @Setter
    /** When {@code true}, {@link #commit()} becomes a no-op. */
    private boolean failed;

    /** Staged key-value pairs applied on successful commit. */

    private final Map<K, V> pendingPuts = new LinkedHashMap<>();

/**
         * Stages a put operation applied only on successful {@link #commit()}.
         *
         * @param key   cache key
         * @param value value to store when committed
         */
    @Override
    public void put(K key, V value) {
        pendingPuts.put(key, value);
    }

/**
         * Commits staged puts via {@link CaffeineTbTransactionalCache#commit}.
         *
         * @return {@code true} if the transaction was not invalidated before commit
         */
    @Override
    public boolean commit() {
        return cache.commit(id, pendingPuts);
    }

/**
         * Aborts the transaction and releases its registry entry without writing to cache.
         */
    @Override
    public void rollback() {
        cache.rollback(id);
    }

}
