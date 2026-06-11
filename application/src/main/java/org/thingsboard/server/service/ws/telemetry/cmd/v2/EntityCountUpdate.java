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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.thingsboard.server.service.subscription.SubscriptionErrorCode;
/**
 * Outbound WebSocket update payload for entity count.
 * <p>Serialized to JSON and pushed to the client session that owns the subscription.
 */

@ToString
public class EntityCountUpdate extends CmdUpdate {

    @Getter
    private int count;

    /**
     * Constructs {@link EntityCountUpdate} with the supplied dependencies and configuration.
     * @param cmdId client command id
     * @param count count
     */

    public EntityCountUpdate(int cmdId, int count) {
        super(cmdId, SubscriptionErrorCode.NO_ERROR.getCode(), null);
        this.count = count;
    }

    /**
     * Constructs {@link EntityCountUpdate} with the supplied dependencies and configuration.
     * @param cmdId client command id
     * @param errorCode subscription error code
     * @param errorMsg human-readable error detail
     */

    public EntityCountUpdate(int cmdId, int errorCode, String errorMsg) {
        super(cmdId, errorCode, errorMsg);
    }

    /**
     * Returns cmd update type.
     * @return {@link CmdUpdateType}
     */

    @Override
    public CmdUpdateType getCmdUpdateType() {
        return CmdUpdateType.COUNT_DATA;
    }

    @JsonCreator
    public EntityCountUpdate(@JsonProperty("cmdId") int cmdId,
                             @JsonProperty("count") int count,
                             @JsonProperty("errorCode") int errorCode,
                             @JsonProperty("errorMsg") String errorMsg) {
        super(cmdId, errorCode, errorMsg);
        this.count = count;
    }

}
