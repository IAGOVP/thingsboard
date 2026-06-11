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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.UserActivationLink;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;
import org.thingsboard.server.service.security.system.SystemSecurityService;
/**
 * Default Spring implementation for user service (REST-layer entity operations (application-layer entity CRUD with audit logging and version-control hooks)).
 *
 * <p>Registered as a {@code @Service} or {@code @Component} bean.
 */


@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultUserService extends AbstractTbEntityService implements TbUserService {

    private final UserService userService;
    private final MailService mailService;
    private final SystemSecurityService systemSecurityService;
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

    @Override
    public User save(TenantId tenantId, CustomerId customerId, User tbUser, boolean sendActivationMail,
                     HttpServletRequest request, User user) throws ThingsboardException {
        ActionType actionType = tbUser.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {
            boolean sendEmail = tbUser.getId() == null && sendActivationMail;
            User savedUser = checkNotNull(userService.saveUser(tenantId, tbUser));
            if (sendEmail) {
                UserActivationLink activationLink = getActivationLink(tenantId, customerId, savedUser.getId(), request);
                try {
                    mailService.sendActivationEmail(activationLink.value(), activationLink.ttlMs(), savedUser.getEmail());
                } catch (ThingsboardException e) {
                    userService.deleteUser(tenantId, savedUser);
                    throw new ThingsboardException("Couldn't send user activation email", ThingsboardErrorCode.GENERAL);
                }
            }
            logEntityActionService.logEntityAction(tenantId, savedUser.getId(), savedUser, customerId, actionType, user);
            return savedUser;
        } catch (Exception e) {
            logEntityActionService.logEntityAction(tenantId, emptyId(EntityType.USER), tbUser, actionType, user, e);
            throw e;
        }
    }
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

    @Override
    public void delete(TenantId tenantId, CustomerId customerId, User user, User responsibleUser) throws ThingsboardException {
        ActionType actionType = ActionType.DELETED;
        UserId userId = user.getId();

        try {
            userService.deleteUser(tenantId, user);
            logEntityActionService.logEntityAction(tenantId, userId, user, customerId, actionType, responsibleUser, customerId.toString());
        } catch (Exception e) {
            logEntityActionService.logEntityAction(tenantId, emptyId(EntityType.USER),
                    actionType, responsibleUser, e, userId.toString());
            throw e;
        }
    }
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

    @Override
    public UserActivationLink getActivationLink(TenantId tenantId, CustomerId customerId, UserId userId, HttpServletRequest request) throws ThingsboardException {
        UserCredentials userCredentials = userService.findUserCredentialsByUserId(tenantId, userId);
        if (!userCredentials.isEnabled() && userCredentials.getActivateToken() != null) {
            userCredentials = userService.checkUserActivationToken(tenantId, userCredentials);
            String baseUrl = systemSecurityService.getBaseUrl(tenantId, customerId, request);
            String link = baseUrl + "/api/noauth/activate?activateToken=" + userCredentials.getActivateToken();
            return new UserActivationLink(link, userCredentials.getActivationTokenTtl());
        } else {
            throw new ThingsboardException("User is already activated!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

}
