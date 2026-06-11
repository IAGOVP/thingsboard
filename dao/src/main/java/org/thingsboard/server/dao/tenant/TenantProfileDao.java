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

import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for tenant profile.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (tenants, tenant profiles, and profile caching).

 */


public interface TenantProfileDao extends Dao<TenantProfile> {
    /**
     * Finds tenant profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfileId tenant profile id ({@link UUID})
     * @return {@link EntityInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityInfo findTenantProfileInfoById(TenantId tenantId, UUID tenantProfileId);
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfile tenant profile ({@link TenantProfile})
     * @return {@link TenantProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    TenantProfile save(TenantId tenantId, TenantProfile tenantProfile);
    /**
     * Finds tenant profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TenantProfile> findTenantProfiles(TenantId tenantId, PageLink pageLink);
    /**
     * Finds tenant profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityInfo> findTenantProfileInfos(TenantId tenantId, PageLink pageLink);
    /**
     * Finds default tenant profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TenantProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    TenantProfile findDefaultTenantProfile(TenantId tenantId);
    /**
     * Finds default tenant profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link EntityInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityInfo findDefaultTenantProfileInfo(TenantId tenantId);
    /**
     * Finds tenant profiles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ids ids
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TenantProfile> findTenantProfilesByIds(TenantId tenantId, UUID[] ids);

}
