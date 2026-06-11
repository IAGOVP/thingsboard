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

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.UserAuthSettings;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.UserAuthSettingsEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.user.UserAuthSettingsDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of user auth settings dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@RequiredArgsConstructor
@SqlDao
public class JpaUserAuthSettingsDao extends JpaAbstractDao<UserAuthSettingsEntity, UserAuthSettings> implements UserAuthSettingsDao, TenantEntityDao<UserAuthSettings> {

    private final UserAuthSettingsRepository repository;
    /**
     * Finds by user id.
     *
     * @param userId target user identifier
     * @return {@link UserAuthSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public UserAuthSettings findByUserId(UserId userId) {
        return DaoUtil.getData(repository.findByUserId(userId.getId()));
    }
    /**
     * Removes by user id.
     *
     * @param userId target user identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeByUserId(UserId userId) {
        repository.deleteByUserId(userId.getId());
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<UserAuthSettingsEntity> getEntityClass() {
        return UserAuthSettingsEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<UserAuthSettingsEntity, UUID> getRepository() {
        return repository;
    }

}
