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

import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilterType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;


/**

 * Persistence contract for notification target.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (notification templates, targets, rules, and delivery requests).

 */


public interface NotificationTargetDao extends Dao<NotificationTarget>, TenantEntityDao<NotificationTarget>, ExportableEntityDao<NotificationTargetId, NotificationTarget> {
    /**
     * Finds by tenant id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationTarget> findByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink);
    /**
     * Finds by tenant id and supported notification type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationType notification type ({@link NotificationType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationTarget> findByTenantIdAndSupportedNotificationTypeAndPageLink(TenantId tenantId, NotificationType notificationType, PageLink pageLink);
    /**
     * Finds by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ids ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<NotificationTarget> findByTenantIdAndIds(TenantId tenantId, List<NotificationTargetId> ids);
    /**
     * Finds by tenant id and users filter type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filterType filter type ({@link UsersFilterType})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<NotificationTarget> findByTenantIdAndUsersFilterType(TenantId tenantId, UsersFilterType filterType);
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByTenantId(TenantId tenantId);

}
