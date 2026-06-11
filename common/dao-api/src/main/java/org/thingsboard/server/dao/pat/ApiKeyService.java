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
package org.thingsboard.server.dao.pat;

import org.thingsboard.server.common.data.id.ApiKeyId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.common.data.pat.ApiKeyInfo;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for api key persistence and domain operations.
 */
public interface ApiKeyService extends EntityDaoService {

    /**
     * Saves or persists api key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKey api key ({@link ApiKeyInfo})
     * @return {@link ApiKey}
     */
    ApiKey saveApiKey(TenantId tenantId, ApiKeyInfo apiKey);

    /**
     * Saves or persists api key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKeyInfo api key info ({@link ApiKeyInfo})
     * @param value value ({@link String})
     * @param doValidate whether to run validation before persist
     * @return {@link ApiKey}
     */
    ApiKey saveApiKey(TenantId tenantId, ApiKeyInfo apiKeyInfo, String value, boolean doValidate);

    /**
     * Deletes api key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKey api key ({@link ApiKey})
     * @param force force
     */
    void deleteApiKey(TenantId tenantId, ApiKey apiKey, boolean force);

    /**
     * Deletes by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     */
    void deleteByUserId(TenantId tenantId, UserId userId);

    /**
     * Finds api key by value.
     *
     * @param value value ({@link String})
     * @return {@link ApiKey}
     */
    ApiKey findApiKeyByValue(String value);

    /**
     * Finds api key by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKeyId api key id ({@link ApiKeyId})
     * @return {@link ApiKey}
     */
    ApiKey findApiKeyById(TenantId tenantId, ApiKeyId apiKeyId);

    /**
     * Finds api keys by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<ApiKeyInfo> findApiKeysByUserId(TenantId tenantId, UserId userId, PageLink pageLink);

    /**
     * Finds api keys by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @return {@link List}
     */
    List<ApiKey> findApiKeysByUserId(TenantId tenantId, UserId userId);

    /**
     * Finds api keys by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<ApiKey> findApiKeysByTenantId(TenantId tenantId, PageLink pageLink);

}
