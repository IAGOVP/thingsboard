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
package org.thingsboard.server.dao.sql.event;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.event.EventType;

import java.util.concurrent.TimeUnit;
/**
 * Spring configuration for event partitionuration DAO beans.
 *
 * <p>Registers entity managers, repositories, and datasource routing.
 */


@Component
public class EventPartitionConfiguration {

    @Getter
    @Value("${sql.events.partition_size:168}")
    private int regularPartitionSizeInHours;
    @Getter
    @Value("${sql.events.debug_partition_size:1}")
    private int debugPartitionSizeInHours;

    private long regularPartitionSizeInMs;
    private long debugPartitionSizeInMs;
    /**
     * Init.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PostConstruct
    public void init() {
        regularPartitionSizeInMs = TimeUnit.HOURS.toMillis(regularPartitionSizeInHours);
        debugPartitionSizeInMs = TimeUnit.HOURS.toMillis(debugPartitionSizeInHours);
    }
    /**
     * Returns partition size in ms.
     *
     * @param eventType event type ({@link EventType})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    public long getPartitionSizeInMs(EventType eventType) {
        return eventType.isDebug() ? debugPartitionSizeInMs : regularPartitionSizeInMs;
    }
}
