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

import lombok.Data;

/** Tunables for {@link DefaultTbActorSystem} and {@link TbActorMailbox}. */
@Data
public class TbActorSystemSettings {

    /** Max messages processed per mailbox drain cycle. */
    private final int actorThroughput;
    /** Pool for init retry scheduling. */
    private final int schedulerPoolSize;
    /** Max init attempts before {@link TbActorMailbox} destroys the actor (0 = unlimited). */
    private final int maxActorInitAttempts;

}
