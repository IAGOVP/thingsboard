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
package org.thingsboard.server.service.ttl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.queue.discovery.PartitionService;

    /**
     * Abstract clean up service (time-to-live cleanup for alarms, events, and telemetry).
     */


@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCleanUpService {

    private final PartitionService partitionService;
    /**
     * Is system tenant partition mine.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected boolean isSystemTenantPartitionMine() {
        return partitionService.resolve(ServiceType.TB_CORE, TenantId.SYS_TENANT_ID, TenantId.SYS_TENANT_ID).isMyPartition();
    }
    /**
     * Is tenant partition mine.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected boolean isTenantPartitionMine(TenantId tenantId) {
        return partitionService.resolve(ServiceType.TB_CORE, tenantId, tenantId).isMyPartition();
    }

}
