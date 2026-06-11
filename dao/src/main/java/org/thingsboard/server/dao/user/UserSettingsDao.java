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
import org.thingsboard.server.common.data.settings.UserSettings;
import org.thingsboard.server.common.data.settings.UserSettingsCompositeKey;
import org.thingsboard.server.common.data.settings.UserSettingsType;

import java.util.List;


/**

 * Persistence contract for user settings.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (users, credentials, and user settings).

 */


public interface UserSettingsDao {
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userSettings user settings ({@link UserSettings})
     * @return {@link UserSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserSettings save(TenantId tenantId, UserSettings userSettings);
    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link UserSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserSettings findById(TenantId tenantId, UserSettingsCompositeKey key);
    /**
     * Removes by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeById(TenantId tenantId, UserSettingsCompositeKey key);
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
     * Finds by type and path.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link UserSettingsType})
     * @param path path
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<UserSettings> findByTypeAndPath(TenantId tenantId, UserSettingsType type, String... path);

}
