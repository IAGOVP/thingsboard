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
package org.thingsboard.server.common.msg.edqs;

import org.thingsboard.server.common.data.ObjectType;
import org.thingsboard.server.common.data.edqs.EdqsObject;
import org.thingsboard.server.common.data.edqs.EdqsState;
import org.thingsboard.server.common.data.edqs.ToCoreEdqsMsg;
import org.thingsboard.server.common.data.edqs.ToCoreEdqsRequest;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;

/**
 * Service API for edqs persistence and domain operations.
 */
public interface EdqsService {

    /**
     * Handles update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param entity entity ({@link Object})
     */
    void onUpdate(TenantId tenantId, EntityId entityId, Object entity);

    /**
     * Handles update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param objectType object type ({@link ObjectType})
     * @param object object ({@link EdqsObject})
     */
    void onUpdate(TenantId tenantId, ObjectType objectType, EdqsObject object);

    /**
     * Handles delete.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     */
    void onDelete(TenantId tenantId, EntityId entityId);

    /**
     * Handles delete.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param objectType object type ({@link ObjectType})
     * @param object object ({@link EdqsObject})
     */
    void onDelete(TenantId tenantId, ObjectType objectType, EdqsObject object);

    /**
     * Processes system request.
     *
     * @param request request payload with operation parameters
     */
    void processSystemRequest(ToCoreEdqsRequest request);

    /**
     * Processes system msg.
     *
     * @param request request payload with operation parameters
     */
    void processSystemMsg(ToCoreEdqsMsg request);

    /**
     * Is api enabled.
     *
     * @return the boolean result
     */
    boolean isApiEnabled();

    /**
     * Returns state.
     *
     * @return {@link EdqsState}
     */
    EdqsState getState();

}
