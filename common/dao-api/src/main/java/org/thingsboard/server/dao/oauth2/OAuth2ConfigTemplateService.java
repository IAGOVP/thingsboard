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
package org.thingsboard.server.dao.oauth2;

import org.thingsboard.server.common.data.id.OAuth2ClientRegistrationTemplateId;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Service API for oauth2config template persistence and domain operations.
 */
public interface OAuth2ConfigTemplateService {
    /**
     * Saves or persists client registration template.
     *
     * @param clientRegistrationTemplate client registration template ({@link OAuth2ClientRegistrationTemplate})
     * @return {@link OAuth2ClientRegistrationTemplate}
     */
    OAuth2ClientRegistrationTemplate saveClientRegistrationTemplate(OAuth2ClientRegistrationTemplate clientRegistrationTemplate);

    /**
     * Finds client registration template by provider id.
     *
     * @param providerId provider id ({@link String})
     * @return optional {@link OAuth2ClientRegistrationTemplate}, empty if not found
     */
    Optional<OAuth2ClientRegistrationTemplate> findClientRegistrationTemplateByProviderId(String providerId);

    /**
     * Finds client registration template by id.
     *
     * @param templateId template id ({@link OAuth2ClientRegistrationTemplateId})
     * @return {@link OAuth2ClientRegistrationTemplate}
     */
    OAuth2ClientRegistrationTemplate findClientRegistrationTemplateById(OAuth2ClientRegistrationTemplateId templateId);

    /**
     * Finds all client registration templates.
     *
     * @return {@link List}
     */
    List<OAuth2ClientRegistrationTemplate> findAllClientRegistrationTemplates();

    /**
     * Deletes client registration template by id.
     *
     * @param templateId template id ({@link OAuth2ClientRegistrationTemplateId})
     */
    void deleteClientRegistrationTemplateById(OAuth2ClientRegistrationTemplateId templateId);
}
