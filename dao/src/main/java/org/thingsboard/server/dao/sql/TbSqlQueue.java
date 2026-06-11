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

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;






















/**






 * tb sql queue contract (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).






 */







public interface TbSqlQueue<E, R> {
    /**
     * Init.
     *
     * @param logExecutor log executor ({@link ScheduledLogExecutorComponent})
     * @param saveFunction save function ({@link Function})
     * @param batchUpdateComparator batch update comparator ({@link Comparator})
     * @param filter filter ({@link Function})
     * @param queueIndex queue index
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void init(ScheduledLogExecutorComponent logExecutor, Function<List<E>, List<R>> saveFunction, Comparator<E> batchUpdateComparator, Function<List<TbSqlQueueElement<E, R>>, List<TbSqlQueueElement<E, R>>> filter, int queueIndex);
    /**
     * Destroy.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void destroy();
    /**
     * Add.
     *
     * @param element element ({@link E})
     * @return future completing with {@link R}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<R> add(E element);
}
