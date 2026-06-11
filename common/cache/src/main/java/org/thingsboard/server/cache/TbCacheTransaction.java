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

/**
 * Unit of work for batched, atomic cache writes in {@link TbTransactionalCache}.
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@link CaffeineTbCacheTransaction} — in-process staging with commit conflict detection</li>
 *   <li>{@link RedisTbCacheTransaction} — Redis WATCH/MULTI/EXEC transaction</li>
 * </ul>
 *
 * <p>Typical lifecycle: {@link #put} stages entries, then {@link #commit()} applies them
 * or {@link #rollback()} discards them.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see TbTransactionalCache#newTransactionForKey
 */
public interface TbCacheTransaction<K, V> {

    void put(K key, V value);

    boolean commit();

    void rollback();

}
