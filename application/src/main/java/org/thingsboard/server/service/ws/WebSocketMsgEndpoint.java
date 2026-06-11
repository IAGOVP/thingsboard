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

import java.io.IOException;

/**
 * Abstraction for sending text/binary WebSocket frames to a connected client session.
 */

public interface WebSocketMsgEndpoint {

    /**
     * Sends send.
     * @param sessionRef reference to the WebSocket session
     * @param subscriptionId client command/subscription id
     * @param msg queue or transport message
     * @throws IOException if processing fails
     */

    void send(WebSocketSessionRef sessionRef, int subscriptionId, String msg) throws IOException;

    /**
     * Sends ping.
     * @param sessionRef reference to the WebSocket session
     * @param currentTime current time
     * @throws IOException if processing fails
     */

    void sendPing(WebSocketSessionRef sessionRef, long currentTime) throws IOException;

    /**
     * Closes close.
     * @param sessionRef reference to the WebSocket session
     * @param withReason with reason
     * @throws IOException if processing fails
     */

    void close(WebSocketSessionRef sessionRef, CloseStatus withReason) throws IOException;

    /**
     * Is open.
     * @param sessionId WebSocket session identifier
     * @return {@code true} when the condition holds
     */

    boolean isOpen(String sessionId);
}
