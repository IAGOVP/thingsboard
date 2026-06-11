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

import com.google.common.util.concurrent.ListenableFuture;
import jakarta.validation.constraints.NotNull;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.audit.AuditLog;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;

import java.util.List;

/**
 * Service API for audit log persistence and domain operations.
 */
public interface AuditLogService {

    /**
     * Finds audit logs by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AuditLog> findAuditLogsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, List<ActionType> actionTypes, TimePageLink pageLink);

    /**
     * Finds audit logs by tenant id and user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AuditLog> findAuditLogsByTenantIdAndUserId(TenantId tenantId, UserId userId, List<ActionType> actionTypes, TimePageLink pageLink);

    /**
     * Finds audit logs by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AuditLog> findAuditLogsByTenantIdAndEntityId(TenantId tenantId, EntityId entityId, List<ActionType> actionTypes, TimePageLink pageLink);

    /**
     * Finds audit logs by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AuditLog> findAuditLogsByTenantId(TenantId tenantId, List<ActionType> actionTypes, TimePageLink pageLink);

    /**
     * Log entity action.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param userId user id ({@link UserId})
     * @param userName user name ({@link String})
     * @param entityId entity id ({@link I})
     * @param entity entity ({@link E})
     * @param actionType action type ({@link ActionType})
     * @param e e ({@link Exception})
     * @param additionalInfo additional info
     * @return the operation result
     */
    <E extends HasName, I extends EntityId> ListenableFuture<Void> logEntityAction(
            TenantId tenantId,
            CustomerId customerId,
            UserId userId,
            String userName,
            @NotNull I entityId,
            E entity,
            ActionType actionType,
            Exception e, Object... additionalInfo);
}
