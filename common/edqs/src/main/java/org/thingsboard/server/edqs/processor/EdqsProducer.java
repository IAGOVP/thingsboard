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
package org.thingsboard.server.edqs.processor;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.thingsboard.server.common.data.ObjectType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.edqs.state.EdqsPartitionService;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdqsMsg;
import org.thingsboard.server.queue.TbQueueCallback;
import org.thingsboard.server.queue.TbQueueMsgMetadata;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.kafka.TbKafkaProducerTemplate;

/**
 * Publishes EDQS sync events from tb-core into the EDQS Kafka topic.
 *
 * <p>Used when entities, attributes, relations, or latest telemetry change and the SQL-based entity query path is disabled in favor of EDQS.
 */

@Slf4j
@Builder
@RequiredArgsConstructor
public class EdqsProducer {

    private final TbQueueProducer<TbProtoQueueMsg<ToEdqsMsg>> producer;
    private final EdqsPartitionService partitionService;
    /**
     * Send.
     *
     * @param tenantId tenant that owns the indexed entities
     * @param type type ({@link ObjectType})
     * @param key key ({@link String})
     * @param msg Kafka queue message wrapper
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void send(TenantId tenantId, ObjectType type, String key, ToEdqsMsg msg) {
        TopicPartitionInfo tpi = TopicPartitionInfo.builder()
                .topic(producer.getDefaultTopic())
                .partition(partitionService.resolvePartition(tenantId, key))
                .build();
        TbQueueCallback callback = new TbQueueCallback() {
            @Override
            public void onSuccess(TbQueueMsgMetadata metadata) {
                log.trace("[{}][{}][{}] Published msg to {}: {}", tenantId, type, key, tpi, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof RecordTooLargeException) {
                    if (!log.isDebugEnabled()) {
                        log.warn("[{}][{}][{}] Failed to publish msg to {}", tenantId, type, key, tpi, t); // not logging the whole message
                        return;
                    }
                }
                log.warn("[{}][{}][{}] Failed to publish msg to {}: {}", tenantId, type, key, tpi, msg, t);
            }
        };
        if (producer instanceof TbKafkaProducerTemplate<TbProtoQueueMsg<ToEdqsMsg>> kafkaProducer) {
            kafkaProducer.send(tpi, key, new TbProtoQueueMsg<>(null, msg), callback); // specifying custom key for compaction
        } else {
            producer.send(tpi, new TbProtoQueueMsg<>(null, msg), callback);
        }
    }
    /**
     * Shuts down EDQS consumers and flushes pending state.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void stop() {
        producer.stop();
    }

}
