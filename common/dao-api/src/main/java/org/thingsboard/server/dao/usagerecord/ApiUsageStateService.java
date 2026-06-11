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
package org.thingsboard.server.dao.usagerecord;

import org.thingsboard.server.common.data.ApiUsageState;
import org.thingsboard.server.common.data.id.ApiUsageStateId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.entity.EntityDaoService;

/**
 * Service API for api usage state persistence and domain operations.
 */
public interface ApiUsageStateService extends EntityDaoService {

    /**
     * Creates default api usage state.
     *
     * @param id id ({@link TenantId})
     * @param entityId entity id ({@link EntityId})
     * @return {@link ApiUsageState}
     */
    ApiUsageState createDefaultApiUsageState(TenantId id, EntityId entityId);

    /**
     * Updates the requested data.
     *
     * @param apiUsageState api usage state ({@link ApiUsageState})
     * @return {@link ApiUsageState}
     */
    ApiUsageState update(ApiUsageState apiUsageState);

    /**
     * Finds tenant api usage state.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link ApiUsageState}
     */
    ApiUsageState findTenantApiUsageState(TenantId tenantId);

    /**
     * Finds api usage state by entity id.
     *
     * @param entityId entity id ({@link EntityId})
     * @return {@link ApiUsageState}
     */
    ApiUsageState findApiUsageStateByEntityId(EntityId entityId);

    /**
     * Deletes api usage state by entity id.
     *
     * @param entityId entity id ({@link EntityId})
     */
    void deleteApiUsageStateByEntityId(EntityId entityId);

    /**
     * Finds api usage state by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link ApiUsageStateId})
     * @return {@link ApiUsageState}
     */
    ApiUsageState findApiUsageStateById(TenantId tenantId, ApiUsageStateId id);

}
