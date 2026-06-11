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
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.ProfileEntityIdInfo;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.dao.device.provision.ProvisionRequest;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.UUID;

/**
 * Persistence API for {@link Device} entities (credentials via {@link DeviceCredentialsService}).
 *
 * <p>Implementation: {@code dao} module. REST: {@code DeviceController} ({@code /api/device*}).
 */
public interface DeviceService extends EntityDaoService {

    /**
     * Finds device info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link DeviceInfo}
     */
    DeviceInfo findDeviceInfoById(TenantId tenantId, DeviceId deviceId);

    /**
     * Finds device by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link Device}
     */
    Device findDeviceById(TenantId tenantId, DeviceId deviceId);

    /**
     * Finds device by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return future completing with {@link Device}
     */
    ListenableFuture<Device> findDeviceByIdAsync(TenantId tenantId, DeviceId deviceId);

    /**
     * Finds device by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link Device}
     */
    Device findDeviceByTenantIdAndName(TenantId tenantId, String name);

    /**
     * Finds device by tenant id and name async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return future completing with {@link Device}
     */
    ListenableFuture<Device> findDeviceByTenantIdAndNameAsync(TenantId tenantId, String name);

    /**
     * Saves or persists device.
     *
     * @param device device ({@link Device})
     * @return {@link Device}
     */
    Device saveDevice(Device device);

    /**
     * Saves or persists device.
     *
     * @param device device ({@link Device})
     * @param doValidate whether to run validation before persist
     * @return {@link Device}
     */
    Device saveDevice(Device device, boolean doValidate);

    /**
     * Saves or persists device with access token.
     *
     * @param device device ({@link Device})
     * @param accessToken device access token for MQTT/HTTP connectivity
     * @return {@link Device}
     */
    Device saveDeviceWithAccessToken(Device device, String accessToken);

    /**
     * Saves or persists device with access token.
     *
     * @param device device ({@link Device})
     * @param accessToken device access token for MQTT/HTTP connectivity
     * @param nameConflictStrategy behavior when an entity with the same name already exists
     * @return {@link Device}
     */
    Device saveDeviceWithAccessToken(Device device, String accessToken, NameConflictStrategy nameConflictStrategy);

    /**
     * Saves a device with credentials the requested data.
     *
     * @param device device ({@link Device})
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     * @return {@link Device}
     */
    Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials);

    /**
     * Saves a device with credentials the requested data.
     *
     * @param device device ({@link Device})
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     * @param nameConflictStrategy behavior when an entity with the same name already exists
     * @return {@link Device}
     */
    Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials, NameConflictStrategy nameConflictStrategy);

    /**
     * Saves or persists device.
     *
     * @param provisionRequest provision request ({@link ProvisionRequest})
     * @param profile profile ({@link DeviceProfile})
     * @return {@link Device}
     */
    Device saveDevice(ProvisionRequest provisionRequest, DeviceProfile profile);

    /**
     * Assigns device to customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param customerId customer to assign or filter by
     * @return {@link Device}
     */
    Device assignDeviceToCustomer(TenantId tenantId, DeviceId deviceId, CustomerId customerId);

    /**
     * Unassigns device from customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link Device}
     */
    Device unassignDeviceFromCustomer(TenantId tenantId, DeviceId deviceId);

    /**
     * Deletes device.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     */
    void deleteDevice(TenantId tenantId, DeviceId deviceId);

    /**
     * Finds devices by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Device> findDevicesByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds device infos by filter.
     *
     * @param filter filter ({@link DeviceInfoFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DeviceInfo> findDeviceInfosByFilter(DeviceInfoFilter filter, PageLink pageLink);

    /**
     * Finds device id infos.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DeviceIdInfo> findDeviceIdInfos(PageLink pageLink);

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
     * Finds devices by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Device> findDevicesByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    /**
     * Finds device ids by tenant id and device profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DeviceId> findDeviceIdsByTenantIdAndDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId, PageLink pageLink);

    /**
     * Finds devices by tenant id and type and empty ota package.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @param type type ({@link OtaPackageType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Device> findDevicesByTenantIdAndTypeAndEmptyOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType type, PageLink pageLink);

    /**
     * Counts devices by tenant id and device profile id and empty ota package.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @param otaPackageType ota package type ({@link OtaPackageType})
     * @return the long result
     */
    long countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType);

    /**
     * Finds devices by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceIds device ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(TenantId tenantId, List<DeviceId> deviceIds);

    /**
     * Finds devices by ids.
     *
     * @param deviceIds device ids ({@link List})
     * @return {@link List}
     */
    List<Device> findDevicesByIds(List<DeviceId> deviceIds);

    /**
     * Finds devices by ids async.
     *
     * @param deviceIds device ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Device>> findDevicesByIdsAsync(List<DeviceId> deviceIds);

    /**
     * Deletes devices by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteDevicesByTenantId(TenantId tenantId);

    /**
     * Finds devices by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Device> findDevicesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds devices by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Device> findDevicesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    /**
     * Finds devices by tenant id customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param deviceIds device ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<DeviceId> deviceIds);

    /**
     * Unassigns customer devices.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void unassignCustomerDevices(TenantId tenantId, CustomerId customerId);

    /**
     * Finds devices by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link DeviceSearchQuery})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Device>> findDevicesByQuery(TenantId tenantId, DeviceSearchQuery query);

    @Deprecated(since = "3.6.2", forRemoval = true)
    /**
     * Finds device types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntitySubtype>> findDeviceTypesByTenantId(TenantId tenantId);

    /**
     * Assigns device to tenant.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param device device ({@link Device})
     * @return {@link Device}
     */
    Device assignDeviceToTenant(TenantId tenantId, Device device);

    /**
     * Finds devices ids by device profile transport type.
     *
     * @param transportType transport type ({@link DeviceTransportType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<UUID> findDevicesIdsByDeviceProfileTransportType(DeviceTransportType transportType, PageLink pageLink);

    /**
     * Assigns device to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Device}
     */
    Device assignDeviceToEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId);

    /**
     * Unassigns device from edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Device}
     */
    Device unassignDeviceFromEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId);

    /**
     * Finds devices by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Device> findDevicesByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    /**
     * Finds devices by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Device> findDevicesByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);

}
