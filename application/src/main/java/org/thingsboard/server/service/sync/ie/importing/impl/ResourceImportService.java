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
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports resource entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class ResourceImportService extends BaseEntityImportService<TbResourceId, TbResource, EntityExportData<TbResource>> {

    private final ResourceService resourceService;
    private final ImageService imageService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resource resource ({@link TbResource})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, TbResource resource, IdProvider idProvider) {
        resource.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param resource resource ({@link TbResource})
     * @param oldResource old resource ({@link TbResource})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link TbResource}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected TbResource prepare(EntitiesImportCtx ctx, TbResource resource, TbResource oldResource, EntityExportData<TbResource> exportData, IdProvider idProvider) {
        return resource;
    }
    /**
     * Finds existing entity.
     *
     * @param ctx calculated-field execution context
     * @param resource resource ({@link TbResource})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link TbResource}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected TbResource findExistingEntity(EntitiesImportCtx ctx, TbResource resource, IdProvider idProvider) {
        TbResource existingResource = super.findExistingEntity(ctx, resource, idProvider);
        if (existingResource == null && ctx.isFindExistingByName()) {
            existingResource = resourceService.findResourceByTenantIdAndKey(ctx.getTenantId(), resource.getResourceType(), resource.getResourceKey());
        }
        return existingResource;
    }
    /**
     * Deep copy.
     *
     * @param resource resource ({@link TbResource})
     * @return {@link TbResource}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected TbResource deepCopy(TbResource resource) {
        return new TbResource(resource);
    }
    /**
     * Cleanup for comparison.
     *
     * @param resource resource ({@link TbResource})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void cleanupForComparison(TbResource resource) {
        super.cleanupForComparison(resource);
        resource.setSearchText(null);
        if (resource.getDescriptor() != null && resource.getDescriptor().isNull()) {
            resource.setDescriptor(null);
        }
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param resource resource ({@link TbResource})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link TbResource}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected TbResource saveOrUpdate(EntitiesImportCtx ctx, TbResource resource, EntityExportData<TbResource> exportData, IdProvider idProvider, CompareResult compareResult) {
        if (resource.getResourceType() == ResourceType.IMAGE) {
            return new TbResource(imageService.saveImage(resource));
        } else {
            if (compareResult.isExternalIdChangedOnly()) {
                resource = resourceService.saveResource(resource, false);
            } else {
                resource = resourceService.saveResource(resource);
            }
            resource.setData(null);
            resource.setPreview(null);
            return resource;
        }
    }
    /**
     * Handles entity saved.
     *
     * @param user authenticated user performing the action
     * @param savedResource saved resource ({@link TbResource})
     * @param oldResource old resource ({@link TbResource})
     * @return nothing
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    @Override
    protected void onEntitySaved(User user, TbResource savedResource, TbResource oldResource) throws ThingsboardException {
        super.onEntitySaved(user, savedResource, oldResource);
        clusterService.onResourceChange(savedResource, null);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.TB_RESOURCE;
    }

}
