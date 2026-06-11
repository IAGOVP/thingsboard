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
import org.thingsboard.server.common.data.AttributeScope;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.AttributesSaveResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Entity attributes ({@link org.thingsboard.server.common.data.AttributeScope}: SERVER_SCOPE, SHARED_SCOPE, CLIENT_SCOPE).
 *
 * <p>REST: {@code TelemetryController} attribute endpoints.
 */
public interface AttributesService {

    /**
     * Finds the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param scope scope ({@link AttributeScope})
     * @param attributeKey attribute key ({@link String})
     * @return future completing with optional {@link AttributeKvEntry}, empty if not found
     */
    ListenableFuture<Optional<AttributeKvEntry>> find(TenantId tenantId, EntityId entityId, AttributeScope scope, String attributeKey);

    /**
     * Finds the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param scope scope ({@link AttributeScope})
     * @param attributeKeys attribute keys ({@link Collection})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<AttributeKvEntry>> find(TenantId tenantId, EntityId entityId, AttributeScope scope, Collection<String> attributeKeys);

    /**
     * Finds all.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param scope scope ({@link AttributeScope})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<AttributeKvEntry>> findAll(TenantId tenantId, EntityId entityId, AttributeScope scope);

    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param scope scope ({@link AttributeScope})
     * @param attributes attributes ({@link List})
     * @return future completing with {@link AttributesSaveResult}
     */
    ListenableFuture<AttributesSaveResult> save(TenantId tenantId, EntityId entityId, AttributeScope scope, List<AttributeKvEntry> attributes);

    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param scope scope ({@link AttributeScope})
     * @param attribute attribute ({@link AttributeKvEntry})
     * @return future completing with {@link AttributesSaveResult}
     */
    ListenableFuture<AttributesSaveResult> save(TenantId tenantId, EntityId entityId, AttributeScope scope, AttributeKvEntry attribute);

    /**
     * Removes all.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param scope scope ({@link AttributeScope})
     * @param attributeKeys attribute keys ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<String>> removeAll(TenantId tenantId, EntityId entityId, AttributeScope scope, List<String> attributeKeys);

    /**
     * Finds all keys by device profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return {@link List}
     */
    List<String> findAllKeysByDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId);

    /**
     * Finds all keys by entity ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return {@link List}
     */
    List<String> findAllKeysByEntityIds(TenantId tenantId, List<EntityId> entityIds);

    /**
     * Finds all keys by entity ids and scope.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope scope ({@link AttributeScope})
     * @return {@link List}
     */
    List<String> findAllKeysByEntityIdsAndScope(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);

    /**
     * Finds all keys by entity ids and scope async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope scope ({@link AttributeScope})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<String>> findAllKeysByEntityIdsAndScopeAsync(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);

    /**
     * Finds latest by entity ids and scope.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope scope ({@link AttributeScope})
     * @return {@link List}
     */
    List<AttributeKvEntry> findLatestByEntityIdsAndScope(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);

    /**
     * Finds latest by entity ids and scope async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @param scope scope ({@link AttributeScope})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<AttributeKvEntry>> findLatestByEntityIdsAndScopeAsync(TenantId tenantId, List<EntityId> entityIds, AttributeScope scope);

    /**
     * Removes all by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return the int result
     */
    int removeAllByEntityId(TenantId tenantId, EntityId entityId);

}
