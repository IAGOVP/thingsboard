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

import com.google.common.util.concurrent.FluentFuture;
import org.thingsboard.server.common.data.ai.AiModel;
import org.thingsboard.server.common.data.id.AiModelId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.Optional;

/**
 * Service API for ai model persistence and domain operations.
 */
public interface AiModelService extends EntityDaoService {

    /**
     * Saves or persists the requested data.
     *
     * @param model model ({@link AiModel})
     * @return {@link AiModel}
     */
    AiModel save(AiModel model);

    /**
     * Saves or persists the requested data.
     *
     * @param model model ({@link AiModel})
     * @param doValidate whether to run validation before persist
     * @return {@link AiModel}
     */
    AiModel save(AiModel model, boolean doValidate);

    /**
     * Finds ai model by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return optional {@link AiModel}, empty if not found
     */
    Optional<AiModel> findAiModelById(TenantId tenantId, AiModelId modelId);

    /**
     * Finds ai models by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AiModel> findAiModelsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds ai model by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return optional {@link AiModel}, empty if not found
     */
    Optional<AiModel> findAiModelByTenantIdAndId(TenantId tenantId, AiModelId modelId);

    /**
     * Finds ai model by tenant id and id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return {@link FluentFuture}
     */
    FluentFuture<Optional<AiModel>> findAiModelByTenantIdAndIdAsync(TenantId tenantId, AiModelId modelId);

    /**
     * Finds ai model by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return optional {@link AiModel}, empty if not found
     */
    Optional<AiModel> findAiModelByTenantIdAndName(TenantId tenantId, String name);

    /**
     * Deletes by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return the boolean result
     */
    boolean deleteByTenantIdAndId(TenantId tenantId, AiModelId modelId);

}
