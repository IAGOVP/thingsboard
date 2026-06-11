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

import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.js.JsInvokeProtos;
import org.thingsboard.server.gen.transport.TransportProtos.JobStatsMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeEventNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToHousekeeperServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToOtaPackageStateServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToVersionControlServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.TbQueueRequestTemplate;
import org.thingsboard.server.queue.common.TbProtoJsQueueMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;


/**
 * Factory creating queue producers and consumers for TbCore message flows.
 */
public interface TbCoreQueueFactory extends TbUsageStatsClientQueueFactory, HousekeeperClientQueueFactory, EdqsClientQueueFactory {

    
        
    TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createRuleEngineNotificationsMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer();
        
    TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer();
        
    TbQueueConsumer<TbProtoQueueMsg<ToCoreMsg>> createToCoreMsgConsumer();
        
    TbQueueConsumer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgConsumer();
        
    TbQueueConsumer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgConsumer();
        
    TbQueueProducer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgProducer();
        
    TbQueueConsumer<TbProtoQueueMsg<ToCoreNotificationMsg>> createToCoreNotificationsMsgConsumer();
        
    TbQueueConsumer<TbProtoQueueMsg<TransportApiRequestMsg>> createTransportApiRequestConsumer();
    TbQueueProducer<TbProtoQueueMsg<TransportApiResponseMsg>> createTransportApiResponseProducer();

        
    TbQueueRequestTemplate<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> createRemoteJsRequestTemplate();
    TbQueueProducer<TbProtoQueueMsg<ToVersionControlServiceMsg>> createVersionControlMsgProducer();

    TbQueueConsumer<TbProtoQueueMsg<ToHousekeeperServiceMsg>> createHousekeeperMsgConsumer();

    TbQueueProducer<TbProtoQueueMsg<ToHousekeeperServiceMsg>> createHousekeeperReprocessingMsgProducer();

    TbQueueConsumer<TbProtoQueueMsg<ToHousekeeperServiceMsg>> createHousekeeperReprocessingMsgConsumer();

    TbQueueConsumer<TbProtoQueueMsg<ToEdgeMsg>> createEdgeMsgConsumer();

    TbQueueProducer<TbProtoQueueMsg<ToEdgeMsg>> createEdgeMsgProducer();

    TbQueueConsumer<TbProtoQueueMsg<ToEdgeNotificationMsg>> createToEdgeNotificationsMsgConsumer();

    TbQueueProducer<TbProtoQueueMsg<ToEdgeNotificationMsg>> createEdgeNotificationsMsgProducer();

    default TbQueueConsumer<TbProtoQueueMsg<ToEdgeEventNotificationMsg>> createEdgeEventMsgConsumer(TenantId tenantId, EdgeId edgeId) {
        return null;
    }

    default TbQueueProducer<TbProtoQueueMsg<ToEdgeEventNotificationMsg>> createEdgeEventMsgProducer() {
        return null;
    }

    TbQueueProducer<TbProtoQueueMsg<ToCalculatedFieldMsg>> createToCalculatedFieldMsgProducer();

    TbQueueProducer<TbProtoQueueMsg<ToCalculatedFieldNotificationMsg>> createToCalculatedFieldNotificationMsgProducer();

    TbQueueConsumer<TbProtoQueueMsg<JobStatsMsg>> createJobStatsConsumer();

}
