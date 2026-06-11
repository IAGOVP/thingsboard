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
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.ImageContainerDao;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for device profile.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (devices, credentials, profiles, and connectivity).

 */


public interface DeviceProfileDao extends Dao<DeviceProfile>, ExportableEntityDao<DeviceProfileId, DeviceProfile>, ImageContainerDao<DeviceProfileInfo> {
    /**
     * Finds device profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link DeviceProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfileInfo findDeviceProfileInfoById(TenantId tenantId, UUID deviceProfileId);
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfile save(TenantId tenantId, DeviceProfile deviceProfile);
    /**
     * Saves or persists and flush.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfile saveAndFlush(TenantId tenantId, DeviceProfile deviceProfile);
    /**
     * Finds device profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DeviceProfile> findDeviceProfiles(TenantId tenantId, PageLink pageLink);
    /**
     * Finds device profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @param transportType transport type ({@link String})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DeviceProfileInfo> findDeviceProfileInfos(TenantId tenantId, PageLink pageLink, String transportType);
    /**
     * Finds default device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfile findDefaultDeviceProfile(TenantId tenantId);
    /**
     * Finds default device profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfileInfo findDefaultDeviceProfileInfo(TenantId tenantId);
    /**
     * Finds by provision device key.
     *
     * @param provisionDeviceKey provision device key ({@link String})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfile findByProvisionDeviceKey(String provisionDeviceKey);
    /**
     * Finds by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfile findByName(TenantId tenantId, String profileName);
    /**
     * Finds all with images.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DeviceProfile> findAllWithImages(PageLink pageLink);
    /**
     * Finds tenant device profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activeOnly active only
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityInfo> findTenantDeviceProfileNames(UUID tenantId, boolean activeOnly);
    /**
     * Finds device profiles by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileIds device profile ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<DeviceProfileInfo> findDeviceProfilesByTenantIdAndIds(UUID tenantId, List<UUID> deviceProfileIds);

}
