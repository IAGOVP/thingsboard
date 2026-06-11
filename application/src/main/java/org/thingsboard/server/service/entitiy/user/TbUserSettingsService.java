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

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.settings.UserDashboardAction;
import org.thingsboard.server.common.data.settings.UserDashboardsInfo;
import org.thingsboard.server.common.data.settings.UserSettings;
import org.thingsboard.server.common.data.settings.UserSettingsType;

import java.util.List;

/**

 * Application-layer service API for user settings entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbUserSettingsService {
/**
 * Updates user settings.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param userId user id ({@link UserId})
 * @param type type ({@link UserSettingsType})
 * @param settings settings ({@link JsonNode})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */



    void updateUserSettings(TenantId tenantId, UserId userId, UserSettingsType type, JsonNode settings);
/**
 * Saves or persists user settings.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param userSettings user settings ({@link UserSettings})
 * @return {@link UserSettings}
 * @throws Exception if an unexpected error occurs during processing
 */

    UserSettings saveUserSettings(TenantId tenantId, UserSettings userSettings);
/**
 * Finds user settings.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param userId user id ({@link UserId})
 * @param type type ({@link UserSettingsType})
 * @return {@link UserSettings}
 * @throws Exception if an unexpected error occurs during processing
 */

    UserSettings findUserSettings(TenantId tenantId, UserId userId, UserSettingsType type);
/**
 * Deletes user settings.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param userId user id ({@link UserId})
 * @param type type ({@link UserSettingsType})
 * @param jsonPaths json paths ({@link List})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void deleteUserSettings(TenantId tenantId, UserId userId, UserSettingsType type, List<String> jsonPaths);
/**
 * Finds user dashboards info.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param id id ({@link UserId})
 * @return {@link UserDashboardsInfo}
 * @throws Exception if an unexpected error occurs during processing
 */

    UserDashboardsInfo findUserDashboardsInfo(TenantId tenantId, UserId id);
/**
 * Report user dashboard action.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param id id ({@link UserId})
 * @param dashboardId dashboard id ({@link DashboardId})
 * @param action action ({@link UserDashboardAction})
 * @return {@link UserDashboardsInfo}
 * @throws Exception if an unexpected error occurs during processing
 */

    UserDashboardsInfo reportUserDashboardAction(TenantId tenantId, UserId id, DashboardId dashboardId, UserDashboardAction action);
}
