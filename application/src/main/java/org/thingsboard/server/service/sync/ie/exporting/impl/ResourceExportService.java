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

import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesExportCtx;

import java.util.Set;
/**
 * Exports resource entities to portable JSON.
 *
 * <p>Used by version control and tenant migration to serialize entity graphs with dependencies.
 */

@Service
@TbCoreComponent
public class ResourceExportService extends BaseEntityExportService<TbResourceId, TbResource, EntityExportData<TbResource>> {
    
    /**
     * Set additional export data.
     *
     * @param ctx calculated-field execution context
     * @param resource resource ({@link TbResource})
     * @param exportData export data ({@link EntityExportData})
     * @return nothing
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */


    @Override
    protected void setAdditionalExportData(EntitiesExportCtx<?> ctx, TbResource resource, EntityExportData<TbResource> exportData) throws ThingsboardException {
        super.setAdditionalExportData(ctx, resource, exportData);
        resource.setPreview(null); // will be generated on import
    }
    /**
     * Returns supported entity types.
     *
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return Set.of(EntityType.TB_RESOURCE);
    }

}
