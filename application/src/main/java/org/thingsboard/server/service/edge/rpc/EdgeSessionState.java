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
package org.thingsboard.server.service.edge.rpc;

import com.google.common.util.concurrent.SettableFuture;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Edge session state for ThingsBoard Edge integration.
 */

@Getter
public class EdgeSessionState {

    @Setter
    private SettableFuture<Boolean> sendDownlinkMsgsFuture;
    @Setter
    private ScheduledFuture<?> scheduledSendDownlinkTask;
    @Setter
    private volatile boolean connected;
    @Setter
    private EdgeVersion edgeVersion;
    private final UUID sessionId = UUID.randomUUID();
    private final Map<Integer, DownlinkMsg> pendingMsgsMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Lock sequenceDependencyLock = new ReentrantLock();
    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);
    private TenantId tenantId;
    private Edge edge;

    /**
     * Set edge.
     *
     * @param edge edge (Edge)
     */

    public void setEdge(Edge edge) {
        if (edge == null) {
            return;
        }
        this.edge = edge;
        this.tenantId = edge.getTenantId();
    }

    /**
     * Returns edge id.
     *
     */

    public EdgeId getEdgeId() {
        return edge != null ? edge.getId() : null;
    }

    /**
     * Try start sync.
     *
     */

    public boolean tryStartSync() {
        return syncInProgress.compareAndSet(false, true);
    }

    /**
     * Finish sync.
     *
     */

    public void finishSync() {
        syncInProgress.set(false);
    }

    /**
     * Returns whether sync in progress.
     *
     */

    public boolean isSyncInProgress() {
        return syncInProgress.get();
    }

    /**
     * Lock sequence dependency.
     *
     */

    public void lockSequenceDependency() {
        sequenceDependencyLock.lock();
    }

    /**
     * Unlock sequence dependency.
     *
     */

    public void unlockSequenceDependency() {
        sequenceDependencyLock.unlock();
    }
}
