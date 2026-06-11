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
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.kv.TsKvLatestRemovingResult;

import java.util.List;
import java.util.Optional;


/**

 * Persistence contract for timeseries latest.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (Cassandra telemetry and latest-value DAO (Cassandra time-series DAO and latest-value caches)).

 */


public interface TimeseriesLatestDao {

    
    /**
     * Finds latest opt.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param key attribute or cache key
     * @return future completing with optional {@link TsKvEntry}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Optional<TsKvEntry>> findLatestOpt(TenantId tenantId, EntityId entityId, String key);

    
    /**
     * Finds latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param key attribute or cache key
     * @return future completing with {@link TsKvEntry}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<TsKvEntry> findLatest(TenantId tenantId, EntityId entityId, String key);
    /**
     * Finds all latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId);
    /**
     * Saves or persists latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param tsKvEntry ts kv entry ({@link TsKvEntry})
     * @return future completing with {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Long> saveLatest(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry);
    /**
     * Removes latest.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param query filter and sort query definition
     * @return future completing with {@link TsKvLatestRemovingResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<TsKvLatestRemovingResult> removeLatest(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query);
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
     * Finds all keys by entity ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<String>> findAllKeysByEntityIdsAsync(TenantId tenantId, List<EntityId> entityIds);

    
    /**
     * Finds latest by entity ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TsKvEntry> findLatestByEntityIds(TenantId tenantId, List<EntityId> entityIds);
    /**
     * Finds latest by entity ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityIds entity ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<TsKvEntry>> findLatestByEntityIdsAsync(TenantId tenantId, List<EntityId> entityIds);

}
