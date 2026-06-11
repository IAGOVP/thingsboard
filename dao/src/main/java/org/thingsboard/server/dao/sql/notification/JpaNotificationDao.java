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
package org.thingsboard.server.dao.sql.notification;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
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
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.sql.NotificationEntity;
import org.thingsboard.server.dao.notification.NotificationDao;
import org.thingsboard.server.dao.sql.JpaPartitionedAbstractDao;
import org.thingsboard.server.dao.sqlts.insert.sql.SqlPartitioningRepository;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
/**
 * JPA/PostgreSQL implementation of notification dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
@RequiredArgsConstructor
public class JpaNotificationDao extends JpaPartitionedAbstractDao<NotificationEntity, Notification> implements NotificationDao {

    private final NotificationRepository notificationRepository;
    private final SqlPartitioningRepository partitioningRepository;

    @Value("${sql.notifications.partition_size:168}")
    private int partitionSizeInHours;
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

    @Override
    public PageData<Notification> findUnreadByDeliveryMethodAndRecipientIdAndPageLink(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRepository.findByDeliveryMethodAndRecipientIdAndStatusNot(deliveryMethod,
                recipientId.getId(), NotificationStatus.READ, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }
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

    @Override
    public PageData<Notification> findUnreadByDeliveryMethodAndRecipientIdAndNotificationTypesAndPageLink(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, Set<NotificationType> types, PageLink pageLink) {
        if (CollectionUtils.isEmpty(types)) {
            return findUnreadByDeliveryMethodAndRecipientIdAndPageLink(tenantId, deliveryMethod, recipientId, pageLink);
        }
        return DaoUtil.toPageData(notificationRepository.findByDeliveryMethodAndRecipientIdAndTypeInAndStatusNot(deliveryMethod,
                recipientId.getId(), types, NotificationStatus.READ, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }
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

    public PageData<Notification> findByDeliveryMethodAndRecipientIdAndPageLink(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRepository.findByDeliveryMethodAndRecipientId(deliveryMethod, recipientId.getId(),
                pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }
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

    @Override
    public boolean updateStatusByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId, NotificationStatus status) {
        return notificationRepository.updateStatusByIdAndRecipientId(notificationId.getId(), recipientId.getId(), status) != 0;
    }

    
    /**
     * Counts unread by delivery method and recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deliveryMethod delivery method ({@link NotificationDeliveryMethod})
     * @param recipientId recipient id ({@link UserId})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int countUnreadByDeliveryMethodAndRecipientId(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId) {
        return notificationRepository.countByDeliveryMethodAndRecipientIdAndStatusNot(deliveryMethod, recipientId.getId(), NotificationStatus.READ);
    }
    /**
     * Deletes by id and recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @param notificationId notification id ({@link NotificationId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean deleteByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId) {
        return notificationRepository.deleteByIdAndRecipientId(notificationId.getId(), recipientId.getId()) != 0;
    }
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

    @Override
    public int updateStatusByDeliveryMethodAndRecipientId(TenantId tenantId, NotificationDeliveryMethod deliveryMethod, UserId recipientId, NotificationStatus status) {
        return notificationRepository.updateStatusByDeliveryMethodAndRecipientIdAndStatusNot(deliveryMethod, recipientId.getId(), status);
    }
    /**
     * Deletes by request id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param requestId request id ({@link NotificationRequestId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void deleteByRequestId(TenantId tenantId, NotificationRequestId requestId) {
        notificationRepository.deleteByRequestId(requestId.getId());
    }
    /**
     * Deletes by recipient id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param recipientId recipient id ({@link UserId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void deleteByRecipientId(TenantId tenantId, UserId recipientId) {
        notificationRepository.deleteByRecipientId(recipientId.getId());
    }
    /**
     * Creates partition.
     *
     * @param entity domain entity to persist or validate
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void createPartition(NotificationEntity entity) {
        partitioningRepository.createPartitionIfNotExists(ModelConstants.NOTIFICATION_TABLE_NAME,
                entity.getCreatedTime(), TimeUnit.HOURS.toMillis(partitionSizeInHours));
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<NotificationEntity> getEntityClass() {
        return NotificationEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<NotificationEntity, UUID> getRepository() {
        return notificationRepository;
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
