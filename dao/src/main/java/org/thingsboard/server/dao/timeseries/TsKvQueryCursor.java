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

import lombok.Getter;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.TsKvEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.timeseries.CassandraBaseTimeseriesDao.DESC_ORDER;
















/**





 * Ts kv query cursor (Cassandra telemetry and latest-value DAO (Cassandra time-series DAO and latest-value caches)).





 */






public class TsKvQueryCursor extends QueryCursor {

    @Getter
    private final List<TsKvEntry> data;
    @Getter
    private final String orderBy;

    private int partitionIndex;
    private int currentLimit;

    public TsKvQueryCursor(String entityType, UUID entityId, ReadTsKvQuery baseQuery, List<Long> partitions) {
        super(entityType, entityId, baseQuery, partitions);
        this.orderBy = baseQuery.getOrder();
        this.partitionIndex = isDesc() ? partitions.size() - 1 : 0;
        this.data = new ArrayList<>();
        this.currentLimit = baseQuery.getLimit();
    }
    /**
     * Has next partition.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean hasNextPartition() {
        return isDesc() ? partitionIndex >= 0 : partitionIndex <= partitions.size() - 1;
    }
    /**
     * Is full.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isFull() {
        return currentLimit <= 0;
    }
    /**
     * Returns next partition.
     *
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public long getNextPartition() {
        long partition = partitions.get(partitionIndex);
        if (isDesc()) {
            partitionIndex--;
        } else {
            partitionIndex++;
        }
        return partition;
    }
    /**
     * Returns current limit.
     *
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    public int getCurrentLimit() {
        return currentLimit;
    }
    /**
     * Add data.
     *
     * @param newData new data ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addData(List<TsKvEntry> newData) {
        currentLimit -= newData.size();
        data.addAll(newData);
    }

    private boolean isDesc() {
        return orderBy.equals(DESC_ORDER);
    }
}
