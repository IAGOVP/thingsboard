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
package org.thingsboard.server.dao.entity;

import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityData;
import org.thingsboard.server.common.data.query.EntityDataQuery;


/**

 * Persistence contract for entity query.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (generic entity services, counts, and DAO registry).

 */


public interface EntityQueryDao {
    /**
     * Counts entities by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    long countEntitiesByQuery(TenantId tenantId, CustomerId customerId, EntityCountQuery query);
    /**
     * Finds entity data by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityData> findEntityDataByQuery(TenantId tenantId, CustomerId customerId, EntityDataQuery query);

}
