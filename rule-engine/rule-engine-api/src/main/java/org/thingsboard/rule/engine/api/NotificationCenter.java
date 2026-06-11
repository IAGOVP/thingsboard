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
package org.thingsboard.rule.engine.api;

import com.google.common.util.concurrent.FutureCallback;
import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.NotificationRequest;
import org.thingsboard.server.common.data.notification.NotificationRequestStats;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.info.GeneralNotificationInfo;
import org.thingsboard.server.common.data.notification.info.NotificationInfo;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilter;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;

import java.util.List;

/**

 * Dispatches notification rules from rule engine.

 */


/**

 * Dispatches notification rules triggered from the rule engine.

 */


public interface NotificationCenter {
    /**
     * Processes notification request.
     *
     * @param tenantId tenant UUID
     * @param notificationRequest notification request ({@link NotificationRequest})
     * @param callback completion callback for async rule engine operations
     * @return {@link NotificationRequest}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationRequest processNotificationRequest(TenantId tenantId, NotificationRequest notificationRequest, FutureCallback<NotificationRequestStats> callback);
    /**
     * Send general web notification.
     *
     * @param tenantId tenant UUID
     * @param recipients recipients ({@link UsersFilter})
     * @param template template ({@link NotificationTemplate})
     * @param info info ({@link GeneralNotificationInfo})
     * @throws Exception if an unexpected error occurs during processing
     */

    void sendGeneralWebNotification(TenantId tenantId, UsersFilter recipients, NotificationTemplate template, GeneralNotificationInfo info); // for future use
    /**
     * Send system notification.
     *
     * @param tenantId tenant UUID
     * @param targetId target id ({@link NotificationTargetId})
     * @param type type ({@link NotificationType})
     * @param info info ({@link NotificationInfo})
     * @throws Exception if an unexpected error occurs during processing
     */

    void sendSystemNotification(TenantId tenantId, NotificationTargetId targetId, NotificationType type, NotificationInfo info); // for future use and compatibility with PE
    /**
     * Deletes notification request.
     *
     * @param tenantId tenant UUID
     * @param notificationRequestId notification request id ({@link NotificationRequestId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteNotificationRequest(TenantId tenantId, NotificationRequestId notificationRequestId);
    /**
     * Mark notification as read.
     *
     * @param tenantId tenant UUID
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void markNotificationAsRead(TenantId tenantId, UserId recipientId, NotificationId notificationId);
    /**
     * Mark all notifications as read.
     *
     * @param tenantId tenant UUID
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void markAllNotificationsAsRead(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId);
    /**
     * Deletes notification.
     *
     * @param tenantId tenant UUID
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteNotification(TenantId tenantId, UserId recipientId, NotificationId notificationId);
    /**
     * Returns available delivery methods.
     *
     * @param tenantId tenant UUID
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<NotificationDeliveryMethod> getAvailableDeliveryMethods(TenantId tenantId);

}
