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

/**
 * Typed outbound WebSocket message queued by {@link TbWebSocketHandler.SessionMetaData}.
 *
 * @param <T> payload type ({@link String} for text, {@link java.nio.ByteBuffer} for ping)
 */
public interface TbWebSocketMsg<T> {

    /**
     * Returns the wire format discriminator for this message.
     *
     * @return {@link TbWebSocketMsgType#TEXT} or {@link TbWebSocketMsgType#PING}
     */
    TbWebSocketMsgType getType();

    /**
     * Returns the message payload to send on the wire.
     *
     * @return text JSON or empty ping buffer
     */
    T getMsg();

}
