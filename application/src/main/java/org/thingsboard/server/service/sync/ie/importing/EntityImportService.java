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
package org.thingsboard.server.service.sync.ie.importing;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.common.data.sync.ie.EntityImportResult;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;

/**

 * Imports entity entities from export JSON.

 *

 * <p>Resolves references, applies conflict strategy, and persists through DAO services.

 */

public interface EntityImportService<I extends EntityId, E extends ExportableEntity<I>, D extends EntityExportData<E>> {
/**
 * Imports entity.
 *
 * @param ctx calculated-field execution context
 * @param exportData export data ({@link D})
 * @return {@link EntityImportResult}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    EntityImportResult<E> importEntity(EntitiesImportCtx ctx, D exportData) throws ThingsboardException;
/**
 * Returns entity type.
 *
 * @return {@link EntityType}
 * @throws Exception if an unexpected error occurs during processing
 */

    EntityType getEntityType();

}
