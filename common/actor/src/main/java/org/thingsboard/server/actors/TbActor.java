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
package org.thingsboard.server.actors;

import org.thingsboard.server.common.msg.TbActorMsg;
import org.thingsboard.server.common.msg.TbActorStopReason;

/**
 * Application actor: receives {@link TbActorMsg} sequentially in its mailbox.
 *
 * <p>Implementations live in {@code application} (device, rule chain, calculated field, …).
 */
public interface TbActor {

    /** Handles one mailbox message; return value meaning is actor-specific (often ignored). */
    boolean process(TbActorMsg msg);

    /** Reference used to enqueue messages to this actor. */
    TbActorRef getActorRef();

    /** Invoked once after the actor is created; override to initialize state. */
    default void init(TbActorCtx ctx) throws TbActorException {
    }

    /** Invoked when the actor stops; override to release resources. */
    default void destroy(TbActorStopReason stopReason, Throwable cause) throws TbActorException {
    }

    /** Defines retry behavior after {@link #init} failure. */
    default InitFailureStrategy onInitFailure(int attempt, Throwable t) {
        return InitFailureStrategy.retryWithDelay(5000L * attempt);
    }

    /** Defines behavior after an uncaught error in {@link #process}. */
    default ProcessFailureStrategy onProcessFailure(TbActorMsg msg, Throwable t) {
        if (t instanceof Error) {
            return ProcessFailureStrategy.stop();
        } else {
            return ProcessFailureStrategy.resume();
        }
    }
}
