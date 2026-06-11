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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.edqs.fields.AssetProfileFields;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.asset.AssetProfileDao;
import org.thingsboard.server.dao.model.sql.AssetProfileEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of asset profile dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
public class JpaAssetProfileDao extends JpaAbstractDao<AssetProfileEntity, AssetProfile> implements AssetProfileDao, TenantEntityDao<AssetProfile> {

    @Autowired
    private AssetProfileRepository assetProfileRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<AssetProfileEntity> getEntityClass() {
        return AssetProfileEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<AssetProfileEntity, UUID> getRepository() {
        return assetProfileRepository;
    }

    
    /**
     * Finds asset profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @return {@link AssetProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfileInfo findAssetProfileInfoById(TenantId tenantId, UUID assetProfileId) {
        return assetProfileRepository.findAssetProfileInfoById(assetProfileId);
    }

    
    /**
     * Finds asset profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetProfile> findAssetProfiles(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                assetProfileRepository.findAssetProfiles(
                        tenantId.getId(),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds asset profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetProfileInfo> findAssetProfileInfos(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(
                assetProfileRepository.findAssetProfileInfos(
                        tenantId.getId(),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds default asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfile findDefaultAssetProfile(TenantId tenantId) {
        return DaoUtil.getData(assetProfileRepository.findByDefaultTrueAndTenantId(tenantId.getId()));
    }

    
    /**
     * Finds default asset profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfileInfo findDefaultAssetProfileInfo(TenantId tenantId) {
        return assetProfileRepository.findDefaultAssetProfileInfo(tenantId.getId());
    }

    
    /**
     * Finds by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfile findByName(TenantId tenantId, String profileName) {
        return DaoUtil.getData(assetProfileRepository.findByTenantIdAndName(tenantId.getId(), profileName));
    }

    
    /**
     * Finds all with images.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AssetProfile> findAllWithImages(PageLink pageLink) {
        return DaoUtil.toPageData(assetProfileRepository.findAllByImageNotNull(DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds tenant asset profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activeOnly active only
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityInfo> findTenantAssetProfileNames(UUID tenantId, boolean activeOnly) {
        return activeOnly ?
                assetProfileRepository.findActiveTenantAssetProfileNames(tenantId) :
                assetProfileRepository.findAllTenantAssetProfileNames(tenantId);
    }

    
    /**
     * Finds asset profiles by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileIds asset profile ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<AssetProfileInfo> findAssetProfilesByTenantIdAndIds(UUID tenantId, List<UUID> assetProfileIds) {
        return assetProfileRepository.findAssetProfileInfosByTenantIdAndIdIn(tenantId, assetProfileIds);
    }

    
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfile findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(assetProfileRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }

    
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfile findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(assetProfileRepository.findByTenantIdAndName(tenantId, name));
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
    public PageData<AssetProfile> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findAssetProfiles(TenantId.fromUUID(tenantId), pageLink);
    }

    
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link AssetProfileId})
     * @return {@link AssetProfileId}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfileId getExternalIdByInternal(AssetProfileId internalId) {
        return Optional.ofNullable(assetProfileRepository.getExternalIdById(internalId.getId()))
                .map(AssetProfileId::new).orElse(null);
    }

    
    /**
     * Finds default entity by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AssetProfile findDefaultEntityByTenantId(UUID tenantId) {
        return findDefaultAssetProfile(TenantId.fromUUID(tenantId));
    }

    
    /**
     * Finds by tenant and image link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageLink image link ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<AssetProfileInfo> findByTenantAndImageLink(TenantId tenantId, String imageLink, int limit) {
        return assetProfileRepository.findByTenantAndImageLink(tenantId.getId(), imageLink, PageRequest.of(0, limit));
    }

    
    /**
     * Finds by image link.
     *
     * @param imageLink image link ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<AssetProfileInfo> findByImageLink(String imageLink, int limit) {
        return assetProfileRepository.findByImageLink(imageLink, PageRequest.of(0, limit));
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
    public PageData<AssetProfile> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
    }

    
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<AssetProfileFields> findNextBatch(UUID id, int batchSize) {
        return assetProfileRepository.findNextBatch(id, Limit.of(batchSize));
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.ASSET_PROFILE;
    }

}
