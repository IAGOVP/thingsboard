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
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.asset.AssetProfileService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports asset profile entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class AssetProfileImportService extends BaseEntityImportService<AssetProfileId, AssetProfile, EntityExportData<AssetProfile>> {

    private final AssetProfileService assetProfileService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfile asset profile ({@link AssetProfile})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, AssetProfile assetProfile, IdProvider idProvider) {
        assetProfile.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param assetProfile asset profile ({@link AssetProfile})
     * @param old old ({@link AssetProfile})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected AssetProfile prepare(EntitiesImportCtx ctx, AssetProfile assetProfile, AssetProfile old, EntityExportData<AssetProfile> exportData, IdProvider idProvider) {
        assetProfile.setDefaultRuleChainId(idProvider.getInternalId(assetProfile.getDefaultRuleChainId()));
        assetProfile.setDefaultDashboardId(idProvider.getInternalId(assetProfile.getDefaultDashboardId()));
        assetProfile.setDefaultEdgeRuleChainId(idProvider.getInternalId(assetProfile.getDefaultEdgeRuleChainId()));
        return assetProfile;
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param assetProfile asset profile ({@link AssetProfile})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected AssetProfile saveOrUpdate(EntitiesImportCtx ctx, AssetProfile assetProfile, EntityExportData<AssetProfile> exportData, IdProvider idProvider, CompareResult compareResult) {
        AssetProfile saved = assetProfileService.saveAssetProfile(assetProfile);
        if (ctx.isFinalImportAttempt() || ctx.getCurrentImportResult().isUpdatedAllExternalIds()) {
            importCalculatedFields(ctx, saved, exportData, idProvider);
        }
        return saved;
    }
    /**
     * Handles entity saved.
     *
     * @param user authenticated user performing the action
     * @param savedAssetProfile saved asset profile ({@link AssetProfile})
     * @param oldAssetProfile old asset profile ({@link AssetProfile})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void onEntitySaved(User user, AssetProfile savedAssetProfile, AssetProfile oldAssetProfile) {
        logEntityActionService.logEntityAction(savedAssetProfile.getTenantId(), savedAssetProfile.getId(),
                savedAssetProfile, null, oldAssetProfile == null ? ActionType.ADDED : ActionType.UPDATED, user);
    }
    /**
     * Deep copy.
     *
     * @param assetProfile asset profile ({@link AssetProfile})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected AssetProfile deepCopy(AssetProfile assetProfile) {
        return new AssetProfile(assetProfile);
    }
    /**
     * Cleanup for comparison.
     *
     * @param assetProfile asset profile ({@link AssetProfile})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void cleanupForComparison(AssetProfile assetProfile) {
        super.cleanupForComparison(assetProfile);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.ASSET_PROFILE;
    }

}
