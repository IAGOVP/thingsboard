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
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;

/**
 * Service API for edge event persistence and domain operations.
 */
public interface EdgeEventService {

    /**
     * Saves or persists async.
     *
     * @param edgeEvent edge event ({@link EdgeEvent})
     * @return future completing with {@link Void}
     */
    ListenableFuture<Void> saveAsync(EdgeEvent edgeEvent);

    /**
     * Finds edge events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param seqIdStart seq id start ({@link Long})
     * @param seqIdEnd seq id end ({@link Long})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeEvent> findEdgeEvents(TenantId tenantId, EdgeId edgeId, Long seqIdStart, Long seqIdEnd, TimePageLink pageLink);

    /**
     * Cleanup events.
     *
     * @param ttl ttl
     */
    void cleanupEvents(long ttl);

}
