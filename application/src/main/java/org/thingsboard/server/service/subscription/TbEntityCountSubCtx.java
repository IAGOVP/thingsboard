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
package org.thingsboard.server.service.subscription;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.entity.EntityService;
import org.thingsboard.server.service.ws.WebSocketService;
import org.thingsboard.server.service.ws.WebSocketSessionRef;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.EntityCountUpdate;
/**
 * Subscription context for tb entity count WebSocket commands.
 * <p>Maintains query state, caches, and pending updates for one command id.
 */

@Slf4j
public class TbEntityCountSubCtx extends TbAbstractEntityQuerySubCtx<EntityCountQuery> {

    private volatile int result;

    /**
     * Constructs {@link TbEntityCountSubCtx} with the supplied dependencies and configuration.
     * @param serviceId service id
     * @param wsService ws service
     * @param entityService entity service
     * @param localSubscriptionService local subscription service
     * @param attributesService attributes service
     * @param stats stats
     * @param sessionRef reference to the WebSocket session
     * @param cmdId client command id
     */

    public TbEntityCountSubCtx(String serviceId, WebSocketService wsService, EntityService entityService,
                               TbLocalSubscriptionService localSubscriptionService, AttributesService attributesService,
                               SubscriptionServiceStatistics stats, WebSocketSessionRef sessionRef, int cmdId) {
        super(serviceId, wsService, entityService, localSubscriptionService, attributesService, stats, sessionRef, cmdId);
    }

    /**
     * Fetches data.
     * @return @Override
    public void
     */

    @Override
    public void fetchData() {
        result = (int) entityService.countEntitiesByQuery(getTenantId(), getCustomerId(), query);
        sendWsMsg(new EntityCountUpdate(cmdId, result));
    }

    /**
     * Updates update.
     * @return @Override
    protected void
     */

    @Override
    protected void update() {
        int newCount = (int) entityService.countEntitiesByQuery(getTenantId(), getCustomerId(), query);
        if (newCount != result) {
            result = newCount;
            sendWsMsg(new EntityCountUpdate(cmdId, result));
        }
    }

    /**
     * Is dynamic.
     * @return {@code true} when the condition holds
     */

    @Override
    public boolean isDynamic() {
        return true;
    }
}
