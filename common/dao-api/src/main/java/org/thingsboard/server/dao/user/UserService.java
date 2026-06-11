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
package org.thingsboard.server.dao.user;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.UserAuthDetails;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.id.UserCredentialsId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.mobile.MobileSessionInfo;
import org.thingsboard.server.common.data.notification.targets.platform.SystemLevelUsersFilter;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilter;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.Map;

/**
 * Service API for user persistence and domain operations.
 */
public interface UserService extends EntityDaoService {

    /**
     * Finds user by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @return {@link User}
     */
    User findUserById(TenantId tenantId, UserId userId);

    /**
     * Finds user by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @return future completing with {@link User}
     */
    ListenableFuture<User> findUserByIdAsync(TenantId tenantId, UserId userId);

    /**
     * Finds user by email.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return {@link User}
     */
    User findUserByEmail(TenantId tenantId, String email);

    /**
     * Finds user by tenant id and email.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return {@link User}
     */
    User findUserByTenantIdAndEmail(TenantId tenantId, String email);

    /**
     * Finds user by tenant id and email async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return future completing with {@link User}
     */
    ListenableFuture<User> findUserByTenantIdAndEmailAsync(TenantId tenantId, String email);

    /**
     * Saves or persists user.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param user user ({@link User})
     * @return {@link User}
     */
    User saveUser(TenantId tenantId, User user);

    /**
     * Saves or persists user.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param user user ({@link User})
     * @param doValidate whether to run validation before persist
     * @return {@link User}
     */
    User saveUser(TenantId tenantId, User user, boolean doValidate);

    /**
     * Finds user credentials by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @return {@link UserCredentials}
     */
    UserCredentials findUserCredentialsByUserId(TenantId tenantId, UserId userId);

    /**
     * Finds user credentials by activate token.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activateToken activate token ({@link String})
     * @return {@link UserCredentials}
     */
    UserCredentials findUserCredentialsByActivateToken(TenantId tenantId, String activateToken);

    /**
     * Finds user credentials by reset token.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resetToken reset token ({@link String})
     * @return {@link UserCredentials}
     */
    UserCredentials findUserCredentialsByResetToken(TenantId tenantId, String resetToken);

    /**
     * Saves or persists user credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userCredentials user credentials ({@link UserCredentials})
     * @return {@link UserCredentials}
     */
    UserCredentials saveUserCredentials(TenantId tenantId, UserCredentials userCredentials);

    /**
     * Saves or persists user credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userCredentials user credentials ({@link UserCredentials})
     * @param doValidate whether to run validation before persist
     * @return {@link UserCredentials}
     */
    UserCredentials saveUserCredentials(TenantId tenantId, UserCredentials userCredentials, boolean doValidate);

    /**
     * Activate user credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activateToken activate token ({@link String})
     * @param password password ({@link String})
     * @return {@link UserCredentials}
     */
    UserCredentials activateUserCredentials(TenantId tenantId, String activateToken, String password);

    /**
     * Request password reset.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return {@link UserCredentials}
     */
    UserCredentials requestPasswordReset(TenantId tenantId, String email);

    /**
     * Request expired password reset.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userCredentialsId user credentials id ({@link UserCredentialsId})
     * @return {@link UserCredentials}
     */
    UserCredentials requestExpiredPasswordReset(TenantId tenantId, UserCredentialsId userCredentialsId);

    /**
     * Generate password reset token.
     *
     * @param userCredentials user credentials ({@link UserCredentials})
     * @return {@link UserCredentials}
     */
    UserCredentials generatePasswordResetToken(UserCredentials userCredentials);

    /**
     * Generate user activation token.
     *
     * @param userCredentials user credentials ({@link UserCredentials})
     * @return {@link UserCredentials}
     */
    UserCredentials generateUserActivationToken(UserCredentials userCredentials);

    /**
     * Checks user activation token.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userCredentials user credentials ({@link UserCredentials})
     * @return {@link UserCredentials}
     */
    UserCredentials checkUserActivationToken(TenantId tenantId, UserCredentials userCredentials);

