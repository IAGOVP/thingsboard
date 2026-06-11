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
package org.thingsboard.server.service.ws.notification.sub;

import lombok.Builder;
import lombok.Getter;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.service.subscription.TbSubscription;
import org.thingsboard.server.service.subscription.TbSubscriptionType;
import org.thingsboard.server.service.ws.notification.cmd.UnreadNotificationsCountUpdate;

import java.util.function.BiConsumer;
/**
 * Subscription state holder for notifications count.
 * <p>Links a WebSocket session to entity keys and update processors.
 */

@Getter
public class NotificationsCountSubscription extends AbstractNotificationSubscription<NotificationsSubscriptionUpdate> {

    /**
     * Constructs {@link NotificationsCountSubscription} with the supplied dependencies and configuration.
     * @param serviceId service id
     * @param sessionId WebSocket session identifier
     * @param subscriptionId client command/subscription id
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param updateProcessor update processor
     * @return @Builder
    public
     */

    @Builder
    public NotificationsCountSubscription(String serviceId, String sessionId, int subscriptionId, TenantId tenantId, EntityId entityId,
                                          BiConsumer<TbSubscription<NotificationsSubscriptionUpdate>, NotificationsSubscriptionUpdate> updateProcessor) {
        super(serviceId, sessionId, subscriptionId, tenantId, entityId, TbSubscriptionType.NOTIFICATIONS_COUNT, updateProcessor);
    }

    /**
     * Creates update.
     * @return {@link UnreadNotificationsCountUpdate}
     */

    public UnreadNotificationsCountUpdate createUpdate() {
        return UnreadNotificationsCountUpdate.builder()
                .cmdId(getSubscriptionId())
                .totalUnreadCount(totalUnreadCounter.get())
                .sequenceNumber(sequence.incrementAndGet())
                .build();
    }

}
