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

/** Handle for sending messages to an actor ({@link TbActorMailbox} implements this). */
public interface TbActorRef {

    /** Returns the actor id. */
    TbActorId getActorId();

    /** Enqueues a message on the target actor's mailbox (normal priority). */
    void tell(TbActorMsg actorMsg);

    /** Enqueues a high-priority message processed before normal queue traffic. */
    void tellWithHighPriority(TbActorMsg actorMsg);

}
