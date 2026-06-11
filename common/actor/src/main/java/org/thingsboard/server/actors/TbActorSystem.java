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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

/**
 * Registry and dispatcher for ThingsBoard actors.
 *
 * <p>Actors are created via {@link #createRootActor} / {@link #createChildActor}; messages are sent with
 * {@link #tell} or {@link #tellWithHighPriority}. Each actor processes its mailbox on a dedicated dispatcher executor.
 */
public interface TbActorSystem {

    /** Shared scheduler for actor retries and delayed tasks. */
    ScheduledExecutorService getScheduler();

    /** Binds a thread pool to a dispatcher id used when creating actors. */
    void createDispatcher(String dispatcherId, ExecutorService executor);

    /** Shuts down the executor and unregisters the dispatcher. */
    void destroyDispatcher(String dispatcherId);

    /** Returns the registered actor reference, or {@code null} if not found. */
    TbActorRef getActor(TbActorId actorId);

    /** Creates a top-level actor (e.g. tenant or app root). */
    TbActorRef createRootActor(String dispatcherId, TbActorCreator creator);

    /** Creates a child actor under {@code parent}. */
    TbActorRef createChildActor(String dispatcherId, TbActorCreator creator, TbActorId parent);

    /** Enqueues a message on the target actor's mailbox (normal priority). */
    void tell(TbActorId target, TbActorMsg actorMsg);

    /** Enqueues a high-priority message processed before normal queue traffic. */
    void tellWithHighPriority(TbActorId target, TbActorMsg actorMsg);

    /** Stops the given actor and destroys its mailbox. */
    void stop(TbActorRef actorRef);

    /** Stops the actor identified by {@code actorId}. */
    void stop(TbActorId actorId);

    /** Stops all actors and shuts down the actor system. */
    void stop();

    /** Broadcasts a message to all direct child actors. */
    void broadcastToChildren(TbActorId parent, TbActorMsg msg);

    /** Broadcasts a message to all children, optionally at high priority. */
    void broadcastToChildren(TbActorId parent, TbActorMsg msg, boolean highPriority);

    /** Broadcasts a message to child actors matching {@code childFilter}. */
    void broadcastToChildren(TbActorId parent, Predicate<TbActorId> childFilter, TbActorMsg msg);

    /** Returns child actor ids matching {@code childFilter}. */
    List<TbActorId> filterChildren(TbActorId parent, Predicate<TbActorId> childFilter);
}
