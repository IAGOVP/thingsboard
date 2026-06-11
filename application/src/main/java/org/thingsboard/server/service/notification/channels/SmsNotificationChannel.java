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
package org.thingsboard.server.service.notification.channels;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thingsboard.rule.engine.api.SmsService;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.template.SmsDeliveryMethodNotificationTemplate;
import org.thingsboard.server.service.notification.NotificationProcessingContext;

    /**
     * Spring service component for sms notification channel (notification delivery, templates, targets, and rule-trigger processing).
     */

@Component
@RequiredArgsConstructor
public class SmsNotificationChannel implements NotificationChannel<User, SmsDeliveryMethodNotificationTemplate> {

    private final SmsService smsService;
    /**
     * Send notification.
     *
     * @param recipient recipient ({@link User})
     * @param processedTemplate processed template ({@link SmsDeliveryMethodNotificationTemplate})
     * @param ctx calculated-field execution context
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void sendNotification(User recipient, SmsDeliveryMethodNotificationTemplate processedTemplate, NotificationProcessingContext ctx) throws Exception {
        String phone = recipient.getPhone();
        if (StringUtils.isBlank(phone)) {
            throw new RuntimeException("User does not have phone number");
        }

        smsService.sendSms(ctx.getTenantId(), null, new String[]{phone}, processedTemplate.getBody());
    }
    /**
     * Checks the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void check(TenantId tenantId) throws Exception {
        if (!smsService.isConfigured(tenantId)) {
            throw new RuntimeException("SMS provider is not configured");
        }
    }
    /**
     * Returns delivery method.
     *
     * @return {@link NotificationDeliveryMethod}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationDeliveryMethod getDeliveryMethod() {
        return NotificationDeliveryMethod.SMS;
    }

}
