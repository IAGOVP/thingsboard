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
package org.thingsboard.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.ArrayList;
import java.util.List;
/**
 * Fetches customer entities for edge initial synchronization.
 *
 * <p><b>Responsibilities:</b> Uses EdgeContextComponent and DAO services to persist and propagate changes.
 */

@Slf4j
@AllArgsConstructor
public class CustomerEdgeEventFetcher implements EdgeEventFetcher {

    private final CustomerId customerId;
    /**
     * Returns page link.
     *
     * @param pageSize page size (int)
     * @return {@link PageLink} result
     */
    @Override
    public PageLink getPageLink(int pageSize) {
        return null;
    }
    /**
     * Fetches edge events for edge synchronization.
     *
     * @param tenantId tenant id (TenantId)
     * @param edge edge (Edge)
     * @param pageLink page link (PageLink)
     * @return {@link PageData} result
     */
    @Override
    public PageData<EdgeEvent> fetchEdgeEvents(TenantId tenantId, Edge edge, PageLink pageLink) {
        List<EdgeEvent> result = new ArrayList<>();
        result.add(EdgeUtils.constructEdgeEvent(edge.getTenantId(), edge.getId(),
                EdgeEventType.CUSTOMER, EdgeEventActionType.ADDED, customerId, null));
        // returns PageData object to be in sync with other fetchers
        return new PageData<>(result, 1, result.size(), false);
    }

}
