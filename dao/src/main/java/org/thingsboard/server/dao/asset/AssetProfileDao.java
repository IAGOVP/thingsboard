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
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.ImageContainerDao;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for asset profile.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (asset and asset-profile DAO services and caches).

 */


public interface AssetProfileDao extends Dao<AssetProfile>, ExportableEntityDao<AssetProfileId, AssetProfile>, ImageContainerDao<AssetProfileInfo> {
    /**
     * Finds asset profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @return {@link AssetProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfileInfo findAssetProfileInfoById(TenantId tenantId, UUID assetProfileId);
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfile asset profile ({@link AssetProfile})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfile save(TenantId tenantId, AssetProfile assetProfile);
    /**
     * Saves or persists and flush.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfile asset profile ({@link AssetProfile})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfile saveAndFlush(TenantId tenantId, AssetProfile assetProfile);
    /**
     * Finds asset profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetProfile> findAssetProfiles(TenantId tenantId, PageLink pageLink);
    /**
     * Finds asset profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetProfileInfo> findAssetProfileInfos(TenantId tenantId, PageLink pageLink);
    /**
     * Finds default asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfile findDefaultAssetProfile(TenantId tenantId);
    /**
     * Finds default asset profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfileInfo findDefaultAssetProfileInfo(TenantId tenantId);
    /**
     * Finds by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfile findByName(TenantId tenantId, String profileName);
    /**
     * Finds all with images.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetProfile> findAllWithImages(PageLink pageLink);
    /**
     * Finds tenant asset profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activeOnly active only
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityInfo> findTenantAssetProfileNames(UUID tenantId, boolean activeOnly);
    /**
     * Finds asset profiles by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileIds asset profile ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<AssetProfileInfo> findAssetProfilesByTenantIdAndIds(UUID tenantId, List<UUID> assetProfileIds);

}
