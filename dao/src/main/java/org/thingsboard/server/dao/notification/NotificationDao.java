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
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.NotificationStatus;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.Set;


/**

 * Persistence contract for notification.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (notification templates, targets, rules, and delivery requests).

 */


public interface NotificationDao extends Dao<Notification> {
    /**
     * Finds unread by delivery method and recipient id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Notification> findUnreadByDeliveryMethodAndRecipientIdAndPageLink(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, PageLink pageLink);
    /**
     * Finds unread by delivery method and recipient id and notification types and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param types types ({@link Set})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Notification> findUnreadByDeliveryMethodAndRecipientIdAndNotificationTypesAndPageLink(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, Set<NotificationType> types, PageLink pageLink);
    /**
     * Finds by delivery method and recipient id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Notification> findByDeliveryMethodAndRecipientIdAndPageLink(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, PageLink pageLink);
    /**
     * Updates status by id and recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @param status status ({@link NotificationStatus})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean updateStatusByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId, NotificationStatus status);
    /**
     * Counts unread by delivery method and recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int countUnreadByDeliveryMethodAndRecipientId(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId);
    /**
     * Deletes by id and recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean deleteByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId);
    /**
     * Updates status by delivery method and recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param status status ({@link NotificationStatus})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int updateStatusByDeliveryMethodAndRecipientId(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, NotificationStatus status);
    /**
     * Deletes by request id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param requestId request id ({@link NotificationRequestId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByRequestId(TenantId tenantId, NotificationRequestId requestId);
    /**
     * Deletes by recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByRecipientId(TenantId tenantId, UserId recipientId);

}
