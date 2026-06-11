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

import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Rule-engine message submit strategy: abstract tb rule engine submit strategy.
 * <p>Controls parallelism and ordering when a pack of {@code TbMsg} is handed to actors.
 */

public abstract class AbstractTbRuleEngineSubmitStrategy implements TbRuleEngineSubmitStrategy {

    protected final String queueName;
    protected List<IdMsgPair<TransportProtos.ToRuleEngineMsg>> orderedMsgList;
    private volatile boolean stopped;

    /**
     * Constructs {@link AbstractTbRuleEngineSubmitStrategy} with the supplied dependencies and configuration.
     * @param queueName queue name
     */

    public AbstractTbRuleEngineSubmitStrategy(String queueName) {
        this.queueName = queueName;
    }

    /**
     * Do on success.
     * @param id id
     */

    protected abstract void doOnSuccess(UUID id);

    /**
     * Initializes init.
     * @param msgs msgs
     * @return @Override
    public void
     */

    @Override
    public void init(List<TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> msgs) {
        orderedMsgList = msgs.stream().map(msg -> new IdMsgPair<>(UUID.randomUUID(), msg)).collect(Collectors.toList());
    }

    /**
     * Returns pending map.
     * @return {@link ConcurrentMap}
     */

    @Override
    public ConcurrentMap<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> getPendingMap() {
        return orderedMsgList.stream().collect(Collectors.toConcurrentMap(pair -> pair.uuid(), pair -> pair.msg()));
    }

    /**
     * Updates update.
     * @param reprocessMap reprocess map
     * @return @Override
    public void
     */

    @Override
    public void update(ConcurrentMap<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> reprocessMap) {
        List<IdMsgPair<TransportProtos.ToRuleEngineMsg>> newOrderedMsgList = new ArrayList<>(reprocessMap.size());
        for (IdMsgPair<TransportProtos.ToRuleEngineMsg> pair : orderedMsgList) {
            if (reprocessMap.containsKey(pair.uuid())) {
                if (StringUtils.isNotEmpty(pair.msg().getValue().getFailureMessage())) {
                    var toRuleEngineMsg = TransportProtos.ToRuleEngineMsg.newBuilder(pair.msg().getValue())
                            .clearFailureMessage()
                            .clearRelationTypes()
                            .build();
                    var newMsg = new TbProtoQueueMsg<>(pair.msg().getKey(), toRuleEngineMsg, pair.msg().getHeaders());
                    newOrderedMsgList.add(new IdMsgPair<>(pair.uuid(), newMsg));
                } else {
                    newOrderedMsgList.add(pair);
                }
            }
        }
        orderedMsgList = newOrderedMsgList;
    }

    /**
     * Invoked when success occurs.
     * @param id id
     * @return @Override
    public void
     */

    @Override
    public void onSuccess(UUID id) {
        if (!stopped) {
            doOnSuccess(id);
        }
    }

    /**
     * Stops stop.
     *
     * <p>Default implementation inherited from the supertype.
     * @return @Override
    public void
     */

    @Override
    public void stop() {
        stopped = true;
    }
}
