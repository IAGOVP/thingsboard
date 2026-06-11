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
package org.thingsboard.server.dao.queue;

import org.thingsboard.server.common.data.id.QueueId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for queue persistence and domain operations.
 */
public interface QueueService extends EntityDaoService {

    /**
     * Saves or persists queue.
     *
     * @param queue queue ({@link Queue})
     * @return {@link Queue}
     */
    Queue saveQueue(Queue queue);

    /**
     * Deletes queue.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueId queue id ({@link QueueId})
     */
    void deleteQueue(TenantId tenantId, QueueId queueId);

    /**
     * Finds queues by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     */
    List<Queue> findQueuesByTenantId(TenantId tenantId);

    /**
     * Finds queues by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Queue> findQueuesByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds all queues.
     *
     * @return {@link List}
     */
    List<Queue> findAllQueues();

    /**
     * Finds queue by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueId queue id ({@link QueueId})
     * @return {@link Queue}
     */
    Queue findQueueById(TenantId tenantId, QueueId queueId);

    /**
     * Finds queue by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link Queue}
     */
    Queue findQueueByTenantIdAndName(TenantId tenantId, String name);

    /**
     * Finds queue by tenant id and name internal.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueName queue name ({@link String})
     * @return {@link Queue}
     */
    Queue findQueueByTenantIdAndNameInternal(TenantId tenantId, String queueName);

    /**
     * Deletes queues by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteQueuesByTenantId(TenantId tenantId);
}