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
package org.thingsboard.server.dao.attributes;

import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.lang3.tuple.Pair;
import org.thingsboard.server.common.data.AttributeScope;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for attributes.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (server-side attribute key-value storage and caching).
 */

public interface AttributesDao {
    /**
     * Finds the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param attributeScope attribute scope ({@link AttributeScope})
     * @param attributeKey attribute key ({@link String})
     * @return optional {@link AttributeKvEntry}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<AttributeKvEntry> find(TenantId tenantId, EntityId entityId, AttributeScope attributeScope, String attributeKey);
    /**
     * Finds the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param attributeScope attribute scope ({@link AttributeScope})
     * @param attributeKey attribute key ({@link Collection})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<AttributeKvEntry> find(TenantId tenantId, EntityId entityId, AttributeScope attributeScope, Collection<String> attributeKey);
    /**
     * Finds all.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param attributeScope attribute scope ({@link AttributeScope})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<AttributeKvEntry> findAll(TenantId tenantId, EntityId entityId, AttributeScope attributeScope);
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param attributeScope attribute scope ({@link AttributeScope})
     * @param attribute attribute ({@link AttributeKvEntry})
     * @return future completing with {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Long> save(TenantId tenantId, EntityId entityId, AttributeScope attributeScope, AttributeKvEntry attribute);
    /**
     * Removes all.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param attributeScope attribute scope ({@link AttributeScope})
     * @param keys keys ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<ListenableFuture<String>> removeAll(TenantId tenantId, EntityId entityId, AttributeScope attributeScope, List<String> keys);
    /**
     * Removes all with versions.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param attributeScope attribute scope ({@link AttributeScope})
     * @param keys keys ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<ListenableFuture<TbPair<String, Long>>> removeAllWithVersions(TenantId tenantId, EntityId entityId, AttributeScope attributeScope, List<String> keys);
    /**
     * Finds next batch.
     *
     * @param entityId target entity identifier
     * @param attributeType attribute type
     * @param attributeKey attribute key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<AttributeKvEntity> findNextBatch(UUID entityId, int attributeType, int attributeKey, int batchSize);
    /**
     * Finds all keys by device profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<String> findAllKeysByDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId);
    /**
     * Finds all keys by entity ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<String> findAllKeysByEntityIds(TenantId tenantId, List<EntityId> entityIds);
    /**
     * Finds all keys by entity ids and scope.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope attribute scope (SERVER_SCOPE, SHARED_SCOPE, etc.)
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<String> findAllKeysByEntityIdsAndScope(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);
    /**
     * Finds all keys by entity ids and scope async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope attribute scope (SERVER_SCOPE, SHARED_SCOPE, etc.)
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<String>> findAllKeysByEntityIdsAndScopeAsync(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);
    /**
     * Finds latest by entity ids and scope.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope attribute scope (SERVER_SCOPE, SHARED_SCOPE, etc.)
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<AttributeKvEntry> findLatestByEntityIdsAndScope(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);
    /**
     * Finds latest by entity ids and scope async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope attribute scope (SERVER_SCOPE, SHARED_SCOPE, etc.)
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<AttributeKvEntry>> findLatestByEntityIdsAndScopeAsync(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);
    /**
     * Removes all by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<Pair<AttributeScope, String>> removeAllByEntityId(TenantId tenantId, EntityId entityId);

}
