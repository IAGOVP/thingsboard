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
package org.thingsboard.server.service.entitiy.user;

import jakarta.servlet.http.HttpServletRequest;
import org.thingsboard.server.common.data.UserActivationLink;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;

/**

 * Application-layer service API for user entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbUserService {
/**
 * Saves or persists the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param tbUser tb user ({@link User})
 * @param sendActivationMail send activation mail
 * @param request request payload with operation parameters
 * @param user authenticated user performing the action
 * @return {@link User}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    User save(TenantId tenantId, CustomerId customerId, User tbUser, boolean sendActivationMail, HttpServletRequest request, User user) throws ThingsboardException;
/**
 * Deletes the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param user authenticated user performing the action
 * @param responsibleUser responsible user ({@link User})
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void delete(TenantId tenantId, CustomerId customerId, User user, User responsibleUser) throws ThingsboardException;
/**
 * Returns activation link.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param customerId customer id ({@link CustomerId})
 * @param userId user id ({@link UserId})
 * @param request request payload with operation parameters
 * @return {@link UserActivationLink}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    UserActivationLink getActivationLink(TenantId tenantId, CustomerId customerId, UserId userId, HttpServletRequest request) throws ThingsboardException;

}
