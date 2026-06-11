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
package org.thingsboard.server.dao.sql.query;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.ObjectType;
import org.thingsboard.server.common.data.edqs.EdqsObject;
import org.thingsboard.server.common.data.edqs.EdqsState;
import org.thingsboard.server.common.data.edqs.ToCoreEdqsMsg;
import org.thingsboard.server.common.data.edqs.ToCoreEdqsRequest;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.edqs.EdqsService;
/**
 * Spring component for dummy edqs service (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).
 */







@Service
@ConditionalOnMissingBean(value = EdqsService.class, ignored = DummyEdqsService.class)
public class DummyEdqsService implements EdqsService {
    /**
     * Handles update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param entity domain entity to persist or validate
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void onUpdate(TenantId tenantId, EntityId entityId, Object entity) {}
    /**
     * Handles update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param objectType object type ({@link ObjectType})
     * @param object object ({@link EdqsObject})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void onUpdate(TenantId tenantId, ObjectType objectType, EdqsObject object) {}
    /**
     * Handles delete.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void onDelete(TenantId tenantId, EntityId entityId) {}
    /**
     * Handles delete.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param objectType object type ({@link ObjectType})
     * @param object object ({@link EdqsObject})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void onDelete(TenantId tenantId, ObjectType objectType, EdqsObject object) {}
    /**
     * Processes system request.
     *
     * @param request request payload with operation parameters
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void processSystemRequest(ToCoreEdqsRequest request) {}
    /**
     * Processes system msg.
     *
     * @param request request payload with operation parameters
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void processSystemMsg(ToCoreEdqsMsg request) {}
    /**
     * Is api enabled.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isApiEnabled() {
        return getState().isApiEnabled();
    }
    /**
     * Returns state.
     *
     * @return {@link EdqsState}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EdqsState getState() {
        return new EdqsState();
    }

}
