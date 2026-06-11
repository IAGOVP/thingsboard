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

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.nio.ByteBuffer;

/**
 * Service API for ota package persistence and domain operations.
 */
public interface OtaPackageService extends EntityDaoService {

    /**
     * Saves or persists ota package info.
     *
     * @param otaPackageInfo ota package info ({@link OtaPackageInfo})
     * @param isUrl is url
     * @return {@link OtaPackageInfo}
     */
    OtaPackageInfo saveOtaPackageInfo(OtaPackageInfo otaPackageInfo, boolean isUrl);

    /**
     * Saves or persists ota package.
     *
     * @param otaPackage ota package ({@link OtaPackage})
     * @return {@link OtaPackage}
     */
    OtaPackage saveOtaPackage(OtaPackage otaPackage);

    /**
     * Generate checksum.
     *
     * @param checksumAlgorithm checksum algorithm ({@link ChecksumAlgorithm})
     * @param data data ({@link ByteBuffer})
     * @return {@link String}
     */
    String generateChecksum(ChecksumAlgorithm checksumAlgorithm, ByteBuffer data);

    /**
     * Finds ota package by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param otaPackageId ota package id ({@link OtaPackageId})
     * @return {@link OtaPackage}
     */
    OtaPackage findOtaPackageById(TenantId tenantId, OtaPackageId otaPackageId);

    /**
     * Finds ota package info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param otaPackageId ota package id ({@link OtaPackageId})
     * @return {@link OtaPackageInfo}
     */
    OtaPackageInfo findOtaPackageInfoById(TenantId tenantId, OtaPackageId otaPackageId);

    /**
     * Finds ota package by tenant id and title and version.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @param version version ({@link String})
     * @return {@link OtaPackage}
     */
    OtaPackage findOtaPackageByTenantIdAndTitleAndVersion(TenantId tenantId, String title, String version);

    /**
     * Finds ota package info by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param otaPackageId ota package id ({@link OtaPackageId})
     * @return future completing with {@link OtaPackageInfo}
     */
    ListenableFuture<OtaPackageInfo> findOtaPackageInfoByIdAsync(TenantId tenantId, OtaPackageId otaPackageId);

    /**
     * Finds tenant ota packages by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<OtaPackageInfo> findTenantOtaPackagesByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds tenant ota packages by tenant id and device profile id and type and has data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @param otaPackageType ota package type ({@link OtaPackageType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<OtaPackageInfo> findTenantOtaPackagesByTenantIdAndDeviceProfileIdAndTypeAndHasData(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType, PageLink pageLink);

    /**
     * Deletes ota package.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param otaPackageId ota package id ({@link OtaPackageId})
     */
    void deleteOtaPackage(TenantId tenantId, OtaPackageId otaPackageId);

    /**
     * Deletes ota packages by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteOtaPackagesByTenantId(TenantId tenantId);

    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the long result
     */
    long sumDataSizeByTenantId(TenantId tenantId);

}
