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
package org.thingsboard.server.dao.timeseries;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQueryResult;
import org.thingsboard.server.common.data.kv.TsKvEntry;

import java.util.List;

/**
 * Persistence contract for timeseries.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (Cassandra telemetry and latest-value DAO (Cassandra time-series DAO and latest-value caches)).
 */

public interface TimeseriesDao {
    /**
     * Finds all async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param queries queries ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<ReadTsKvQueryResult>> findAllAsync(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries);
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param tsKvEntry ts kv entry ({@link TsKvEntry})
     * @param ttl ttl
     * @return future completing with {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Integer> save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry, long ttl);
    /**
     * Saves or persists partition.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param tsKvEntryTs ts kv entry ts
     * @param key attribute or cache key
     * @return future completing with {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Integer> savePartition(TenantId tenantId, EntityId entityId, long tsKvEntryTs, String key);
    /**
     * Removes the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param query filter and sort query definition
     * @return future completing with {@link Void}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Void> remove(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query);
    /**
     * Cleanup.
     *
     * @param systemTtl system ttl
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void cleanup(long systemTtl);
}
