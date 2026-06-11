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

import org.thingsboard.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.Optional;


/**

 * Persistence contract for oauth2client registration template.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (OAuth2 client registration templates).

 */


public interface OAuth2ClientRegistrationTemplateDao extends Dao<OAuth2ClientRegistrationTemplate> {
    /**
     * Finds by provider id.
     *
     * @param providerId provider id ({@link String})
     * @return optional {@link OAuth2ClientRegistrationTemplate}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<OAuth2ClientRegistrationTemplate> findByProviderId(String providerId);
    /**
     * Finds all.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<OAuth2ClientRegistrationTemplate> findAll();
}
