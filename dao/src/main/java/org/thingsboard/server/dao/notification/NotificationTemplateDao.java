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
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;

import java.util.Collection;
import java.util.List;


/**

 * Persistence contract for notification template.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (notification templates, targets, rules, and delivery requests).

 */


public interface NotificationTemplateDao extends Dao<NotificationTemplate>, ExportableEntityDao<NotificationTemplateId, NotificationTemplate> {
    /**
     * Finds by tenant id and notification types and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTypes notification types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationTemplate> findByTenantIdAndNotificationTypesAndPageLink(TenantId tenantId, List<NotificationType> notificationTypes, PageLink pageLink);
    /**
     * Counts by tenant id and notification types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTypes notification types ({@link Collection})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int countByTenantIdAndNotificationTypes(TenantId tenantId, Collection<NotificationType> notificationTypes);
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByTenantId(TenantId tenantId);

}
