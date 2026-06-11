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
package org.thingsboard.server.dao;

import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.UUID;


/**

 * Persistence contract for exportable entity.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (ThingsBoard DAO layer).

 */


public interface ExportableEntityDao<I extends EntityId, T extends ExportableEntity<I>> extends Dao<T> {
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    T findByTenantIdAndExternalId(UUID tenantId, UUID externalId);
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    default T findByTenantIdAndName(UUID tenantId, String name) { throw new UnsupportedOperationException(); }
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<T> findByTenantId(UUID tenantId, PageLink pageLink);
    /**
     * Finds ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    default PageData<I> findIdsByTenantId(UUID tenantId, PageLink pageLink) {
        return findByTenantId(tenantId, pageLink).mapData(ExportableEntity::getId);
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link I})
     * @return {@link I}
     * @throws Exception if an unexpected error occurs during processing
     */

    I getExternalIdByInternal(I internalId);
    /**
     * Finds default entity by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    default T findDefaultEntityByTenantId(UUID tenantId) { throw new UnsupportedOperationException(); }

}
