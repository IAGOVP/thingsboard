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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.ThingsBoardExecutors;
import org.thingsboard.server.common.msg.TbActorMsg;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default {@link TbActorSystem}: registers dispatchers (thread pools), creates actor mailboxes,
 * routes {@link TbActorMsg} messages, and maintains parent/child relationships for broadcast and stop.
 *
 * <p>Thread safety: actor creation uses per-id {@link ReentrantLock}; mailboxes process one message
 * batch at a time on their dispatcher executor.
 */
@Slf4j
@Data
public class DefaultTbActorSystem implements TbActorSystem {

    /** Named thread pools; each actor is bound to one dispatcher id at creation. */
    private final ConcurrentMap<String, Dispatcher> dispatchers = new ConcurrentHashMap<>();
    /** Live actors keyed by {@link TbActorId}. */
    private final ConcurrentMap<TbActorId, TbActorMailbox> actors = new ConcurrentHashMap<>();
    /** Prevents duplicate concurrent creation of the same actor id. */
    private final ConcurrentMap<TbActorId, ReentrantLock> actorCreationLocks = new ConcurrentHashMap<>();
    /** Parent actor id → child actor ids (for broadcast and recursive stop). */
    private final ConcurrentMap<TbActorId, Set<TbActorId>> parentChildMap = new ConcurrentHashMap<>();

    @Getter
    private final TbActorSystemSettings settings;
    @Getter
    private final ScheduledExecutorService scheduler;

    /**
     * @param settings throughput, init retry limits, scheduler pool size
     */
    public DefaultTbActorSystem(TbActorSystemSettings settings) {
        this.settings = settings;
        this.scheduler = ThingsBoardExecutors.newScheduledThreadPool(settings.getSchedulerPoolSize(), "actor-system-scheduler");
    }

    /** Registers an executor pool; each actor created with this {@code dispatcherId} runs on it. */
    @Override
    public void createDispatcher(String dispatcherId, ExecutorService executor) {
        Dispatcher current = dispatchers.putIfAbsent(dispatcherId, new Dispatcher(dispatcherId, executor));
        if (current != null) {
            throw new RuntimeException("Dispatcher with id [" + dispatcherId + "] is already registered!");
        }
    }

    /** Shuts down and removes a dispatcher; actors on it must already be stopped. */
    @Override
    public void destroyDispatcher(String dispatcherId) {
        Dispatcher dispatcher = dispatchers.remove(dispatcherId);
        if (dispatcher != null) {
            dispatcher.getExecutor().shutdownNow();
        } else {
            throw new RuntimeException("Dispatcher with id [" + dispatcherId + "] is not registered!");
        }
    }

    /** Returns the mailbox for {@code actorId}, or {@code null} if not registered. */
    @Override
    public TbActorRef getActor(TbActorId actorId) {
        return actors.get(actorId);
    }

    /** Creates a top-level actor (no parent in {@link #parentChildMap}). */
    @Override
    public TbActorRef createRootActor(String dispatcherId, TbActorCreator creator) {
        /** Create actor. */
        return createActor(dispatcherId, creator, null);
    }

    /** Creates an actor and registers it as a child of {@code parent}. */
    @Override
    public TbActorRef createChildActor(String dispatcherId, TbActorCreator creator, TbActorId parent) {
        /** Create actor. */
        return createActor(dispatcherId, creator, parent);
    }

    /**
     * Idempotent actor creation: double-checked locking per {@code actorId}, then {@link TbActorMailbox#initActor()}.
     *
     * @throws RuntimeException if dispatcher unknown or parent not registered
     */
    private TbActorRef createActor(String dispatcherId, TbActorCreator creator, TbActorId parent) {
        Dispatcher dispatcher = dispatchers.get(dispatcherId);
        if (dispatcher == null) {
            log.warn("Dispatcher with id [{}] is not registered!", dispatcherId);
            throw new RuntimeException("Dispatcher with id [" + dispatcherId + "] is not registered!");
        }

        TbActorId actorId = creator.createActorId();
        TbActorMailbox actorMailbox = actors.get(actorId);
        if (actorMailbox != null) {
            log.debug("Actor with id [{}] is already registered!", actorId);
        } else {
            Lock actorCreationLock = actorCreationLocks.computeIfAbsent(actorId, id -> new ReentrantLock());
            actorCreationLock.lock();
            try {
                actorMailbox = actors.get(actorId);
                if (actorMailbox == null) {
                    log.debug("Creating actor with id [{}]!", actorId);
                    TbActor actor = creator.createActor();
                    TbActorRef parentRef = null;
                    if (parent != null) {
                        parentRef = getActor(parent);
                        if (parentRef == null) {
                            throw new TbActorNotRegisteredException(parent, "Parent Actor with id [" + parent + "] is not registered!");
                        }
                    }
                    TbActorMailbox mailbox = new TbActorMailbox(this, settings, actorId, parentRef, actor, dispatcher);
                    actors.put(actorId, mailbox);
                    mailbox.initActor();
                    actorMailbox = mailbox;
                    if (parent != null) {
                        parentChildMap.computeIfAbsent(parent, id -> ConcurrentHashMap.newKeySet()).add(actorId);
                    }
                } else {
                    log.debug("Actor with id [{}] is already registered!", actorId);
                }
            } finally {
                actorCreationLock.unlock();
                actorCreationLocks.remove(actorId);
            }
        }
        return actorMailbox;
    }

