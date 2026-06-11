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
package org.thingsboard.server.service.telemetry;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.AggregationParams;
import org.thingsboard.server.common.data.kv.BaseReadTsKvQuery;
import org.thingsboard.server.common.data.kv.IntervalType;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.service.security.AccessValidator;
import org.thingsboard.server.service.security.ValidationResult;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;

import java.util.List;
import java.util.stream.Collectors;

    /**
     * Default Spring implementation for tb telemetry service (telemetry subscription and WebSocket push to clients).
     *
     * <p>Registered as a {@code @Service} or {@code @Component} bean.
     */

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultTbTelemetryService implements TbTelemetryService {

    private final TimeseriesService tsService;
    private final AccessValidator accessValidator;
    /**
     * Returns timeseries.
     *
     * @param entityId target entity identifier
     * @param keys keys ({@link List})
     * @param startTs start ts ({@link Long})
     * @param endTs end ts ({@link Long})
     * @param intervalType interval type ({@link IntervalType})
     * @param interval interval ({@link Long})
     * @param timeZone time zone ({@link String})
     * @param limit limit ({@link Integer})
     * @param agg agg ({@link Aggregation})
     * @param orderBy order by ({@link String})
     * @param useStrictDataTypes use strict data types ({@link Boolean})
     * @param currentUser current user ({@link SecurityUser})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListenableFuture<List<TsKvEntry>> getTimeseries(EntityId entityId, List<String> keys, Long startTs, Long endTs, IntervalType intervalType,
                                                           Long interval, String timeZone, Integer limit, Aggregation agg, String orderBy,
                                                           Boolean useStrictDataTypes, SecurityUser currentUser) {
        SettableFuture<List<TsKvEntry>> future = SettableFuture.create();
        accessValidator.validate(currentUser, Operation.READ_TELEMETRY, entityId, new FutureCallback<>() {
            /**
             * Handles success.
             *
             * @param validationResult validation result ({@link ValidationResult})
             * @return nothing
             * @throws Exception if an unexpected error occurs during processing
             */
            @Override
            public void onSuccess(ValidationResult validationResult) {
                try {
                    AggregationParams params;
                    if (Aggregation.NONE.equals(agg)) {
                        params = AggregationParams.none();
                    } else if (intervalType == null || IntervalType.MILLISECONDS.equals(intervalType)) {
                        params = interval == 0L ? AggregationParams.none() : AggregationParams.milliseconds(agg, interval);
                    } else {
                        params = AggregationParams.calendar(agg, intervalType, timeZone);
                    }
                    List<ReadTsKvQuery> queries = keys.stream().map(key -> new BaseReadTsKvQuery(key, startTs, endTs, params, limit, orderBy)).collect(Collectors.toList());
                    Futures.addCallback(tsService.findAll(currentUser.getTenantId(), entityId, queries), new FutureCallback<>() {
                        /**
                         * Handles success.
                         *
                         * @param result result ({@link List})
                         * @return nothing
                         * @throws Exception if an unexpected error occurs during processing
                         */
                        @Override
                        public void onSuccess(List<TsKvEntry> result) {
                            future.set(result);
                        }
                        /**
                         * Handles failure.
                         *
                         * @param t t ({@link Throwable})
                         * @return nothing
                         * @throws Exception if an unexpected error occurs during processing
                         */

                        @Override
                        public void onFailure(Throwable t) {
                            future.setException(t);
                        }
                    }, MoreExecutors.directExecutor());
                } catch (Throwable e) {
                    onFailure(e);
                }
            }
            /**
             * Handles failure.
             *
             * @param t t ({@link Throwable})
             * @return nothing
             * @throws Exception if an unexpected error occurs during processing
             */

            @Override
            public void onFailure(Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

}
