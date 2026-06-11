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

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Tb pack processing context component in the ThingsBoard queue layer.
 */

@Slf4j
public class TbPackProcessingContext<T> {

    private final AtomicInteger pendingCount;
    private final CountDownLatch processingTimeoutLatch;
    private final ConcurrentMap<UUID, T> ackMap;
    private final ConcurrentMap<UUID, T> failedMap;

    /**
     * Constructs {@link TbPackProcessingContext} with the supplied dependencies and configuration.
     * @param processingTimeoutLatch processing timeout latch
     * @param ackMap ack map
     * @param failedMap failed map
     */

    public TbPackProcessingContext(CountDownLatch processingTimeoutLatch,
                                   ConcurrentMap<UUID, T> ackMap,
                                   ConcurrentMap<UUID, T> failedMap) {
        this.processingTimeoutLatch = processingTimeoutLatch;
        this.pendingCount = new AtomicInteger(ackMap.size());
        this.ackMap = ackMap;
        this.failedMap = failedMap;
    }

    /**
     * Waits until await.
     * @param packProcessingTimeout pack processing timeout
     * @param milliseconds milliseconds
     * @return boolean result
     * @throws InterruptedException if processing fails
     */

    public boolean await(long packProcessingTimeout, TimeUnit milliseconds) throws InterruptedException {
        return processingTimeoutLatch.await(packProcessingTimeout, milliseconds);
    }

    /**
     * Invoked when success occurs.
     * @param id id
     */

    public void onSuccess(UUID id) {
        boolean empty = false;
        T msg = ackMap.remove(id);
        if (msg != null) {
            empty = pendingCount.decrementAndGet() == 0;
        }
        if (empty) {
            processingTimeoutLatch.countDown();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Items left: {}", ackMap.size());
                for (T t : ackMap.values()) {
                    log.trace("left item: {}", t);
                }
            }
        }
    }

    /**
     * Invoked when failure occurs.
     * @param id id
     * @param t t
     */

    public void onFailure(UUID id, Throwable t) {
        boolean empty = false;
        T msg = ackMap.remove(id);
        if (msg != null) {
            empty = pendingCount.decrementAndGet() == 0;
            failedMap.put(id, msg);
            if (log.isTraceEnabled()) {
                log.trace("Items left: {}", ackMap.size());
                for (T v : ackMap.values()) {
                    log.trace("left item: {}", v);
                }
            }
        }
        if (empty) {
            processingTimeoutLatch.countDown();
        }
    }

    /**
     * Returns ack map.
     * @return {@link ConcurrentMap}
     */

    public ConcurrentMap<UUID, T> getAckMap() {
        return ackMap;
    }

    /**
     * Returns failed map.
     * @return {@link ConcurrentMap}
     */

    public ConcurrentMap<UUID, T> getFailedMap() {
        return failedMap;
    }
}
