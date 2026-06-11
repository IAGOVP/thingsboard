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
package org.thingsboard.monitoring.notification.channels;

import org.thingsboard.monitoring.data.notification.Notification;


/**

 * Outbound notification sink contract (Slack webhook, etc.).

 */


public interface NotificationChannel {
    /**
     * Dispatches a notification to all configured channels asynchronously.
     *
     * @param message Slack or notification message body
     * @param notification alert payload to deliver to notification channels
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void sendNotification(String message, Notification notification);

}
