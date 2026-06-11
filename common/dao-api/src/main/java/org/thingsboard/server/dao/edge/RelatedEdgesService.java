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

import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

/**
 * Service API for related edges persistence and domain operations.
 */
public interface RelatedEdgesService {

    /**
     * Finds edge ids by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeId> findEdgeIdsByEntityId(TenantId tenantId, EntityId entityId, PageLink pageLink);

    /**
     * Publish related edge ids evict event.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     */
    void publishRelatedEdgeIdsEvictEvent(TenantId tenantId, EntityId entityId);

}
