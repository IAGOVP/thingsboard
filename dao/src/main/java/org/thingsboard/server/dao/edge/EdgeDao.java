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
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for edge.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (edge instances, events, sessions, and synchronization).
 */

public interface EdgeDao extends Dao<Edge>, TenantEntityDao<Edge> {
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edge edge ({@link Edge})
     * @return {@link Edge}
     * @throws Exception if an unexpected error occurs during processing
     */

    Edge save(TenantId tenantId, Edge edge);
    /**
     * Finds edge info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @return {@link EdgeInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    EdgeInfo findEdgeInfoById(TenantId tenantId, UUID edgeId);
    /**
     * Finds active edges.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Edge> findActiveEdges(PageLink pageLink);
    /**
     * Finds edge ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EdgeId> findEdgeIdsByTenantId(UUID tenantId, PageLink pageLink);
    /**
     * Finds edges by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Edge> findEdgesByTenantId(UUID tenantId, PageLink pageLink);
    /**
     * Finds edges by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Edge> findEdgesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);
    /**
     * Finds edges by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeIds edge ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<Edge>> findEdgesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> edgeIds);
    /**
     * Finds edges by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Edge> findEdgesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);
    /**
     * Finds edges by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Edge> findEdgesByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);
    /**
     * Finds edge infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);
    /**
     * Finds edge infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);
    /**
     * Finds edges by tenant id customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param edgeIds edge ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<Edge>> findEdgesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> edgeIds);
    /**
     * Finds edge by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return optional {@link Edge}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<Edge> findEdgeByTenantIdAndName(UUID tenantId, String name);
    /**
     * Finds tenant edge types async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<EntitySubtype>> findTenantEdgeTypesAsync(UUID tenantId);
    /**
     * Finds by routing key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param routingKey routing key ({@link String})
     * @return optional {@link Edge}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<Edge> findByRoutingKey(UUID tenantId, String routingKey);
    /**
     * Finds edge infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EdgeInfo> findEdgeInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);
    /**
     * Finds edge infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EdgeInfo> findEdgeInfosByTenantId(UUID tenantId, PageLink pageLink);
    /**
     * Finds edges by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param entityType entity type discriminator
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Edge> findEdgesByTenantIdAndEntityId(UUID tenantId, UUID entityId, EntityType entityType, PageLink pageLink);
    /**
     * Finds edge ids by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param entityType entity type discriminator
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EdgeId> findEdgeIdsByTenantIdAndEntityId(UUID tenantId, UUID entityId, EntityType entityType, PageLink pageLink);
    /**
     * Finds edges by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Edge> findEdgesByTenantProfileId(UUID tenantProfileId, PageLink pageLink);

}
