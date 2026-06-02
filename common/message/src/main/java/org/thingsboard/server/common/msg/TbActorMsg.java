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
package org.thingsboard.server.common.msg;

/**
 * Message delivered to a {@link org.thingsboard.server.actors.TbActor} mailbox.
 *
 * <p>Each implementation declares its {@link MsgType} for routing/logging. Actors process messages
 * serially per actor instance. See {@code application} actors (device, rule chain, calculated field).
 */
public interface TbActorMsg {

    /** Discriminator used by processors (e.g. {@code switch (msg.getMsgType())}). */
    MsgType getMsgType();

    /**
     * Executed when the target TbActor is stopped or destroyed.
     * For example, rule node failed to initialize or removed from rule chain.
     * Implementation should cleanup the resources.
     */
    default void onTbActorStopped(TbActorStopReason reason) {
    }

}
