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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.common.adaptor.JsonConverter;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.coap.CoapTransportResource;

import java.util.Optional;
import java.util.UUID;

/**
 * JSON CoAP adaptor mirroring the HTTP device API payload format.
 */
@Component
@Slf4j
public class JsonCoapAdaptor implements CoapTransportAdaptor {
    /**
     * Convert to post telemetry.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @param telemetryMsgDescriptor telemetry msg descriptor
     * @return the TransportProtos.PostTelemetryMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.PostTelemetryMsg convertToPostTelemetry(UUID sessionId, Request inbound, Descriptors.Descriptor telemetryMsgDescriptor) throws AdaptorException {
        String payload = validatePayload(sessionId, inbound, false);
        try {
            return JsonConverter.convertToTelemetryProto(JsonParser.parseString(payload));
        } catch (IllegalStateException | JsonSyntaxException ex) {
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to post attributes.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @param attributesMsgDescriptor attributes msg descriptor
     * @return the TransportProtos.PostAttributeMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.PostAttributeMsg convertToPostAttributes(UUID sessionId, Request inbound, Descriptors.Descriptor attributesMsgDescriptor) throws AdaptorException {
        String payload = validatePayload(sessionId, inbound, false);
        try {
            return JsonConverter.convertToAttributesProto(JsonParser.parseString(payload));
        } catch (IllegalStateException | JsonSyntaxException ex) {
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to get attributes.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @return the TransportProtos.GetAttributeRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.GetAttributeRequestMsg convertToGetAttributes(UUID sessionId, Request inbound) throws AdaptorException {
        return CoapAdaptorUtils.toGetAttributeRequestMsg(inbound);
    }
    /**
     * Convert to device rpc response.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @param rpcResponseMsgDescriptor rpc response msg descriptor
     * @return the TransportProtos.ToDeviceRpcResponseMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ToDeviceRpcResponseMsg convertToDeviceRpcResponse(UUID sessionId, Request inbound, Descriptors.Descriptor rpcResponseMsgDescriptor) throws AdaptorException {
        Optional<Integer> requestId = CoapTransportResource.getRequestId(inbound);
        String payload = validatePayload(sessionId, inbound, false);
        JsonObject response = JsonParser.parseString(payload).getAsJsonObject();
        return TransportProtos.ToDeviceRpcResponseMsg.newBuilder().setRequestId(requestId.orElseThrow(() -> new AdaptorException("Request id is missing!")))
                .setPayload(response.toString()).build();
    }
    /**
     * Convert to server rpc request.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @return the TransportProtos.ToServerRpcRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ToServerRpcRequestMsg convertToServerRpcRequest(UUID sessionId, Request inbound) throws AdaptorException {
        String payload = validatePayload(sessionId, inbound, false);
        return JsonConverter.convertToServerRpcRequest(JsonParser.parseString(payload), 0);
    }
    /**
     * Convert to claim device.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @param sessionInfo session info
     * @return the TransportProtos.ClaimDeviceMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ClaimDeviceMsg convertToClaimDevice(UUID sessionId, Request inbound, TransportProtos.SessionInfoProto sessionInfo) throws AdaptorException {
        DeviceId deviceId = new DeviceId(new UUID(sessionInfo.getDeviceIdMSB(), sessionInfo.getDeviceIdLSB()));
        String payload = validatePayload(sessionId, inbound, true);
        try {
            return JsonConverter.convertToClaimDeviceProto(deviceId, payload);
        } catch (IllegalStateException | JsonSyntaxException ex) {
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to publish.
     *
     * @param msg msg
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Response convertToPublish(TransportProtos.AttributeUpdateNotificationMsg msg) throws AdaptorException {
        return getObserveNotification(JsonConverter.toJson(msg));
    }
    /**
     * Convert to publish.
     *
     * @param msg msg
     * @param rpcRequestDynamicMessageBuilder rpc request dynamic message builder
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Response convertToPublish(TransportProtos.ToDeviceRpcRequestMsg msg, DynamicMessage.Builder rpcRequestDynamicMessageBuilder) throws AdaptorException {
        return getObserveNotification(JsonConverter.toJson(msg, true));
    }
    /**
     * Convert to publish.
     *
     * @param msg msg
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Response convertToPublish(TransportProtos.ToServerRpcResponseMsg msg) throws AdaptorException {
        Response response = new Response(CoAP.ResponseCode.CONTENT);
        JsonElement result = JsonConverter.toJson(msg);
        response.setPayload(result.toString());
        return response;
    }
    /**
     * Convert to provision request msg.
     *
     * @param sessionId session id ({@link UUID})
     * @param inbound inbound ({@link Request})
     * @return the TransportProtos.ProvisionDeviceRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ProvisionDeviceRequestMsg convertToProvisionRequestMsg(UUID sessionId, Request inbound) throws AdaptorException {
        String payload = validatePayload(sessionId, inbound, false);
        try {
            return JsonConverter.convertToProvisionRequestMsg(payload);
        } catch (IllegalStateException | JsonSyntaxException ex) {
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to publish.
     *
     * @param msg msg
     * @return {@link Response}
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Response convertToPublish(TransportProtos.GetAttributeResponseMsg msg) throws AdaptorException {
        if (msg.getSharedStateMsg()) {
            if (StringUtils.isEmpty(msg.getError())) {
                Response response = new Response(CoAP.ResponseCode.CONTENT);
                TransportProtos.AttributeUpdateNotificationMsg notificationMsg = TransportProtos.AttributeUpdateNotificationMsg.newBuilder().addAllSharedUpdated(msg.getSharedAttributeListList()).build();
                JsonObject result = JsonConverter.toJson(notificationMsg);
                response.setPayload(result.toString());
                return response;
            } else {
                return new Response(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            if (msg.getClientAttributeListCount() == 0 && msg.getSharedAttributeListCount() == 0) {
                return new Response(CoAP.ResponseCode.NOT_FOUND);
            } else {
                Response response = new Response(CoAP.ResponseCode.CONTENT);
                JsonObject result = JsonConverter.toJson(msg);
                response.setPayload(result.toString());
                return response;
            }
        }
    }

    private Response getObserveNotification(JsonElement json) {
        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload(json.toString());
        return response;
    }

    private String validatePayload(UUID sessionId, Request inbound, boolean isEmptyPayloadAllowed) throws AdaptorException {
        String payload = inbound.getPayloadString();
        if (payload == null) {
            log.debug("[{}] Payload is empty!", sessionId);
            if (!isEmptyPayloadAllowed) {
                throw new AdaptorException(new IllegalArgumentException("Payload is empty!"));
            }
        }
        return payload;
    }
    /**
     * Returns content format.
     *
     * @return monotonically increasing MQTT packet identifier
     * @throws Exception on processing failure
     */

    @Override
    public int getContentFormat() {
        return MediaTypeRegistry.APPLICATION_JSON;
    }

}
