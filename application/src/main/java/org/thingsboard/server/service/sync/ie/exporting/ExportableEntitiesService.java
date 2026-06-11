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
package org.thingsboard.server.service.sync.ie.exporting;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

/**

 * Exports exportable entities service entities to portable JSON.

 *

 * <p>Used by version control and tenant migration to serialize entity graphs with dependencies.

 */

public interface ExportableEntitiesService {
/**
 * Finds entity by tenant id and external id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param externalId external id ({@link I})
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */



    <E extends ExportableEntity<I>, I extends EntityId> E findEntityByTenantIdAndExternalId(TenantId tenantId, I externalId);
/**
 * Finds entity by tenant id and id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param id id ({@link I})
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <E extends HasId<I>, I extends EntityId> E findEntityByTenantIdAndId(TenantId tenantId, I id);
/**
 * Finds entity by id.
 *
 * @param id id ({@link I})
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <E extends HasId<I>, I extends EntityId> E findEntityById(I id);
/**
 * Finds entity by tenant id and name.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityType entity type ({@link EntityType})
 * @param name name ({@link String})
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <E extends ExportableEntity<I>, I extends EntityId> E findEntityByTenantIdAndName(TenantId tenantId, EntityType entityType, String name);
/**
 * Finds default entity by tenant id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityType entity type ({@link EntityType})
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <E extends ExportableEntity<I>, I extends EntityId> E findDefaultEntityByTenantId(TenantId tenantId, EntityType entityType);
/**
 * Finds entities by tenant id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityType entity type ({@link EntityType})
 * @param pageLink pagination and sort parameters
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <E extends ExportableEntity<I>, I extends EntityId> PageData<E> findEntitiesByTenantId(TenantId tenantId, EntityType entityType, PageLink pageLink);
/**
 * Finds entities ids by tenant id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityType entity type ({@link EntityType})
 * @param pageLink pagination and sort parameters
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <I extends EntityId> PageData<I> findEntitiesIdsByTenantId(TenantId tenantId, EntityType entityType, PageLink pageLink);
/**
 * Returns external id by internal.
 *
 * @param internalId internal id ({@link I})
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <I extends EntityId> I getExternalIdByInternal(I internalId);
/**
 * Removes by id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param id id ({@link I})
 * @return the operation result
 * @throws Exception if an unexpected error occurs during processing
 */

    <I extends EntityId> void removeById(TenantId tenantId, I id);

}
