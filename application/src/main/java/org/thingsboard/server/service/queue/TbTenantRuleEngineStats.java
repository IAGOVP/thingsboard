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
package org.thingsboard.server.service.queue;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Consumer statistics counters for tb tenant rule engine stats.
 */

@Slf4j
@Data
public class TbTenantRuleEngineStats {

    private final UUID tenantId;

    private final AtomicInteger totalMsgCounter = new AtomicInteger(0);
    private final AtomicInteger successMsgCounter = new AtomicInteger(0);
    private final AtomicInteger tmpTimeoutMsgCounter = new AtomicInteger(0);
    private final AtomicInteger tmpFailedMsgCounter = new AtomicInteger(0);

    private final AtomicInteger timeoutMsgCounter = new AtomicInteger(0);
    private final AtomicInteger failedMsgCounter = new AtomicInteger(0);

    private final Map<String, AtomicInteger> counters = new HashMap<>();

    /**
     * Constructs {@link TbTenantRuleEngineStats} with the supplied dependencies and configuration.
     * @param tenantId tenant that owns the subscription or entity
     */

    public TbTenantRuleEngineStats(UUID tenantId) {
        this.tenantId = tenantId;
        counters.put(TbRuleEngineConsumerStats.TOTAL_MSGS, totalMsgCounter);
        counters.put(TbRuleEngineConsumerStats.SUCCESSFUL_MSGS, successMsgCounter);
        counters.put(TbRuleEngineConsumerStats.TIMEOUT_MSGS, timeoutMsgCounter);
        counters.put(TbRuleEngineConsumerStats.FAILED_MSGS, failedMsgCounter);

        counters.put(TbRuleEngineConsumerStats.TMP_TIMEOUT, tmpTimeoutMsgCounter);
        counters.put(TbRuleEngineConsumerStats.TMP_FAILED, tmpFailedMsgCounter);
    }

    /**
     * Logs success.
     */

public void logSuccess() {
        totalMsgCounter.incrementAndGet();
        successMsgCounter.incrementAndGet();
    }

    /**
     * Logs failed.
     */

public void logFailed() {
        totalMsgCounter.incrementAndGet();
        failedMsgCounter.incrementAndGet();
    }

    /**
     * Logs timeout.
     */

public void logTimeout() {
        totalMsgCounter.incrementAndGet();
        timeoutMsgCounter.incrementAndGet();
    }

    /**
     * Logs tmp failed.
     */

public void logTmpFailed() {
        totalMsgCounter.incrementAndGet();
        tmpFailedMsgCounter.incrementAndGet();
    }

    /**
     * Logs tmp timeout.
     */

public void logTmpTimeout() {
        totalMsgCounter.incrementAndGet();
        tmpTimeoutMsgCounter.incrementAndGet();
    }

    /**
     * Print stats.
     */

public void printStats() {
        int total = totalMsgCounter.get();
        if (total > 0) {
            StringBuilder stats = new StringBuilder();
            counters.forEach((label, value) -> {
                stats.append(label).append(" = [").append(value.get()).append("]");
            });
            log.info("[{}] Stats: {}", tenantId, stats);
        }
    }

    /**
     * Resets reset.
     */

    /**
     * Resets.
     */

    /**
     * Resets.
     */

    /**
     * Resets.
     */

    public void reset() {
        counters.values().forEach(counter -> counter.set(0));
    }
}