    /** Enqueues on the high-priority queue (processed before normal messages in the mailbox). */
    @Override
    public void tellWithHighPriority(TbActorId target, TbActorMsg actorMsg) {
        tell(target, actorMsg, true);
    }

    /** Enqueues a normal-priority message on the target actor's mailbox. */
    @Override
    public void tell(TbActorId target, TbActorMsg actorMsg) {
        tell(target, actorMsg, false);
    }

    /** Delegates to {@link TbActorMailbox#tell(TbActorMsg)} or high-priority variant. */
    private void tell(TbActorId target, TbActorMsg actorMsg, boolean highPriority) {
        TbActorMailbox mailbox = actors.get(target);
        if (mailbox == null) {
            throw new TbActorNotRegisteredException(target, "Actor with id [" + target + "] is not registered!");
        }
        if (highPriority) {
            mailbox.tellWithHighPriority(actorMsg);
        } else {
            mailbox.tell(actorMsg);
        }
    }

    /** Sends {@code msg} to every registered child of {@code parent} (normal priority). */
    @Override
    public void broadcastToChildren(TbActorId parent, TbActorMsg msg) {
        broadcastToChildren(parent, msg, false);
    }

    /** Broadcast to all children with optional high priority. */
    @Override
    public void broadcastToChildren(TbActorId parent, TbActorMsg msg, boolean highPriority) {
        broadcastToChildren(parent, id -> true, msg, highPriority);
    }

    /** Broadcast only to children matching {@code childFilter}. */
    @Override
    public void broadcastToChildren(TbActorId parent, Predicate<TbActorId> childFilter, TbActorMsg msg) {
        broadcastToChildren(parent, childFilter, msg, false);
    }

    /** Fan-out tell; missing children are logged and skipped. */
    private void broadcastToChildren(TbActorId parent, Predicate<TbActorId> childFilter, TbActorMsg msg, boolean highPriority) {
        Set<TbActorId> children = parentChildMap.get(parent);
        if (children != null) {
            children.stream().filter(childFilter).forEach(id -> {
                try {
                    tell(id, msg, highPriority);
                } catch (TbActorNotRegisteredException e) {
                    log.warn("Actor is missing for {}", id);
                }
            });
        }
    }

    /** Lists child actor ids of {@code parent} that satisfy {@code childFilter}. */
    @Override
    public List<TbActorId> filterChildren(TbActorId parent, Predicate<TbActorId> childFilter) {
        Set<TbActorId> children = parentChildMap.get(parent);
        if (children != null) {
            return children.stream().filter(childFilter).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void stop(TbActorRef actorRef) {
        stop(actorRef.getActorId());
    }

    /**
     * Stops {@code actorId} recursively (children first), removes from maps, calls {@link TbActorMailbox#destroy}.
     */
    @Override
    public void stop(TbActorId actorId) {
        Set<TbActorId> children = parentChildMap.remove(actorId);
        if (children != null) {
            for (TbActorId child : children) {
                stop(child);
            }
        }
        parentChildMap.values().forEach(parentChildren -> parentChildren.remove(actorId));

        TbActorMailbox mailbox = actors.remove(actorId);
        if (mailbox != null) {
            mailbox.destroy(null);
        }
    }

    /** Gracefully shuts down all dispatchers, scheduler, and clears actors. */
    @Override
    public void stop() {
        dispatchers.values().forEach(dispatcher -> {
            dispatcher.getExecutor().shutdown();
            try {
                dispatcher.getExecutor().awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.warn("[{}] Failed to stop dispatcher", dispatcher.getDispatcherId(), e);
            }
        });
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        actors.clear();
    }

}
