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
import org.springframework.stereotype.Component;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.rule.engine.api.TbEmail;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.template.EmailDeliveryMethodNotificationTemplate;
import org.thingsboard.server.service.notification.NotificationProcessingContext;

    /**
     * Spring service component for email notification channel (notification delivery, templates, targets, and rule-trigger processing).
     */

@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel<User, EmailDeliveryMethodNotificationTemplate> {

    private final MailService mailService;
    /**
     * Send notification.
     *
     * @param recipient recipient ({@link User})
     * @param processedTemplate processed template ({@link EmailDeliveryMethodNotificationTemplate})
     * @param ctx calculated-field execution context
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void sendNotification(User recipient, EmailDeliveryMethodNotificationTemplate processedTemplate, NotificationProcessingContext ctx) throws Exception {
        mailService.send(ctx.getTenantId(), null, TbEmail.builder()
                .to(recipient.getEmail())
                .subject(processedTemplate.getSubject())
                .body(processedTemplate.getBody())
                .html(true)
                .build());
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
        if (!mailService.isConfigured(tenantId)) {
            throw new RuntimeException("Mail server is not configured");
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
        return NotificationDeliveryMethod.EMAIL;
    }

}
