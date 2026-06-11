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

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.thingsboard.server.common.data.CacheConstants;

import java.util.Map;

/**
 * Central registry mapping cache names to {@link CacheSpecs} loaded from YAML/properties.
 *
 * <p>Bound under the {@code cache} prefix ({@code cache.specs.<cacheName>.maxSize},
 * {@code cache.specs.<cacheName>.timeToLiveInMinutes}). Injected into
 * {@link TbCaffeineCacheConfiguration}, {@link RedisTbTransactionalCache}, and entity caches.
 *
 * <p>On startup, {@link #replaceTheJWTTokenRefreshExpTime()} aligns the
 * {@link org.thingsboard.server.common.data.CacheConstants#USERS_SESSION_INVALIDATION_CACHE}
 * TTL with JWT refresh token lifetime ({@code security.jwt.refreshTokenExpTime}).
 *
 * @see CacheSpecs
 * @see TbCaffeineCacheConfiguration
 */
@Configuration
@ConfigurationProperties(prefix = "cache")
@Data

public class CacheSpecsMap {

    /** JWT refresh token lifetime in seconds. Property: {@code security.jwt.refreshTokenExpTime}. Default: 604800. */
    @Value("${security.jwt.refreshTokenExpTime:604800}")
    private int refreshTokenExpTime;

    /** Map of cache name to per-cache specs from {@code cache.specs}. */
    @Getter
    private Map<String, CacheSpecs> specs;

    /**
     * Adjusts session-invalidation cache TTL to exceed JWT refresh token lifetime.
     *
     * <p>Sets {@code timeToLiveInMinutes} to {@code (refreshTokenExpTime / 60) + 1} so
     * cached credential-update timestamps outlive refresh tokens.
     */
    @PostConstruct
    public void replaceTheJWTTokenRefreshExpTime() {
        if (specs != null) {
            var cacheSpecs = specs.get(CacheConstants.USERS_SESSION_INVALIDATION_CACHE);
            if (cacheSpecs != null) {
                cacheSpecs.setTimeToLiveInMinutes((refreshTokenExpTime / 60) + 1);
            }
        }
    }

}
