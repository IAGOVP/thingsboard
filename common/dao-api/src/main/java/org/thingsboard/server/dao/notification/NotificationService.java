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

import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.Set;

/**
 * Service API for notification persistence and domain operations.
 */
public interface NotificationService {

    /**
     * Saves or persists notification.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notification notification ({@link Notification})
     * @return {@link Notification}
     */
    Notification saveNotification(TenantId tenantId, Notification notification);

    /**
     * Finds notification by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationId notification id ({@link NotificationId})
     * @return {@link Notification}
     */
    Notification findNotificationById(TenantId tenantId, NotificationId notificationId);

    /**
     * Mark notification as read.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @return the boolean result
     */
    boolean markNotificationAsRead(TenantId tenantId, UserId recipientId, NotificationId notificationId);

    /**
     * Mark all notifications as read.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @return the int result
     */
    int markAllNotificationsAsRead(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId);

    /**
     * Finds notifications by recipient id and read status.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param unreadOnly unread only
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Notification> findNotificationsByRecipientIdAndReadStatus(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, boolean unreadOnly, PageLink pageLink);

    /**
     * Finds latest unread notifications by recipient id and notification types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param types types ({@link Set})
     * @param limit limit
     * @return {@link PageData}
     */
    PageData<Notification> findLatestUnreadNotificationsByRecipientIdAndNotificationTypes(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, Set<NotificationType> types, int limit);

    /**
     * Counts unread notifications by recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @return the int result
     */
    int countUnreadNotificationsByRecipientId(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId);

    /**
     * Deletes notification.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @return the boolean result
     */
    boolean deleteNotification(TenantId tenantId, UserId recipientId, NotificationId notificationId);

}
