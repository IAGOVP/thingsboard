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

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.info.RuleOriginatedNotificationInfo;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.targets.platform.PlatformUsersNotificationTargetConfig;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilterType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;

/**
 * Service API for notification target persistence and domain operations.
 */
public interface NotificationTargetService {

    /**
     * Saves or persists notification target.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTarget notification target ({@link NotificationTarget})
     * @return {@link NotificationTarget}
     */
    NotificationTarget saveNotificationTarget(TenantId tenantId, NotificationTarget notificationTarget);

    /**
     * Finds notification target by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationTargetId})
     * @return {@link NotificationTarget}
     */
    NotificationTarget findNotificationTargetById(TenantId tenantId, NotificationTargetId id);

    /**
     * Finds notification targets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationTarget> findNotificationTargetsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds notification targets by tenant id and supported notification type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationType notification type ({@link NotificationType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationTarget> findNotificationTargetsByTenantIdAndSupportedNotificationType(TenantId tenantId, NotificationType notificationType, PageLink pageLink);

    /**
     * Finds notification targets by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ids ids ({@link List})
     * @return {@link List}
     */
    List<NotificationTarget> findNotificationTargetsByTenantIdAndIds(TenantId tenantId, List<NotificationTargetId> ids);

    /**
     * Finds notification targets by tenant id and users filter type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filterType filter type ({@link UsersFilterType})
     * @return {@link List}
     */
    List<NotificationTarget> findNotificationTargetsByTenantIdAndUsersFilterType(TenantId tenantId, UsersFilterType filterType);

    /**
     * Finds recipients for notification target.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param targetId target id ({@link NotificationTargetId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findRecipientsForNotificationTarget(TenantId tenantId, CustomerId customerId, NotificationTargetId targetId, PageLink pageLink);

    /**
     * Finds recipients for notification target config.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param targetConfig target config ({@link PlatformUsersNotificationTargetConfig})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findRecipientsForNotificationTargetConfig(TenantId tenantId, PlatformUsersNotificationTargetConfig targetConfig, PageLink pageLink);

    /**
     * Finds recipients for rule notification target config.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param targetConfig target config ({@link PlatformUsersNotificationTargetConfig})
     * @param info info ({@link RuleOriginatedNotificationInfo})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<User> findRecipientsForRuleNotificationTargetConfig(TenantId tenantId, PlatformUsersNotificationTargetConfig targetConfig, RuleOriginatedNotificationInfo info, PageLink pageLink);

    /**
     * Deletes notification target by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationTargetId})
     */
    void deleteNotificationTargetById(TenantId tenantId, NotificationTargetId id);

    /**
     * Deletes notification targets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteNotificationTargetsByTenantId(TenantId tenantId);

    /**
     * Counts notification targets by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the long result
     */
    long countNotificationTargetsByTenantId(TenantId tenantId);

}
