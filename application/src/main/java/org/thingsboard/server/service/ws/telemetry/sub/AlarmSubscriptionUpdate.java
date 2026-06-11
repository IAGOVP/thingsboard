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
package org.thingsboard.server.service.ws.telemetry.sub;

import lombok.Getter;
import lombok.ToString;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.service.subscription.SubscriptionErrorCode;
/**
 * Outbound WebSocket update payload for alarm subscription.
 * <p>Serialized to JSON and pushed to the client session that owns the subscription.
 */

@ToString
public class AlarmSubscriptionUpdate {

    @Getter
    private int errorCode;
    @Getter
    private String errorMsg;
    @Getter
    private AlarmInfo alarm;
    @Getter
    private boolean alarmDeleted;

    /**
     * Constructs {@link AlarmSubscriptionUpdate} with the supplied dependencies and configuration.
     * @param alarm alarm
     */

    public AlarmSubscriptionUpdate(AlarmInfo alarm) {
        this(alarm, false);
    }

    /**
     * Constructs {@link AlarmSubscriptionUpdate} with the supplied dependencies and configuration.
     * @param alarm alarm
     * @param alarmDeleted alarm deleted
     */

    public AlarmSubscriptionUpdate(AlarmInfo alarm, boolean alarmDeleted) {
        super();
        this.alarm = alarm;
        this.alarmDeleted = alarmDeleted;
    }

    /**
     * Constructs {@link AlarmSubscriptionUpdate} with the supplied dependencies and configuration.
     * @param errorCode subscription error code
     */

    public AlarmSubscriptionUpdate(SubscriptionErrorCode errorCode) {
        this(errorCode, null);
    }

    /**
     * Constructs {@link AlarmSubscriptionUpdate} with the supplied dependencies and configuration.
     * @param errorCode subscription error code
     * @param errorMsg human-readable error detail
     */

    public AlarmSubscriptionUpdate(SubscriptionErrorCode errorCode, String errorMsg) {
        super();
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorMsg != null ? errorMsg : errorCode.getDefaultMsg();
    }

}