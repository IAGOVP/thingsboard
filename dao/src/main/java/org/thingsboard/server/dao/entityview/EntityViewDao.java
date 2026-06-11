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
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for entity view.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (ThingsBoard DAO layer).
 */

public interface EntityViewDao extends Dao<EntityView>, ExportableEntityDao<EntityViewId, EntityView> {

    
    /**
     * Finds entity view info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link UUID})
     * @return {@link EntityViewInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityViewInfo findEntityViewInfoById(TenantId tenantId, UUID entityViewId);

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityView entity view ({@link EntityView})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityView save(TenantId tenantId, EntityView entityView);

    
    /**
     * Finds entity views by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityView> findEntityViewsByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds entity view infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityViewInfo> findEntityViewInfosByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds entity views by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityView> findEntityViewsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    
    /**
     * Finds entity view infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    
    /**
     * Finds entity view by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return optional {@link EntityView}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<EntityView> findEntityViewByTenantIdAndName(UUID tenantId, String name);

    
    /**
     * Finds entity views by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityView> findEntityViewsByTenantIdAndCustomerId(UUID tenantId,
                                                                UUID customerId,
                                                                PageLink pageLink);

    
    /**
     * Finds entity view infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    
    /**
     * Finds entity views by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityView> findEntityViewsByTenantIdAndCustomerIdAndType(UUID tenantId,
                                                                       UUID customerId,
                                                                       String type,
                                                                       PageLink pageLink);

    
    /**
     * Finds entity view infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);
    /**
     * Finds entity views by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityView> findEntityViewsByTenantIdAndEntityId(UUID tenantId, UUID entityId);
    /**
     * Exists by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndEntityId(UUID tenantId, UUID entityId);

    
    /**
     * Finds tenant entity view types async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<EntitySubtype>> findTenantEntityViewTypesAsync(UUID tenantId);

    
    /**
     * Finds entity views by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityView> findEntityViewsByTenantIdAndEdgeId(UUID tenantId,
                                                            UUID edgeId,
                                                            PageLink pageLink);
    /**
     * Finds entity views by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewIds entity view ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityView> findEntityViewsByTenantIdAndIds(UUID tenantId, List<UUID> entityViewIds);

    
    /**
     * Finds entity views by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityView> findEntityViewsByTenantIdAndEdgeIdAndType(UUID tenantId,
                                                            UUID edgeId,
                                                            String type,
                                                            PageLink pageLink);

}
