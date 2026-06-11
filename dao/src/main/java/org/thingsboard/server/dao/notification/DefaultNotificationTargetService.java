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
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.NotificationRequestStatus;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.info.RuleOriginatedNotificationInfo;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.targets.NotificationTargetConfig;
import org.thingsboard.server.common.data.notification.targets.platform.PlatformUsersNotificationTargetConfig;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilter;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilterType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
/**
 * Spring component for default notification target service (notification templates, targets, rules, and delivery requests).
 */







@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultNotificationTargetService extends AbstractEntityService implements NotificationTargetService, EntityDaoService {

    private final NotificationTargetDao notificationTargetDao;
    private final NotificationRequestDao notificationRequestDao;
    private final NotificationRuleDao notificationRuleDao;
    private final UserService userService;

    
    /**
     * Saves or persists notification target.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTarget notification target ({@link NotificationTarget})
     * @return {@link NotificationTarget}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public NotificationTarget saveNotificationTarget(TenantId tenantId, NotificationTarget notificationTarget) {
        try {
            NotificationTarget savedTarget = notificationTargetDao.saveAndFlush(tenantId, notificationTarget);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId).entityId(savedTarget.getId())
                    .created(notificationTarget.getId() == null).build());
            return savedTarget;
        } catch (Exception e) {
            checkConstraintViolation(e, "uq_notification_target_name", "Recipients group with such name already exists");
            throw e;
        }
    }

    
    /**
     * Finds notification target by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link NotificationTarget}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public NotificationTarget findNotificationTargetById(TenantId tenantId, NotificationTargetId id) {
        return notificationTargetDao.findById(tenantId, id.getId());
    }

    
    /**
     * Finds notification targets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<NotificationTarget> findNotificationTargetsByTenantId(TenantId tenantId, PageLink pageLink) {
        return notificationTargetDao.findByTenantIdAndPageLink(tenantId, pageLink);
    }

    
    /**
     * Finds notification targets by tenant id and supported notification type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationType notification type ({@link NotificationType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<NotificationTarget> findNotificationTargetsByTenantIdAndSupportedNotificationType(TenantId tenantId, NotificationType notificationType, PageLink pageLink) {
        return notificationTargetDao.findByTenantIdAndSupportedNotificationTypeAndPageLink(tenantId, notificationType, pageLink);
    }

    
    /**
     * Finds notification targets by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ids ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<NotificationTarget> findNotificationTargetsByTenantIdAndIds(TenantId tenantId, List<NotificationTargetId> ids) {
        return notificationTargetDao.findByTenantIdAndIds(tenantId, ids);
    }

    
    /**
     * Finds notification targets by tenant id and users filter type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filterType filter type ({@link UsersFilterType})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<NotificationTarget> findNotificationTargetsByTenantIdAndUsersFilterType(TenantId tenantId, UsersFilterType filterType) {
        return notificationTargetDao.findByTenantIdAndUsersFilterType(tenantId, filterType);
    }

    
    /**
     * Finds recipients for notification target.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param targetId target id ({@link NotificationTargetId})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<User> findRecipientsForNotificationTarget(TenantId tenantId, CustomerId customerId, NotificationTargetId targetId, PageLink pageLink) {
        NotificationTarget notificationTarget = findNotificationTargetById(tenantId, targetId);
        Objects.requireNonNull(notificationTarget, "Notification target [" + targetId + "] not found");
        NotificationTargetConfig configuration = notificationTarget.getConfiguration();
        return findRecipientsForNotificationTargetConfig(notificationTarget.getTenantId(), (PlatformUsersNotificationTargetConfig) configuration, pageLink);
    }

    
    /**
     * Finds recipients for notification target config.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param targetConfig target config ({@link PlatformUsersNotificationTargetConfig})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<User> findRecipientsForNotificationTargetConfig(TenantId tenantId, PlatformUsersNotificationTargetConfig targetConfig, PageLink pageLink) {
        UsersFilter usersFilter = targetConfig.getUsersFilter();
        return userService.findUsersByFilter(tenantId, usersFilter, pageLink);
    }

    
    /**
     * Finds recipients for rule notification target config.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param targetConfig target config ({@link PlatformUsersNotificationTargetConfig})
     * @param info info ({@link RuleOriginatedNotificationInfo})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<User> findRecipientsForRuleNotificationTargetConfig(TenantId tenantId, PlatformUsersNotificationTargetConfig targetConfig, RuleOriginatedNotificationInfo info, PageLink pageLink) {
        switch (targetConfig.getUsersFilter().getType()) {
            case ORIGINATOR_ENTITY_OWNER_USERS -> {
                CustomerId customerId = info.getAffectedCustomerId();
                if (customerId != null && !customerId.isNullUid()) {
                    return userService.findCustomerUsers(tenantId, customerId, pageLink);
                } else {
                    return userService.findTenantAdmins(tenantId, pageLink);
                }
            }
            case AFFECTED_USER -> {
                UserId userId = info.getAffectedUserId();
                if (userId != null) {
                    return new PageData<>(List.of(userService.findUserById(tenantId, userId)), 1, 1, false);
                }
            }
            case AFFECTED_TENANT_ADMINISTRATORS -> {
                TenantId affectedTenantId = info.getAffectedTenantId();
                if (affectedTenantId == null) {
                    affectedTenantId = tenantId;
                }
                if (!affectedTenantId.isNullUid()) {
                    return userService.findTenantAdmins(affectedTenantId, pageLink);
                }
            }
            default -> throw new IllegalArgumentException("Recipient type not supported");
        }
        return new PageData<>();
    }

    
    /**
     * Deletes notification target by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteNotificationTargetById(TenantId tenantId, NotificationTargetId id) {
        deleteEntity(tenantId, id, false);
    }

    
    /**
     * Deletes entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param force force
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        NotificationTargetId targetId = (NotificationTargetId) id;
        if (!force && notificationRequestDao.existsByTenantIdAndStatusAndTargetId(tenantId, NotificationRequestStatus.SCHEDULED, targetId)) {
            throw new IllegalArgumentException("Recipients group is referenced by scheduled notification request");
        }
        if (!force && notificationRuleDao.existsByTenantIdAndTargetId(tenantId, targetId)) {
            throw new IllegalArgumentException("Recipients group is being used in notification rule");
        }
        notificationTargetDao.removeById(tenantId, id.getId());
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(id).build());
    }

    
    /**
     * Deletes notification targets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteNotificationTargetsByTenantId(TenantId tenantId) {
        notificationTargetDao.removeByTenantId(tenantId);
    }

    
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteByTenantId(TenantId tenantId) {
        deleteNotificationTargetsByTenantId(tenantId);
    }

    
    /**
     * Counts notification targets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public long countNotificationTargetsByTenantId(TenantId tenantId) {
        return notificationTargetDao.countByTenantId(tenantId);
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
        return Optional.ofNullable(findNotificationTargetById(tenantId, new NotificationTargetId(entityId.getId())));
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
        return FluentFuture.from(notificationTargetDao.findByIdAsync(tenantId, entityId.getId()))
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
        return EntityType.NOTIFICATION_TARGET;
    }

}
