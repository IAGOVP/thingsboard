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

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.redis.serializer.SerializationException;
import org.thingsboard.common.util.JacksonUtil;

/**
 * JSON {@link TbRedisSerializer} using Jackson {@link com.fasterxml.jackson.core.type.TypeReference}
 * for generic or complex value types.
 *
 * <p>Used when the cached type is not a simple {@link Class} (e.g. {@code List<Device>},
 * {@code Map<String, Object>}).
 *
 * @param <K> cache key type
 * @param <V> cache value type described by the type reference
 * @see TbJsonRedisSerializer
 */
public class TbTypedJsonRedisSerializer<K, V> implements TbRedisSerializer<K, V> {

    /** Jackson type token for {@code V}. */

    private final TypeReference<V> valueTypeRef;

    /**
     * @param valueTypeRef Jackson type reference for deserialization
     */
    public TbTypedJsonRedisSerializer(TypeReference<V> valueTypeRef) {
        this.valueTypeRef = valueTypeRef;
    }

/**
         * {@inheritDoc}
         *
         * @param v value to serialize
         * @return JSON bytes
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
         * @return deserialized value
         * @throws org.springframework.data.redis.serializer.SerializationException on parse failure
         */
    @Override
    public V deserialize(K key, byte[] bytes) throws SerializationException {
        return JacksonUtil.fromBytes(bytes, valueTypeRef);
    }
}
