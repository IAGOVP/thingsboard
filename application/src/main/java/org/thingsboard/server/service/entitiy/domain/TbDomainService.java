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
package org.thingsboard.server.service.entitiy.domain;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.domain.Domain;
import org.thingsboard.server.common.data.id.OAuth2ClientId;

import java.util.List;

/**

 * Application-layer service API for domain entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbDomainService {
/**
 * Saves or persists the requested data.
 *
 * @param domain domain ({@link Domain})
 * @param oAuth2Clients o auth2clients ({@link List})
 * @param user authenticated user performing the action
 * @return {@link Domain}
 * @throws Exception if an unexpected error occurs during processing
 */



    Domain save(Domain domain, List<OAuth2ClientId> oAuth2Clients, User user) throws Exception;
/**
 * Updates oauth2clients.
 *
 * @param domain domain ({@link Domain})
 * @param oAuth2ClientIds o auth2client ids ({@link List})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void updateOauth2Clients(Domain domain, List<OAuth2ClientId> oAuth2ClientIds, User user);
/**
 * Deletes the requested data.
 *
 * @param domain domain ({@link Domain})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void delete(Domain domain, User user);

}
