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
package org.thingsboard.server.edqs.state;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.ObjectType;
import org.thingsboard.server.common.data.edqs.EdqsEventType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.edqs.processor.EdqsProcessor;
import org.thingsboard.server.edqs.util.EdqsRocksDb;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdqsMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.common.consumer.PartitionedQueueConsumerManager;
import org.thingsboard.server.queue.discovery.DiscoveryService;
import org.thingsboard.server.queue.edqs.InMemoryEdqsComponent;

import java.util.List;
import java.util.Set;

import static org.thingsboard.server.common.msg.queue.TopicPartitionInfo.withTopic;

/**
 * File-based EDQS state store for single-node or development deployments.
 */

@Service
@RequiredArgsConstructor
@InMemoryEdqsComponent
@Slf4j
public class LocalEdqsStateService implements EdqsStateService {

    private final EdqsRocksDb db;
    private final DiscoveryService discoveryService;
    @Autowired @Lazy
    private EdqsProcessor processor;

    private PartitionedQueueConsumerManager<TbProtoQueueMsg<ToEdqsMsg>> eventConsumer;
    private List<PartitionedQueueConsumerManager<?>> otherConsumers;

    private boolean ready = false;
    /**
     * Starts Kafka consumers and wires partition/state services.
     *
     * @param eventConsumer event consumer ({@link PartitionedQueueConsumerManager})
     * @param otherConsumers other consumers ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void init(PartitionedQueueConsumerManager<TbProtoQueueMsg<ToEdqsMsg>> eventConsumer, List<PartitionedQueueConsumerManager<?>> otherConsumers) {
        this.eventConsumer = eventConsumer;
        this.otherConsumers = otherConsumers;
    }
    /**
     * Processes the requested data.
     *
     * @param partitions partitions ({@link Set})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void process(Set<TopicPartitionInfo> partitions) {
        if (!ready) {
            db.forEach((key, value) -> {
                try {
                    ToEdqsMsg edqsMsg = ToEdqsMsg.parseFrom(value);
                    log.trace("[{}] Restored msg from RocksDB: {}", key, edqsMsg);
                    processor.process(edqsMsg, false);
                } catch (Exception e) {
                    log.error("[{}] Failed to restore value", key, e);
                }
            });
            log.info("Restore completed");
        }
        ready = true;
        discoveryService.setReady(true);

        eventConsumer.update(withTopic(partitions, eventConsumer.getTopic()));
        for (PartitionedQueueConsumerManager<?> consumer : otherConsumers) {
            consumer.update(withTopic(partitions, consumer.getTopic()));
        }
    }
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the indexed entities
     * @param type type ({@link ObjectType})
     * @param key key ({@link String})
     * @param eventType event type ({@link EdqsEventType})
     * @param msg Kafka queue message wrapper
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void save(TenantId tenantId, ObjectType type, String key, EdqsEventType eventType, ToEdqsMsg msg) {
        log.trace("Save to RocksDB: {} {} {} {}", tenantId, type, key, msg);
        try {
            if (eventType == EdqsEventType.DELETED) {
                db.delete(key);
            } else {
                db.put(key, msg.toByteArray());
            }
        } catch (Exception e) {
            log.error("[{}] Failed to save event {}", key, msg, e);
        }
    }
    /**
     * Returns true when all assigned Kafka partitions have been restored and the index is queryable.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isReady() {
        return ready;
    }
    /**
     * Shuts down EDQS consumers and flushes pending state.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void stop() {
    }

}
