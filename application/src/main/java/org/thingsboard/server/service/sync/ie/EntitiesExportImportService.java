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
package org.thingsboard.server.service.sync.ie;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.common.data.sync.ie.EntityImportResult;
import org.thingsboard.server.service.sync.vc.data.EntitiesExportCtx;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;

import java.util.Comparator;

/**

 * Exports entities export import service entities to portable JSON.

 *

 * <p>Used by version control and tenant migration to serialize entity graphs with dependencies.

 */

public interface EntitiesExportImportService {
/**
 * Exports entity.
 *
 * @param ctx calculated-field execution context
 * @param entityId target entity identifier
 * @return the operation result
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    <E extends ExportableEntity<I>, I extends EntityId> EntityExportData<E> exportEntity(EntitiesExportCtx<?> ctx, I entityId) throws ThingsboardException;
/**
 * Imports entity.
 *
 * @param ctx calculated-field execution context
 * @param exportData export data ({@link EntityExportData})
 * @return the operation result
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    <E extends ExportableEntity<I>, I extends EntityId> EntityImportResult<E> importEntity(EntitiesImportCtx ctx, EntityExportData<E> exportData) throws ThingsboardException;
/**
 * Saves or persists references and relations.
 *
 * @param ctx calculated-field execution context
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void saveReferencesAndRelations(EntitiesImportCtx ctx) throws ThingsboardException;
/**
 * Returns entity type comparator for import.
 *
 * @return {@link Comparator}
 * @throws Exception if an unexpected error occurs during processing
 */

    Comparator<EntityType> getEntityTypeComparatorForImport();

}
