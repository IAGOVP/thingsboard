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
package org.thingsboard.server.actors.device;

import org.thingsboard.server.common.msg.MsgType;
import org.thingsboard.server.common.msg.TbActorMsg;

/**
 * Periodic tick message broadcast from {@link org.thingsboard.server.actors.app.AppActor} to detect stale device sessions.
 */

public class SessionTimeoutCheckMsg implements TbActorMsg {

    private static final SessionTimeoutCheckMsg INSTANCE = new SessionTimeoutCheckMsg();

    private SessionTimeoutCheckMsg() {
    }
    /**
     * Instance.
     *
     * @return {@link SessionTimeoutCheckMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static SessionTimeoutCheckMsg instance() {
        return INSTANCE;
    }
    
    /**
     * Returns the {@link org.thingsboard.server.common.msg.MsgType} discriminator for this message.
     *
     * @return {@link MsgType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MsgType getMsgType() {
        return MsgType.SESSION_TIMEOUT_MSG;
    }
}
