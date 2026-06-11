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
package org.thingsboard.server.dao.sql.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileInfo;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.edqs.fields.DeviceProfileFields;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.device.DeviceProfileDao;
import org.thingsboard.server.dao.model.sql.DeviceProfileEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of device profile dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
public class JpaDeviceProfileDao extends JpaAbstractDao<DeviceProfileEntity, DeviceProfile> implements DeviceProfileDao, TenantEntityDao<DeviceProfile> {

    @Autowired
    private DeviceProfileRepository deviceProfileRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<DeviceProfileEntity> getEntityClass() {
        return DeviceProfileEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<DeviceProfileEntity, UUID> getRepository() {
        return deviceProfileRepository;
    }

    
    /**
     * Finds device profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link DeviceProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfileInfo findDeviceProfileInfoById(TenantId tenantId, UUID deviceProfileId) {
        return deviceProfileRepository.findDeviceProfileInfoById(deviceProfileId);
    }

    
    /**
     * Finds device profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DeviceProfile> findDeviceProfiles(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceProfileRepository.findDeviceProfiles(
                        tenantId.getId(),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds device profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @param transportType transport type ({@link String})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DeviceProfileInfo> findDeviceProfileInfos(TenantId tenantId, PageLink pageLink, String transportType) {
        if (StringUtils.isNotEmpty(transportType)) {
            return DaoUtil.pageToPageData(
                    deviceProfileRepository.findDeviceProfileInfos(
                            tenantId.getId(),
                            pageLink.getTextSearch(),
                            DeviceTransportType.valueOf(transportType),
                            DaoUtil.toPageable(pageLink)));
        } else {
            return DaoUtil.pageToPageData(
                    deviceProfileRepository.findDeviceProfileInfos(
                            tenantId.getId(),
                            pageLink.getTextSearch(),
                            DaoUtil.toPageable(pageLink)));
        }
    }

    
    /**
     * Finds default device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfile findDefaultDeviceProfile(TenantId tenantId) {
        return DaoUtil.getData(deviceProfileRepository.findByDefaultTrueAndTenantId(tenantId.getId()));
    }

    
    /**
     * Finds default device profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfileInfo findDefaultDeviceProfileInfo(TenantId tenantId) {
        return deviceProfileRepository.findDefaultDeviceProfileInfo(tenantId.getId());
    }

    
    /**
     * Finds by provision device key.
     *
     * @param provisionDeviceKey provision device key ({@link String})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfile findByProvisionDeviceKey(String provisionDeviceKey) {
        return DaoUtil.getData(deviceProfileRepository.findByProvisionDeviceKey(provisionDeviceKey));
    }

    
    /**
     * Finds by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfile findByName(TenantId tenantId, String profileName) {
        return DaoUtil.getData(deviceProfileRepository.findByTenantIdAndName(tenantId.getId(), profileName));
    }

    
    /**
     * Finds all with images.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DeviceProfile> findAllWithImages(PageLink pageLink) {
        return DaoUtil.toPageData(deviceProfileRepository.findAllByImageNotNull(DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds tenant device profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activeOnly active only
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityInfo> findTenantDeviceProfileNames(UUID tenantId, boolean activeOnly) {
        return activeOnly ?
                deviceProfileRepository.findActiveTenantDeviceProfileNames(tenantId) :
                deviceProfileRepository.findAllTenantDeviceProfileNames(tenantId);
    }

    
    /**
     * Finds device profiles by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileIds device profile ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<DeviceProfileInfo> findDeviceProfilesByTenantIdAndIds(UUID tenantId, List<UUID> deviceProfileIds) {
        return deviceProfileRepository.findDeviceProfileInfosByTenantIdAndIdIn(tenantId, deviceProfileIds);
    }

    
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfile findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(deviceProfileRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }

    
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfile findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(deviceProfileRepository.findByTenantIdAndName(tenantId, name));
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
    public PageData<DeviceProfile> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findDeviceProfiles(TenantId.fromUUID(tenantId), pageLink);
    }

    
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link DeviceProfileId})
     * @return {@link DeviceProfileId}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfileId getExternalIdByInternal(DeviceProfileId internalId) {
        return Optional.ofNullable(deviceProfileRepository.getExternalIdById(internalId.getId()))
                .map(DeviceProfileId::new).orElse(null);
    }

    
    /**
     * Finds default entity by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceProfile findDefaultEntityByTenantId(UUID tenantId) {
        return findDefaultDeviceProfile(TenantId.fromUUID(tenantId));
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
    public List<DeviceProfileInfo> findByTenantAndImageLink(TenantId tenantId, String imageLink, int limit) {
        return deviceProfileRepository.findByTenantAndImageLink(tenantId.getId(), imageLink, PageRequest.of(0, limit));
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
    public List<DeviceProfileInfo> findByImageLink(String imageLink, int limit) {
        return deviceProfileRepository.findByImageLink(imageLink, PageRequest.of(0, limit));
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
    public PageData<DeviceProfile> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findDeviceProfiles(tenantId, pageLink);
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
    public List<DeviceProfileFields> findNextBatch(UUID id, int batchSize) {
        return deviceProfileRepository.findNextBatch(id, Limit.of(batchSize));
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.DEVICE_PROFILE;
    }

}
