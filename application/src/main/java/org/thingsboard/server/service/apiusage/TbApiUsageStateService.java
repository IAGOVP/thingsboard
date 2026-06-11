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
package org.thingsboard.server.service.apiusage;

import org.springframework.context.ApplicationListener;
import org.thingsboard.rule.engine.api.RuleEngineApiUsageStateService;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.common.stats.TbApiUsageStateClient;
import org.thingsboard.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.discovery.event.PartitionChangeEvent;

/**

 * Service contract for tb api usage state operations (tenant API usage metering and rate-limit state).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface TbApiUsageStateService extends TbApiUsageStateClient, RuleEngineApiUsageStateService, ApplicationListener<PartitionChangeEvent> {

    void process(TbProtoQueueMsg<ToUsageStatsServiceMsg> msg, TbCallback callback);

    /**
     * Handles tenant profile update.
     *
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onTenantProfileUpdate(TenantProfileId tenantProfileId);

    /**
     * Handles tenant update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onTenantUpdate(TenantId tenantId);

    /**
     * Handles tenant delete.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onTenantDelete(TenantId tenantId);

    /**
     * Handles customer delete.
     *
     * @param customerId customer id ({@link CustomerId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onCustomerDelete(CustomerId customerId);

    /**
     * Handles api usage state update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onApiUsageStateUpdate(TenantId tenantId);
}
