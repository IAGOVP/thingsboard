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
package org.thingsboard.server.service.queue.processing;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
/**
 * Rule-engine message submit strategy: sequential tb rule engine submit strategy.
 * <p>Controls parallelism and ordering when a pack of {@code TbMsg} is handed to actors.
 */

@Slf4j
public class SequentialTbRuleEngineSubmitStrategy extends AbstractTbRuleEngineSubmitStrategy {

    private final AtomicInteger msgIdx = new AtomicInteger(0);
    private volatile BiConsumer<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> msgConsumer;
    private volatile UUID expectedMsgId;

    /**
     * Constructs {@link SequentialTbRuleEngineSubmitStrategy} with the supplied dependencies and configuration.
     * @param queueName queue name
     */

    public SequentialTbRuleEngineSubmitStrategy(String queueName) {
        super(queueName);
    }

    /**
     * Submits attempt.
     * @param msgConsumer msg consumer
     * @return @Override
    public void
     */

    @Override
    public void submitAttempt(BiConsumer<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> msgConsumer) {
        this.msgConsumer = msgConsumer;
        msgIdx.set(0);
        submitNext();
    }

    /**
     * Updates update.
     * @param reprocessMap reprocess map
     * @return @Override
    public void
     */

    @Override
    public void update(ConcurrentMap<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> reprocessMap) {
        super.update(reprocessMap);
    }

    /**
     * Do on success.
     *
     * <p>Default implementation inherited from the supertype.
     * @param id id
     * @return @Override
    protected void
     */

    @Override
    protected void doOnSuccess(UUID id) {
        if (expectedMsgId.equals(id)) {
            msgIdx.incrementAndGet();
            submitNext();
        }
    }

    private void submitNext() {
        int listSize = orderedMsgList.size();
        int idx = msgIdx.get();
        if (idx < listSize) {
            IdMsgPair<TransportProtos.ToRuleEngineMsg> pair = orderedMsgList.get(idx);
            expectedMsgId = pair.uuid();
            if (log.isDebugEnabled()) {
                log.debug("[{}] submitting [{}] message to rule engine", queueName, pair.msg());
            }
            msgConsumer.accept(pair.uuid(), pair.msg());
        }
    }

}
