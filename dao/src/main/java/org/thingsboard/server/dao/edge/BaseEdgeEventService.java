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

import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.cache.limits.RateLimitService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.limit.LimitedApi;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.msg.tools.TbRateLimitsException;
import org.thingsboard.server.dao.service.DataValidator;


/**

 * Default DAO-layer service implementation for edge event.

 *

 * <p>Coordinates validation, caching, cluster events, and {@code *Dao} persistence (edge instances, events, sessions, and synchronization).

 */


public abstract class BaseEdgeEventService implements EdgeEventService {

    @Autowired
    private EdgeEventDao edgeEventDao;
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private DataValidator<EdgeEvent> edgeEventValidator;

    
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


    @Override
    public PageData<EdgeEvent> findEdgeEvents(TenantId tenantId, EdgeId edgeId, Long seqIdStart, Long seqIdEnd, TimePageLink pageLink) {
        return edgeEventDao.findEdgeEvents(tenantId.getId(), edgeId, seqIdStart, seqIdEnd, pageLink);
    }

    
    /**
     * Cleanup events.
     *
     * @param ttl ttl
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void cleanupEvents(long ttl) {
        edgeEventDao.cleanupEvents(ttl);
    }

    
    /**
     * Validates edge event.
     *
     * @param edgeEvent edge event ({@link EdgeEvent})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    protected void validateEdgeEvent(EdgeEvent edgeEvent) {
        if (!rateLimitService.checkRateLimit(LimitedApi.EDGE_EVENTS, edgeEvent.getTenantId())) {
            throw new TbRateLimitsException(EntityType.TENANT);
        }
        if (!rateLimitService.checkRateLimit(LimitedApi.EDGE_EVENTS_PER_EDGE, edgeEvent.getTenantId(), edgeEvent.getEdgeId())) {
            throw new TbRateLimitsException(EntityType.EDGE);
        }
        edgeEventValidator.validate(edgeEvent, EdgeEvent::getTenantId);
    }

}
