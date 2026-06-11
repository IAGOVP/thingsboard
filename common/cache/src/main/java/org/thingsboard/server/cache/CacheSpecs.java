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

import lombok.Data;

/**
 * Per-cache sizing and time-to-live settings bound from {@code cache.specs.<name>.*}.
 *
 * <p>Used by {@link CacheSpecsMap} to configure both Caffeine weight limits
 * ({@link #maxSize}) and Redis/Caffeine entry expiration ({@link #timeToLiveInMinutes}).
 * A TTL of {@code 0} means entries never expire.
 *
 * @see CacheSpecsMap
 * @see TbCaffeineCacheConfiguration
 * @see RedisTbTransactionalCache
 */
@Data

public class CacheSpecs {
    /** Entry TTL in minutes; {@code 0} disables expiration. */
    private Integer timeToLiveInMinutes;
    /** Maximum cache weight (Caffeine) or enable flag (Redis: {@code > 0} enables). */
    private Integer maxSize;
}
