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

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service API for tenant persistence and domain operations.
 */
public interface TenantService extends EntityDaoService {

    /**
     * Finds tenant by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Tenant}
     */
    Tenant findTenantById(TenantId tenantId);

    /**
     * Finds tenant info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TenantInfo}
     */
    TenantInfo findTenantInfoById(TenantId tenantId);

    /**
     * Finds tenant by id async.
     *
     * @param callerId caller id ({@link TenantId})
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link Tenant}
     */
    ListenableFuture<Tenant> findTenantByIdAsync(TenantId callerId, TenantId tenantId);

    /**
     * Saves or persists tenant.
     *
     * @param tenant tenant ({@link Tenant})
     * @return {@link Tenant}
     */
    Tenant saveTenant(Tenant tenant);

    /**
     * Saves or persists tenant.
     *
     * @param tenant tenant ({@link Tenant})
     * @param defaultEntitiesCreator default entities creator ({@link Consumer})
     * @return {@link Tenant}
     */
    Tenant saveTenant(Tenant tenant, Consumer<TenantId> defaultEntitiesCreator);

    /**
     * Tenant exists.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the boolean result
     */
    boolean tenantExists(TenantId tenantId);

    /**
     * Deletes tenant.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteTenant(TenantId tenantId);

    /**
     * Finds tenants.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Tenant> findTenants(PageLink pageLink);

    /**
     * Finds tenant infos.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TenantInfo> findTenantInfos(PageLink pageLink);

    /**
     * Finds tenant ids by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return {@link List}
     */
    List<TenantId> findTenantIdsByTenantProfileId(TenantProfileId tenantProfileId);

    /**
     * Finds tenant by name.
     *
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link Tenant}
     */
    Tenant findTenantByName(String name);

    /**
     * Deletes tenants.
     *
     */
    void deleteTenants();

    /**
     * Finds tenants ids.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TenantId> findTenantsIds(PageLink pageLink);

    /**
     * Finds tenants by ids.
     *
     * @param callerId caller id ({@link TenantId})
     * @param tenantIds tenant ids ({@link List})
     * @return {@link List}
     */
    List<Tenant> findTenantsByIds(TenantId callerId, List<TenantId> tenantIds);

}
