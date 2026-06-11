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
package org.thingsboard.server.dao.sql.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.UserAuthDetails;
import org.thingsboard.server.common.data.edqs.fields.UserFields;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.UserEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.user.UserDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;

/**
 * JPA/PostgreSQL implementation of user dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */

@Component
@SqlDao
public class JpaUserDao extends JpaAbstractDao<UserEntity, User> implements UserDao {

    @Autowired
    private UserRepository userRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<UserEntity, UUID> getRepository() {
        return userRepository;
    }
    /**
     * Finds by email.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return {@link User}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public User findByEmail(TenantId tenantId, String email) {
        return DaoUtil.getData(userRepository.findByEmail(email));
    }
    /**
     * Finds by tenant id and email.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param email email ({@link String})
     * @return {@link User}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public User findByTenantIdAndEmail(TenantId tenantId, String email) {
        return DaoUtil.getData(userRepository.findByTenantIdAndEmail(tenantId.getId(), email));
    }
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                userRepository
                        .findByTenantId(
                                tenantId,
                                pageLink.getTextSearch(),
                                DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds tenant admins.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findTenantAdmins(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                userRepository
                        .findUsersByAuthority(
                                tenantId,
                                NULL_UUID,
                                pageLink.getTextSearch(),
                                Authority.TENANT_ADMIN,
                                DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds customer users.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findCustomerUsers(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                userRepository
                        .findUsersByAuthority(
                                tenantId,
                                customerId,
                                pageLink.getTextSearch(),
                                Authority.CUSTOMER_USER,
                                DaoUtil.toPageable(pageLink)));

    }
    /**
     * Finds users by customer ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerIds customer ids ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findUsersByCustomerIds(UUID tenantId, List<CustomerId> customerIds, PageLink pageLink) {
        return DaoUtil.toPageData(
                userRepository
                        .findTenantAndCustomerUsers(
                                tenantId,
                                DaoUtil.toUUIDs(customerIds),
                                pageLink.getTextSearch(),
                                DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds all.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findAll(PageLink pageLink) {
        return DaoUtil.toPageData(userRepository.findAll(DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds all by authority.
     *
     * @param authority authority ({@link Authority})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findAllByAuthority(Authority authority, PageLink pageLink) {
        return DaoUtil.toPageData(userRepository.findAllByAuthority(authority, DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by authority and tenants ids.
     *
     * @param authority authority ({@link Authority})
     * @param tenantsIds tenants ids ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findByAuthorityAndTenantsIds(Authority authority, List<TenantId> tenantsIds, PageLink pageLink) {
        return DaoUtil.toPageData(userRepository.findByAuthorityAndTenantIdIn(authority, DaoUtil.toUUIDs(tenantsIds), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by authority and tenant profiles ids.
     *
     * @param authority authority ({@link Authority})
     * @param tenantProfilesIds tenant profiles ids ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findByAuthorityAndTenantProfilesIds(Authority authority, List<TenantProfileId> tenantProfilesIds, PageLink pageLink) {
        return DaoUtil.toPageData(userRepository.findByAuthorityAndTenantProfilesIds(authority, DaoUtil.toUUIDs(tenantProfilesIds),
                DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds user auth details by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link UserAuthDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public UserAuthDetails findUserAuthDetailsByUserId(UUID tenantId, UUID userId) {
        TbPair<UserEntity, Boolean> result = userRepository.findUserAuthDetailsByUserId(userId);
        return result != null ? new UserAuthDetails(result.getFirst().toData(), result.getSecond()) : null;
    }
    /**
     * Counts tenant admins.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int countTenantAdmins(UUID tenantId) {
        return userRepository.countByTenantIdAndAuthority(tenantId, Authority.TENANT_ADMIN);
    }
    /**
     * Finds users by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userIds user ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<User> findUsersByTenantIdAndIds(UUID tenantId, List<UUID> userIds) {
        return DaoUtil.convertDataList(userRepository.findUsersByTenantIdAndIdIn(tenantId, userIds));
    }
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return userRepository.countByTenantId(tenantId.getId());
    }
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<User> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
    }
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<UserFields> findNextBatch(UUID id, int batchSize) {
        return userRepository.findNextBatch(id, Limit.of(batchSize));
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
    }

}
