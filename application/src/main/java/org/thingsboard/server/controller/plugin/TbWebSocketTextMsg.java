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
package org.thingsboard.server.controller.plugin;

import lombok.RequiredArgsConstructor;

/**
 * UTF-8 text WebSocket message (typically JSON subscription data).
 *
 * @see TbWebSocketHandler#send(org.thingsboard.server.service.ws.WebSocketSessionRef, int, String)
 */
@RequiredArgsConstructor
public class TbWebSocketTextMsg implements TbWebSocketMsg<String> {

    private final String value;

    /**
     * {@inheritDoc}
     *
     * @return {@link TbWebSocketMsgType#TEXT}
     */
    @Override
    public TbWebSocketMsgType getType() {
        return TbWebSocketMsgType.TEXT;
    }

    /**
     * {@inheritDoc}
     *
     * @return JSON text payload
     */
    @Override
    public String getMsg() {
        return value;
    }
}
