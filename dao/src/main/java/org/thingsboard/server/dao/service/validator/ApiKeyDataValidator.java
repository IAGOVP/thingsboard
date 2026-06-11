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
package org.thingsboard.server.dao.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.dao.pat.ApiKeyDao;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.user.UserService;
/**
 * Validates api key entities before persistence.
 *
 * <p>Enforces constraints, uniqueness, and referential integrity at the DAO layer.
 */


@Component
@RequiredArgsConstructor
public class ApiKeyDataValidator extends DataValidator<ApiKey> {

    private final ApiKeyDao apiKeyDao;
    private final TenantService tenantService;
    private final UserService userService;

    
    /**
     * Validates data impl.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKey api key ({@link ApiKey})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void validateDataImpl(TenantId tenantId, ApiKey apiKey) {
        if (apiKey.getId() != null) {
            if (apiKey.getUuidId() == null) {
                throw new DataValidationException("API Key UUID should be specified!");
            }
            if (apiKey.getId().isNullUid()) {
                throw new DataValidationException("API key UUID must not be the reserved null value!");
            }
        }

        if (apiKey.getTenantId() == null || apiKey.getTenantId().getId() == null) {
            throw new DataValidationException("API key should be assigned to tenant!");
        }
        if (!TenantId.SYS_TENANT_ID.equals(apiKey.getTenantId()) && !tenantService.tenantExists(apiKey.getTenantId())) {
            throw new DataValidationException("API key reference a non-existent tenant!");
        }

        if (apiKey.getUserId() == null || apiKey.getUserId().getId() == null) {
            throw new DataValidationException("API key should be assigned to user!");
        }
        if (userService.findUserById(apiKey.getTenantId(), apiKey.getUserId()) == null) {
            throw new DataValidationException("API key reference a non-existent user!");
        }
    }

    
    /**
     * Validates update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKey api key ({@link ApiKey})
     * @return {@link ApiKey}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected ApiKey validateUpdate(TenantId tenantId, ApiKey apiKey) {
        ApiKey old = apiKeyDao.findById(tenantId, apiKey.getUuidId());
        if (old == null) {
            throw new DataValidationException("Cannot update non-existent API key!");
        }
        if (!old.getUserId().equals(apiKey.getUserId())) {
            throw new DataValidationException("Cannot update API key user id!");
        }
        if (old.getExpirationTime() != apiKey.getExpirationTime()) {
            throw new DataValidationException("Cannot update API key expiration time!");
        }
        return old;
    }

}
