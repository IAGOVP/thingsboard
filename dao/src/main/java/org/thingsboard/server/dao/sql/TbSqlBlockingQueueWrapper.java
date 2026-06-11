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
package org.thingsboard.server.dao.sql;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.stats.MessagesStats;
import org.thingsboard.server.common.stats.StatsFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
/**
 * Tb sql blocking queue wrapper (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).
 */







@Slf4j
@Data
public class TbSqlBlockingQueueWrapper<E, R> {
    private final CopyOnWriteArrayList<TbSqlBlockingQueue<E, R>> queues = new CopyOnWriteArrayList<>();
    private final TbSqlBlockingQueueParams params;
    private final Function<E, Integer> hashCodeFunction;
    private final int maxThreads;
    private final StatsFactory statsFactory;

    
    /**
     * Init.
     *
     * @param logExecutor log executor ({@link ScheduledLogExecutorComponent})
     * @param saveFunction save function ({@link Consumer})
     * @param batchUpdateComparator batch update comparator ({@link Comparator})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void init(ScheduledLogExecutorComponent logExecutor, Consumer<List<E>> saveFunction, Comparator<E> batchUpdateComparator) {
        init(logExecutor, l -> { saveFunction.accept(l); return null; }, batchUpdateComparator, l -> l);
    }
    /**
     * Init.
     *
     * @param logExecutor log executor ({@link ScheduledLogExecutorComponent})
     * @param saveFunction save function ({@link Function})
     * @param batchUpdateComparator batch update comparator ({@link Comparator})
     * @param filter filter ({@link Function})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void init(ScheduledLogExecutorComponent logExecutor, Function<List<E>, List<R>> saveFunction, Comparator<E> batchUpdateComparator, Function<List<TbSqlQueueElement<E, R>>, List<TbSqlQueueElement<E, R>>> filter) {
        for (int i = 0; i < maxThreads; i++) {
            MessagesStats stats = statsFactory.createMessagesStats(params.getStatsNamePrefix() + ".queue." + i);
            TbSqlBlockingQueue<E, R> queue = new TbSqlBlockingQueue<>(params, stats);
            queues.add(queue);
            queue.init(logExecutor, saveFunction, batchUpdateComparator, filter, i);
        }
    }
    /**
     * Add.
     *
     * @param element element ({@link E})
     * @return future completing with {@link R}
     * @throws Exception if an unexpected error occurs during processing
     */

    public ListenableFuture<R> add(E element) {
        int queueIndex = element != null ? (hashCodeFunction.apply(element) & 0x7FFFFFFF) % maxThreads : 0;
        return queues.get(queueIndex).add(element);
    }
    /**
     * Destroy.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void destroy() {
        queues.forEach(TbSqlBlockingQueue::destroy);
    }
}
