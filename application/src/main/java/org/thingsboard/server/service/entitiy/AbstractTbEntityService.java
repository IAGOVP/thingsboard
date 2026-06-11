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
package org.thingsboard.server.service.entitiy;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.dao.alarm.AlarmService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.edge.EdgeService;
import org.thingsboard.server.dao.entity.EntityService;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.service.sync.vc.EntitiesVersionControlService;
import org.thingsboard.server.service.telemetry.AlarmSubscriptionService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
/**
 * Base class for tenant-scoped entity services in the REST layer.
 *
 * <p>Provides audit logging, version-control auto-commit, validation helpers, and cluster notification hooks.
 */

@Slf4j
public abstract class AbstractTbEntityService {

    @Autowired
    private Environment env;

    @Autowired(required = false)
    protected TbLogEntityActionService logEntityActionService;
    @Autowired(required = false)
    protected EdgeService edgeService;
    @Autowired
    protected AlarmService alarmService;
    @Autowired
    @Lazy
    protected AlarmSubscriptionService alarmSubscriptionService;
    @Autowired
    protected CustomerService customerService;
    @Autowired
    protected TbClusterService tbClusterService;
    @Autowired(required = false)
    @Lazy
    private EntitiesVersionControlService vcService;
    @Autowired
    protected EntityService entityService;
    /**
     * Is test profile.
     *
     * @return the boolean result
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    protected boolean isTestProfile() {
        return Set.of(this.env.getActiveProfiles()).contains("test");
    }
    /**
     * Checks not null.
     *
     * @param reference reference ({@link T})
     * @return {@link T}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    protected <T> T checkNotNull(T reference) throws ThingsboardException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }
    /**
     * Checks not null.
     *
     * @param reference reference ({@link T})
     * @param notFoundMessage not found message ({@link String})
     * @return {@link T}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    protected <T> T checkNotNull(T reference, String notFoundMessage) throws ThingsboardException {
        if (reference == null) {
            throw new ThingsboardException(notFoundMessage, ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }
    /**
     * Checks not null.
     *
     * @param reference reference ({@link Optional})
     * @return {@link T}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    protected <T> T checkNotNull(Optional<T> reference) throws ThingsboardException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }
    /**
     * Checks not null.
     *
     * @param reference reference ({@link Optional})
     * @param notFoundMessage not found message ({@link String})
     * @return {@link T}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    protected <T> T checkNotNull(Optional<T> reference, String notFoundMessage) throws ThingsboardException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new ThingsboardException(notFoundMessage, ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
    }
    /**
     * Empty id.
     *
     * @param entityType entity type ({@link EntityType})
     * @return {@link I}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected <I extends EntityId> I emptyId(EntityType entityType) {
        return (I) EntityIdFactory.getByTypeAndUuid(entityType, ModelConstants.NULL_UUID);
    }
    /**
     * Returns or empty id.
     *
     * @param entityId target entity identifier
     * @param entityType entity type ({@link EntityType})
     * @return {@link I}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected <I extends EntityId> I getOrEmptyId(I entityId, EntityType entityType) {
        return entityId == null ? emptyId(entityType) : entityId;
    }
    /**
     * Triggers auto-commit for the requested data.
     *
     * @param user authenticated user performing the action
     * @param entityId target entity identifier
     * @return future completing with {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected ListenableFuture<UUID> autoCommit(User user, EntityId entityId) {
        if (vcService != null) {
            return vcService.autoCommit(user, entityId);
        } else {
            // We do not support auto-commit for rule engine
            return Futures.immediateFailedFuture(new RuntimeException("Operation not supported!"));
        }
    }
    /**
     * Triggers auto-commit for the requested data.
     *
     * @param user authenticated user performing the action
     * @param entityType entity type ({@link EntityType})
     * @param entityIds entity ids ({@link List})
     * @return future completing with {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected ListenableFuture<UUID> autoCommit(User user, EntityType entityType, List<UUID> entityIds) {
        if (vcService != null) {
            return vcService.autoCommit(user, entityType, entityIds);
        } else {
            // We do not support auto-commit for rule engine
            return Futures.immediateFailedFuture(new RuntimeException("Operation not supported!"));
        }
    }

}
