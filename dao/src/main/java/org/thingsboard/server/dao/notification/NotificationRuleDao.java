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

import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.NotificationRule;
import org.thingsboard.server.common.data.notification.rule.NotificationRuleInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;

import java.util.List;


/**

 * Persistence contract for notification rule.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (notification templates, targets, rules, and delivery requests).

 */


public interface NotificationRuleDao extends Dao<NotificationRule>, ExportableEntityDao<NotificationRuleId, NotificationRule> {
    /**
     * Finds by tenant id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationRule> findByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink);
    /**
     * Finds infos by tenant id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationRuleInfo> findInfosByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink);
    /**
     * Exists by tenant id and target id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param targetId target id ({@link NotificationTargetId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndTargetId(TenantId tenantId, NotificationTargetId targetId);
    /**
     * Finds by tenant id and trigger type and enabled.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param triggerType trigger type ({@link NotificationRuleTriggerType})
     * @param enabled enabled
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<NotificationRule> findByTenantIdAndTriggerTypeAndEnabled(TenantId tenantId, NotificationRuleTriggerType triggerType, boolean enabled);
    /**
     * Finds info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link NotificationRuleInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationRuleInfo findInfoById(TenantId tenantId, NotificationRuleId id);
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByTenantId(TenantId tenantId);

}
