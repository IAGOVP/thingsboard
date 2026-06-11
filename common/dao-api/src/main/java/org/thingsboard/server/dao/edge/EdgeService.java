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
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.edge.EdgeSearchQuery;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.Optional;

/**
 * Service API for edge persistence and domain operations.
 */
public interface EdgeService extends EntityDaoService {

    /**
     * Finds edge by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Edge}
     */
    Edge findEdgeById(TenantId tenantId, EdgeId edgeId);

    /**
     * Finds edge info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link EdgeInfo}
     */
    EdgeInfo findEdgeInfoById(TenantId tenantId, EdgeId edgeId);

    /**
     * Finds edge by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @return future completing with {@link Edge}
     */
    ListenableFuture<Edge> findEdgeByIdAsync(TenantId tenantId, EdgeId edgeId);

    /**
     * Finds edge by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link Edge}
     */
    Edge findEdgeByTenantIdAndName(TenantId tenantId, String name);

    /**
     * Finds edge by tenant id and name async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return future completing with {@link Edge}
     */
    ListenableFuture<Edge> findEdgeByTenantIdAndNameAsync(TenantId tenantId, String name);

    /**
     * Finds edge by routing key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param routingKey routing key ({@link String})
     * @return optional {@link Edge}, empty if not found
     */
    Optional<Edge> findEdgeByRoutingKey(TenantId tenantId, String routingKey);

    /**
     * Finds active edges.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Edge> findActiveEdges(PageLink pageLink);

    /**
     * Saves or persists edge.
     *
     * @param edge edge ({@link Edge})
     * @return {@link Edge}
     */
    Edge saveEdge(Edge edge);

    /**
     * Assigns edge to customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param customerId customer to assign or filter by
     * @return {@link Edge}
     */
    Edge assignEdgeToCustomer(TenantId tenantId, EdgeId edgeId, CustomerId customerId);

    /**
     * Unassigns edge from customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Edge}
     */
    Edge unassignEdgeFromCustomer(TenantId tenantId, EdgeId edgeId);

    /**
     * Deletes edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     */
    void deleteEdge(TenantId tenantId, EdgeId edgeId);

    /**
     * Finds edge ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeId> findEdgeIdsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds edges by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Edge> findEdgesByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds edges by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Edge> findEdgesByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    /**
     * Finds edge infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeInfo> findEdgeInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    /**
     * Finds edge infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeInfo> findEdgeInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds edges by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeIds edge ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Edge>> findEdgesByTenantIdAndIdsAsync(TenantId tenantId, List<EdgeId> edgeIds);

    /**
     * Deletes edges by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteEdgesByTenantId(TenantId tenantId);

    /**
     * Finds edges by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Edge> findEdgesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds edges by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Edge> findEdgesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    /**
     * Finds edge infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds edge infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    /**
     * Finds edges by tenant id customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param edgeIds edge ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Edge>> findEdgesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<EdgeId> edgeIds);

    /**
     * Unassigns customer edges.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void unassignCustomerEdges(TenantId tenantId, CustomerId customerId);

    /**
     * Finds edges by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link EdgeSearchQuery})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<Edge>> findEdgesByQuery(TenantId tenantId, EdgeSearchQuery query);

    /**
     * Finds edge types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntitySubtype>> findEdgeTypesByTenantId(TenantId tenantId);

    /**
     * Assigns default rule chains to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     */
    void assignDefaultRuleChainsToEdge(TenantId tenantId, EdgeId edgeId);

    /**
     * Finds edges by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link EntityId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Edge> findEdgesByTenantIdAndEntityId(TenantId tenantId, EntityId ruleChainId, PageLink pageLink);

    /**
     * Finds edge ids by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link EntityId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeId> findEdgeIdsByTenantIdAndEntityId(TenantId tenantId, EntityId ruleChainId, PageLink pageLink);

    /**
     * Finds edges by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Edge> findEdgesByTenantProfileId(TenantProfileId tenantProfileId, PageLink pageLink);

    /**
     * Finds all related edge ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return {@link List}
     */
    List<EdgeId> findAllRelatedEdgeIds(TenantId tenantId, EntityId entityId);

    /**
     * Finds related edge ids by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EdgeId> findRelatedEdgeIdsByEntityId(TenantId tenantId, EntityId entityId, PageLink pageLink);

    /**
     * Finds missing to related rule chains.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param tbRuleChainInputNodeClassName tb rule chain input node class name ({@link String})
     * @return {@link String}
     */
    String findMissingToRelatedRuleChains(TenantId tenantId, EdgeId edgeId, String tbRuleChainInputNodeClassName);

    /**
     * Set edge root rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edge edge ({@link Edge})
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return {@link Edge}
     * @throws Exception if exception is thrown
     */
    Edge setEdgeRootRuleChain(TenantId tenantId, Edge edge, RuleChainId ruleChainId) throws Exception;

    /**
     * Is edge active async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param activityState activity state ({@link String})
     * @return future completing with {@link Boolean}
     */
    ListenableFuture<Boolean> isEdgeActiveAsync(TenantId tenantId, EdgeId edgeId, String activityState);

}
