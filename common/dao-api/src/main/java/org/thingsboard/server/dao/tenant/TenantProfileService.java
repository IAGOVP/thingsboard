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
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.UUID;

/**
 * Service API for tenant profile persistence and domain operations.
 */
public interface TenantProfileService extends EntityDaoService {

    /**
     * Finds tenant profile by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return {@link TenantProfile}
     */
    TenantProfile findTenantProfileById(TenantId tenantId, TenantProfileId tenantProfileId);

    /**
     * Finds tenant profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return {@link EntityInfo}
     */
    EntityInfo findTenantProfileInfoById(TenantId tenantId, TenantProfileId tenantProfileId);

    /**
     * Saves or persists tenant profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfile tenant profile ({@link TenantProfile})
     * @return {@link TenantProfile}
     */
    TenantProfile saveTenantProfile(TenantId tenantId, TenantProfile tenantProfile);

    /**
     * Deletes tenant profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     */
    void deleteTenantProfile(TenantId tenantId, TenantProfileId tenantProfileId);

    /**
     * Finds tenant profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TenantProfile> findTenantProfiles(TenantId tenantId, PageLink pageLink);

    /**
     * Finds tenant profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityInfo> findTenantProfileInfos(TenantId tenantId, PageLink pageLink);

    /**
     * Finds or create default tenant profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TenantProfile}
     */
    TenantProfile findOrCreateDefaultTenantProfile(TenantId tenantId);

    /**
     * Finds default tenant profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TenantProfile}
     */
    TenantProfile findDefaultTenantProfile(TenantId tenantId);

    /**
     * Finds default tenant profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link EntityInfo}
     */
    EntityInfo findDefaultTenantProfileInfo(TenantId tenantId);

    /**
     * Set default tenant profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return {@link TenantProfile}
     */
    TenantProfile setDefaultTenantProfile(TenantId tenantId, TenantProfileId tenantProfileId);

    /**
     * Deletes tenant profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteTenantProfiles(TenantId tenantId);

    /**
     * Finds tenant profiles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ids ids
     * @return {@link List}
     */
    List<TenantProfile> findTenantProfilesByIds(TenantId tenantId, UUID[] ids);

}
