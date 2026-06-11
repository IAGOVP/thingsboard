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
import java.util.function.BiConsumer;
/**
 * Rule-engine message submit strategy: burst tb rule engine submit strategy.
 * <p>Controls parallelism and ordering when a pack of {@code TbMsg} is handed to actors.
 */

@Slf4j
public class BurstTbRuleEngineSubmitStrategy extends AbstractTbRuleEngineSubmitStrategy {

    /**
     * Constructs {@link BurstTbRuleEngineSubmitStrategy} with the supplied dependencies and configuration.
     * @param queueName queue name
     */

    public BurstTbRuleEngineSubmitStrategy(String queueName) {
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
        if (log.isDebugEnabled()) {
            log.debug("[{}] submitting [{}] messages to rule engine", queueName, orderedMsgList.size());
        }
        orderedMsgList.forEach(pair -> msgConsumer.accept(pair.uuid(), pair.msg()));
    }

    /**
     * Do on success.
     * @param id id
     * @return @Override
    protected void
     */

    @Override
    protected void doOnSuccess(UUID id) {

    }
}
