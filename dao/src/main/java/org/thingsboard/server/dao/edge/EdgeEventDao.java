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
package org.thingsboard.server.dao.edge;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

/**
 * Persistence contract for edge event.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (edge instances, events, sessions, and synchronization).
 */

public interface EdgeEventDao extends Dao<EdgeEvent> {

    
    /**
     * Saves or persists async.
     *
     * @param edgeEvent edge event ({@link EdgeEvent})
     * @return future completing with {@link Void}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Void> saveAsync(EdgeEvent edgeEvent);


    
    /**
     * Finds edge events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param seqIdStart seq id start ({@link Long})
     * @param seqIdEnd seq id end ({@link Long})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EdgeEvent> findEdgeEvents(UUID tenantId, EdgeId edgeId, Long seqIdStart, Long seqIdEnd, TimePageLink pageLink);

    
    /**
     * Cleanup events.
     *
     * @param ttl ttl
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void cleanupEvents(long ttl);

}
