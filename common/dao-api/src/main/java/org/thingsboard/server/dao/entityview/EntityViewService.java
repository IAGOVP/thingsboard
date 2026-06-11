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
package org.thingsboard.server.dao.entityview;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.EntityViewInfo;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.entityview.EntityViewSearchQuery;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for entity view persistence and domain operations.
 */
public interface EntityViewService extends EntityDaoService {

    /**
     * Saves or persists entity view.
     *
     * @param entityView entity view ({@link EntityView})
     * @return {@link EntityView}
     */
    EntityView saveEntityView(EntityView entityView);

    /**
     * Saves or persists entity view.
     *
     * @param entityView entity view ({@link EntityView})
     * @param nameConflictStrategy behavior when an entity with the same name already exists
     * @return {@link EntityView}
     */
    EntityView saveEntityView(EntityView entityView, NameConflictStrategy nameConflictStrategy);

    /**
     * Saves or persists entity view.
     *
     * @param entityView entity view ({@link EntityView})
     * @param doValidate whether to run validation before persist
     * @return {@link EntityView}
     */
    EntityView saveEntityView(EntityView entityView, boolean doValidate);

    /**
     * Assigns entity view to customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param customerId customer to assign or filter by
     * @return {@link EntityView}
     */
    EntityView assignEntityViewToCustomer(TenantId tenantId, EntityViewId entityViewId, CustomerId customerId);

    /**
     * Unassigns entity view from customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return {@link EntityView}
     */
    EntityView unassignEntityViewFromCustomer(TenantId tenantId, EntityViewId entityViewId);

    /**
     * Unassigns customer entity views.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void unassignCustomerEntityViews(TenantId tenantId, CustomerId customerId);

    /**
     * Finds entity view info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return {@link EntityViewInfo}
     */
    EntityViewInfo findEntityViewInfoById(TenantId tenantId, EntityViewId entityViewId);

    /**
     * Finds entity view by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return {@link EntityView}
     */
    EntityView findEntityViewById(TenantId tenantId, EntityViewId entityViewId);

    /**
     * Finds entity view by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param putInCache put in cache
     * @return {@link EntityView}
     */
    EntityView findEntityViewById(TenantId tenantId, EntityViewId entityViewId, boolean putInCache);

    /**
     * Finds entity view by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return future completing with {@link EntityView}
     */
    ListenableFuture<EntityView> findEntityViewByIdAsync(TenantId tenantId, EntityViewId entityViewId);

    /**
     * Finds entity view by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link EntityView}
     */
    EntityView findEntityViewByTenantIdAndName(TenantId tenantId, String name);

    /**
     * Finds entity view by tenant id and name async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return future completing with {@link EntityView}
     */
    ListenableFuture<EntityView> findEntityViewByTenantIdAndNameAsync(TenantId tenantId, String name);

    /**
     * Finds entity view by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityView> findEntityViewByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds entity view infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityViewInfo> findEntityViewInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds entity view by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @param type type ({@link String})
     * @return {@link PageData}
     */
    PageData<EntityView> findEntityViewByTenantIdAndType(TenantId tenantId, PageLink pageLink, String type);

    /**
     * Finds entity view infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    /**
     * Finds entity views by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityView> findEntityViewsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds entity views by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewIds entity view ids ({@link List})
     * @return {@link List}
     */
    List<EntityView> findEntityViewsByTenantIdAndIds(TenantId tenantId, List<EntityViewId> entityViewIds);

    /**
     * Finds entity view infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds entity views by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @param type type ({@link String})
     * @return {@link PageData}
     */
    PageData<EntityView> findEntityViewsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, PageLink pageLink, String type);

    /**
     * Finds entity view infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    /**
     * Finds entity views by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link EntityViewSearchQuery})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityView>> findEntityViewsByQuery(TenantId tenantId, EntityViewSearchQuery query);

    /**
     * Finds entity views by tenant id and entity id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityView>> findEntityViewsByTenantIdAndEntityIdAsync(TenantId tenantId, EntityId entityId);

    /**
     * Finds entity views by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return {@link List}
     */
    List<EntityView> findEntityViewsByTenantIdAndEntityId(TenantId tenantId, EntityId entityId);

    /**
     * Exists by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return the boolean result
     */
    boolean existsByTenantIdAndEntityId(TenantId tenantId, EntityId entityId);

    /**
     * Deletes entity view.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     */
    void deleteEntityView(TenantId tenantId, EntityViewId entityViewId);

    /**
     * Deletes entity views by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteEntityViewsByTenantId(TenantId tenantId);

    /**
     * Finds entity view types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntitySubtype>> findEntityViewTypesByTenantId(TenantId tenantId);

    /**
     * Assigns entity view to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link EntityView}
     */
    EntityView assignEntityViewToEdge(TenantId tenantId, EntityViewId entityViewId, EdgeId edgeId);

    /**
     * Unassigns entity view from edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link EntityView}
     */
    EntityView unassignEntityViewFromEdge(TenantId tenantId, EntityViewId entityViewId, EdgeId edgeId);

    /**
     * Finds entity views by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityView> findEntityViewsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    /**
     * Finds entity views by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntityView> findEntityViewsByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);

}
