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
package org.thingsboard.server.dao.asset;

import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for asset profile persistence and domain operations.
 */
public interface AssetProfileService extends EntityDaoService {

    /**
     * Finds asset profile by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @return {@link AssetProfile}
     */
    AssetProfile findAssetProfileById(TenantId tenantId, AssetProfileId assetProfileId);

    /**
     * Finds asset profile by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @param putInCache put in cache
     * @return {@link AssetProfile}
     */
    AssetProfile findAssetProfileById(TenantId tenantId, AssetProfileId assetProfileId, boolean putInCache);

    /**
     * Finds asset profile by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link AssetProfile}
     */
    AssetProfile findAssetProfileByName(TenantId tenantId, String profileName);

    /**
     * Finds asset profile by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @param putInCache put in cache
     * @return {@link AssetProfile}
     */
    AssetProfile findAssetProfileByName(TenantId tenantId, String profileName, boolean putInCache);

    /**
     * Finds asset profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @return {@link AssetProfileInfo}
     */
    AssetProfileInfo findAssetProfileInfoById(TenantId tenantId, AssetProfileId assetProfileId);

    /**
     * Saves or persists asset profile.
     *
     * @param assetProfile asset profile ({@link AssetProfile})
     * @return {@link AssetProfile}
     */
    AssetProfile saveAssetProfile(AssetProfile assetProfile);

    /**
     * Saves or persists asset profile.
     *
     * @param assetProfile asset profile ({@link AssetProfile})
     * @param doValidate whether to run validation before persist
     * @param publishSaveEvent publish save event
     * @return {@link AssetProfile}
     */
    AssetProfile saveAssetProfile(AssetProfile assetProfile, boolean doValidate, boolean publishSaveEvent);

    /**
     * Deletes asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     */
    void deleteAssetProfile(TenantId tenantId, AssetProfileId assetProfileId);

    /**
     * Finds asset profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetProfile> findAssetProfiles(TenantId tenantId, PageLink pageLink);

    /**
     * Finds asset profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetProfileInfo> findAssetProfileInfos(TenantId tenantId, PageLink pageLink);

    /**
     * Finds or create asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link AssetProfile}
     */
    AssetProfile findOrCreateAssetProfile(TenantId tenantId, String profileName);

    /**
     * Creates default asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfile}
     */
    AssetProfile createDefaultAssetProfile(TenantId tenantId);

    /**
     * Finds default asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfile}
     */
    AssetProfile findDefaultAssetProfile(TenantId tenantId);

    /**
     * Finds default asset profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfileInfo}
     */
    AssetProfileInfo findDefaultAssetProfileInfo(TenantId tenantId);

    /**
     * Set default asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @return the boolean result
     */
    boolean setDefaultAssetProfile(TenantId tenantId, AssetProfileId assetProfileId);

    /**
     * Deletes asset profiles by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteAssetProfilesByTenantId(TenantId tenantId);

    /**
     * Finds asset profile names by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activeOnly active only
     * @return {@link List}
     */
    List<EntityInfo> findAssetProfileNamesByTenantId(TenantId tenantId, boolean activeOnly);

    /**
     * Finds asset profiles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileIds asset profile ids ({@link List})
     * @return {@link List}
     */
    List<AssetProfileInfo> findAssetProfilesByIds(TenantId tenantId, List<AssetProfileId> assetProfileIds);

}
