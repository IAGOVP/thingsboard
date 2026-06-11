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

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.settings.UserSettings;
import org.thingsboard.server.common.data.settings.UserSettingsType;

import java.util.List;

/**
 * Service API for user settings persistence and domain operations.
 */
public interface UserSettingsService {

    /**
     * Updates user settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param type type ({@link UserSettingsType})
     * @param settings settings ({@link JsonNode})
     */
    void updateUserSettings(TenantId tenantId, UserId userId, UserSettingsType type, JsonNode settings);

    /**
     * Saves or persists user settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userSettings user settings ({@link UserSettings})
     * @return {@link UserSettings}
     */
    UserSettings saveUserSettings(TenantId tenantId, UserSettings userSettings);

    /**
     * Finds user settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param type type ({@link UserSettingsType})
     * @return {@link UserSettings}
     */
    UserSettings findUserSettings(TenantId tenantId, UserId userId, UserSettingsType type);

    /**
     * Deletes user settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param type type ({@link UserSettingsType})
     * @param jsonPaths json paths ({@link List})
     */
    void deleteUserSettings(TenantId tenantId, UserId userId, UserSettingsType type, List<String> jsonPaths);

}
