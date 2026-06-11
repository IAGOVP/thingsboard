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


/**
 * Per-session metadata tracked by {@link DefaultWebSocketService} (auth state, ping counters, command ids).
 */


public class WsSessionMetaData {
    private WebSocketSessionRef sessionRef;
    private long lastActivityTime;

    /**
     * Constructs {@link WsSessionMetaData} with the supplied dependencies and configuration.
     * @param sessionRef reference to the WebSocket session
     */

    public WsSessionMetaData(WebSocketSessionRef sessionRef) {
        super();
        this.sessionRef = sessionRef;
        this.lastActivityTime = System.currentTimeMillis();
    }

    /**
     * Returns session ref.
     * @return {@link WebSocketSessionRef}
     */

    public WebSocketSessionRef getSessionRef() {
        return sessionRef;
    }

    /**
     * Sets session ref.
     * @param sessionRef reference to the WebSocket session
     */

    public void setSessionRef(WebSocketSessionRef sessionRef) {
        this.sessionRef = sessionRef;
    }

    /**
     * Returns last activity time.
     * @return numeric result
     */

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    /**
     * Sets last activity time.
     * @param lastActivityTime last activity time
     */

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    /**
     * To string.
     * @return string value
     */

    @Override
    public String toString() {
        return "WsSessionMetaData [sessionRef=" + sessionRef + ", lastActivityTime=" + lastActivityTime + "]";
    }
}
