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

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.ProfileEntityIdInfo;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for asset.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (asset and asset-profile DAO services and caches).
 */

public interface AssetDao extends Dao<Asset>, TenantEntityDao<Asset>, ExportableEntityDao<AssetId, Asset> {

    
    /**
     * Finds asset info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link UUID})
     * @return {@link AssetInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetInfo findAssetInfoById(TenantId tenantId, UUID assetId);

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param asset asset ({@link Asset})
     * @return {@link Asset}
     * @throws Exception if an unexpected error occurs during processing
     */

    Asset save(TenantId tenantId, Asset asset);

    
    /**
     * Finds assets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Asset> findAssetsByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds asset infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetInfo> findAssetInfosByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds assets by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Asset> findAssetsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    
    /**
     * Finds asset infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetInfo> findAssetInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    
    /**
     * Finds asset infos by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetInfo> findAssetInfosByTenantIdAndAssetProfileId(UUID tenantId, UUID assetProfileId, PageLink pageLink);

    
    /**
     * Finds asset ids by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetId> findAssetIdsByTenantIdAndAssetProfileId(UUID tenantId, UUID assetProfileId, PageLink pageLink);

    
    /**
     * Finds assets by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetIds asset ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<Asset>> findAssetsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> assetIds);

    
    /**
     * Finds assets by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Asset> findAssetsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    
    /**
     * Finds asset infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    
    /**
     * Finds assets by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Asset> findAssetsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    
    /**
     * Finds asset infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    
    /**
     * Finds asset infos by tenant id and customer id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param assetProfileId asset profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndAssetProfileId(UUID tenantId, UUID customerId, UUID assetProfileId, PageLink pageLink);

    
    /**
     * Finds assets by tenant id and customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param assetIds asset ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<Asset>> findAssetsByTenantIdAndCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> assetIds);

    
    /**
     * Finds assets by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return optional {@link Asset}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<Asset> findAssetsByTenantIdAndName(UUID tenantId, String name);

    
    /**
     * Finds tenant asset types async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated(since = "3.6.2", forRemoval = true)
    ListenableFuture<List<EntitySubtype>> findTenantAssetTypesAsync(UUID tenantId);
    /**
     * Counts assets by asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long countAssetsByAssetProfileId(TenantId tenantId, UUID assetProfileId);

    
    /**
     * Finds assets by tenant id and profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileId profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Asset> findAssetsByTenantIdAndProfileId(UUID tenantId, UUID profileId, PageLink pageLink);

    
    /**
     * Finds assets by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Asset> findAssetsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink);

    
    /**
     * Finds assets by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Asset> findAssetsByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink);
    /**
     * Returns all asset types.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TbPair<UUID, String>> getAllAssetTypes(PageLink pageLink);
    /**
     * Finds profile entity id infos.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<ProfileEntityIdInfo> findProfileEntityIdInfos(PageLink pageLink);
    /**
     * Finds profile entity id infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<ProfileEntityIdInfo> findProfileEntityIdInfosByTenantId(UUID tenantId, PageLink pageLink);

}
