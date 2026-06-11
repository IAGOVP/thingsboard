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
import org.thingsboard.common.util.JacksonUtil;

import java.io.IOException;

/**
 * JSON {@link TbRedisSerializer} for entity caches stored as UTF-8 JSON in Redis.
 *
 * <p>Uses {@link org.thingsboard.common.util.JacksonUtil} with unknown-property ignoring
 * on deserialize for forward-compatible schema evolution.
 *
 * @param <K> cache key type
 * @param <V> concrete value class
 * @see TbRedisSerializer
 * @see TbTypedJsonRedisSerializer
 */
public class TbJsonRedisSerializer<K, V> implements TbRedisSerializer<K, V> {

    /** Target class for Jackson deserialization. */

    private final Class<V> clazz;

    /**
     * Binds deserialization to a concrete value class.
     *
     * @param clazz target type for JSON deserialization
     */
    public TbJsonRedisSerializer(Class<V> clazz) {
        this.clazz = clazz;
    }

/**
         * {@inheritDoc}
         *
         * @param v value to serialize
         * @return JSON UTF-8 bytes
         */
    @Override
    public byte[] serialize(V v) throws SerializationException {
        return JacksonUtil.writeValueAsBytes(v);
    }

/**
         * {@inheritDoc}
         *
         * @param key   cache key (unused)
         * @param bytes JSON bytes
         * @return deserialized entity, or {@code null} when bytes are {@code null}
         * @throws org.springframework.data.redis.serializer.SerializationException when JSON is invalid
         */
    @Override
    public V deserialize(K key, byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        try {
            return JacksonUtil.IGNORE_UNKNOWN_PROPERTIES_JSON_MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize cached value", e);
        }
    }
}
