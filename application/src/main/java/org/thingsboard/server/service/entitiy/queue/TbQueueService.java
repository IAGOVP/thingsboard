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
package org.thingsboard.server.service.entitiy.queue;

import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.id.QueueId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.queue.Queue;

import java.util.List;

/**

 * Application-layer service API for queue entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbQueueService {
/**
 * Saves or persists queue.
 *
 * @param queue queue ({@link Queue})
 * @return {@link Queue}
 * @throws Exception if an unexpected error occurs during processing
 */



    Queue saveQueue(Queue queue);
/**
 * Deletes queue.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param queueId queue id ({@link QueueId})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void deleteQueue(TenantId tenantId, QueueId queueId);
/**
 * Deletes queue by queue name.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param queueName queue name ({@link String})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void deleteQueueByQueueName(TenantId tenantId, String queueName);
/**
 * Updates queues by tenants.
 *
 * @param tenantIds tenant ids ({@link List})
 * @param newTenantProfile new tenant profile ({@link TenantProfile})
 * @param oldTenantProfile old tenant profile ({@link TenantProfile})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void updateQueuesByTenants(List<TenantId> tenantIds, TenantProfile newTenantProfile, TenantProfile oldTenantProfile);
}
