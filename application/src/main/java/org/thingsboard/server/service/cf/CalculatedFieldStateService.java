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
package org.thingsboard.server.service.cf;

import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.exception.CalculatedFieldStateException;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.common.consumer.PartitionedQueueConsumerManager;
import org.thingsboard.server.queue.discovery.QueueKey;
import org.thingsboard.server.service.cf.ctx.CalculatedFieldEntityCtxId;
import org.thingsboard.server.service.cf.ctx.state.CalculatedFieldState;

import java.util.Set;


/**

 * Service contract for calculated field state operations (calculated fields (calculated-field argument resolution, runtime state, and result processing)).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */


public interface CalculatedFieldStateService {
/**
 * Init.
 *
 * @param eventConsumer event consumer ({@link PartitionedQueueConsumerManager})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */



    void init(PartitionedQueueConsumerManager<TbProtoQueueMsg<ToCalculatedFieldMsg>> eventConsumer);
/**
 * Persist state.
 *
 * @param stateId state id ({@link CalculatedFieldEntityCtxId})
 * @param state state ({@link CalculatedFieldState})
 * @param callback queue callback invoked when processing completes
 * @return nothing
 * @throws CalculatedFieldStateException if calculated field state exception is thrown during processing
 */

    void persistState(CalculatedFieldEntityCtxId stateId, CalculatedFieldState state, TbCallback callback) throws CalculatedFieldStateException;
/**
 * Deletes state.
 *
 * @param stateId state id ({@link CalculatedFieldEntityCtxId})
 * @param callback queue callback invoked when processing completes
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void deleteState(CalculatedFieldEntityCtxId stateId, TbCallback callback);
/**
 * Restore.
 *
 * @param queueKey queue key ({@link QueueKey})
 * @param partitions partitions ({@link Set})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void restore(QueueKey queueKey, Set<TopicPartitionInfo> partitions);
/**
 * Deletes the requested data.
 *
 * @param partitions partitions ({@link Set})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void delete(Set<TopicPartitionInfo> partitions);
/**
 * Returns partitions.
 *
 * @return {@link Set}
 * @throws Exception if an unexpected error occurs during processing
 */

    Set<TopicPartitionInfo> getPartitions();
/**
 * Stop.
 *
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void stop();

}
