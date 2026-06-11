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
package org.thingsboard.server.dao.ai;

import org.thingsboard.server.common.data.ai.AiModel;
import org.thingsboard.server.common.data.id.AiModelId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.Optional;
import java.util.Set;


/**

 * Persistence contract for ai model.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (ThingsBoard DAO layer).

 */


public interface AiModelDao extends TenantEntityDao<AiModel>, ExportableEntityDao<AiModelId, AiModel> {
    /**
     * Finds by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return optional {@link AiModel}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<AiModel> findByTenantIdAndId(TenantId tenantId, AiModelId modelId);
    /**
     * Deletes by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean deleteById(TenantId tenantId, AiModelId modelId);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    Set<AiModelId> deleteByTenantId(TenantId tenantId);
    /**
     * Deletes by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean deleteByTenantIdAndId(TenantId tenantId, AiModelId modelId);

}
