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
package org.thingsboard.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.OtaPackageExportData;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports ota package entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class OtaPackageImportService extends BaseEntityImportService<OtaPackageId, OtaPackage, OtaPackageExportData> {

    private final OtaPackageService otaPackageService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param otaPackage ota package ({@link OtaPackage})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, OtaPackage otaPackage, IdProvider idProvider) {
        otaPackage.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param otaPackage ota package ({@link OtaPackage})
     * @param oldOtaPackage old ota package ({@link OtaPackage})
     * @param exportData export data ({@link OtaPackageExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link OtaPackage}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected OtaPackage prepare(EntitiesImportCtx ctx, OtaPackage otaPackage, OtaPackage oldOtaPackage, OtaPackageExportData exportData, IdProvider idProvider) {
        otaPackage.setDeviceProfileId(idProvider.getInternalId(otaPackage.getDeviceProfileId()));
        return otaPackage;
    }
    /**
     * Finds existing entity.
     *
     * @param ctx calculated-field execution context
     * @param otaPackage ota package ({@link OtaPackage})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link OtaPackage}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected OtaPackage findExistingEntity(EntitiesImportCtx ctx, OtaPackage otaPackage, IdProvider idProvider) {
        OtaPackage existingOtaPackage = super.findExistingEntity(ctx, otaPackage, idProvider);
        if (existingOtaPackage == null && ctx.isFindExistingByName()) {
            existingOtaPackage = otaPackageService.findOtaPackageByTenantIdAndTitleAndVersion(ctx.getTenantId(), otaPackage.getTitle(), otaPackage.getVersion());
        }
        return existingOtaPackage;
    }
    /**
     * Deep copy.
     *
     * @param otaPackage ota package ({@link OtaPackage})
     * @return {@link OtaPackage}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected OtaPackage deepCopy(OtaPackage otaPackage) {
        return new OtaPackage(otaPackage);
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param otaPackage ota package ({@link OtaPackage})
     * @param exportData export data ({@link OtaPackageExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link OtaPackage}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected OtaPackage saveOrUpdate(EntitiesImportCtx ctx, OtaPackage otaPackage, OtaPackageExportData exportData, IdProvider idProvider, CompareResult compareResult) {
        if (otaPackage.hasUrl()) {
            OtaPackageInfo info = new OtaPackageInfo(otaPackage);
            return new OtaPackage(otaPackageService.saveOtaPackageInfo(info, info.hasUrl()));
        }
        return otaPackageService.saveOtaPackage(otaPackage);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.OTA_PACKAGE;
    }

}
