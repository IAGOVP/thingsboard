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
package org.thingsboard.server.dao.audit;

import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.audit.AuditLog;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for audit log.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (audit log persistence and log-level configuration).

 */


public interface AuditLogDao extends Dao<AuditLog> {
    /**
     * Finds audit logs by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AuditLog> findAuditLogsByTenantIdAndEntityId(UUID tenantId, EntityId entityId, List<ActionType> actionTypes, TimePageLink pageLink);
    /**
     * Finds audit logs by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AuditLog> findAuditLogsByTenantIdAndCustomerId(UUID tenantId, CustomerId customerId, List<ActionType> actionTypes, TimePageLink pageLink);
    /**
     * Finds audit logs by tenant id and user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AuditLog> findAuditLogsByTenantIdAndUserId(UUID tenantId, UserId userId, List<ActionType> actionTypes, TimePageLink pageLink);
    /**
     * Finds audit logs by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AuditLog> findAuditLogsByTenantId(UUID tenantId, List<ActionType> actionTypes, TimePageLink pageLink);
    /**
     * Clean up audit logs.
     *
     * @param expTime exp time
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void cleanUpAuditLogs(long expTime);

}
