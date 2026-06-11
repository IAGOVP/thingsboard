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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.calculatedField.CalculatedFieldStateRestoreMsg;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.exception.TenantNotFoundException;
import org.thingsboard.server.common.msg.CalculatedFieldStatePartitionRestoreMsg;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.exception.CalculatedFieldStateException;
import org.thingsboard.server.gen.transport.TransportProtos.CalculatedFieldStateProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.common.state.QueueStateService;
import org.thingsboard.server.queue.discovery.QueueKey;
import org.thingsboard.server.service.cf.ctx.CalculatedFieldEntityCtxId;
import org.thingsboard.server.service.cf.ctx.state.CalculatedFieldState;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.thingsboard.server.utils.CalculatedFieldUtils.fromProto;
import static org.thingsboard.server.utils.CalculatedFieldUtils.toProto;
/**
 * Abstract calculated field state service (calculated fields (calculated-field argument resolution, runtime state, and result processing)).
 */

@Slf4j
public abstract class AbstractCalculatedFieldStateService implements CalculatedFieldStateService {

    @Autowired
    @Lazy
    private ActorSystemContext actorSystemContext;

    protected QueueStateService<TbProtoQueueMsg<ToCalculatedFieldMsg>, TbProtoQueueMsg<CalculatedFieldStateProto>> stateService;
    /**
     * Persist state.
     *
     * @param stateId state id ({@link CalculatedFieldEntityCtxId})
     * @param state state ({@link CalculatedFieldState})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public final void persistState(CalculatedFieldEntityCtxId stateId, CalculatedFieldState state, TbCallback callback) {
        if (state.isSizeExceedsLimit()) {
            throw new CalculatedFieldStateException("State size exceeds the maximum allowed limit. The state will not be persisted to RocksDB.");
        }
        doPersist(stateId, toProto(stateId, state), callback);
    }
    /**
     * Do persist.
     *
     * @param stateId state id ({@link CalculatedFieldEntityCtxId})
     * @param stateMsgProto state msg proto ({@link CalculatedFieldStateProto})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract void doPersist(CalculatedFieldEntityCtxId stateId, CalculatedFieldStateProto stateMsgProto, TbCallback callback);
    /**
     * Deletes state.
     *
     * @param stateId state id ({@link CalculatedFieldEntityCtxId})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public final void deleteState(CalculatedFieldEntityCtxId stateId, TbCallback callback) {
        doRemove(stateId, callback);
    }
    /**
     * Do remove.
     *
     * @param stateId state id ({@link CalculatedFieldEntityCtxId})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract void doRemove(CalculatedFieldEntityCtxId stateId, TbCallback callback);
    /**
     * Processes restored state.
     *
     * @param stateMsg state msg ({@link CalculatedFieldStateProto})
     * @param partition partition ({@link TopicPartitionInfo})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void processRestoredState(CalculatedFieldStateProto stateMsg, TopicPartitionInfo partition, TbCallback callback) {
        var id = fromProto(stateMsg.getId());
        if (partition == null) {
            try {
                partition = actorSystemContext.resolve(ServiceType.TB_RULE_ENGINE, DataConstants.CF_QUEUE_NAME, id.tenantId(), id.entityId());
            } catch (TenantNotFoundException e) {
                log.debug("Skipping CF state msg for non-existing tenant {}", id.tenantId());
                return;
            }
        }
        var state = fromProto(id, stateMsg);
        processRestoredState(id, state, partition, callback);
    }
    /**
     * Processes restored state.
     *
     * @param id id ({@link CalculatedFieldEntityCtxId})
     * @param state state ({@link CalculatedFieldState})
     * @param partition partition ({@link TopicPartitionInfo})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void processRestoredState(CalculatedFieldEntityCtxId id, CalculatedFieldState state, TopicPartitionInfo partition, TbCallback callback) {
        partition = partition.withTopic(DataConstants.CF_STATES_QUEUE_NAME);
        actorSystemContext.tellWithHighPriority(new CalculatedFieldStateRestoreMsg(id, state, partition, callback));
    }
    /**
     * Restore.
     *
     * @param queueKey queue key ({@link QueueKey})
     * @param partitions partitions ({@link Set})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void restore(QueueKey queueKey, Set<TopicPartitionInfo> partitions) {
        stateService.update(queueKey, partitions, new QueueStateService.RestoreCallback() {
            
            /**
             * Handles all partitions restored.
             *
             * @return nothing
             * @throws Exception if an unexpected error occurs during processing
             */

            @Override
            public void onAllPartitionsRestored() {
            }
            /**
             * Handles partition restored.
             *
             * @param partition partition ({@link TopicPartitionInfo})
             * @return nothing
             * @throws Exception if an unexpected error occurs during processing
             */

            @Override
            public void onPartitionRestored(TopicPartitionInfo partition) {
                partition = partition.withTopic(DataConstants.CF_STATES_QUEUE_NAME);
                actorSystemContext.tellWithHighPriority(new CalculatedFieldStatePartitionRestoreMsg(partition));
            }
        });
    }
    /**
     * Deletes the requested data.
     *
     * @param partitions partitions ({@link Set})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void delete(Set<TopicPartitionInfo> partitions) {
        stateService.delete(partitions);
    }
    /**
     * Returns partitions.
     *
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Set<TopicPartitionInfo> getPartitions() {
        return stateService.getPartitions().values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }
    /**
     * Stop.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void stop() {
        stateService.stop();
    }

}
