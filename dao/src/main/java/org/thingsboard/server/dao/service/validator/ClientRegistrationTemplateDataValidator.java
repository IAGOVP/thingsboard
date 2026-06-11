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

import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
/**
 * Validates client registration template entities before persistence.
 *
 * <p>Enforces constraints, uniqueness, and referential integrity at the DAO layer.
 */


@Component
public class ClientRegistrationTemplateDataValidator extends DataValidator<OAuth2ClientRegistrationTemplate> {

    
    /**
     * Validates create.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param clientRegistrationTemplate client registration template ({@link OAuth2ClientRegistrationTemplate})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void validateCreate(TenantId tenantId, OAuth2ClientRegistrationTemplate clientRegistrationTemplate) {
    }

    
    /**
     * Validates update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param clientRegistrationTemplate client registration template ({@link OAuth2ClientRegistrationTemplate})
     * @return {@link OAuth2ClientRegistrationTemplate}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected OAuth2ClientRegistrationTemplate validateUpdate(TenantId tenantId, OAuth2ClientRegistrationTemplate clientRegistrationTemplate) {
        return null;
    }

    
    /**
     * Validates data impl.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param clientRegistrationTemplate client registration template ({@link OAuth2ClientRegistrationTemplate})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void validateDataImpl(TenantId tenantId, OAuth2ClientRegistrationTemplate clientRegistrationTemplate) {
        if (StringUtils.isEmpty(clientRegistrationTemplate.getProviderId())) {
            throw new DataValidationException("Provider ID should be specified!");
        }
        if (clientRegistrationTemplate.getMapperConfig() == null) {
            throw new DataValidationException("Mapper config should be specified!");
        }
        if (clientRegistrationTemplate.getMapperConfig().getType() == null) {
            throw new DataValidationException("Mapper type should be specified!");
        }
        if (clientRegistrationTemplate.getMapperConfig().getBasic() == null) {
            throw new DataValidationException("Basic mapper config should be specified!");
        }
    }
}
