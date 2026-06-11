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
 * Wrapper distinguishing a cache miss from a stored {@code null} (negative cache hit).
 *
 * <p>{@link #get()} returns the cached value which may legitimately be {@code null}
 * when the wrapper represents a negative-cache entry. A {@code null} wrapper reference
 * from {@link TbTransactionalCache#get} indicates a complete cache miss.
 *
 * @param <T> cached value type
 * @see SimpleTbCacheValueWrapper
 * @see TbTransactionalCache#get
 */
public interface TbCacheValueWrapper<T> {

/**
         * Returns the cached value, which may be {@code null} for negative-cache entries.
         *
         * @return cached value or {@code null} when negatively cached
         */
    T get();

}
