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
package org.thingsboard.server.queue.edqs;

import org.thingsboard.server.gen.transport.TransportProtos.FromEdqsMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdqsMsg;
import org.thingsboard.server.queue.TbQueueAdmin;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.TbQueueHandler;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.common.PartitionedQueueResponseTemplate;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;


/**
 * Factory for EDQS Kafka topics, producers, and request-response templates.
 */

public interface EdqsQueueFactory {
    /**
     * Creates edqs events consumer.
     *
     * @return {@link TbQueueConsumer}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbQueueConsumer<TbProtoQueueMsg<ToEdqsMsg>> createEdqsEventsConsumer();
    /**
     * Creates edqs events to backup consumer.
     *
     * @return {@link TbQueueConsumer}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbQueueConsumer<TbProtoQueueMsg<ToEdqsMsg>> createEdqsEventsToBackupConsumer();
    /**
     * Creates edqs state consumer.
     *
     * @return {@link TbQueueConsumer}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbQueueConsumer<TbProtoQueueMsg<ToEdqsMsg>> createEdqsStateConsumer();
    /**
     * Creates edqs state producer.
     *
     * @return {@link TbQueueProducer}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbQueueProducer<TbProtoQueueMsg<ToEdqsMsg>> createEdqsStateProducer();
    /**
     * Creates edqs response template.
     *
     * @param handler handler ({@link TbQueueHandler})
     * @return {@link PartitionedQueueResponseTemplate}
     * @throws Exception if an unexpected error occurs during processing
     */

    PartitionedQueueResponseTemplate<TbProtoQueueMsg<ToEdqsMsg>, TbProtoQueueMsg<FromEdqsMsg>> createEdqsResponseTemplate(TbQueueHandler<TbProtoQueueMsg<ToEdqsMsg>, TbProtoQueueMsg<FromEdqsMsg>> handler);
    /**
     * Returns edqs queue admin.
     *
     * @return {@link TbQueueAdmin}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbQueueAdmin getEdqsQueueAdmin();

}
