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
package org.thingsboard.server.service.entitiy.oauth2client;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.oauth2.OAuth2Client;

/**

 * Application-layer service API for oauth2client entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbOauth2ClientService {
/**
 * Saves or persists the requested data.
 *
 * @param oAuth2Client o auth2client ({@link OAuth2Client})
 * @param user authenticated user performing the action
 * @return {@link OAuth2Client}
 * @throws Exception if an unexpected error occurs during processing
 */



    OAuth2Client save(OAuth2Client oAuth2Client, User user) throws Exception;
/**
 * Deletes the requested data.
 *
 * @param oAuth2Client o auth2client ({@link OAuth2Client})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void delete(OAuth2Client oAuth2Client, User user);

}
