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
package org.thingsboard.server.dao;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.EntityFields;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Generic tenant-scoped CRUD contract for a single domain entity type.
 *
 * <p>Implemented by {@code Jpa*Dao} (PostgreSQL) or Cassandra DAOs for telemetry-related types.
 * Provides synchronous and asynchronous find/exists, save, remove, and batch id scan operations.
 */

public interface Dao<T> {

    
    /**
     * Finds the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<T> find(TenantId tenantId);
    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    T findById(TenantId tenantId, UUID id);
    /**
     * Finds by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return future completing with {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<T> findByIdAsync(TenantId tenantId, UUID id);
    /**
     * Finds entity infos by name prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    default List<EntityInfo> findEntityInfosByNamePrefix(TenantId tenantId, String name) {
        throw new UnsupportedOperationException();
    }
    /**
     * Exists by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsById(TenantId tenantId, UUID id);
    /**
     * Exists by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return future completing with {@link Boolean}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Boolean> existsByIdAsync(TenantId tenantId, UUID id);

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param t t ({@link T})
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    T save(TenantId tenantId, T t);

    
    /**
     * Saves or persists and flush.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param t t ({@link T})
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    T saveAndFlush(TenantId tenantId, T t);
    /**
     * Removes by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeById(TenantId tenantId, UUID id);
    /**
     * Removes all by ids.
     *
     * @param ids ids ({@link Collection})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeAllByIds(Collection<UUID> ids);

    
    /**
     * Finds ids by tenant id and id offset.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param idOffset cursor for batch id scan (exclusive lower bound)
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<UUID> findIdsByTenantIdAndIdOffset(TenantId tenantId, UUID idOffset, int limit);
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    default List<? extends EntityFields> findNextBatch(UUID id, int batchSize) {
        throw new UnsupportedOperationException();
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    default EntityType getEntityType() {
        return null;
    }

}
