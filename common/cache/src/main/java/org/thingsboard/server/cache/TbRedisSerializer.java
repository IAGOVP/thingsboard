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

import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

/**
 * Pluggable serializer for Redis cache values in {@link RedisTbTransactionalCache}.
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@link TbJsonRedisSerializer} — JSON for human-readable/interoperable storage</li>
 *   <li>{@link TbTypedJsonRedisSerializer} — JSON with {@link com.fasterxml.jackson.core.type.TypeReference}</li>
 *   <li>{@link TbJavaRedisSerializer} — Java native serialization</li>
 * </ul>
 *
 * @param <K> cache key type (may inform deserialization)
 * @param <T> cache value type
 * @see RedisTbTransactionalCache#getRawValue
 */
public interface TbRedisSerializer<K, T> {

/**
         * Serializes a value to Redis bytes.
         *
         * @param t value to serialize; may be {@code null}
         * @return serialized bytes, or {@code null}
         * @throws org.springframework.data.redis.serializer.SerializationException on failure
         */
    @Nullable
    byte[] serialize(@Nullable T t) throws SerializationException;

/**
         * Deserializes Redis bytes to a value.
         *
         * @param key   original cache key (may guide type resolution)
         * @param bytes serialized data; may be {@code null}
         * @return deserialized value, or {@code null}
         * @throws org.springframework.data.redis.serializer.SerializationException on failure
         */
    @Nullable
    T deserialize(K key, @Nullable byte[] bytes) throws SerializationException;

}
