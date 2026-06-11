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
package org.thingsboard.server.service.subscription;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.ThingsBoardExecutors;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
/**
 * Scheduled tasks for subscription housekeeping (stale session cleanup, stats).
 */

@TbCoreComponent
@Service
public class SubscriptionSchedulerComponent {

    @Getter
    private ScheduledExecutorService scheduler;

    /**
     * Initializes executor.
     * @return @PostConstruct
    public void
     */

    @PostConstruct
    public void initExecutor() {
        scheduler = ThingsBoardExecutors.newSingleThreadScheduledExecutor("subscription-scheduler");
    }

    /**
     * Shutdown executor.
     * @return @PreDestroy
    public void
     */

    @PreDestroy
    public void shutdownExecutor() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    /**
     * Schedules with fixed delay.
     * @param command command
     * @param initialDelay initial delay
     * @param delay delay
     * @param unit unit
     * @return {@link ScheduledFuture}
     */

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
