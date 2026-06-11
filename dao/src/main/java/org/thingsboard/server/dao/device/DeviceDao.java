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
package org.thingsboard.server.dao.device;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceIdInfo;
import org.thingsboard.server.common.data.DeviceInfo;
import org.thingsboard.server.common.data.DeviceInfoFilter;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.ProfileEntityIdInfo;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for device.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (devices, credentials, profiles, and connectivity).
 */

public interface DeviceDao extends Dao<Device>, TenantEntityDao<Device>, ExportableEntityDao<DeviceId, Device> {

    
    /**
     * Finds device info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link DeviceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceInfo findDeviceInfoById(TenantId tenantId, UUID deviceId);

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param device device ({@link Device})
     * @return {@link Device}
     * @throws Exception if an unexpected error occurs during processing
     */

    Device save(TenantId tenantId, Device device);

    
    /**
     * Saves or persists and flush.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param device device ({@link Device})
     * @return {@link Device}
     * @throws Exception if an unexpected error occurs during processing
     */

    Device saveAndFlush(TenantId tenantId, Device device);

    
    /**
     * Finds devices by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds devices by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    
    /**
     * Finds device ids by tenant id and device profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DeviceId> findDeviceIdsByTenantIdAndDeviceProfileId(UUID tenantId, UUID deviceProfileId, PageLink pageLink);
    /**
     * Finds devices by tenant id and type and empty ota package.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @param type type ({@link OtaPackageType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantIdAndTypeAndEmptyOtaPackage(UUID tenantId,
                                                                    UUID deviceProfileId,
                                                                    OtaPackageType type,
                                                                    PageLink pageLink);
    /**
     * Counts devices by tenant id and device profile id and empty ota package.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @param otaPackageType ota package type ({@link OtaPackageType})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(UUID tenantId, UUID deviceProfileId, OtaPackageType otaPackageType);

    
    /**
     * Finds devices by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceIds device ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> deviceIds);

    
    /**
     * Finds devices by ids.
     *
     * @param deviceIds device ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<Device> findDevicesByIds(List<UUID> deviceIds);

    
    /**
     * Finds devices by ids async.
     *
     * @param deviceIds device ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<Device>> findDevicesByIdsAsync(List<UUID> deviceIds);

    
    /**
     * Finds devices by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    
    /**
     * Finds devices by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    
    /**
     * Finds devices by tenant id customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param deviceIds device ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> deviceIds);

    
    /**
     * Finds device by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return optional {@link Device}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<Device> findDeviceByTenantIdAndName(UUID tenantId, String name);

    
    /**
     * Finds tenant device types async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated(since = "3.6.2", forRemoval = true)
    ListenableFuture<List<EntitySubtype>> findTenantDeviceTypesAsync(UUID tenantId);

    
    /**
     * Finds device by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link Device}
     * @throws Exception if an unexpected error occurs during processing
     */

    Device findDeviceByTenantIdAndId(TenantId tenantId, UUID id);

    
    /**
     * Finds device by tenant id and id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return future completing with {@link Device}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Device> findDeviceByTenantIdAndIdAsync(TenantId tenantId, UUID id);
    /**
     * Counts devices by device profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long countDevicesByDeviceProfileId(TenantId tenantId, UUID deviceProfileId);

    
    /**
     * Finds devices by tenant id and profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileId profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantIdAndProfileId(UUID tenantId, UUID profileId, PageLink pageLink);
    /**
     * Finds devices ids by device profile transport type.
     *
     * @param transportType transport type ({@link DeviceTransportType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<UUID> findDevicesIdsByDeviceProfileTransportType(DeviceTransportType transportType, PageLink pageLink);

    
    /**
     * Finds devices by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink);

    
    /**
     * Finds devices by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Device> findDevicesByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink);
    /**
     * Finds device id infos.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DeviceIdInfo> findDeviceIdInfos(PageLink pageLink);
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
    /**
     * Finds device infos by filter.
     *
     * @param filter filter ({@link DeviceInfoFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DeviceInfo> findDeviceInfosByFilter(DeviceInfoFilter filter, PageLink pageLink);

}
