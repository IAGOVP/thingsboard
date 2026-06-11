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
package org.thingsboard.server.service.ws;

import org.springframework.web.socket.CloseStatus;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.service.subscription.SubscriptionErrorCode;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.CmdUpdate;
import org.thingsboard.server.service.ws.telemetry.sub.TelemetrySubscriptionUpdate;

/**
 * Service API for the ThingsBoard WebSocket plugin ({@code /api/ws}). Routes session lifecycle events, inbound commands, subscription updates, and errors to connected UI clients.
 */

public interface WebSocketService {

    /**
     * Handles session event.
     * @param sessionRef reference to the WebSocket session
     * @param sessionEvent session event
     */

    void handleSessionEvent(WebSocketSessionRef sessionRef, SessionEvent sessionEvent);

    /**
     * Handles commands.
     * @param sessionRef reference to the WebSocket session
     * @param commandsWrapper batch of inbound WebSocket commands
     */

    void handleCommands(WebSocketSessionRef sessionRef, WsCommandsWrapper commandsWrapper);

    /**
     * Sends update.
     * @param sessionId WebSocket session identifier
     * @param cmdId client command id
     * @param update subscription update payload
     */

    void sendUpdate(String sessionId, int cmdId, TelemetrySubscriptionUpdate update);

    /**
     * Sends update.
     * @param sessionId WebSocket session identifier
     * @param update subscription update payload
     */

    void sendUpdate(String sessionId, CmdUpdate update);

    /**
     * Sends error.
     * @param sessionRef reference to the WebSocket session
     * @param subId sub id
     * @param errorCode subscription error code
     * @param errorMsg human-readable error detail
     */

    void sendError(WebSocketSessionRef sessionRef, int subId, SubscriptionErrorCode errorCode, String errorMsg);

    /**
     * Closes close.
     * @param sessionId WebSocket session identifier
     * @param status WebSocket close status
     */

    void close(String sessionId, CloseStatus status);

    /**
     * Cleans up if stale.
     * @param tenantId tenant that owns the subscription or entity
     * @param sessionId WebSocket session identifier
     */

    void cleanupIfStale(TenantId tenantId, String sessionId);

}