    /**
     * Replace user credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userCredentials user credentials ({@link UserCredentials})
     * @return {@link UserCredentials}
     */
    UserCredentials replaceUserCredentials(TenantId tenantId, UserCredentials userCredentials);

    /**
     * Deletes user credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userCredentials user credentials ({@link UserCredentials})
     */
    void deleteUserCredentials(TenantId tenantId, UserCredentials userCredentials);

    /**
     * Deletes user.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param user user ({@link User})
     */
    void deleteUser(TenantId tenantId, User user);

    /**
     * Finds users by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findUsersByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds tenant admins.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findTenantAdmins(TenantId tenantId, PageLink pageLink);

    /**
     * Finds sys admins.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findSysAdmins(PageLink pageLink);

    /**
     * Finds all tenant admins.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findAllTenantAdmins(PageLink pageLink);

    /**
     * Finds tenant admins by tenants ids.
     *
     * @param tenantsIds tenants ids ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findTenantAdminsByTenantsIds(List<TenantId> tenantsIds, PageLink pageLink);

    /**
     * Finds tenant admins by tenant profiles ids.
     *
     * @param tenantProfilesIds tenant profiles ids ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findTenantAdminsByTenantProfilesIds(List<TenantProfileId> tenantProfilesIds, PageLink pageLink);

    /**
     * Finds all users.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findAllUsers(PageLink pageLink);

    /**
     * Deletes tenant admins.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteTenantAdmins(TenantId tenantId);

    /**
     * Deletes all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteAllByTenantId(TenantId tenantId);

    /**
     * Finds customer users.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findCustomerUsers(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds users by customer ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerIds customer ids ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findUsersByCustomerIds(TenantId tenantId, List<CustomerId> customerIds, PageLink pageLink);

    /**
     * Deletes customer users.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void deleteCustomerUsers(TenantId tenantId, CustomerId customerId);

    /**
     * Set user credentials enabled.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param enabled enabled
     */
    void setUserCredentialsEnabled(TenantId tenantId, UserId userId, boolean enabled);

    /**
     * Reset failed login attempts.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     */
    void resetFailedLoginAttempts(TenantId tenantId, UserId userId);

    /**
     * Increase failed login attempts.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @return the int result
     */
    int increaseFailedLoginAttempts(TenantId tenantId, UserId userId);

    /**
     * Updates last login ts.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     */
    void updateLastLoginTs(TenantId tenantId, UserId userId);

    /**
     * Saves or persists mobile session.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param mobileToken mobile token ({@link String})
     * @param sessionInfo session info ({@link MobileSessionInfo})
     */
    void saveMobileSession(TenantId tenantId, UserId userId, String mobileToken, MobileSessionInfo sessionInfo);

    /**
     * Finds mobile sessions.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @return {@link Map}
     */
    Map<String, MobileSessionInfo> findMobileSessions(TenantId tenantId, UserId userId);

    /**
     * Finds mobile session.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param mobileToken mobile token ({@link String})
     * @return {@link MobileSessionInfo}
     */
    MobileSessionInfo findMobileSession(TenantId tenantId, UserId userId, String mobileToken);

    /**
     * Removes mobile session.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileToken mobile token ({@link String})
     */
    void removeMobileSession(TenantId tenantId, String mobileToken);

    /**
     * Counts tenant admins.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the int result
     */
    int countTenantAdmins(TenantId tenantId);

    /**
     * Finds users by filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filter filter ({@link UsersFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findUsersByFilter(TenantId tenantId, UsersFilter filter, PageLink pageLink);

    /**
     * Matches filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filter filter ({@link SystemLevelUsersFilter})
     * @param user user ({@link User})
     * @return the boolean result
     */
    boolean matchesFilter(TenantId tenantId, SystemLevelUsersFilter filter, User user);

    /**
     * Finds user auth details by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @return {@link UserAuthDetails}
     */
    UserAuthDetails findUserAuthDetailsByUserId(TenantId tenantId, UserId userId);


    /**
     * Finds users by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userIds user ids ({@link List})
     * @return {@link List}
     */
    List<User> findUsersByTenantIdAndIds(TenantId tenantId, List<UserId> userIds);

}
