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

import lombok.Data;
/**
 * Outbound WebSocket update payload for notifications subscription.
 * <p>Serialized to JSON and pushed to the client session that owns the subscription.
 */

@Data
public class NotificationsSubscriptionUpdate {

    private final NotificationUpdate notificationUpdate;
    private final NotificationRequestUpdate notificationRequestUpdate;

    /**
     * Constructs {@link NotificationsSubscriptionUpdate} with the supplied dependencies and configuration.
     * @param notificationUpdate notification update
     */

    public NotificationsSubscriptionUpdate(NotificationUpdate notificationUpdate) {
        this.notificationUpdate = notificationUpdate;
        this.notificationRequestUpdate = null;
    }

    /**
     * Constructs {@link NotificationsSubscriptionUpdate} with the supplied dependencies and configuration.
     * @param notificationRequestUpdate notification request update
     */

    public NotificationsSubscriptionUpdate(NotificationRequestUpdate notificationRequestUpdate) {
        this.notificationUpdate = null;
        this.notificationRequestUpdate = notificationRequestUpdate;
    }

}
