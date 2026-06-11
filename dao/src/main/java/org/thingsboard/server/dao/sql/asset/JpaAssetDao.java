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
package org.thingsboard.server.dao.sql.asset;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ProfileEntityIdInfo;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.edqs.fields.AssetFields;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.asset.AssetDao;
import org.thingsboard.server.dao.model.sql.AssetEntity;
import org.thingsboard.server.dao.model.sql.AssetInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.sql.device.NativeAssetRepository;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.DaoUtil.convertTenantEntityInfosToDto;
/**
 * JPA/PostgreSQL implementation of asset dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
@Slf4j
public class JpaAssetDao extends JpaAbstractDao<AssetEntity, Asset> implements AssetDao {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private NativeAssetRepository nativeAssetRepository;

    @Autowired
    private AssetProfileRepository assetProfileRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<AssetEntity> getEntityClass() {
        return AssetEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<AssetEntity, UUID> getRepository() {
        return assetRepository;
    }

    
    /**
     * Finds asset info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link UUID})
     * @return {@link AssetInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetInfo findAssetInfoById(TenantId tenantId, UUID assetId) {
        return DaoUtil.getData(assetRepository.findAssetInfoById(assetId));
    }

    
    /**
     * Finds assets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Asset> findAssetsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds asset infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetInfo> findAssetInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    
    /**
     * Finds assets by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetIds asset ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<Asset>> findAssetsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> assetIds) {
        return service.submit(() ->
                DaoUtil.convertDataList(assetRepository.findByTenantIdAndIdIn(tenantId, assetIds)));
    }

    
    /**
     * Finds assets by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Asset> findAssetsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds asset infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    
    /**
     * Finds assets by tenant id and customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param assetIds asset ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<Asset>> findAssetsByTenantIdAndCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> assetIds) {
        return service.submit(() ->
                DaoUtil.convertDataList(assetRepository.findByTenantIdAndCustomerIdAndIdIn(tenantId, customerId, assetIds)));
    }

    
    /**
     * Finds assets by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return optional {@link Asset}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<Asset> findAssetsByTenantIdAndName(UUID tenantId, String name) {
        Asset asset = DaoUtil.getData(assetRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(asset);
    }

    
    /**
     * Finds assets by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Asset> findAssetsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds asset infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    
    /**
     * Finds asset infos by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndAssetProfileId(UUID tenantId, UUID assetProfileId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndAssetProfileId(
                        tenantId,
                        assetProfileId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    
    /**
     * Finds asset ids by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetId> findAssetIdsByTenantIdAndAssetProfileId(UUID tenantId, UUID assetProfileId, PageLink pageLink) {
        return DaoUtil.pageToPageData(assetRepository.findAssetIdsByTenantIdAndAssetProfileId(
                        tenantId,
                        assetProfileId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)))
                .mapData(AssetId::new);
    }

    
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


    @Override
    public PageData<Asset> findAssetsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
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


    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    
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


    @Override
    public PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndAssetProfileId(UUID tenantId, UUID customerId, UUID assetProfileId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findAssetInfosByTenantIdAndCustomerIdAndAssetProfileId(
                        tenantId,
                        customerId,
                        assetProfileId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, AssetInfoEntity.assetInfoColumnMap)));
    }

    
    /**
     * Finds tenant asset types async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantAssetTypesAsync(UUID tenantId) {
        return service.submit(() -> convertTenantEntityInfosToDto(tenantId, EntityType.ASSET, assetProfileRepository.findActiveTenantAssetProfileNames(tenantId)));
    }

    
    /**
     * Counts assets by asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Long countAssetsByAssetProfileId(TenantId tenantId, UUID assetProfileId) {
        return assetRepository.countByAssetProfileId(assetProfileId);
    }

    
    /**
     * Finds assets by tenant id and profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileId profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Asset> findAssetsByTenantIdAndProfileId(UUID tenantId, UUID profileId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetRepository.findByTenantIdAndProfileId(
                        tenantId,
                        profileId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds assets by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Asset> findAssetsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink) {
        log.debug("Try to find assets by tenantId [{}], edgeId [{}] and pageLink [{}]", tenantId, edgeId, pageLink);
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
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


    @Override
    public PageData<Asset> findAssetsByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink) {
        log.debug("Try to find assets by tenantId [{}], edgeId [{}], type [{}] and pageLink [{}]", tenantId, edgeId, type, pageLink);
        return DaoUtil.toPageData(assetRepository
                .findByTenantIdAndEdgeIdAndType(
                        tenantId,
                        edgeId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Returns all asset types.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    public PageData<TbPair<UUID, String>> getAllAssetTypes(PageLink pageLink) {
        log.debug("Try to find all asset types and pageLink [{}]", pageLink);
        return DaoUtil.pageToPageData(assetRepository.getAllAssetTypes(
                DaoUtil.toPageable(pageLink, Arrays.asList(new SortOrder("tenantId"), new SortOrder("type")))));
    }

    
    /**
     * Finds profile entity id infos.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<ProfileEntityIdInfo> findProfileEntityIdInfos(PageLink pageLink) {
        log.debug("Find profile asset id infos by pageLink [{}]", pageLink);
        return nativeAssetRepository.findProfileEntityIdInfos(DaoUtil.toPageable(pageLink));
    }

    
    /**
     * Finds profile entity id infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<ProfileEntityIdInfo> findProfileEntityIdInfosByTenantId(UUID tenantId, PageLink pageLink) {
        log.debug("Find profile asset id infos by pageLink [{}]", pageLink);
        return nativeAssetRepository.findProfileEntityIdInfosByTenantId(tenantId, DaoUtil.toPageable(pageLink));
    }

    
    /**
     * Finds entity infos by name prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityInfo> findEntityInfosByNamePrefix(TenantId tenantId, String name) {
        log.debug("Find asset entity infos by name [{}]", name);
        return assetRepository.findEntityInfosByNamePrefix(tenantId.getId(), name);
    }

    
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Long countByTenantId(TenantId tenantId) {
        return assetRepository.countByTenantId(tenantId.getId());
    }

    
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link Asset}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Asset findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(assetRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }

    
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link Asset}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Asset findByTenantIdAndName(UUID tenantId, String name) {
        return findAssetsByTenantIdAndName(tenantId, name).orElse(null);
    }

    
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Asset> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findAssetsByTenantId(tenantId, pageLink);
    }

    
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link AssetId})
     * @return {@link AssetId}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetId getExternalIdByInternal(AssetId internalId) {
        return Optional.ofNullable(assetRepository.getExternalIdById(internalId.getId()))
                .map(AssetId::new).orElse(null);
    }

    
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Asset> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
    }

    
    /**
     * Finds next batch.
     *
     * @param uuid uuid ({@link UUID})
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<AssetFields> findNextBatch(UUID uuid, int batchSize) {
        return assetRepository.findAllFields(uuid, Limit.of(batchSize));
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.ASSET;
    }

}
