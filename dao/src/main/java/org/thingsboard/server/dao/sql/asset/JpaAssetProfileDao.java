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
 * JPA implementation of asset profile dao.
 */

@Component
public class JpaAssetProfileDao extends JpaAbstractDao<AssetProfileEntity, AssetProfile> implements AssetProfileDao, TenantEntityDao<AssetProfile> {

    @Autowired
    private AssetProfileRepository assetProfileRepository;

    /**

     * Get entity class.

     */

    @Override
    protected Class<AssetProfileEntity> getEntityClass() {
        return AssetProfileEntity.class;
    }

    /**

     * Get repository.

     */

    @Override
    protected JpaRepository<AssetProfileEntity, UUID> getRepository() {
        return assetProfileRepository;
    }

    /**

     * Loads asset profile info by id.

     */

    @Override
    public AssetProfileInfo findAssetProfileInfoById(TenantId tenantId, UUID assetProfileId) {
        return assetProfileRepository.findAssetProfileInfoById(assetProfileId);
    }

    /**

     * Loads asset profiles.

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

     * Loads asset profile infos.

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

     * Loads default asset profile.

     */

    @Override
    public AssetProfile findDefaultAssetProfile(TenantId tenantId) {
        return DaoUtil.getData(assetProfileRepository.findByDefaultTrueAndTenantId(tenantId.getId()));
    }

    /**

     * Loads default asset profile info.

     */

    @Override
    public AssetProfileInfo findDefaultAssetProfileInfo(TenantId tenantId) {
        return assetProfileRepository.findDefaultAssetProfileInfo(tenantId.getId());
    }

    /**

     * Loads by name.

     */

    @Override
    public AssetProfile findByName(TenantId tenantId, String profileName) {
        return DaoUtil.getData(assetProfileRepository.findByTenantIdAndName(tenantId.getId(), profileName));
    }

    /**

     * Loads all with images.

     */

    @Override
    public PageData<AssetProfile> findAllWithImages(PageLink pageLink) {
        return DaoUtil.toPageData(assetProfileRepository.findAllByImageNotNull(DaoUtil.toPageable(pageLink)));
    }

    /**

     * Loads tenant asset profile names.

     */

    @Override
    public List<EntityInfo> findTenantAssetProfileNames(UUID tenantId, boolean activeOnly) {
        return activeOnly ?
                assetProfileRepository.findActiveTenantAssetProfileNames(tenantId) :
                assetProfileRepository.findAllTenantAssetProfileNames(tenantId);
    }

    /**

     * Loads asset profiles by tenant id and ids.

     */

    @Override
    public List<AssetProfileInfo> findAssetProfilesByTenantIdAndIds(UUID tenantId, List<UUID> assetProfileIds) {
        return assetProfileRepository.findAssetProfileInfosByTenantIdAndIdIn(tenantId, assetProfileIds);
    }

    /**

     * Loads by tenant id and external id.

     */

    @Override
    public AssetProfile findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(assetProfileRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }

    /**

     * Loads by tenant id and name.

     */

    @Override
    public AssetProfile findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(assetProfileRepository.findByTenantIdAndName(tenantId, name));
    }

    /**

     * Loads by tenant id.

     */

    @Override
    public PageData<AssetProfile> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findAssetProfiles(TenantId.fromUUID(tenantId), pageLink);
    }

    /**

     * Get external id by internal.

     */

    @Override
    public AssetProfileId getExternalIdByInternal(AssetProfileId internalId) {
        return Optional.ofNullable(assetProfileRepository.getExternalIdById(internalId.getId()))
                .map(AssetProfileId::new).orElse(null);
    }

    /**

     * Loads default entity by tenant id.

     */

    @Override
    public AssetProfile findDefaultEntityByTenantId(UUID tenantId) {
        return findDefaultAssetProfile(TenantId.fromUUID(tenantId));
    }

    /**

     * Loads by tenant and image link.

     */

    @Override
    public List<AssetProfileInfo> findByTenantAndImageLink(TenantId tenantId, String imageLink, int limit) {
        return assetProfileRepository.findByTenantAndImageLink(tenantId.getId(), imageLink, PageRequest.of(0, limit));
    }

    /**

     * Loads by image link.

     */

    @Override
    public List<AssetProfileInfo> findByImageLink(String imageLink, int limit) {
        return assetProfileRepository.findByImageLink(imageLink, PageRequest.of(0, limit));
    }

    /**

     * Loads all by tenant id.

     */

    @Override
    public PageData<AssetProfile> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
    }

    /**

     * Loads next batch.

     */

    @Override
    public List<AssetProfileFields> findNextBatch(UUID id, int batchSize) {
        return assetProfileRepository.findNextBatch(id, Limit.of(batchSize));
    }

    /**

     * Get entity type.

     */

    @Override
    public EntityType getEntityType() {
        return EntityType.ASSET_PROFILE;
    }

}
