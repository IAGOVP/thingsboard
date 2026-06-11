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
package org.thingsboard.server.vc.service;

import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.queue.discovery.TenantRoutingInfo;
import org.thingsboard.server.queue.discovery.TenantRoutingInfoService;

/**
 * Stub {@link org.thingsboard.server.queue.discovery.TenantRoutingInfoService} for VC executor.
 *
 * <p>Returns tenant id without rule-engine queue routing.
 */

@Service
public class VersionControlTenantRoutingInfoService implements TenantRoutingInfoService {

    
    /**
     * Returns tenant routing info without queue name for VC executor.
     *
     * @param tenantId target tenant UUID in the test environment
     * @return {@link TenantRoutingInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TenantRoutingInfo getRoutingInfo(TenantId tenantId) {
        return new TenantRoutingInfo(tenantId, null, false);
    }
}
