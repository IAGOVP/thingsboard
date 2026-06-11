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
package org.thingsboard.server.dao.ota;

import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;


/**

 * Persistence contract for ota package info.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (OTA firmware/software package metadata and data cache).

 */


public interface OtaPackageInfoDao extends Dao<OtaPackageInfo> {
    /**
     * Finds ota package info by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<OtaPackageInfo> findOtaPackageInfoByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Finds ota package info by tenant id and device profile id and type and has data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @param otaPackageType ota package type ({@link OtaPackageType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<OtaPackageInfo> findOtaPackageInfoByTenantIdAndDeviceProfileIdAndTypeAndHasData(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType, PageLink pageLink);
    /**
     * Is ota package used.
     *
     * @param otaPackageId ota package id ({@link OtaPackageId})
     * @param otaPackageType ota package type ({@link OtaPackageType})
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean isOtaPackageUsed(OtaPackageId otaPackageId, OtaPackageType otaPackageType, DeviceProfileId deviceProfileId);

}
