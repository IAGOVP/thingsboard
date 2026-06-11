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
import org.thingsboard.server.common.data.ai.AiModel;
import org.thingsboard.server.common.data.id.AiModelId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.ai.AiModelService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports ai model entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
class AiModelImportService extends BaseEntityImportService<AiModelId, AiModel, EntityExportData<AiModel>> {

    private final AiModelService aiModelService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param model model ({@link AiModel})
     * @param idProvider id provider ({@link BaseEntityImportService})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(
            TenantId tenantId,
            AiModel model,
            BaseEntityImportService<AiModelId, AiModel, EntityExportData<AiModel>>.IdProvider idProvider
    ) {
        model.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param model model ({@link AiModel})
     * @param oldModel old model ({@link AiModel})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link BaseEntityImportService})
     * @return {@link AiModel}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected AiModel prepare(
            EntitiesImportCtx ctx,
            AiModel model,
            AiModel oldModel,
            EntityExportData<AiModel> exportData,
            BaseEntityImportService<AiModelId, AiModel, EntityExportData<AiModel>>.IdProvider idProvider
    ) {
        return model;
    }
    /**
     * Deep copy.
     *
     * @param model model ({@link AiModel})
     * @return {@link AiModel}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected AiModel deepCopy(AiModel model) {
        return new AiModel(model);
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param model model ({@link AiModel})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link BaseEntityImportService})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link AiModel}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected AiModel saveOrUpdate(
            EntitiesImportCtx ctx,
            AiModel model,
            EntityExportData<AiModel> exportData,
            BaseEntityImportService<AiModelId, AiModel, EntityExportData<AiModel>>.IdProvider idProvider,
            CompareResult compareResult
    ) {
        return aiModelService.save(model);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.AI_MODEL;
    }

}
