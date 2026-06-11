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
package org.thingsboard.server.service.entitiy.cf;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.cf.CalculatedField;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.service.security.model.SecurityUser;

/**

 * Application-layer service API for calculated field entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbCalculatedFieldService {
/**
 * Saves or persists the requested data.
 *
 * @param calculatedField calculated field ({@link CalculatedField})
 * @param user authenticated user performing the action
 * @return {@link CalculatedField}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    CalculatedField save(CalculatedField calculatedField, SecurityUser user) throws ThingsboardException;
/**
 * Finds by id.
 *
 * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
 * @param user authenticated user performing the action
 * @return {@link CalculatedField}
 * @throws Exception if an unexpected error occurs during processing
 */

    CalculatedField findById(CalculatedFieldId calculatedFieldId, SecurityUser user);
/**
 * Finds by tenant id and entity id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @param type type ({@link CalculatedFieldType})
 * @param pageLink pagination and sort parameters
 * @return {@link PageData}
 * @throws Exception if an unexpected error occurs during processing
 */

    PageData<CalculatedField> findByTenantIdAndEntityId(TenantId tenantId, EntityId entityId, CalculatedFieldType type, PageLink pageLink);
/**
 * Deletes the requested data.
 *
 * @param calculatedField calculated field ({@link CalculatedField})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void delete(CalculatedField calculatedField, SecurityUser user);
/**
 * Executes test script.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param inputParams input params ({@link JsonNode})
 * @return {@link JsonNode}
 * @throws Exception if an unexpected error occurs during processing
 */

    JsonNode executeTestScript(TenantId tenantId, JsonNode inputParams);

}
