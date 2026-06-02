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

import com.google.common.util.concurrent.FluentFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.NotificationStatus;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.dao.sql.query.EntityKeyMapping;

import java.util.Optional;
import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
/**
 * Default notification service.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService, EntityDaoService {

    private final NotificationDao notificationDao;

    /**

     * Persists notification.

     */

    @Override
    public Notification saveNotification(TenantId tenantId, Notification notification) {
        return notificationDao.save(tenantId, notification);
    }

    /**

     * Loads notification by id.

     */

    @Override
    public Notification findNotificationById(TenantId tenantId, NotificationId notificationId) {
        return notificationDao.findById(tenantId, notificationId.getId());
    }

    /**

     * Mark notification as read.

     */

    @Override
    public boolean markNotificationAsRead(TenantId tenantId, UserId recipientId, NotificationId notificationId) {
        return notificationDao.updateStatusByIdAndRecipientId(tenantId, recipientId, notificationId, NotificationStatus.READ);
    }

    /**

     * Mark all notifications as read.

     */

    @Override
    public int markAllNotificationsAsRead(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId) {
        return notificationDao.updateStatusByDeliveryMethodAndRecipientId(tenantId, deliveryMethod, recipientId, NotificationStatus.READ);
    }

    /**

     * Loads notifications by recipient id and read status.

     */

    @Override
    public PageData<Notification> findNotificationsByRecipientIdAndReadStatus(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, boolean unreadOnly, PageLink pageLink) {
        if (unreadOnly) {
            return notificationDao.findUnreadByDeliveryMethodAndRecipientIdAndPageLink(tenantId, deliveryMethod, recipientId, pageLink);
        } else {
            return notificationDao.findByDeliveryMethodAndRecipientIdAndPageLink(tenantId, deliveryMethod, recipientId, pageLink);
        }
    }

    /**

     * Loads latest unread notifications by recipient id and notification types.

     */

    @Override
    public PageData<Notification> findLatestUnreadNotificationsByRecipientIdAndNotificationTypes(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, Set<NotificationType> types, int limit) {
        SortOrder sortOrder = new SortOrder(EntityKeyMapping.CREATED_TIME, SortOrder.Direction.DESC);
        PageLink pageLink = new PageLink(limit, 0, null, sortOrder);
        return notificationDao.findUnreadByDeliveryMethodAndRecipientIdAndNotificationTypesAndPageLink(tenantId, deliveryMethod, recipientId, types, pageLink);
    }

    /**

     * Counts unread notifications by recipient id.

     */

    @Override
    public int countUnreadNotificationsByRecipientId(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId) {
        return notificationDao.countUnreadByDeliveryMethodAndRecipientId(tenantId, deliveryMethod, recipientId);
    }

    /**

     * Removes notification.

     */

    @Override
    public boolean deleteNotification(TenantId tenantId, UserId recipientId, NotificationId notificationId) {
        return notificationDao.deleteByIdAndRecipientId(tenantId, recipientId, notificationId);
    }

    /**

     * Loads entity.

     */

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findNotificationById(tenantId, new NotificationId(entityId.getId())));
    }

    /**

     * Loads entity async.

     */

    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(notificationDao.findByIdAsync(tenantId, entityId.getId()))
                .transform(Optional::ofNullable, directExecutor());
    }

    /**

     * Get entity type.

     */

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION;
    }

}
