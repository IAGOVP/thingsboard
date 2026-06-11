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
package org.thingsboard.server.service.entitiy.entity.relation;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.relation.EntityRelation;

/**

 * Application-layer service API for entity relation entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbEntityRelationService {
/**
 * Saves or persists the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param entity entity ({@link EntityRelation})
 * @param user authenticated user performing the action
 * @return {@link EntityRelation}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    EntityRelation save(TenantId tenantId, CustomerId customerId, EntityRelation entity, User user) throws ThingsboardException;
/**
 * Deletes the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param entity entity ({@link EntityRelation})
 * @param user authenticated user performing the action
 * @return {@link EntityRelation}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    EntityRelation delete(TenantId tenantId, CustomerId customerId, EntityRelation entity, User user) throws ThingsboardException;
/**
 * Deletes common relations.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param entityId target entity identifier
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void deleteCommonRelations(TenantId tenantId, CustomerId customerId, EntityId entityId, User user) throws ThingsboardException;

}
