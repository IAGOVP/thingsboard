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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.ThingsBoardExecutors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * Spring component for scheduled log executor component (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).
 */







@Component
public class ScheduledLogExecutorComponent {

    private ScheduledExecutorService schedulerLogExecutor;
    /**
     * Init.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PostConstruct
    public void init() {
        schedulerLogExecutor = ThingsBoardExecutors.newSingleThreadScheduledExecutor("sql-log");
    }
    /**
     * Stop.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PreDestroy
    public void stop() {
        if (schedulerLogExecutor != null) {
            schedulerLogExecutor.shutdownNow();
        }
    }
    /**
     * Schedule at fixed rate.
     *
     * @param command command ({@link Runnable})
     * @param initialDelay initial delay
     * @param period period
     * @param unit unit ({@link TimeUnit})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        schedulerLogExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
}
