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
package org.thingsboard.server.service.subscription;

import org.thingsboard.server.service.ws.WebSocketSessionRef;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.AlarmCountCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.AlarmDataCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.AlarmStatusCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.EntityCountCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.EntityDataCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.UnsubscribeCmd;

/**
 * Complex entity-data WebSocket subscriptions (v2 API): entity queries, alarm data, counts, and aggregated time-series.
 */

public interface TbEntityDataSubscriptionService {

    /**
     * Handles cmd.
     * @param sessionId WebSocket session identifier
     * @param cmd cmd
     */

    void handleCmd(WebSocketSessionRef sessionId, EntityDataCmd cmd);

    /**
     * Handles cmd.
     * @param sessionId WebSocket session identifier
     * @param cmd cmd
     */

    void handleCmd(WebSocketSessionRef sessionId, EntityCountCmd cmd);

    /**
     * Handles cmd.
     * @param sessionId WebSocket session identifier
     * @param cmd cmd
     */

    void handleCmd(WebSocketSessionRef sessionId, AlarmDataCmd cmd);

    /**
     * Handles cmd.
     * @param sessionId WebSocket session identifier
     * @param cmd cmd
     */

    void handleCmd(WebSocketSessionRef sessionId, AlarmCountCmd cmd);

    /**
     * Handles cmd.
     * @param session session
     * @param cmd cmd
     */

    void handleCmd(WebSocketSessionRef session, AlarmStatusCmd cmd);

    /**
     * Cancels subscription.
     * @param sessionId WebSocket session identifier
     * @param subscriptionId client command/subscription id
     */

    void cancelSubscription(String sessionId, UnsubscribeCmd subscriptionId);

    /**
     * Cancels all session subscriptions.
     * @param sessionId WebSocket session identifier
     */

    void cancelAllSessionSubscriptions(String sessionId);

}
