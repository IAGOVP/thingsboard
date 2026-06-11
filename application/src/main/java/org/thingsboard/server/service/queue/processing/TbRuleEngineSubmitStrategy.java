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

import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * Rule-engine message submit strategy: tb rule engine submit strategy.
 * <p>Controls parallelism and ordering when a pack of {@code TbMsg} is handed to actors.
 */

public interface TbRuleEngineSubmitStrategy {

    /**
     * Initializes init.
     * @param msgs msgs
     */

    /**
     * Initializes.
     * @param msgs msgs
     */

    /**
     * Initializes.
     * @param msgs msgs
     */

    /**
     * Initializes.
     * @param msgs msgs
     */

    void init(List<TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> msgs);

    /**
     * Returns pending map.
     * @return {@link ConcurrentMap}
     */

    ConcurrentMap<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> getPendingMap();

    /**
     * Submits attempt.
     * @param msgConsumer msg consumer
     */

    void submitAttempt(BiConsumer<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> msgConsumer);

    /**
     * Updates update.
     * @param reprocessMap reprocess map
     */

    void update(ConcurrentMap<UUID, TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> reprocessMap);

    /**
     * Invoked when success occurs.
     * @param id id
     */

    void onSuccess(UUID id);

    /**
     * Stops stop.
     */

    /**
     * Stops.
     */

    /**
     * Stops.
     */

    /**
     * Stops.
     */

    void stop();
}
