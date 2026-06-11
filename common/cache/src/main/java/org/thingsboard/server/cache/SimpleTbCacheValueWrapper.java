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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.cache.Cache;

/**
 * Default {@link TbCacheValueWrapper} implementation holding a value or an explicit empty marker.
 *
 * <p>Use {@link #empty()} to represent a negative-cache hit (key present, value absent).
 * Use {@link #wrap(Object)} for normal entries. {@link #wrap(org.springframework.cache.Cache.ValueWrapper)}
 * adapts Spring Cache API results.
 *
 * @param <T> wrapped value type
 * @see TbCacheValueWrapper
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleTbCacheValueWrapper<T> implements TbCacheValueWrapper<T> {

    /** Cached value; {@code null} for negative-cache wrappers. */

    private final T value;

/**
         * {@inheritDoc}
         *
         * @return stored value, or {@code null} for {@link #empty()} wrappers
         */
    @Override
    public T get() {
        return value;
    }

    /**
     * Creates a wrapper representing a negative-cache entry.
     *
     * @param <T> value type
     * @return wrapper whose {@link #get()} returns {@code null}
     */
    public static <T> SimpleTbCacheValueWrapper<T> empty() {
        return new SimpleTbCacheValueWrapper<>(null);
    }

    /**
     * Wraps a concrete cached value.
     *
     * @param value cached value (may be {@code null})
     * @param <T>   value type
     * @return new wrapper
     */
    public static <T> SimpleTbCacheValueWrapper<T> wrap(T value) {
        return new SimpleTbCacheValueWrapper<>(value);
    }

/**
         * Adapts a Spring {@link org.springframework.cache.Cache.ValueWrapper}.
         *
         * @param source Spring cache wrapper, or {@code null} for a miss
         * @param <T>    value type
         * @return wrapper, or {@code null} when {@code source} is {@code null}
         */
    @SuppressWarnings("unchecked")
    public static <T> SimpleTbCacheValueWrapper<T> wrap(Cache.ValueWrapper source) {
        return source == null ? null : new SimpleTbCacheValueWrapper<>((T) source.get());
    }
}
