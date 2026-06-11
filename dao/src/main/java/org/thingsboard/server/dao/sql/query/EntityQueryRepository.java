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
package org.thingsboard.server.dao.sql.query;

import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityData;
import org.thingsboard.server.common.data.query.EntityDataQuery;


/**

 * Spring Data JPA repository for entity query entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface EntityQueryRepository {
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
    /**
     * Finds entity data by query internal.
     *
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityData> findEntityDataByQueryInternal(EntityDataQuery query);

}
