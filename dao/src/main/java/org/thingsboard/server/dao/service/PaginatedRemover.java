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
package org.thingsboard.server.dao.service;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;






















/**






 * Paginated remover (shared DAO validators, removers, and constraints).






 */







public abstract class PaginatedRemover<I, D> {

    private static final int DEFAULT_LIMIT = 100;
    /**
     * Removes entities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void removeEntities(TenantId tenantId, I id) {
        PageLink pageLink = new PageLink(DEFAULT_LIMIT);
        boolean hasNext = true;
        while (hasNext) {
            PageData<D> entities = findEntities(tenantId, id, pageLink);
            for (D entity : entities.getData()) {
                removeEntity(tenantId, entity);
            }
            hasNext = entities.hasNext();
        }
    }
    /**
     * Finds entities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract PageData<D> findEntities(TenantId tenantId, I id, PageLink pageLink);
    /**
     * Removes entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entity domain entity to persist or validate
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract void removeEntity(TenantId tenantId, D entity);

}
