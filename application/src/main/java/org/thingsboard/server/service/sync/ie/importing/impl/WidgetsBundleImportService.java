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
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.sync.ie.WidgetsBundleExportData;
import org.thingsboard.server.common.data.util.CollectionsUtil;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports widgets bundle entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class WidgetsBundleImportService extends BaseEntityImportService<WidgetsBundleId, WidgetsBundle, WidgetsBundleExportData> {

    private final WidgetsBundleService widgetsBundleService;
    private final WidgetTypeService widgetTypeService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundle widgets bundle ({@link WidgetsBundle})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, WidgetsBundle widgetsBundle, IdProvider idProvider) {
        widgetsBundle.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param widgetsBundle widgets bundle ({@link WidgetsBundle})
     * @param old old ({@link WidgetsBundle})
     * @param exportData export data ({@link WidgetsBundleExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected WidgetsBundle prepare(EntitiesImportCtx ctx, WidgetsBundle widgetsBundle, WidgetsBundle old, WidgetsBundleExportData exportData, IdProvider idProvider) {
        return widgetsBundle;
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param widgetsBundle widgets bundle ({@link WidgetsBundle})
     * @param exportData export data ({@link WidgetsBundleExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected WidgetsBundle saveOrUpdate(EntitiesImportCtx ctx, WidgetsBundle widgetsBundle, WidgetsBundleExportData exportData, IdProvider idProvider, CompareResult compareResult) {
        if (CollectionsUtil.isNotEmpty(exportData.getWidgets())) {
            exportData.getWidgets().forEach(widgetTypeNode -> {
                String bundleAlias = widgetTypeNode.remove("bundleAlias").asText();
                String alias = widgetTypeNode.remove("alias").asText();
                String fqn = String.format("%s.%s", bundleAlias, alias);
                exportData.addFqn(fqn);
                WidgetTypeDetails widgetType = JacksonUtil.treeToValue(widgetTypeNode, WidgetTypeDetails.class);
                widgetType.setTenantId(ctx.getTenantId());
                widgetType.setFqn(fqn);
                var existingWidgetType = widgetTypeService.findWidgetTypeByTenantIdAndFqn(ctx.getTenantId(), fqn);
                if (existingWidgetType == null) {
                    widgetType.setId(null);
                } else {
                    widgetType.setId(existingWidgetType.getId());
                    widgetType.setCreatedTime(existingWidgetType.getCreatedTime());
                }
                widgetTypeService.saveWidgetType(widgetType);
            });
        }
        WidgetsBundle savedWidgetsBundle = widgetsBundleService.saveWidgetsBundle(widgetsBundle);
        widgetTypeService.updateWidgetsBundleWidgetFqns(ctx.getTenantId(), savedWidgetsBundle.getId(), exportData.getFqns());
        return savedWidgetsBundle;
    }
    /**
     * Compares the requested data.
     *
     * @param ctx calculated-field execution context
     * @param exportData export data ({@link WidgetsBundleExportData})
     * @param prepared prepared ({@link WidgetsBundle})
     * @param existing existing ({@link WidgetsBundle})
     * @return {@link CompareResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected CompareResult compare(EntitiesImportCtx ctx, WidgetsBundleExportData exportData, WidgetsBundle prepared, WidgetsBundle existing) {
        return new CompareResult(true);
    }
    /**
     * Deep copy.
     *
     * @param widgetsBundle widgets bundle ({@link WidgetsBundle})
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected WidgetsBundle deepCopy(WidgetsBundle widgetsBundle) {
        return new WidgetsBundle(widgetsBundle);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGETS_BUNDLE;
    }

}
