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

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.dao.Dao;

import java.util.List;


/**

 * Persistence contract for queue.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (ThingsBoard DAO layer).

 */


public interface QueueDao extends Dao<Queue> {
    /**
     * Finds queue by tenant id and topic.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param topic topic ({@link String})
     * @return {@link Queue}
     * @throws Exception if an unexpected error occurs during processing
     */
    Queue findQueueByTenantIdAndTopic(TenantId tenantId, String topic);
    /**
     * Finds queue by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link Queue}
     * @throws Exception if an unexpected error occurs during processing
     */

    Queue findQueueByTenantIdAndName(TenantId tenantId, String name);
    /**
     * Finds all main queues.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<Queue> findAllMainQueues();
    /**
     * Finds all queues.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<Queue> findAllQueues();
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<Queue> findAllByTenantId(TenantId tenantId);
    /**
     * Finds queues by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Queue> findQueuesByTenantId(TenantId tenantId, PageLink pageLink);
}