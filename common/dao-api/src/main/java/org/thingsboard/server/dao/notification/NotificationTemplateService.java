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

import org.thingsboard.server.common.data.id.NotificationTemplateId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service API for notification template persistence and domain operations.
 */
public interface NotificationTemplateService {

    /**
     * Finds notification template by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationTemplateId})
     * @return {@link NotificationTemplate}
     */
    NotificationTemplate findNotificationTemplateById(TenantId tenantId, NotificationTemplateId id);

    /**
     * Saves or persists notification template.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTemplate notification template ({@link NotificationTemplate})
     * @return {@link NotificationTemplate}
     */
    NotificationTemplate saveNotificationTemplate(TenantId tenantId, NotificationTemplate notificationTemplate);

    /**
     * Finds notification templates by tenant id and notification types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTypes notification types ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationTemplate> findNotificationTemplatesByTenantIdAndNotificationTypes(TenantId tenantId, List<NotificationType> notificationTypes, PageLink pageLink);

    /**
     * Finds tenant or system notification template.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationType notification type ({@link NotificationType})
     * @return optional {@link NotificationTemplate}, empty if not found
     */
    Optional<NotificationTemplate> findTenantOrSystemNotificationTemplate(TenantId tenantId, NotificationType notificationType);

    /**
     * Finds notification template by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationType notification type ({@link NotificationType})
     * @return optional {@link NotificationTemplate}, empty if not found
     */
    Optional<NotificationTemplate> findNotificationTemplateByTenantIdAndType(TenantId tenantId, NotificationType notificationType);

    /**
     * Counts notification templates by tenant id and notification types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTypes notification types ({@link Collection})
     * @return the int result
     */
    int countNotificationTemplatesByTenantIdAndNotificationTypes(TenantId tenantId, Collection<NotificationType> notificationTypes);

    /**
     * Deletes notification template by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationTemplateId})
     */
    void deleteNotificationTemplateById(TenantId tenantId, NotificationTemplateId id);

    /**
     * Deletes notification templates by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteNotificationTemplatesByTenantId(TenantId tenantId);

}
