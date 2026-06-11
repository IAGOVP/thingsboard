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

import org.thingsboard.server.common.data.id.QueueStatsId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.queue.QueueStats;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for queue stats persistence and domain operations.
 */
public interface QueueStatsService extends EntityDaoService {

    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueStats queue stats ({@link QueueStats})
     * @return {@link QueueStats}
     */
    QueueStats save(TenantId tenantId, QueueStats queueStats);

    /**
     * Finds queue stats by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueStatsId queue stats id ({@link QueueStatsId})
     * @return {@link QueueStats}
     */
    QueueStats findQueueStatsById(TenantId tenantId, QueueStatsId queueStatsId);

    /**
     * Finds queue stats by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueStatsId queue stats id ({@link List})
     * @return {@link List}
     */
    List<QueueStats> findQueueStatsByIds(TenantId tenantId, List<QueueStatsId> queueStatsId);

    /**
     * Finds by tenant id and name and service id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueName queue name ({@link String})
     * @param serviceId service id ({@link String})
     * @return {@link QueueStats}
     */
    QueueStats findByTenantIdAndNameAndServiceId(TenantId tenantId, String queueName, String serviceId);

    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<QueueStats> findByTenantId(TenantId tenantId, PageLink pageLink);

}
