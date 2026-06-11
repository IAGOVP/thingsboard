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
package org.thingsboard.server.dao.tenant;

import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for tenant.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (tenants, tenant profiles, and profile caching).

 */


public interface TenantDao extends Dao<Tenant> {
    /**
     * Finds tenant info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link TenantInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    TenantInfo findTenantInfoById(TenantId tenantId, UUID id);
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenant tenant ({@link Tenant})
     * @return {@link Tenant}
     * @throws Exception if an unexpected error occurs during processing
     */

    Tenant save(TenantId tenantId, Tenant tenant);
    /**
     * Finds tenants.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Tenant> findTenants(TenantId tenantId, PageLink pageLink);
    /**
     * Finds tenant infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TenantInfo> findTenantInfos(TenantId tenantId, PageLink pageLink);
    /**
     * Finds tenants ids.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TenantId> findTenantsIds(PageLink pageLink);
    /**
     * Finds tenant ids by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TenantId> findTenantIdsByTenantProfileId(TenantProfileId tenantProfileId);
    /**
     * Finds tenant by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link Tenant}
     * @throws Exception if an unexpected error occurs during processing
     */

    Tenant findTenantByName(TenantId tenantId, String name);
    /**
     * Finds tenants by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantIds tenant ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<Tenant> findTenantsByIds(UUID tenantId, List<UUID> tenantIds);

}
