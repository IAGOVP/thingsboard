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
package org.thingsboard.server.dao.nosql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thingsboard.server.cache.limits.RateLimitService;
import org.thingsboard.server.common.stats.StatsFactory;
import org.thingsboard.server.dao.entity.EntityService;
import org.thingsboard.server.dao.util.AbstractBufferedRateExecutor;
import org.thingsboard.server.dao.util.AsyncTaskContext;
import org.thingsboard.server.dao.util.BufferedRateExecutorType;
import org.thingsboard.server.dao.util.NoSqlAnyDao;
import org.thingsboard.server.queue.discovery.TbServiceInfoProvider;

/**
 * Spring component for cassandra buffered rate read executor (Cassandra async DAO infrastructure (Cassandra async DAO base classes)).
 */






@Component
@Slf4j
@NoSqlAnyDao
public class CassandraBufferedRateReadExecutor extends AbstractBufferedRateExecutor<CassandraStatementTask, TbResultSetFuture, TbResultSet> {

    public CassandraBufferedRateReadExecutor(
            @Value("${cassandra.query.buffer_size}") int queueLimit,
            @Value("${cassandra.query.concurrent_limit}") int concurrencyLimit,
            @Value("${cassandra.query.permit_max_wait_time}") long maxWaitTime,
            @Value("${cassandra.query.dispatcher_threads:2}") int dispatcherThreads,
            @Value("${cassandra.query.callback_threads:4}") int callbackThreads,
            @Value("${cassandra.query.poll_ms:50}") long pollMs,
            @Value("${cassandra.query.tenant_rate_limits.print_tenant_names}") boolean printTenantNames,
            @Value("${cassandra.query.print_queries_freq:0}") int printQueriesFreq,
            @Autowired StatsFactory statsFactory,
            @Autowired EntityService entityService,
            @Autowired RateLimitService rateLimitService,
            @Autowired(required = false) TbServiceInfoProvider serviceInfoProvider) {
        super(queueLimit, concurrencyLimit, maxWaitTime, dispatcherThreads, callbackThreads, pollMs, printQueriesFreq,
                BufferedRateExecutorType.READ, serviceInfoProvider, rateLimitService, statsFactory, entityService, printTenantNames);
    }
    /**
     * Print stats.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Scheduled(fixedDelayString = "${cassandra.query.rate_limit_print_interval_ms}")
    @Override
    public void printStats() {
        super.printStats();
    }
    /**
     * Stop.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PreDestroy
    public void stop() {
        super.stop();
    }
    /**
     * Creates the requested data.
     *
     * @return {@link SettableFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected SettableFuture<TbResultSet> create() {
        return SettableFuture.create();
    }
    /**
     * Wrap.
     *
     * @param task task ({@link CassandraStatementTask})
     * @param future future ({@link SettableFuture})
     * @return {@link TbResultSetFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected TbResultSetFuture wrap(CassandraStatementTask task, SettableFuture<TbResultSet> future) {
        return new TbResultSetFuture(future);
    }
    /**
     * Executes the requested data.
     *
     * @param taskCtx task ctx ({@link AsyncTaskContext})
     * @return future completing with {@link TbResultSet}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected ListenableFuture<TbResultSet> execute(AsyncTaskContext<CassandraStatementTask, TbResultSet> taskCtx) {
        CassandraStatementTask task = taskCtx.getTask();
        return task.executeAsync(
                statement ->
                        this.submit(new CassandraStatementTask(task.getTenantId(), task.getSession(), statement))
        );
    }

}
