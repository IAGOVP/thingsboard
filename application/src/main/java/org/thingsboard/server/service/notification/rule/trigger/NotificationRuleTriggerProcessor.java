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
package org.thingsboard.server.service.notification.rule.trigger;

import org.thingsboard.server.common.data.notification.info.RuleOriginatedNotificationInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.NotificationRuleTrigger;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerConfig;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;

/**

 * notification rule trigger processor contract for notification delivery, templates, targets, and rule-trigger processing.

 */

public interface NotificationRuleTriggerProcessor<T extends NotificationRuleTrigger, C extends NotificationRuleTriggerConfig> {

    boolean matchesFilter(T trigger, C triggerConfig);

    /**
     * Matches clear rule.
     *
     * @param trigger trigger ({@link T})
     * @param triggerConfig trigger config ({@link C})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    default boolean matchesClearRule(T trigger, C triggerConfig) {
        return false;
    }
/**
 * Construct notification info.
 *
 * @param trigger trigger ({@link T})
 * @return {@link RuleOriginatedNotificationInfo}
 * @throws Exception if an unexpected error occurs during processing
 */

    RuleOriginatedNotificationInfo constructNotificationInfo(T trigger);

    /**
     * Returns trigger type.
     *
     * @return {@link NotificationRuleTriggerType}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationRuleTriggerType getTriggerType();

}
