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
import org.apache.commons.collections4.CollectionUtils;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.service.subscription.TbSubscription;
import org.thingsboard.server.service.subscription.TbSubscriptionType;
import org.thingsboard.server.service.ws.notification.cmd.UnreadNotificationsUpdate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
/**
 * Subscription state holder for notifications.
 * <p>Links a WebSocket session to entity keys and update processors.
 */

@Getter
public class NotificationsSubscription extends AbstractNotificationSubscription<NotificationsSubscriptionUpdate> {

    private final Map<UUID, Notification> latestUnreadNotifications = new HashMap<>();
    private final int limit;
    private final Set<NotificationType> notificationTypes;

    /**
     * Constructs {@link NotificationsSubscription} with the supplied dependencies and configuration.
     * @param serviceId service id
     * @param sessionId WebSocket session identifier
     * @param subscriptionId client command/subscription id
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param updateProcessor update processor
     * @param limit limit
     * @param notificationTypes notification types
     * @return @Builder
    public
     */

    @Builder
    public NotificationsSubscription(String serviceId, String sessionId, int subscriptionId, TenantId tenantId, EntityId entityId,
                                     BiConsumer<TbSubscription<NotificationsSubscriptionUpdate>, NotificationsSubscriptionUpdate> updateProcessor,
                                     int limit, Set<NotificationType> notificationTypes) {
        super(serviceId, sessionId, subscriptionId, tenantId, entityId, TbSubscriptionType.NOTIFICATIONS, updateProcessor);
        this.limit = limit;
        this.notificationTypes = notificationTypes;
    }

    /**
     * Checks notification type.
     * @param type type
     * @return boolean result
     */

    public boolean checkNotificationType(NotificationType type) {
        return CollectionUtils.isEmpty(notificationTypes) || notificationTypes.contains(type);
    }

    /**
     * Creates full update.
     * @return {@link UnreadNotificationsUpdate}
     */

    public UnreadNotificationsUpdate createFullUpdate() {
        return UnreadNotificationsUpdate.builder()
                .cmdId(getSubscriptionId())
                .notifications(getSortedNotifications())
                .totalUnreadCount(totalUnreadCounter.get())
                .sequenceNumber(sequence.incrementAndGet())
                .build();
    }

    /**
     * Returns sorted notifications.
     * @return {@link List}
     */

    public List<Notification> getSortedNotifications() {
        return latestUnreadNotifications.values().stream()
                .sorted(Comparator.comparing(BaseData::getCreatedTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * Creates partial update.
     * @param notification notification
     * @return {@link UnreadNotificationsUpdate}
     */

    public UnreadNotificationsUpdate createPartialUpdate(Notification notification) {
        return UnreadNotificationsUpdate.builder()
                .cmdId(getSubscriptionId())
                .update(notification)
                .totalUnreadCount(totalUnreadCounter.get())
                .sequenceNumber(sequence.incrementAndGet())
                .build();
    }

    /**
     * Creates count update.
     * @return {@link UnreadNotificationsUpdate}
     */

    public UnreadNotificationsUpdate createCountUpdate() {
        return UnreadNotificationsUpdate.builder()
                .cmdId(getSubscriptionId())
                .totalUnreadCount(totalUnreadCounter.get())
                .sequenceNumber(sequence.incrementAndGet())
                .build();
    }

}
