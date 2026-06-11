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

import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.id.TenantId;

/**
 * Supplies {@link org.thingsboard.server.common.data.TenantProfile} data for rate-limit configuration lookup.
 *
 * <p>Implemented in the application module; injected into {@link DefaultRateLimitService}.
 *
 * @see RateLimitService
 */
public interface TenantProfileProvider {

/**
         * Loads the tenant profile containing API limit configuration.
         *
         * @param tenantId tenant identifier
         * @return tenant profile, or {@code null} when not found
         */
    TenantProfile get(TenantId tenantId);

}
