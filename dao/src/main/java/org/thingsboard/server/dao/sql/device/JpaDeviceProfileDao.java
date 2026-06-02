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
 * JPA implementation of device profile dao.
 */

@Component
@SqlDao
public class JpaDeviceProfileDao extends JpaAbstractDao<DeviceProfileEntity, DeviceProfile> implements DeviceProfileDao, TenantEntityDao<DeviceProfile> {

    @Autowired
    private DeviceProfileRepository deviceProfileRepository;

    /**

     * Get entity class.

     */

    @Override
    protected Class<DeviceProfileEntity> getEntityClass() {
        return DeviceProfileEntity.class;
    }

    /**

     * Get repository.

     */

    @Override
    protected JpaRepository<DeviceProfileEntity, UUID> getRepository() {
        return deviceProfileRepository;
    }

    /**

     * Loads device profile info by id.

     */

    @Override
    public DeviceProfileInfo findDeviceProfileInfoById(TenantId tenantId, UUID deviceProfileId) {
        return deviceProfileRepository.findDeviceProfileInfoById(deviceProfileId);
    }

    /**

     * Loads device profiles.

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

     * Loads device profile infos.

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

     * Loads default device profile.

     */

    @Override
    public DeviceProfile findDefaultDeviceProfile(TenantId tenantId) {
        return DaoUtil.getData(deviceProfileRepository.findByDefaultTrueAndTenantId(tenantId.getId()));
    }

    /**

     * Loads default device profile info.

     */

    @Override
    public DeviceProfileInfo findDefaultDeviceProfileInfo(TenantId tenantId) {
        return deviceProfileRepository.findDefaultDeviceProfileInfo(tenantId.getId());
    }

    /**

     * Loads by provision device key.

     */

    @Override
    public DeviceProfile findByProvisionDeviceKey(String provisionDeviceKey) {
        return DaoUtil.getData(deviceProfileRepository.findByProvisionDeviceKey(provisionDeviceKey));
    }

    /**

     * Loads by name.

     */

    @Override
    public DeviceProfile findByName(TenantId tenantId, String profileName) {
        return DaoUtil.getData(deviceProfileRepository.findByTenantIdAndName(tenantId.getId(), profileName));
    }

    /**

     * Loads all with images.

     */

    @Override
    public PageData<DeviceProfile> findAllWithImages(PageLink pageLink) {
        return DaoUtil.toPageData(deviceProfileRepository.findAllByImageNotNull(DaoUtil.toPageable(pageLink)));
    }

    /**

     * Loads tenant device profile names.

     */

    @Override
    public List<EntityInfo> findTenantDeviceProfileNames(UUID tenantId, boolean activeOnly) {
        return activeOnly ?
                deviceProfileRepository.findActiveTenantDeviceProfileNames(tenantId) :
                deviceProfileRepository.findAllTenantDeviceProfileNames(tenantId);
    }

    /**

     * Loads device profiles by tenant id and ids.

     */

    @Override
    public List<DeviceProfileInfo> findDeviceProfilesByTenantIdAndIds(UUID tenantId, List<UUID> deviceProfileIds) {
        return deviceProfileRepository.findDeviceProfileInfosByTenantIdAndIdIn(tenantId, deviceProfileIds);
    }

    /**

     * Loads by tenant id and external id.

     */

    @Override
    public DeviceProfile findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(deviceProfileRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }

    /**

     * Loads by tenant id and name.

     */

    @Override
    public DeviceProfile findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(deviceProfileRepository.findByTenantIdAndName(tenantId, name));
    }

    /**

     * Loads by tenant id.

     */

    @Override
    public PageData<DeviceProfile> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findDeviceProfiles(TenantId.fromUUID(tenantId), pageLink);
    }

    /**

     * Get external id by internal.

     */

    @Override
    public DeviceProfileId getExternalIdByInternal(DeviceProfileId internalId) {
        return Optional.ofNullable(deviceProfileRepository.getExternalIdById(internalId.getId()))
                .map(DeviceProfileId::new).orElse(null);
    }

    /**

     * Loads default entity by tenant id.

     */

    @Override
    public DeviceProfile findDefaultEntityByTenantId(UUID tenantId) {
        return findDefaultDeviceProfile(TenantId.fromUUID(tenantId));
    }

    /**

     * Loads by tenant and image link.

     */

    @Override
    public List<DeviceProfileInfo> findByTenantAndImageLink(TenantId tenantId, String imageLink, int limit) {
        return deviceProfileRepository.findByTenantAndImageLink(tenantId.getId(), imageLink, PageRequest.of(0, limit));
    }

    /**

     * Loads by image link.

     */

    @Override
    public List<DeviceProfileInfo> findByImageLink(String imageLink, int limit) {
        return deviceProfileRepository.findByImageLink(imageLink, PageRequest.of(0, limit));
    }

    /**

     * Loads all by tenant id.

     */

    @Override
    public PageData<DeviceProfile> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findDeviceProfiles(tenantId, pageLink);
    }

    /**

     * Loads next batch.

     */

    @Override
    public List<DeviceProfileFields> findNextBatch(UUID id, int batchSize) {
        return deviceProfileRepository.findNextBatch(id, Limit.of(batchSize));
    }

    /**

     * Get entity type.

     */

    @Override
    public EntityType getEntityType() {
        return EntityType.DEVICE_PROFILE;
    }

}
