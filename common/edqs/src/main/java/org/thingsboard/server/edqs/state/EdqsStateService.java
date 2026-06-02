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

import org.thingsboard.server.common.data.ObjectType;
import org.thingsboard.server.common.data.edqs.EdqsEventType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdqsMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.common.consumer.PartitionedQueueConsumerManager;

import java.util.List;
import java.util.Set;
/**
 * Persists and replays EDQS Kafka state topic so consumers catch up after restart; tracks partition assignment and {@link #isReady()} for health checks.
 */
public interface EdqsStateService {

    /** Registers consumers that start after state replay for assigned partitions. */
    void init(PartitionedQueueConsumerManager<TbProtoQueueMsg<ToEdqsMsg>> eventConsumer, List<PartitionedQueueConsumerManager<?>> otherConsumers);

    /** Replays state records for newly assigned Kafka partitions. */
    void process(Set<TopicPartitionInfo> partitions);

    /** Persists raw event before {@link org.thingsboard.server.edqs.repo.EdqsRepository} mutation. */
    void save(TenantId tenantId, ObjectType type, String key, EdqsEventType eventType, ToEdqsMsg msg);

    /** True when state replay finished and {@link org.thingsboard.server.edqs.EdqsController} may return 200. */
    boolean isReady();

    /** Flushes and closes state storage. */
    void stop();

}
