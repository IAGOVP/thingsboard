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

import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeEventNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToHousekeeperServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToVersionControlServiceMsg;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;

/**
 * Spring bean providing typed {@link TbQueueProducer} instances by message category.
 */
public interface TbQueueProducerProvider {

    
        
    TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> getTransportNotificationsMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> getRuleEngineMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> getRuleEngineNotificationsMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> getTbCoreMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> getTbCoreNotificationsMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> getTbUsageStatsMsgProducer();
    TbQueueProducer<TbProtoQueueMsg<ToVersionControlServiceMsg>> getTbVersionControlMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToHousekeeperServiceMsg>> getHousekeeperMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToEdgeMsg>> getTbEdgeMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToEdgeNotificationMsg>> getTbEdgeNotificationsMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToEdgeEventNotificationMsg>> getTbEdgeEventsMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToCalculatedFieldMsg>> getCalculatedFieldsMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToCalculatedFieldNotificationMsg>> getCalculatedFieldsNotificationsMsgProducer();

}
