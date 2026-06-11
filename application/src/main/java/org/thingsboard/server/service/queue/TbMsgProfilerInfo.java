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

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.RuleNodeId;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Tb msg profiler info component in the ThingsBoard queue layer.
 */

@Slf4j
public class TbMsgProfilerInfo {
    private final UUID msgId;
    private AtomicLong totalProcessingTime = new AtomicLong();
    private Lock stateLock = new ReentrantLock();
    private RuleNodeId currentRuleNodeId;
    private long stateChangeTime;

    /**
     * Constructs {@link TbMsgProfilerInfo} with the supplied dependencies and configuration.
     * @param msgId msg id
     */

    public TbMsgProfilerInfo(UUID msgId) {
        this.msgId = msgId;
    }

    /**
     * Invoked when start occurs.
     * @param ruleNodeId rule node identifier
     */

    public void onStart(RuleNodeId ruleNodeId) {
        long currentTime = System.currentTimeMillis();
        stateLock.lock();
        try {
            currentRuleNodeId = ruleNodeId;
            stateChangeTime = currentTime;
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Invoked when end occurs.
     * @param ruleNodeId rule node identifier
     * @return numeric result
     */

    public long onEnd(RuleNodeId ruleNodeId) {
        long currentTime = System.currentTimeMillis();
        stateLock.lock();
        try {
            if (ruleNodeId.equals(currentRuleNodeId)) {
                long processingTime = currentTime - stateChangeTime;
                stateChangeTime = currentTime;
                totalProcessingTime.addAndGet(processingTime);
                currentRuleNodeId = null;
                return processingTime;
            } else {
                log.trace("[{}] Invalid sequence of rule node processing detected. Expected [{}] but was [{}]", msgId, currentRuleNodeId, ruleNodeId);
                return 0;
            }
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Invoked when timeout occurs.
     * @return {@link Map.Entry}
     */

    public Map.Entry<UUID, Long> onTimeout() {
        long currentTime = System.currentTimeMillis();
        stateLock.lock();
        try {
            if (currentRuleNodeId != null && stateChangeTime > 0) {
                long timeoutTime = currentTime - stateChangeTime;
                totalProcessingTime.addAndGet(timeoutTime);
                return new AbstractMap.SimpleEntry<>(currentRuleNodeId.getId(), timeoutTime);
            }
        } finally {
            stateLock.unlock();
        }
        return null;
    }
}
