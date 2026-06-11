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
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.NotificationRule;
import org.thingsboard.server.common.data.notification.rule.NotificationRuleInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;

/**
 * Service API for notification rule persistence and domain operations.
 */
public interface NotificationRuleService {

    /**
     * Saves or persists notification rule.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationRule notification rule ({@link NotificationRule})
     * @return {@link NotificationRule}
     */
    NotificationRule saveNotificationRule(TenantId tenantId, NotificationRule notificationRule);

    /**
     * Finds notification rule by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationRuleId})
     * @return {@link NotificationRule}
     */
    NotificationRule findNotificationRuleById(TenantId tenantId, NotificationRuleId id);

    /**
     * Finds notification rule info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationRuleId})
     * @return {@link NotificationRuleInfo}
     */
    NotificationRuleInfo findNotificationRuleInfoById(TenantId tenantId, NotificationRuleId id);

    /**
     * Finds notification rules infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationRuleInfo> findNotificationRulesInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds notification rules by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationRule> findNotificationRulesByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds enabled notification rules by tenant id and trigger type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param triggerType trigger type ({@link NotificationRuleTriggerType})
     * @return {@link List}
     */
    List<NotificationRule> findEnabledNotificationRulesByTenantIdAndTriggerType(TenantId tenantId, NotificationRuleTriggerType triggerType);

    /**
     * Deletes notification rule by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationRuleId})
     */
    void deleteNotificationRuleById(TenantId tenantId, NotificationRuleId id);

    /**
     * Deletes notification rules by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteNotificationRulesByTenantId(TenantId tenantId);

}
