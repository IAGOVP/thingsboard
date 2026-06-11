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
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports device profile entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class DeviceProfileImportService extends BaseEntityImportService<DeviceProfileId, DeviceProfile, EntityExportData<DeviceProfile>> {

    private final DeviceProfileService deviceProfileService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, DeviceProfile deviceProfile, IdProvider idProvider) {
        deviceProfile.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @param old old ({@link DeviceProfile})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected DeviceProfile prepare(EntitiesImportCtx ctx, DeviceProfile deviceProfile, DeviceProfile old, EntityExportData<DeviceProfile> exportData, IdProvider idProvider) {
        deviceProfile.setDefaultRuleChainId(idProvider.getInternalId(deviceProfile.getDefaultRuleChainId()));
        deviceProfile.setDefaultEdgeRuleChainId(idProvider.getInternalId(deviceProfile.getDefaultEdgeRuleChainId()));
        deviceProfile.setDefaultDashboardId(idProvider.getInternalId(deviceProfile.getDefaultDashboardId()));
        deviceProfile.setFirmwareId(idProvider.getInternalId(deviceProfile.getFirmwareId(), false));
        deviceProfile.setSoftwareId(idProvider.getInternalId(deviceProfile.getSoftwareId(), false));
        return deviceProfile;
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected DeviceProfile saveOrUpdate(EntitiesImportCtx ctx, DeviceProfile deviceProfile, EntityExportData<DeviceProfile> exportData, IdProvider idProvider, CompareResult compareResult) {
        boolean toUpdate = ctx.isFinalImportAttempt() || ctx.getCurrentImportResult().isUpdatedAllExternalIds();
        if (toUpdate) {
            deviceProfile.setFirmwareId(idProvider.getInternalId(deviceProfile.getFirmwareId()));
            deviceProfile.setSoftwareId(idProvider.getInternalId(deviceProfile.getSoftwareId()));
        }
        DeviceProfile saved = deviceProfileService.saveDeviceProfile(deviceProfile);
        if (toUpdate) {
            importCalculatedFields(ctx, saved, exportData, idProvider);
        }
        return saved;
    }
    /**
     * Handles entity saved.
     *
     * @param user authenticated user performing the action
     * @param savedDeviceProfile saved device profile ({@link DeviceProfile})
     * @param oldDeviceProfile old device profile ({@link DeviceProfile})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void onEntitySaved(User user, DeviceProfile savedDeviceProfile, DeviceProfile oldDeviceProfile) {
        logEntityActionService.logEntityAction(savedDeviceProfile.getTenantId(), savedDeviceProfile.getId(), savedDeviceProfile,
                null, oldDeviceProfile == null ? ActionType.ADDED : ActionType.UPDATED, user);
    }
    /**
     * Deep copy.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected DeviceProfile deepCopy(DeviceProfile deviceProfile) {
        return new DeviceProfile(deviceProfile);
    }
    /**
     * Cleanup for comparison.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void cleanupForComparison(DeviceProfile deviceProfile) {
        super.cleanupForComparison(deviceProfile);
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
