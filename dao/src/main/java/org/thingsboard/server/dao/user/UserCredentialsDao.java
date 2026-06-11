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

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

/**
 * Persistence contract for user credentials.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (users, credentials, and user settings).
 */

public interface UserCredentialsDao extends Dao<UserCredentials> {

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userCredentials user credentials ({@link UserCredentials})
     * @return {@link UserCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserCredentials save(TenantId tenantId, UserCredentials userCredentials);

    
    /**
     * Finds by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link UserCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserCredentials findByUserId(TenantId tenantId, UUID userId);

    
    /**
     * Finds by activate token.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param activateToken activate token ({@link String})
     * @return {@link UserCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserCredentials findByActivateToken(TenantId tenantId, String activateToken);

    
    /**
     * Finds by reset token.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resetToken reset token ({@link String})
     * @return {@link UserCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserCredentials findByResetToken(TenantId tenantId, String resetToken);
    /**
     * Removes by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByUserId(TenantId tenantId, UserId userId);
    /**
     * Set last login ts.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @param lastLoginTs last login ts
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void setLastLoginTs(TenantId tenantId, UserId userId, long lastLoginTs);
    /**
     * Increment failed login attempts.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int incrementFailedLoginAttempts(TenantId tenantId, UserId userId);
    /**
     * Set failed login attempts.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @param failedLoginAttempts failed login attempts
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void setFailedLoginAttempts(TenantId tenantId, UserId userId, int failedLoginAttempts);

}
