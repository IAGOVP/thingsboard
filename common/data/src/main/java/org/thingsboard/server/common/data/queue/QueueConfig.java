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
package org.thingsboard.server.common.data.queue;

import lombok.Data;

/**
 * queue config contract.
 */
public interface QueueConfig {

    boolean isConsumerPerPartition();
/**
 * Returns poll interval.
 *
 * @return the int result
 */

    int getPollInterval();
/**
 * Of.
 *
 * @param consumerPerPartition consumer per partition
 * @param pollInterval poll interval
 * @return {@link QueueConfig}
 */

    static QueueConfig of(boolean consumerPerPartition, long pollInterval) {
        /**
         * Basic queue config.
         *
         * @param pollInterval poll interval
         * @return the return new value
         */
        return new BasicQueueConfig(consumerPerPartition, (int) pollInterval);
    }

    @Data
    class BasicQueueConfig implements QueueConfig {
        private final boolean consumerPerPartition;
        private final int pollInterval;
    }

}
