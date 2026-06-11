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
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.sync.ie.WidgetTypeExportData;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports widget type entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class WidgetTypeImportService extends BaseEntityImportService<WidgetTypeId, WidgetTypeDetails, WidgetTypeExportData> {

    private final WidgetTypeService widgetTypeService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundle widgets bundle ({@link WidgetTypeDetails})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, WidgetTypeDetails widgetsBundle, IdProvider idProvider) {
        widgetsBundle.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @param old old ({@link WidgetTypeDetails})
     * @param exportData export data ({@link WidgetTypeExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected WidgetTypeDetails prepare(EntitiesImportCtx ctx, WidgetTypeDetails widgetTypeDetails, WidgetTypeDetails old, WidgetTypeExportData exportData, IdProvider idProvider) {
        return widgetTypeDetails;
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param widgetsBundle widgets bundle ({@link WidgetTypeDetails})
     * @param exportData export data ({@link WidgetTypeExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected WidgetTypeDetails saveOrUpdate(EntitiesImportCtx ctx, WidgetTypeDetails widgetsBundle, WidgetTypeExportData exportData, IdProvider idProvider, CompareResult compareResult) {
        return widgetTypeService.saveWidgetType(widgetsBundle);
    }
    /**
     * Compares the requested data.
     *
     * @param ctx calculated-field execution context
     * @param exportData export data ({@link WidgetTypeExportData})
     * @param prepared prepared ({@link WidgetTypeDetails})
     * @param existing existing ({@link WidgetTypeDetails})
     * @return {@link CompareResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected CompareResult compare(EntitiesImportCtx ctx, WidgetTypeExportData exportData, WidgetTypeDetails prepared, WidgetTypeDetails existing) {
        return new CompareResult(true);
    }
    /**
     * Deep copy.
     *
     * @param widgetsBundle widgets bundle ({@link WidgetTypeDetails})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected WidgetTypeDetails deepCopy(WidgetTypeDetails widgetsBundle) {
        return new WidgetTypeDetails(widgetsBundle);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGET_TYPE;
    }

}
