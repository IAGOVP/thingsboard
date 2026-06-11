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
package org.thingsboard.server.dao.notification;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.settings.NotificationSettings;
import org.thingsboard.server.common.data.notification.settings.UserNotificationSettings;

import java.util.Map;

/**
 * Service API for notification settings persistence and domain operations.
 */
public interface NotificationSettingsService {

    /**
     * Saves or persists notification settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param settings settings ({@link NotificationSettings})
     */
    void saveNotificationSettings(TenantId tenantId, NotificationSettings settings);

    /**
     * Finds notification settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link NotificationSettings}
     */
    NotificationSettings findNotificationSettings(TenantId tenantId);

    /**
     * Deletes notification settings.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteNotificationSettings(TenantId tenantId);

    /**
     * Saves or persists user notification settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param settings settings ({@link UserNotificationSettings})
     * @return {@link UserNotificationSettings}
     */
    UserNotificationSettings saveUserNotificationSettings(TenantId tenantId, UserId userId, UserNotificationSettings settings);

    /**
     * Returns user notification settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param format format
     * @return {@link UserNotificationSettings}
     */
    UserNotificationSettings getUserNotificationSettings(TenantId tenantId, UserId userId, boolean format);

    /**
     * Creates default notification configs.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void createDefaultNotificationConfigs(TenantId tenantId);

    /**
     * Updates default notification configs.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void updateDefaultNotificationConfigs(TenantId tenantId);

    /**
     * Move mail templates to notification center.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mailTemplates mail templates ({@link JsonNode})
     * @param mailTemplatesNames mail templates names ({@link Map})
     */
    void moveMailTemplatesToNotificationCenter(TenantId tenantId, JsonNode mailTemplates, Map<String, NotificationType> mailTemplatesNames);

}
