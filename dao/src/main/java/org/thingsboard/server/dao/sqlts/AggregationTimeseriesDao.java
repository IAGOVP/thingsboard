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
package org.thingsboard.server.dao.sqlts;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQueryResult;


/**

 * Persistence contract for aggregation timeseries.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (time-series SQL/Timescale persistence (SQL/Timescale time-series key-value storage)).

 */


public interface AggregationTimeseriesDao {
    /**
     * Finds all async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param query filter and sort query definition
     * @return future completing with {@link ReadTsKvQueryResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<ReadTsKvQueryResult> findAllAsync(TenantId tenantId, EntityId entityId, ReadTsKvQuery query);
}