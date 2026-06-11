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

import java.io.Serializable;

/**
 * Marker interface for cache keys participating in versioned optimistic concurrency.
 *
 * <p>Implemented by keys such as {@link org.thingsboard.server.cache.device.DeviceCacheKey}.
 * When {@link #isVersioned()} returns {@code true}, {@link VersionedRedisTbCache} stores
 * an 8-byte big-endian version prefix before the serialized value and uses Lua compare-and-set.
 *
 * @see VersionedTbCache
 * @see VersionedRedisTbCache
 * @see VersionedCaffeineTbCache
 */
public interface VersionedCacheKey extends Serializable {

/**
         * Indicates whether version-prefix storage applies to this key.
         *
         * @return {@code false} by default; override to enable versioned Redis storage
         */
    default boolean isVersioned() {
        return false;
    }

}
