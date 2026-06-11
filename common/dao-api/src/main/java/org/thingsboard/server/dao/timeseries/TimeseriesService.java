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
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQueryResult;
import org.thingsboard.server.common.data.kv.TimeseriesSaveResult;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.kv.TsKvLatestRemovingResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Telemetry time-series storage API (SQL Timescale and/or Cassandra depending on deployment).
 *
 * <p>REST read/write: {@code TelemetryController} ({@code /api/plugins/telemetry}). Ingest also via transport → queue.
 */
public interface TimeseriesService {

    /**
     * Finds all by queries.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param queries queries ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<ReadTsKvQueryResult>> findAllByQueries(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries);

    /**
     * Finds all.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param queries queries ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<TsKvEntry>> findAll(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries);

    /**
     * Finds latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param key key ({@link String})
     * @return future completing with optional {@link TsKvEntry}, empty if not found
     */
    ListenableFuture<Optional<TsKvEntry>> findLatest(TenantId tenantId, EntityId entityId, String key);

    /**
     * Finds latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param keys keys ({@link Collection})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<TsKvEntry>> findLatest(TenantId tenantId, EntityId entityId, Collection<String> keys);

    /**
     * Finds all latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId);

    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param tsKvEntry ts kv entry ({@link TsKvEntry})
     * @return future completing with {@link TimeseriesSaveResult}
     */
    ListenableFuture<TimeseriesSaveResult> save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry);

    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param tsKvEntry ts kv entry ({@link List})
     * @param ttl ttl
     * @return future completing with {@link TimeseriesSaveResult}
     */
    ListenableFuture<TimeseriesSaveResult> save(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntry, long ttl);

    /**
     * Saves or persists without latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param tsKvEntry ts kv entry ({@link List})
     * @param ttl ttl
     * @return future completing with {@link TimeseriesSaveResult}
     */
    ListenableFuture<TimeseriesSaveResult> saveWithoutLatest(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntry, long ttl);

    /**
     * Saves or persists latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param tsKvEntries ts kv entries ({@link List})
     * @return future completing with {@link TimeseriesSaveResult}
     */
    ListenableFuture<TimeseriesSaveResult> saveLatest(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntries);

    /**
     * Removes the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param queries queries ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<TsKvLatestRemovingResult>> remove(TenantId tenantId, EntityId entityId, List<DeleteTsKvQuery> queries);

    /**
     * Removes latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param keys keys ({@link Collection})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<TsKvLatestRemovingResult>> removeLatest(TenantId tenantId, EntityId entityId, Collection<String> keys);

    /**
     * Removes all latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<String>> removeAllLatest(TenantId tenantId, EntityId entityId);

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
     * Finds all keys by entity ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<String>> findAllKeysByEntityIdsAsync(TenantId tenantId, List<EntityId> entityIds);

    /**
     * Finds latest by entity ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return {@link List}
     */
    List<TsKvEntry> findLatestByEntityIds(TenantId tenantId, List<EntityId> entityIds);

    /**
     * Finds latest by entity ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<TsKvEntry>> findLatestByEntityIdsAsync(TenantId tenantId, List<EntityId> entityIds);

    /**
     * Cleanup.
     *
     * @param systemTtl system ttl
     */
    void cleanup(long systemTtl);

}
