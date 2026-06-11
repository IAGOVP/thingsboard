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
package org.thingsboard.server.common.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.common.util.ThingsBoardExecutors;
import org.thingsboard.server.cache.ota.OtaPackageDataCache;
import org.thingsboard.server.common.transport.limits.TransportRateLimitService;
import org.thingsboard.server.queue.discovery.TbServiceInfoProvider;
import org.thingsboard.server.queue.scheduler.SchedulerComponent;

import java.util.concurrent.ExecutorService;

/**
 * Shared Spring context for transport microservices: {@link TransportService}, profile caches, rate limits, and scheduling.
 */
@Slf4j
@Data
public abstract class TransportContext {

    protected final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected TransportService transportService;

    @Autowired
    private TbServiceInfoProvider serviceInfoProvider;

    @Autowired
    private SchedulerComponent scheduler;

    @Getter
    private ExecutorService executor;

    @Getter
    @Autowired
    private OtaPackageDataCache otaPackageDataCache;

    @Autowired
    private TransportResourceCache transportResourceCache;

    @Autowired
    protected TransportRateLimitService rateLimitService;
    /**
     * Init.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @PostConstruct
    public void init() {
        executor = ThingsBoardExecutors.newWorkStealingPool(50, getClass());
    }
    /**
     * Stop.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @PreDestroy
    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
    /**
     * Returns node id.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getNodeId() {
        return serviceInfoProvider.getServiceId();
    }

}
