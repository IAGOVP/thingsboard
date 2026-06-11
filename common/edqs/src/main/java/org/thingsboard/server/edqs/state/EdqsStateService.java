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
 * Persists and replays EDQS Kafka partition state for crash recovery.
 *
 * <p>Tracks which tenants are assigned to each consumer partition and exposes {@link #isReady()} for readiness probes.
 */

public interface EdqsStateService {

    
  /**
   * Starts Kafka consumers and wires partition/state services.
   *
   * @param eventConsumer event consumer ({@link PartitionedQueueConsumerManager})
   * @param otherConsumers other consumers ({@link List})
   * @return nothing
   * @throws Exception if an unexpected error occurs during processing
   */

    
    void init(PartitionedQueueConsumerManager<TbProtoQueueMsg<ToEdqsMsg>> eventConsumer, List<PartitionedQueueConsumerManager<?>> otherConsumers);

    
  /**
   * Processes the requested data.
   *
   * @param partitions partitions ({@link Set})
   * @return nothing
   * @throws Exception if an unexpected error occurs during processing
   */

    
    void process(Set<TopicPartitionInfo> partitions);

    
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

    
    void save(TenantId tenantId, ObjectType type, String key, EdqsEventType eventType, ToEdqsMsg msg);

    
   /**
    * Returns true when all assigned Kafka partitions have been restored and the index is queryable.
    *
    * @return the boolean result
    * @throws Exception if an unexpected error occurs during processing
    */

    
    boolean isReady();

    
    /**
     * Shuts down EDQS consumers and flushes pending state.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    
    void stop();

}
