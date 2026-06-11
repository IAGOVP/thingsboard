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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.entityview.TbEntityViewService;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports entity view entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class EntityViewImportService extends BaseEntityImportService<EntityViewId, EntityView, EntityExportData<EntityView>> {

    private final EntityViewService entityViewService;

    @Lazy
    @Autowired
    private TbEntityViewService tbEntityViewService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityView entity view ({@link EntityView})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, EntityView entityView, IdProvider idProvider) {
        entityView.setTenantId(tenantId);
        entityView.setCustomerId(idProvider.getInternalId(entityView.getCustomerId()));
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param entityView entity view ({@link EntityView})
     * @param old old ({@link EntityView})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected EntityView prepare(EntitiesImportCtx ctx, EntityView entityView, EntityView old, EntityExportData<EntityView> exportData, IdProvider idProvider) {
        entityView.setEntityId(idProvider.getInternalId(entityView.getEntityId()));
        return entityView;
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param entityView entity view ({@link EntityView})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected EntityView saveOrUpdate(EntitiesImportCtx ctx, EntityView entityView, EntityExportData<EntityView> exportData, IdProvider idProvider, CompareResult compareResult) {
        return entityViewService.saveEntityView(entityView);
    }
    /**
     * Handles entity saved.
     *
     * @param user authenticated user performing the action
     * @param savedEntityView saved entity view ({@link EntityView})
     * @param oldEntityView old entity view ({@link EntityView})
     * @return nothing
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    @Override
    protected void onEntitySaved(User user, EntityView savedEntityView, EntityView oldEntityView) throws ThingsboardException {
        tbEntityViewService.updateEntityViewAttributes(user.getTenantId(), savedEntityView, oldEntityView, user);
        super.onEntitySaved(user, savedEntityView, oldEntityView);
    }
    /**
     * Deep copy.
     *
     * @param entityView entity view ({@link EntityView})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected EntityView deepCopy(EntityView entityView) {
        return new EntityView(entityView);
    }
    /**
     * Cleanup for comparison.
     *
     * @param e e ({@link EntityView})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void cleanupForComparison(EntityView e) {
        super.cleanupForComparison(e);
        if (e.getCustomerId() != null && e.getCustomerId().isNullUid()) {
            e.setCustomerId(null);
        }
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.ENTITY_VIEW;
    }

}
