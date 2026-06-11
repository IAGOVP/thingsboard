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
 * Spring component for default notification service (notification templates, targets, rules, and delivery requests).
 */







@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService, EntityDaoService {

    private final NotificationDao notificationDao;

    
    /**
     * Saves or persists notification.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notification notification ({@link Notification})
     * @return {@link Notification}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Notification saveNotification(TenantId tenantId, Notification notification) {
        return notificationDao.save(tenantId, notification);
    }

    
    /**
     * Finds notification by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationId notification id ({@link NotificationId})
     * @return {@link Notification}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Notification findNotificationById(TenantId tenantId, NotificationId notificationId) {
        return notificationDao.findById(tenantId, notificationId.getId());
    }

    
    /**
     * Mark notification as read.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public boolean markNotificationAsRead(TenantId tenantId, UserId recipientId, NotificationId notificationId) {
        return notificationDao.updateStatusByIdAndRecipientId(tenantId, recipientId, notificationId, NotificationStatus.READ);
    }

    
    /**
     * Mark all notifications as read.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public int markAllNotificationsAsRead(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId) {
        return notificationDao.updateStatusByDeliveryMethodAndRecipientId(tenantId, deliveryMethod, recipientId, NotificationStatus.READ);
    }

    
    /**
     * Finds notifications by recipient id and read status.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param unreadOnly unread only
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
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
     * Finds latest unread notifications by recipient id and notification types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @param types types ({@link Set})
     * @param limit maximum number of records to return
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Notification> findLatestUnreadNotificationsByRecipientIdAndNotificationTypes(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, Set<NotificationType> types, int limit) {
        SortOrder sortOrder = new SortOrder(EntityKeyMapping.CREATED_TIME, SortOrder.Direction.DESC);
        PageLink pageLink = new PageLink(limit, 0, null, sortOrder);
        return notificationDao.findUnreadByDeliveryMethodAndRecipientIdAndNotificationTypesAndPageLink(tenantId, deliveryMethod, recipientId, types, pageLink);
    }

    
    /**
     * Counts unread notifications by recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public int countUnreadNotificationsByRecipientId(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId) {
        return notificationDao.countUnreadByDeliveryMethodAndRecipientId(tenantId, deliveryMethod, recipientId);
    }

    
    /**
     * Deletes notification.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public boolean deleteNotification(TenantId tenantId, UserId recipientId, NotificationId notificationId) {
        return notificationDao.deleteByIdAndRecipientId(tenantId, recipientId, notificationId);
    }

    
    /**
     * Finds entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return optional {@link HasId}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findNotificationById(tenantId, new NotificationId(entityId.getId())));
    }

    
    /**
     * Finds entity async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(notificationDao.findByIdAsync(tenantId, entityId.getId()))
                .transform(Optional::ofNullable, directExecutor());
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION;
    }

}
