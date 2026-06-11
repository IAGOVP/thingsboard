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

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.IntervalType;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQueryResult;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.stats.StatsFactory;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.dictionary.KeyDictionaryDao;
import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.TbSqlBlockingQueueParams;
import org.thingsboard.server.dao.sql.TbSqlBlockingQueueWrapper;
import org.thingsboard.server.dao.sqlts.insert.InsertTsRepository;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
import org.thingsboard.server.dao.timeseries.TimeseriesDao;
import org.thingsboard.server.dao.util.TimeUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
/**
 * Abstract chunked aggregation timeseries dao (time-series SQL/Timescale persistence (SQL/Timescale time-series key-value storage)).
 */







@SuppressWarnings("UnstableApiUsage")
@Slf4j
public abstract class AbstractChunkedAggregationTimeseriesDao extends AbstractSqlTimeseriesDao implements TimeseriesDao {

    @Autowired
    protected TsKvRepository tsKvRepository;

    @Autowired
    protected InsertTsRepository<TsKvEntity> insertRepository;

    protected TbSqlBlockingQueueWrapper<TsKvEntity, Void> tsQueue;
    @Autowired
    private StatsFactory statsFactory;

    @Autowired
    private KeyDictionaryDao keyDictionaryDao;
    /**
     * Init.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PostConstruct
    protected void init() {
        TbSqlBlockingQueueParams tsParams = TbSqlBlockingQueueParams.builder()
                .logName("TS")
                .batchSize(tsBatchSize)
                .maxDelay(tsMaxDelay)
                .statsPrintIntervalMs(tsStatsPrintIntervalMs)
                .statsNamePrefix("ts")
                .batchSortEnabled(batchSortEnabled)
                .build();

        Function<TsKvEntity, Integer> hashcodeFunction = entity -> entity.getEntityId().hashCode();
        tsQueue = new TbSqlBlockingQueueWrapper<>(tsParams, hashcodeFunction, tsBatchThreads, statsFactory);
        tsQueue.init(logExecutor, v -> insertRepository.saveOrUpdate(v),
                Comparator.comparing((Function<TsKvEntity, UUID>) AbstractTsKvEntity::getEntityId)
                        .thenComparing(AbstractTsKvEntity::getKey)
                        .thenComparing(AbstractTsKvEntity::getTs)
        );
    }
    /**
     * Destroy.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PreDestroy
    protected void destroy() {
        if (tsQueue != null) {
            tsQueue.destroy();
        }
    }
    /**
     * Removes the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param query filter and sort query definition
     * @return future completing with {@link Void}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListenableFuture<Void> remove(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        return service.submit(() -> {
            tsKvRepository.delete(
                    entityId.getId(),
                    keyDictionaryDao.getOrSaveKeyId(query.getKey()),
                    query.getStartTs(),
                    query.getEndTs());
            return null;
        });
    }
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

    @Override
    public ListenableFuture<Integer> savePartition(TenantId tenantId, EntityId entityId, long tsKvEntryTs, String key) {
        return Futures.immediateFuture(null);
    }
    /**
     * Finds all async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param queries queries ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListenableFuture<List<ReadTsKvQueryResult>> findAllAsync(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries) {
        return processFindAllAsync(tenantId, entityId, queries);
    }
    /**
     * Finds all async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param query filter and sort query definition
     * @return future completing with {@link ReadTsKvQueryResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListenableFuture<ReadTsKvQueryResult> findAllAsync(TenantId tenantId, EntityId entityId, ReadTsKvQuery query) {
        var aggParams = query.getAggParameters();
        if (Aggregation.NONE.equals(aggParams.getAggregation()) || aggParams.getInterval() < 1) {
            return service.submit(() -> findAllWithLimit(entityId, query));
        } else {
            List<ListenableFuture<Optional<TsKvEntity>>> futures = new ArrayList<>();
            var intervalType = aggParams.getIntervalType();
            long startPeriod = query.getStartTs();
            long endPeriod = Math.max(query.getStartTs() + 1, query.getEndTs());
            while (startPeriod < endPeriod) {
                long startTs = startPeriod;
                long endTs;
                if (IntervalType.MILLISECONDS.equals(intervalType)) {
                    endTs = startPeriod + aggParams.getInterval();
                } else {
                    endTs = TimeUtils.calculateIntervalEnd(startTs, intervalType, aggParams.getTzId());
                }
                endTs = Math.min(endTs, endPeriod);
                long ts = startTs + (endTs - startTs) / 2;
                ListenableFuture<Optional<TsKvEntity>> aggregateTsKvEntry = findAndAggregateAsync(entityId, query.getKey(), startTs, endTs, ts, query.getAggregation());
                futures.add(aggregateTsKvEntry);
                startPeriod = endTs;
            }
            return getReadTsKvQueryResultFuture(query, Futures.allAsList(futures));
        }
    }

    ReadTsKvQueryResult findAllWithLimit(EntityId entityId, ReadTsKvQuery query) {
        Integer keyId = keyDictionaryDao.getOrSaveKeyId(query.getKey());
        List<TsKvEntity> tsKvEntities = tsKvRepository.findAllWithLimit(
                entityId.getId(),
                keyId,
                query.getStartTs(),
                query.getEndTs(),
                PageRequest.ofSize(query.getLimit()).withSort(Direction.fromString(query.getOrder()), "ts"));
        tsKvEntities.forEach(tsKvEntity -> tsKvEntity.setStrKey(query.getKey()));
        List<TsKvEntry> tsKvEntries = DaoUtil.convertDataList(tsKvEntities);
        long lastTs = tsKvEntries.stream().map(TsKvEntry::getTs).max(Long::compare).orElse(query.getStartTs());
        return new ReadTsKvQueryResult(query.getId(), tsKvEntries, lastTs);
    }

    ListenableFuture<Optional<TsKvEntity>> findAndAggregateAsync(EntityId entityId, String key, long startTs, long endTs, long ts, Aggregation aggregation) {
        return service.submit(() -> {
            TsKvEntity entity = switchAggregation(entityId, key, startTs, endTs, aggregation);
            if (entity != null && entity.isNotEmpty()) {
                entity.setEntityId(entityId.getId());
                entity.setStrKey(key);
                entity.setTs(ts);
                return Optional.of(entity);
            } else {
                return Optional.empty();
            }
        });
    }
    /**
     * Switch aggregation.
     *
     * @param entityId target entity identifier
     * @param key attribute or cache key
     * @param startTs interval start timestamp (epoch ms)
     * @param endTs interval end timestamp (epoch ms)
     * @param aggregation aggregation ({@link Aggregation})
     * @return {@link TsKvEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected TsKvEntity switchAggregation(EntityId entityId, String key, long startTs, long endTs, Aggregation aggregation) {
        var keyId = keyDictionaryDao.getOrSaveKeyId(key);
        switch (aggregation) {
            case AVG:
                return tsKvRepository.findAvg(entityId.getId(), keyId, startTs, endTs);
            case MAX:
                var max = tsKvRepository.findNumericMax(entityId.getId(), keyId, startTs, endTs);
                if (max.isNotEmpty()) {
                    return max;
                } else {
                    return tsKvRepository.findStringMax(entityId.getId(), keyId, startTs, endTs);
                }
            case MIN:
                var min = tsKvRepository.findNumericMin(entityId.getId(), keyId, startTs, endTs);
                if (min.isNotEmpty()) {
                    return min;
                } else {
                    return tsKvRepository.findStringMin(entityId.getId(), keyId, startTs, endTs);
                }
            case SUM:
                return tsKvRepository.findSum(entityId.getId(), keyId, startTs, endTs);
            case COUNT:
                return tsKvRepository.findCount(entityId.getId(), keyId, startTs, endTs);
            default:
                throw new IllegalArgumentException("Not supported aggregation type: " + aggregation);
        }
    }
}
