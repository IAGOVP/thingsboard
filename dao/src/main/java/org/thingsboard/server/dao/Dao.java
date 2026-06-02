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
 * Generic tenant-scoped CRUD for one domain type {@code T}.
 *
 * <p>Implemented by {@code Jpa*Dao} (PostgreSQL) or Cassandra DAOs for telemetry-related types.
 */
public interface Dao<T> {

    /** All entities for tenant (use with care on large tenants). */
    List<T> find(TenantId tenantId);

    T findById(TenantId tenantId, UUID id);

    ListenableFuture<T> findByIdAsync(TenantId tenantId, UUID id);

    default List<EntityInfo> findEntityInfosByNamePrefix(TenantId tenantId, String name) {
        throw new UnsupportedOperationException();
    }

    boolean existsById(TenantId tenantId, UUID id);

    ListenableFuture<Boolean> existsByIdAsync(TenantId tenantId, UUID id);

    /** Insert or update; may not flush until transaction commit. */
    T save(TenantId tenantId, T t);

    /** Insert or update with immediate flush to the database. */
    T saveAndFlush(TenantId tenantId, T t);

    void removeById(TenantId tenantId, UUID id);

    void removeAllByIds(Collection<UUID> ids);

    /** Cursor-based id scan for batch jobs (TTL, migration). */
    List<UUID> findIdsByTenantIdAndIdOffset(TenantId tenantId, UUID idOffset, int limit);

    default List<? extends EntityFields> findNextBatch(UUID id, int batchSize) {
        throw new UnsupportedOperationException();
    }

    default EntityType getEntityType() {
        return null;
    }

}
