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
package org.thingsboard.server.service.ws.telemetry.cmd.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.thingsboard.server.service.subscription.SubscriptionErrorCode;
/**
 * Outbound WebSocket update payload for alarm status.
 * <p>Serialized to JSON and pushed to the client session that owns the subscription.
 */

@ToString
@Getter
public class AlarmStatusUpdate extends CmdUpdate {

    @Getter
    private boolean active;

    /**
     * Constructs {@link AlarmStatusUpdate} with the supplied dependencies and configuration.
     * @param cmdId client command id
     * @param active active
     */

    public AlarmStatusUpdate(int cmdId, boolean active) {
        super(cmdId, SubscriptionErrorCode.NO_ERROR.getCode(), null);
        this.active = active;
    }

    /**
     * Constructs {@link AlarmStatusUpdate} with the supplied dependencies and configuration.
     * @param cmdId client command id
     * @param errorCode subscription error code
     * @param errorMsg human-readable error detail
     */

    public AlarmStatusUpdate(int cmdId, int errorCode, String errorMsg) {
        super(cmdId, errorCode, errorMsg);
    }

    @Builder
    public AlarmStatusUpdate(@JsonProperty("cmdId") int cmdId,
                             @JsonProperty("present") boolean active,
                             @JsonProperty("errorCode") int errorCode,
                             @JsonProperty("errorMsg") String errorMsg) {
        super(cmdId, errorCode, errorMsg);
        this.active = active;
    }

    /**
     * Returns cmd update type.
     * @return {@link CmdUpdateType}
     */

    @Override
    public CmdUpdateType getCmdUpdateType() {
        return CmdUpdateType.ALARM_STATUS;
    }

}
