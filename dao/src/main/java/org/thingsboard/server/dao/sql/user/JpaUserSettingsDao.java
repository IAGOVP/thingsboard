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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.settings.UserSettings;
import org.thingsboard.server.common.data.settings.UserSettingsCompositeKey;
import org.thingsboard.server.common.data.settings.UserSettingsType;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.UserSettingsEntity;
import org.thingsboard.server.dao.user.UserSettingsDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
/**
 * JPA/PostgreSQL implementation of user settings dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
public class JpaUserSettingsDao implements UserSettingsDao, TenantEntityDao<UserSettings> {

    @Autowired
    private UserSettingsRepository userSettingsRepository;
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userSettings user settings ({@link UserSettings})
     * @return {@link UserSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public UserSettings save(TenantId tenantId, UserSettings userSettings) {
        log.trace("save [{}][{}]", tenantId, userSettings);
        return DaoUtil.getData(userSettingsRepository.save(new UserSettingsEntity(userSettings)));
    }
    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link UserSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public UserSettings findById(TenantId tenantId, UserSettingsCompositeKey id) {
        return DaoUtil.getData(userSettingsRepository.findById(id));
    }
    /**
     * Removes by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeById(TenantId tenantId, UserSettingsCompositeKey id) {
        userSettingsRepository.deleteById(id);
    }
    /**
     * Removes by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeByUserId(TenantId tenantId, UserId userId) {
        userSettingsRepository.deleteByUserId(userId.getId());
    }
    /**
     * Finds by type and path.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link UserSettingsType})
     * @param path path
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<UserSettings> findByTypeAndPath(TenantId tenantId, UserSettingsType type, String... path) {
        log.trace("findByTypeAndPath [{}][{}][{}]", tenantId, type, path);
        return DaoUtil.convertDataList(userSettingsRepository.findByTypeAndPathExisting(type.name(), path));
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
    public PageData<UserSettings> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(userSettingsRepository.findByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }

}
