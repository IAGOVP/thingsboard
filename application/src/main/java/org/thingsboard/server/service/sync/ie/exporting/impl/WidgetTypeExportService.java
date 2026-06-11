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
package org.thingsboard.server.service.sync.ie.exporting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.sync.ie.WidgetTypeExportData;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesExportCtx;

import java.util.Set;
/**
 * Exports widget type entities to portable JSON.
 *
 * <p>Used by version control and tenant migration to serialize entity graphs with dependencies.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class WidgetTypeExportService extends BaseEntityExportService<WidgetTypeId, WidgetTypeDetails, WidgetTypeExportData> {
    
    /**
     * Set related entities.
     *
     * @param ctx calculated-field execution context
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @param exportData export data ({@link WidgetTypeExportData})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, WidgetTypeDetails widgetTypeDetails, WidgetTypeExportData exportData) {
        if (widgetTypeDetails.getTenantId() == null || widgetTypeDetails.getTenantId().isNullUid()) {
            throw new IllegalArgumentException("Export of system Widget Type is not allowed");
        }
    }
    /**
     * Returns supported entity types.
     *
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return Set.of(EntityType.WIDGET_TYPE);
    }

}