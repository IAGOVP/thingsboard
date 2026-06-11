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
package org.thingsboard.server.transport.coap.adaptors;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.ProvisionDeviceRequestMsg;

import java.util.UUID;

/**
 * Converts CoAP request payloads to transport protobuf and builds CoAP responses.
 */
public interface CoapTransportAdaptor {

    TransportProtos.PostTelemetryMsg convertToPostTelemetry(UUID sessionId, Request inbound, Descriptors.Descriptor telemetryMsgDescriptor) throws AdaptorException;

    /**
     * Convert to post attributes.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @param attributesMsgDescriptor attributes msg descriptor
     * @return the TransportProtos.PostAttributeMsg value
     * @throws AdaptorException on invalid payload or topic format
     */
    TransportProtos.PostAttributeMsg convertToPostAttributes(UUID sessionId, Request inbound, Descriptors.Descriptor attributesMsgDescriptor) throws AdaptorException;

    /**
     * Convert to get attributes.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @return the TransportProtos.GetAttributeRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */
    TransportProtos.GetAttributeRequestMsg convertToGetAttributes(UUID sessionId, Request inbound) throws AdaptorException;

    /**
     * Convert to device rpc response.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @param rpcResponseMsgDescriptor rpc response msg descriptor
     * @return the TransportProtos.ToDeviceRpcResponseMsg value
     * @throws AdaptorException on invalid payload or topic format
     */
    TransportProtos.ToDeviceRpcResponseMsg convertToDeviceRpcResponse(UUID sessionId, Request inbound, Descriptors.Descriptor rpcResponseMsgDescriptor) throws AdaptorException;

    /**
     * Convert to server rpc request.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @return the TransportProtos.ToServerRpcRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */
    TransportProtos.ToServerRpcRequestMsg convertToServerRpcRequest(UUID sessionId, Request inbound) throws AdaptorException;

    /**
     * Convert to claim device.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @param sessionInfo session info
     * @return the TransportProtos.ClaimDeviceMsg value
     * @throws AdaptorException on invalid payload or topic format
     */
    TransportProtos.ClaimDeviceMsg convertToClaimDevice(UUID sessionId, Request inbound, TransportProtos.SessionInfoProto sessionInfo) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param responseMsg response msg
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */
    Response convertToPublish(TransportProtos.GetAttributeResponseMsg responseMsg) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param notificationMsg notification msg
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */
    Response convertToPublish(TransportProtos.AttributeUpdateNotificationMsg notificationMsg) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param rpcRequest rpc request
     * @param rpcRequestDynamicMessageBuilder rpc request dynamic message builder
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */
    Response convertToPublish(TransportProtos.ToDeviceRpcRequestMsg rpcRequest, DynamicMessage.Builder rpcRequestDynamicMessageBuilder) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param msg msg
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */
    Response convertToPublish(TransportProtos.ToServerRpcResponseMsg msg) throws AdaptorException;

    /**
     * Convert to provision request msg.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @return {@link ProvisionDeviceRequestMsg}
     * @throws AdaptorException on invalid payload or topic format
     */
    ProvisionDeviceRequestMsg convertToProvisionRequestMsg(UUID sessionId, Request inbound) throws AdaptorException;

    /**
     * Returns content format.
     *
     * @return monotonically increasing MQTT packet identifier
     * @throws Exception on processing failure
     */
    int getContentFormat();

}
