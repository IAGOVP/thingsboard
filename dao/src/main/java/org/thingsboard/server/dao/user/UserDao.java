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

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.UserAuthDetails;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for user.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (users, credentials, and user settings).

 */


public interface UserDao extends Dao<User>, TenantEntityDao<User> {

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param user authenticated user performing the action
     * @return {@link User}
     * @throws Exception if an unexpected error occurs during processing
     */

    User save(TenantId tenantId, User user);

    
    /**
     * Finds by email.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return {@link User}
     * @throws Exception if an unexpected error occurs during processing
     */

    User findByEmail(TenantId tenantId, String email);

    
    /**
     * Finds by tenant id and email.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return {@link User}
     * @throws Exception if an unexpected error occurs during processing
     */

    User findByTenantIdAndEmail(TenantId tenantId, String email);

    
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds tenant admins.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findTenantAdmins(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds customer users.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findCustomerUsers(UUID tenantId, UUID customerId, PageLink pageLink);

    
    /**
     * Finds users by customer ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerIds customer ids ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findUsersByCustomerIds(UUID tenantId, List<CustomerId> customerIds, PageLink pageLink);
    /**
     * Finds all.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findAll(PageLink pageLink);
    /**
     * Finds all by authority.
     *
     * @param authority authority ({@link Authority})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findAllByAuthority(Authority authority, PageLink pageLink);
    /**
     * Finds by authority and tenants ids.
     *
     * @param authority authority ({@link Authority})
     * @param tenantsIds tenants ids ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findByAuthorityAndTenantsIds(Authority authority, List<TenantId> tenantsIds, PageLink pageLink);
    /**
     * Finds by authority and tenant profiles ids.
     *
     * @param authority authority ({@link Authority})
     * @param tenantProfilesIds tenant profiles ids ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<User> findByAuthorityAndTenantProfilesIds(Authority authority, List<TenantProfileId> tenantProfilesIds, PageLink pageLink);
    /**
     * Counts tenant admins.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int countTenantAdmins(UUID tenantId);
    /**
     * Finds user auth details by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link UserAuthDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserAuthDetails findUserAuthDetailsByUserId(UUID tenantId, UUID userId);
    /**
     * Finds users by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userIds user ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<User> findUsersByTenantIdAndIds(UUID tenantId, List<UUID> userIds);

}
