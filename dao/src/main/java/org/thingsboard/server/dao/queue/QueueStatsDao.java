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
import org.thingsboard.server.common.data.queue.QueueStats;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;


/**

 * Persistence contract for queue stats.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (ThingsBoard DAO layer).

 */


public interface QueueStatsDao extends Dao<QueueStats>, TenantEntityDao<QueueStats> {
    /**
     * Finds by tenant id queue name and service id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueName queue name ({@link String})
     * @param serviceId service id ({@link String})
     * @return {@link QueueStats}
     * @throws Exception if an unexpected error occurs during processing
     */

    QueueStats findByTenantIdQueueNameAndServiceId(TenantId tenantId, String queueName, String serviceId);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByTenantId(TenantId tenantId);
    /**
     * Finds by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param queueStatsIds queue stats ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<QueueStats> findByIds(TenantId tenantId, List<QueueStatsId> queueStatsIds);

}