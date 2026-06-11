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

import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileInfo;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for device profile persistence and domain operations.
 */
public interface DeviceProfileService extends EntityDaoService {

    /**
     * Finds device profile by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return {@link DeviceProfile}
     */
    DeviceProfile findDeviceProfileById(TenantId tenantId, DeviceProfileId deviceProfileId);

    /**
     * Finds device profile by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @param putInCache put in cache
     * @return {@link DeviceProfile}
     */
    DeviceProfile findDeviceProfileById(TenantId tenantId, DeviceProfileId deviceProfileId, boolean putInCache);

    /**
     * Finds device profile by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link DeviceProfile}
     */
    DeviceProfile findDeviceProfileByName(TenantId tenantId, String profileName);

    /**
     * Finds device profile by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @param putInCache put in cache
     * @return {@link DeviceProfile}
     */
    DeviceProfile findDeviceProfileByName(TenantId tenantId, String profileName, boolean putInCache);

    /**
     * Finds device profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return {@link DeviceProfileInfo}
     */
    DeviceProfileInfo findDeviceProfileInfoById(TenantId tenantId, DeviceProfileId deviceProfileId);

    /**
     * Saves or persists device profile.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return {@link DeviceProfile}
     */
    DeviceProfile saveDeviceProfile(DeviceProfile deviceProfile);

    /**
     * Saves or persists device profile.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @param doValidate whether to run validation before persist
     * @param publishSaveEvent publish save event
     * @return {@link DeviceProfile}
     */
    DeviceProfile saveDeviceProfile(DeviceProfile deviceProfile, boolean doValidate, boolean publishSaveEvent);

    /**
     * Deletes device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     */
    void deleteDeviceProfile(TenantId tenantId, DeviceProfileId deviceProfileId);

    /**
     * Finds device profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DeviceProfile> findDeviceProfiles(TenantId tenantId, PageLink pageLink);

    /**
     * Finds device profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @param transportType transport type ({@link String})
     * @return {@link PageData}
     */
    PageData<DeviceProfileInfo> findDeviceProfileInfos(TenantId tenantId, PageLink pageLink, String transportType);

    /**
     * Finds device profile by provision device key.
     *
     * @param provisionDeviceKey provision device key ({@link String})
     * @return {@link DeviceProfile}
     */
    DeviceProfile findDeviceProfileByProvisionDeviceKey(String provisionDeviceKey);

    /**
     * Finds or create device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link DeviceProfile}
     */
    DeviceProfile findOrCreateDeviceProfile(TenantId tenantId, String profileName);

    /**
     * Creates default device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfile}
     */
    DeviceProfile createDefaultDeviceProfile(TenantId tenantId);

    /**
     * Finds default device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfile}
     */
    DeviceProfile findDefaultDeviceProfile(TenantId tenantId);

    /**
     * Finds default device profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfileInfo}
     */
    DeviceProfileInfo findDefaultDeviceProfileInfo(TenantId tenantId);

    /**
     * Set default device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return the boolean result
     */
    boolean setDefaultDeviceProfile(TenantId tenantId, DeviceProfileId deviceProfileId);

    /**
     * Deletes device profiles by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteDeviceProfilesByTenantId(TenantId tenantId);

    /**
     * Finds device profile names by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activeOnly active only
     * @return {@link List}
     */
    List<EntityInfo> findDeviceProfileNamesByTenantId(TenantId tenantId, boolean activeOnly);

    /**
     * Finds device profiles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileIds device profile ids ({@link List})
     * @return {@link List}
     */
    List<DeviceProfileInfo> findDeviceProfilesByIds(TenantId tenantId, List<DeviceProfileId> deviceProfileIds);

}
