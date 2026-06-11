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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
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
 * Spring {@code @Service} implementing the dummy audit log DAO API.
 *
 * <p>Delegates to {@code *Dao} implementations and manages cache eviction (audit log persistence and log-level configuration).
 */


@Service
@ConditionalOnProperty(prefix = "audit-log", value = "enabled", havingValue = "false")
public class DummyAuditLogServiceImpl implements AuditLogService {

    
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


    @Override
    public PageData<AuditLog> findAuditLogsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return new PageData<>();
    }

    
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


    @Override
    public PageData<AuditLog> findAuditLogsByTenantIdAndUserId(TenantId tenantId, UserId userId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return new PageData<>();
    }

    
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


    @Override
    public PageData<AuditLog> findAuditLogsByTenantIdAndEntityId(TenantId tenantId, EntityId entityId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return new PageData<>();
    }

    
    /**
     * Finds audit logs by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param actionTypes action types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AuditLog> findAuditLogsByTenantId(TenantId tenantId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return new PageData<>();
    }

    
    /**
     * Log entity action.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param userId target user identifier
     * @param userName user name ({@link String})
     * @param entityId target entity identifier
     * @param entity domain entity to persist or validate
     * @param actionType action type ({@link ActionType})
     * @param e e ({@link Exception})
     * @param additionalInfo additional info
     * @return future completing with {@link Void}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public <E extends HasName, I extends EntityId> ListenableFuture<Void> logEntityAction(TenantId tenantId, CustomerId customerId, UserId userId, String userName, I entityId, E entity, ActionType actionType, Exception e, Object... additionalInfo) {
        return null;
    }

}
