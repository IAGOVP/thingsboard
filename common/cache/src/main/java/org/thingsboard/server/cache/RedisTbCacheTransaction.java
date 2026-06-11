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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;

import java.io.Serializable;
import java.util.Objects;

/**
 * Redis WATCH/MULTI/EXEC transaction wrapping a {@link RedisTbTransactionalCache}.
 *
 * <p>Each {@link #put} stages a SET command on the watched connection opened by
 * {@link RedisTbTransactionalCache#watch}. {@link #commit()} executes EXEC;
 * {@link #rollback()} sends DISCARD. The connection is always closed afterward.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see RedisTbTransactionalCache#newTransactionForKey
 */
@Slf4j
@RequiredArgsConstructor
public class RedisTbCacheTransaction<K extends Serializable, V extends Serializable> implements TbCacheTransaction<K, V> {

    /** Parent cache providing serialization and key prefixing. */

    private final RedisTbTransactionalCache<K, V> cache;
    /** WATCH/MULTI connection; closed on commit or rollback. */
    private final RedisConnection connection;

/**
         * Stages a SET on the open MULTI connection.
         *
         * @param key   cache key
         * @param value value to store when the transaction commits
         */
    @Override
    public void put(K key, V value) {
        cache.put(key, value, connection);
    }

/**
         * Executes the Redis transaction and closes the connection.
         *
         * @return {@code true} when EXEC returns at least one non-null result
         */
    @Override
    public boolean commit() {
        try {
            var execResult = connection.exec();
            var result = execResult != null && execResult.stream().anyMatch(Objects::nonNull);
            return result;
        } finally {
            connection.close();
        }
    }

/**
         * Discards staged commands and closes the connection.
         */
    @Override
    public void rollback() {
        try {
            connection.discard();
        } finally {
            connection.close();
        }
    }

}
