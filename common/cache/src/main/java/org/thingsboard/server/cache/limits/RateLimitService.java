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
package org.thingsboard.server.cache.limits;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.limit.LimitedApi;

/**
 * Tenant and API rate limiting service backed by in-memory token buckets.
 *
 * <p>Implementations consult {@link TenantProfileProvider} for per-tenant limit strings
 * and track consumption in a Caffeine cache of {@link org.thingsboard.server.common.msg.tools.TbRateLimits} instances.
 *
 * @see DefaultRateLimitService
 * @see TenantProfileProvider
 */
public interface RateLimitService {

/**
         * Checks rate limit at tenant level (level = tenant id).
         *
         * @param api      limited API identifier
         * @param tenantId tenant to check
         * @return {@code true} when the request is within limits
         */
    boolean checkRateLimit(LimitedApi api, TenantId tenantId);

/**
         * Checks rate limit at a sub-tenant level (e.g. device, customer).
         *
         * @param api      limited API
         * @param tenantId owning tenant
         * @param level    limit scope object
         * @return {@code true} when within limits
         */
    boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level);

/**
         * Checks rate limit with optional tolerance for missing tenant profiles.
         *
         * @param api                   limited API
         * @param tenantId              tenant
         * @param level                 limit scope
         * @param ignoreTenantNotFound  when {@code true}, allows requests if profile is missing
         * @return {@code true} when within limits
         * @throws org.thingsboard.server.common.data.exception.TenantProfileNotFoundException when profile missing and not ignored
         */
    boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level, boolean ignoreTenantNotFound);

/**
         * Checks rate limit using an explicit configuration string.
         *
         * @param api              limited API
         * @param level            limit scope
         * @param rateLimitConfig  limit definition string; empty disables limiting
         * @return {@code true} when within limits
         */
    boolean checkRateLimit(LimitedApi api, Object level, String rateLimitConfig);

/**
         * Invalidates the cached rate-limit bucket for the given scope.
         *
         * @param api   limited API
         * @param level limit scope
         */
    void cleanUp(LimitedApi api, Object level);

}
