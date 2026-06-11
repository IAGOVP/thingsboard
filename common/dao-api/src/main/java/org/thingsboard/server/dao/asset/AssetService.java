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
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.ProfileEntityIdInfo;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.asset.AssetSearchQuery;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for asset persistence and domain operations.
 */
public interface AssetService extends EntityDaoService {

    /**
     * Finds asset info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @return {@link AssetInfo}
     */
    AssetInfo findAssetInfoById(TenantId tenantId, AssetId assetId);

    /**
     * Finds asset by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @return {@link Asset}
     */
    Asset findAssetById(TenantId tenantId, AssetId assetId);

    /**
     * Finds asset by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @return future completing with {@link Asset}
     */
    ListenableFuture<Asset> findAssetByIdAsync(TenantId tenantId, AssetId assetId);

    /**
     * Finds asset by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link Asset}
     */
    Asset findAssetByTenantIdAndName(TenantId tenantId, String name);

    /**
     * Finds asset by tenant id and name async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return future completing with {@link Asset}
     */
    ListenableFuture<Asset> findAssetByTenantIdAndNameAsync(TenantId tenantId, String name);

    /**
     * Saves or persists asset.
     *
     * @param asset asset ({@link Asset})
     * @param doValidate whether to run validation before persist
     * @return {@link Asset}
     */
    Asset saveAsset(Asset asset, boolean doValidate);

    /**
     * Saves or persists asset.
     *
     * @param asset asset ({@link Asset})
     * @return {@link Asset}
     */
    Asset saveAsset(Asset asset);

    /**
     * Saves or persists asset.
     *
     * @param asset asset ({@link Asset})
     * @param nameConflictStrategy behavior when an entity with the same name already exists
     * @return {@link Asset}
     */
    Asset saveAsset(Asset asset, NameConflictStrategy nameConflictStrategy);

    /**
     * Assigns asset to customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @param customerId customer to assign or filter by
     * @return {@link Asset}
     */
    Asset assignAssetToCustomer(TenantId tenantId, AssetId assetId, CustomerId customerId);

    /**
     * Unassigns asset from customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @return {@link Asset}
     */
    Asset unassignAssetFromCustomer(TenantId tenantId, AssetId assetId);

    /**
     * Deletes asset.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     */
    void deleteAsset(TenantId tenantId, AssetId assetId);

    /**
     * Finds assets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Asset> findAssetsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds asset infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetInfo> findAssetInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds assets by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Asset> findAssetsByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    /**
     * Finds asset infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetInfo> findAssetInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    /**
     * Finds asset infos by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetInfo> findAssetInfosByTenantIdAndAssetProfileId(TenantId tenantId, AssetProfileId assetProfileId, PageLink pageLink);

    /**
     * Finds profile entity id infos.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<ProfileEntityIdInfo> findProfileEntityIdInfos(PageLink pageLink);

    /**
     * Finds profile entity id infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<ProfileEntityIdInfo> findProfileEntityIdInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds asset ids by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetId> findAssetIdsByTenantIdAndAssetProfileId(TenantId tenantId, AssetProfileId assetProfileId, PageLink pageLink);

    /**
     * Finds assets by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetIds asset ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Asset>> findAssetsByTenantIdAndIdsAsync(TenantId tenantId, List<AssetId> assetIds);

    /**
     * Deletes assets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteAssetsByTenantId(TenantId tenantId);

    /**
     * Finds assets by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Asset> findAssetsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds asset infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds assets by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Asset> findAssetsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    /**
     * Finds asset infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    /**
     * Finds asset infos by tenant id and customer id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndAssetProfileId(TenantId tenantId, CustomerId customerId, AssetProfileId assetProfileId, PageLink pageLink);

    /**
     * Finds assets by tenant id customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param assetIds asset ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Asset>> findAssetsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<AssetId> assetIds);

    /**
     * Unassigns customer assets.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void unassignCustomerAssets(TenantId tenantId, CustomerId customerId);

    /**
     * Finds assets by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link AssetSearchQuery})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Asset>> findAssetsByQuery(TenantId tenantId, AssetSearchQuery query);

    @Deprecated(since = "3.6.2", forRemoval = true)
    /**
     * Finds asset types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntitySubtype>> findAssetTypesByTenantId(TenantId tenantId);

    /**
     * Assigns asset to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Asset}
     */
    Asset assignAssetToEdge(TenantId tenantId, AssetId assetId, EdgeId edgeId);

    /**
     * Unassigns asset from edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Asset}
     */
    Asset unassignAssetFromEdge(TenantId tenantId, AssetId assetId, EdgeId edgeId);

    /**
     * Finds assets by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Asset> findAssetsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    /**
     * Finds assets by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Asset> findAssetsByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);
}
