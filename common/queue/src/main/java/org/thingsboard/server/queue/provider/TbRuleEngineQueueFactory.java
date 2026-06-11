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
package org.thingsboard.server.queue.provider;

import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.gen.js.JsInvokeProtos;
import org.thingsboard.server.gen.transport.TransportProtos.CalculatedFieldStateProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeEventNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToOtaPackageStateServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportMsg;
import org.thingsboard.server.queue.TbQueueAdmin;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.TbQueueRequestTemplate;
import org.thingsboard.server.queue.common.TbProtoJsQueueMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;


/**
 * Factory creating queue producers and consumers for TbRuleEngine message flows.
 */
public interface TbRuleEngineQueueFactory extends TbUsageStatsClientQueueFactory, HousekeeperClientQueueFactory, EdqsClientQueueFactory {

    
        
    TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createRuleEngineNotificationsMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer();
    TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToEdgeMsg>> createEdgeMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToEdgeNotificationMsg>> createEdgeNotificationsMsgProducer();

    default TbQueueProducer<TbProtoQueueMsg<ToEdgeEventNotificationMsg>> createEdgeEventMsgProducer() {
        return null;
    }

    
        
    TbQueueProducer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgProducer();
        
    TbQueueConsumer<TbProtoQueueMsg<ToRuleEngineMsg>> createToRuleEngineMsgConsumer(Queue configuration);
    default TbQueueConsumer<TbProtoQueueMsg<ToRuleEngineMsg>> createToRuleEngineMsgConsumer(Queue configuration, Integer partitionId) {
        return createToRuleEngineMsgConsumer(configuration);
    }

    
    TbQueueConsumer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createToRuleEngineNotificationsMsgConsumer();

    TbQueueRequestTemplate<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> createRemoteJsRequestTemplate();

    TbQueueConsumer<TbProtoQueueMsg<ToCalculatedFieldMsg>> createToCalculatedFieldMsgConsumer(TopicPartitionInfo tpi);

    TbQueueAdmin getCalculatedFieldQueueAdmin();

    TbQueueProducer<TbProtoQueueMsg<ToCalculatedFieldMsg>> createToCalculatedFieldMsgProducer();

    TbQueueConsumer<TbProtoQueueMsg<ToCalculatedFieldNotificationMsg>> createToCalculatedFieldNotificationMsgConsumer();

    TbQueueProducer<TbProtoQueueMsg<ToCalculatedFieldNotificationMsg>> createToCalculatedFieldNotificationMsgProducer();

    TbQueueConsumer<TbProtoQueueMsg<CalculatedFieldStateProto>> createCalculatedFieldStateConsumer();

    TbQueueProducer<TbProtoQueueMsg<CalculatedFieldStateProto>> createCalculatedFieldStateProducer();

}
