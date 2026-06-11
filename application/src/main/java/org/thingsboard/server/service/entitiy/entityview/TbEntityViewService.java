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
package org.thingsboard.server.service.entitiy.entityview;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.plugin.ComponentLifecycleListener;

import java.util.List;

/**

 * Application-layer service API for entity view entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbEntityViewService extends ComponentLifecycleListener {
/**
 * Saves or persists the requested data.
 *
 * @param entityView entity view ({@link EntityView})
 * @param existingEntityView existing entity view ({@link EntityView})
 * @param user authenticated user performing the action
 * @return {@link EntityView}
 * @throws Exception if an unexpected error occurs during processing
 */



    EntityView save(EntityView entityView, EntityView existingEntityView, User user) throws Exception;
/**
 * Saves or persists the requested data.
 *
 * @param entityView entity view ({@link EntityView})
 * @param existingEntityView existing entity view ({@link EntityView})
 * @param nameConflictStrategy name conflict strategy ({@link NameConflictStrategy})
 * @param user authenticated user performing the action
 * @return {@link EntityView}
 * @throws Exception if an unexpected error occurs during processing
 */

    EntityView save(EntityView entityView, EntityView existingEntityView, NameConflictStrategy nameConflictStrategy, User user) throws Exception;
/**
 * Updates entity view attributes.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param savedEntityView saved entity view ({@link EntityView})
 * @param oldEntityView old entity view ({@link EntityView})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void updateEntityViewAttributes(TenantId tenantId, EntityView savedEntityView, EntityView oldEntityView, User user) throws ThingsboardException;
/**
 * Deletes the requested data.
 *
 * @param entity entity ({@link EntityView})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void delete(EntityView entity, User user) throws ThingsboardException;
/**
 * Assigns entity view to customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityViewId entity view id ({@link EntityViewId})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link EntityView}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    EntityView assignEntityViewToCustomer(TenantId tenantId, EntityViewId entityViewId, Customer customer, User user) throws ThingsboardException;
/**
 * Assigns entity view to public customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityViewId entity view id ({@link EntityViewId})
 * @param user authenticated user performing the action
 * @return {@link EntityView}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    EntityView assignEntityViewToPublicCustomer(TenantId tenantId, EntityViewId entityViewId, User user) throws ThingsboardException;
/**
 * Assigns entity view to edge.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param entityViewId entity view id ({@link EntityViewId})
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link EntityView}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    EntityView assignEntityViewToEdge(TenantId tenantId, CustomerId customerId, EntityViewId entityViewId, Edge edge, User user) throws ThingsboardException;
/**
 * Unassigns entity view from edge.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param entityView entity view ({@link EntityView})
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link EntityView}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    EntityView unassignEntityViewFromEdge(TenantId tenantId, CustomerId customerId, EntityView entityView, Edge edge, User user) throws ThingsboardException;
/**
 * Unassigns entity view from customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityViewId entity view id ({@link EntityViewId})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link EntityView}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    EntityView unassignEntityViewFromCustomer(TenantId tenantId, EntityViewId entityViewId, Customer customer, User user) throws ThingsboardException;
/**
 * Finds entity views by tenant id and entity id async.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<EntityView>> findEntityViewsByTenantIdAndEntityIdAsync(TenantId tenantId, EntityId entityId);
}
