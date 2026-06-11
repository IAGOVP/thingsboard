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

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Java native serialization {@link TbRedisSerializer} using Spring {@link org.springframework.data.redis.serializer.RedisSerializer#java()}.
 *
 * <p>Produces compact binary payloads but requires compatible class versions across cluster nodes.
 * Prefer {@link TbJsonRedisSerializer} for entity caches unless binary compatibility is required.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see TbRedisSerializer
 */
public class TbJavaRedisSerializer<K, V> implements TbRedisSerializer<K, V> {

    /** Delegating Spring Java serializer. */

    final RedisSerializer<Object> serializer = RedisSerializer.java();

/**
         * {@inheritDoc}
         *
         * @param value value to serialize
         * @return Java-serialized bytes
         * @throws org.springframework.data.redis.serializer.SerializationException on failure
         */
    @Override
    public byte[] serialize(V value) throws SerializationException {
        return serializer.serialize(value);
    }

/**
         * {@inheritDoc}
         *
         * @param key   cache key (unused)
         * @param bytes Java-serialized bytes
         * @return deserialized object
         * @throws org.springframework.data.redis.serializer.SerializationException on failure
         */
    @Override
    public V deserialize(K key, byte[] bytes) throws SerializationException {
        return (V) serializer.deserialize(bytes);
    }

}
