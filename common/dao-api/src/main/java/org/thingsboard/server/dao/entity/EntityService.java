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
package org.thingsboard.server.dao.entity;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.NameLabelAndCustomerDetails;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityData;
import org.thingsboard.server.common.data.query.EntityDataQuery;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service API for entity persistence and domain operations.
 */
public interface EntityService {

    /**
     * Fetches entity name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return optional {@link String}, empty if not found
     */
    Optional<String> fetchEntityName(TenantId tenantId, EntityId entityId);

    /**
     * Fetches entity label.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return optional {@link String}, empty if not found
     */
    Optional<String> fetchEntityLabel(TenantId tenantId, EntityId entityId);

    /**
     * Fetches entity customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return optional {@link CustomerId}, empty if not found
     */
    Optional<CustomerId> fetchEntityCustomerId(TenantId tenantId, EntityId entityId);

    /**
     * Fetches entity customer id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return {@link FluentFuture}
     */
    FluentFuture<Optional<CustomerId>> fetchEntityCustomerIdAsync(TenantId tenantId, EntityId entityId);

    /**
     * Fetches entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return optional {@link HasId}, empty if not found
     */
    Optional<HasId<?>> fetchEntity(TenantId tenantId, EntityId entityId);

    /**
     * Fetches entity infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param entityIds entity ids ({@link Set})
     * @return {@link Map}
     */
    Map<EntityId, EntityInfo> fetchEntityInfos(TenantId tenantId, CustomerId customerId, Set<EntityId> entityIds);

    /**
     * Fetches name label and customer details.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return optional {@link NameLabelAndCustomerDetails}, empty if not found
     */
    Optional<NameLabelAndCustomerDetails> fetchNameLabelAndCustomerDetails(TenantId tenantId, EntityId entityId);

    /**
     * Counts entities by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param query query ({@link EntityCountQuery})
     * @return the long result
     */
    long countEntitiesByQuery(TenantId tenantId, CustomerId customerId, EntityCountQuery query);

    /**
     * Finds entity data by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param query query ({@link EntityDataQuery})
     * @return {@link PageData}
     */
    PageData<EntityData> findEntityDataByQuery(TenantId tenantId, CustomerId customerId, EntityDataQuery query);

    /**
     * Finds entity data by query async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param query query ({@link EntityDataQuery})
     * @return future completing with {@link PageData}
     */
    ListenableFuture<PageData<EntityData>> findEntityDataByQueryAsync(TenantId tenantId, CustomerId customerId, EntityDataQuery query);

}
