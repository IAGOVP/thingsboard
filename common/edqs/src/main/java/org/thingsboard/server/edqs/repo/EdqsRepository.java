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
package org.thingsboard.server.edqs.repo;

import org.thingsboard.server.common.data.edqs.EdqsEvent;
import org.thingsboard.server.common.data.edqs.query.QueryResult;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityDataQuery;

import java.util.function.Predicate;

/**
 * Tenant-scoped in-memory entity index API.
 *
 * <p>Applies {@link org.thingsboard.server.common.data.edqs.EdqsEvent} updates and executes {@link org.thingsboard.server.common.data.query.EntityDataQuery} / {@link org.thingsboard.server.common.data.query.EntityCountQuery} without PostgreSQL.
 */

public interface EdqsRepository {

    
     /**
      * Applies create/update/delete of an entity, relation, attribute, or latest telemetry key in the index.
      *
      * @param event EDQS create/update/delete event from Kafka
      * @return nothing
      * @throws Exception if an unexpected error occurs during processing
      */

    
    void processEvent(EdqsEvent event);

    
    /**
     * Returns entity count for the filter without loading full entity rows.
     *
     * @param tenantId tenant that owns the indexed entities
     * @param customerId customer scope for permission filtering (may be null)
     * @param query entity count or data query with filter, sort, and key selections
     * @param ignorePermissionCheck when true, skips customer/user permission filtering (system use only)
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    
    long countEntitiesByQuery(TenantId tenantId, CustomerId customerId, EntityCountQuery query, boolean ignorePermissionCheck);

    
    /**
     * Returns a page of entities matching filter, sort, and selected keys.
     *
     * @param tenantId tenant that owns the indexed entities
     * @param customerId customer scope for permission filtering (may be null)
     * @param query entity count or data query with filter, sort, and key selections
     * @param ignorePermissionCheck when true, skips customer/user permission filtering (system use only)
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    
    PageData<QueryResult> findEntityDataByQuery(TenantId tenantId, CustomerId customerId, EntityDataQuery query, boolean ignorePermissionCheck);

    
     /**
      * Removes tenant repos matching the predicate (e.g. lost Kafka partitions).
      *
      * @param predicate tenant id predicate selecting which repos to evict
      * @return nothing
      * @throws Exception if an unexpected error occurs during processing
      */

    
    void clearIf(Predicate<TenantId> predicate);

    
    /**
     * Clears all tenant indexes (used on OOM recovery or full resync).
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    
    void clear();

}
