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
package org.thingsboard.server.transport.mqtt.adaptors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.common.adaptor.JsonConverter;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.device.profile.MqttTopics;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.mqtt.session.MqttDeviceAwareSessionContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.thingsboard.server.common.data.device.profile.MqttTopics.DEVICE_SOFTWARE_FIRMWARE_RESPONSES_TOPIC_FORMAT;

/**
 * JSON payload adaptor for default MQTT device API topics (telemetry, attributes, RPC, provision, OTA).
 */
@Component
@Slf4j
public class JsonMqttAdaptor implements MqttTransportAdaptor {

    protected static final Charset UTF8 = StandardCharsets.UTF_8;
    /**
     * Convert to post telemetry.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @return the TransportProtos.PostTelemetryMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.PostTelemetryMsg convertToPostTelemetry(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException {
        String payload = validatePayload(ctx.getSessionId(), inbound.payload(), false);
        try {
            return JsonConverter.convertToTelemetryProto(JsonParser.parseString(payload));
        } catch (IllegalStateException | JsonSyntaxException ex) {
            log.debug("Failed to decode post telemetry request", ex);
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to post attributes.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @return the TransportProtos.PostAttributeMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.PostAttributeMsg convertToPostAttributes(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException {
        String payload = validatePayload(ctx.getSessionId(), inbound.payload(), false);
        try {
            return JsonConverter.convertToAttributesProto(JsonParser.parseString(payload));
        } catch (IllegalStateException | JsonSyntaxException ex) {
            log.debug("Failed to decode post attributes request", ex);
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to claim device.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @return the TransportProtos.ClaimDeviceMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ClaimDeviceMsg convertToClaimDevice(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException {
        String payload = validatePayload(ctx.getSessionId(), inbound.payload(), true);
        try {
            return JsonConverter.convertToClaimDeviceProto(ctx.getDeviceId(), payload);
        } catch (IllegalStateException | JsonSyntaxException ex) {
            log.debug("Failed to decode claim device request", ex);
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to provision request msg.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @return the TransportProtos.ProvisionDeviceRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ProvisionDeviceRequestMsg convertToProvisionRequestMsg(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException {
        String payload = validatePayload(ctx.getSessionId(), inbound.payload(), false);
        try {
            return JsonConverter.convertToProvisionRequestMsg(payload);
        } catch (IllegalStateException | JsonSyntaxException ex) {
            throw new AdaptorException(ex);
        }
    }
    /**
     * Convert to get attributes.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return the TransportProtos.GetAttributeRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.GetAttributeRequestMsg convertToGetAttributes(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound, String topicBase) throws AdaptorException {
        return processGetAttributeRequestMsg(inbound, topicBase);
    }
    /**
     * Convert to device rpc response.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return the TransportProtos.ToDeviceRpcResponseMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ToDeviceRpcResponseMsg convertToDeviceRpcResponse(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound, String topicBase) throws AdaptorException {
        return processToDeviceRpcResponseMsg(inbound, topicBase);
    }
    /**
     * Convert to server rpc request.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return the TransportProtos.ToServerRpcRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ToServerRpcRequestMsg convertToServerRpcRequest(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound, String topicBase) throws AdaptorException {
        return processToServerRpcRequestMsg(ctx, inbound, topicBase);
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param responseMsg response msg
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.GetAttributeResponseMsg responseMsg, String topicBase) throws AdaptorException {
        return processConvertFromAttributeResponseMsg(ctx, responseMsg, topicBase);
    }
    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param responseMsg response msg
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, TransportProtos.GetAttributeResponseMsg responseMsg) throws AdaptorException {
        return processConvertFromGatewayAttributeResponseMsg(ctx, deviceName, responseMsg);
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param notificationMsg notification msg
     * @param topic topic ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.AttributeUpdateNotificationMsg notificationMsg, String topic) {
        return Optional.of(createMqttPublishMsg(ctx, topic, JsonConverter.toJson(notificationMsg)));
    }
    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param notificationMsg notification msg
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, TransportProtos.AttributeUpdateNotificationMsg notificationMsg) {
        JsonObject result = JsonConverter.getJsonObjectForGateway(deviceName, notificationMsg);
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_ATTRIBUTES_TOPIC, result));
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param rpcRequest rpc request
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.ToDeviceRpcRequestMsg rpcRequest, String topicBase) {
        return Optional.of(createMqttPublishMsg(ctx, topicBase + rpcRequest.getRequestId(), JsonConverter.toJson(rpcRequest, false)));
    }
    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param rpcRequest rpc request
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, TransportProtos.ToDeviceRpcRequestMsg rpcRequest) {
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_RPC_TOPIC, JsonConverter.toGatewayJson(deviceName, rpcRequest)));
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param rpcResponse rpc response
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.ToServerRpcResponseMsg rpcResponse, String topicBase) {
        return Optional.of(createMqttPublishMsg(ctx, topicBase + rpcResponse.getRequestId(), JsonConverter.toJson(rpcResponse)));
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param provisionResponse provision response
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.ProvisionDeviceResponseMsg provisionResponse) {
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.DEVICE_PROVISION_RESPONSE_TOPIC, JsonConverter.toJson(provisionResponse)));
    }
    /**
     * Convert to gateway device disconnect publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param reasonCode reason code
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToGatewayDeviceDisconnectPublish(MqttDeviceAwareSessionContext ctx, String deviceName, int reasonCode) {
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_DISCONNECT_TOPIC, JsonConverter.toGatewayDeviceDisconnectJson(deviceName, reasonCode)));
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param firmwareChunk firmware chunk
     * @param requestId request id ({@link String})
     * @param chunk chunk
     * @param firmwareType firmware type ({@link OtaPackageType})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws Exception on processing failure
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, byte[] firmwareChunk, String requestId, int chunk, OtaPackageType firmwareType) {
        return Optional.of(createMqttPublishMsg(ctx, String.format(DEVICE_SOFTWARE_FIRMWARE_RESPONSES_TOPIC_FORMAT, firmwareType.getKeyPrefix(), requestId, chunk), firmwareChunk));
    }
    /**
     * Validates json payload.
     *
     * @param sessionId session id ({@link UUID})
     * @param payloadData payload data ({@link ByteBuf})
     * @return {@link JsonElement}
     * @throws AdaptorException on invalid payload or topic format
     */
    public static JsonElement validateJsonPayload(UUID sessionId, ByteBuf payloadData) throws AdaptorException {
        String payload = validatePayload(sessionId, payloadData, false);
        try {
            return JsonParser.parseString(payload);
        } catch (JsonSyntaxException ex) {
            log.debug("Payload is in incorrect format: {}", payload);
            throw new AdaptorException(ex);
        }
    }

    private TransportProtos.GetAttributeRequestMsg processGetAttributeRequestMsg(MqttPublishMessage inbound, String topicBase) throws AdaptorException {
        String topicName = inbound.variableHeader().topicName();
        try {
            TransportProtos.GetAttributeRequestMsg.Builder result = TransportProtos.GetAttributeRequestMsg.newBuilder();
            result.setRequestId(getRequestId(topicName, topicBase));
            String payload = inbound.payload().toString(UTF8);
            JsonElement requestBody = JsonParser.parseString(payload);
            Set<String> clientKeys = toStringSet(requestBody, "clientKeys");
            Set<String> sharedKeys = toStringSet(requestBody, "sharedKeys");
            if (clientKeys != null) {
                result.addAllClientAttributeNames(clientKeys);
            }
            if (sharedKeys != null) {
                result.addAllSharedAttributeNames(sharedKeys);
            }
            return result.build();
        } catch (RuntimeException e) {
            log.debug("Failed to decode get attributes request", e);
            throw new AdaptorException(e);
        }
    }

    private TransportProtos.ToDeviceRpcResponseMsg processToDeviceRpcResponseMsg(MqttPublishMessage inbound, String topicBase) throws AdaptorException {
        String topicName = inbound.variableHeader().topicName();
        try {
            int requestId = getRequestId(topicName, topicBase);
            String payload = inbound.payload().toString(UTF8);
            return TransportProtos.ToDeviceRpcResponseMsg.newBuilder().setRequestId(requestId).setPayload(payload).build();
        } catch (RuntimeException e) {
            log.debug("Failed to decode rpc response", e);
            throw new AdaptorException(e);
        }
    }

    private TransportProtos.ToServerRpcRequestMsg processToServerRpcRequestMsg(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound, String topicBase) throws AdaptorException {
        String topicName = inbound.variableHeader().topicName();
        String payload = validatePayload(ctx.getSessionId(), inbound.payload(), false);
        try {
            int requestId = getRequestId(topicName, topicBase);
            return JsonConverter.convertToServerRpcRequest(JsonParser.parseString(payload), requestId);
        } catch (IllegalStateException | JsonSyntaxException ex) {
            log.debug("Failed to decode to server rpc request", ex);
            throw new AdaptorException(ex);
        }
    }

    private Optional<MqttMessage> processConvertFromAttributeResponseMsg(MqttDeviceAwareSessionContext ctx, TransportProtos.GetAttributeResponseMsg responseMsg, String topicBase) throws AdaptorException {
        if (!StringUtils.isEmpty(responseMsg.getError())) {
            throw new AdaptorException(responseMsg.getError());
        } else {
            int requestId = responseMsg.getRequestId();
            if (requestId >= 0) {
                return Optional.of(createMqttPublishMsg(ctx,
                        topicBase + requestId,
                        JsonConverter.toJson(responseMsg)));
            }
            return Optional.empty();
        }
    }

    private Optional<MqttMessage> processConvertFromGatewayAttributeResponseMsg(MqttDeviceAwareSessionContext ctx, String deviceName, TransportProtos.GetAttributeResponseMsg responseMsg) throws AdaptorException {
        if (!StringUtils.isEmpty(responseMsg.getError())) {
            throw new AdaptorException(responseMsg.getError());
        } else {
            JsonObject result = JsonConverter.getJsonObjectForGateway(deviceName, responseMsg);
            return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_ATTRIBUTES_RESPONSE_TOPIC, result));
        }
    }
    /**
     * Creates mqtt publish msg.
     *
     * @param ctx MQTT session context
     * @param topic topic ({@link String})
     * @param json json ({@link JsonElement})
     * @return {@link MqttPublishMessage}
     * @throws Exception on processing failure
     */
    protected MqttPublishMessage createMqttPublishMsg(MqttDeviceAwareSessionContext ctx, String topic, JsonElement json) {
        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, ctx.getQoSForTopic(topic), false, 0);
        MqttPublishVariableHeader header = new MqttPublishVariableHeader(topic, ctx.nextMsgId());
        ByteBuf payload = ALLOCATOR.buffer();
        payload.writeBytes(json.toString().getBytes(UTF8));
        return new MqttPublishMessage(mqttFixedHeader, header, payload);
    }

    private Set<String> toStringSet(JsonElement requestBody, String name) {
        JsonElement element = requestBody.getAsJsonObject().get(name);
        if (element != null) {
            return new HashSet<>(Arrays.asList(element.getAsString().split(",")));
        } else {
            return null;
        }
    }

    private static String validatePayload(UUID sessionId, ByteBuf payloadData, boolean isEmptyPayloadAllowed) throws AdaptorException {
        String payload = payloadData.toString(UTF8);
        if (payload == null) {
            log.debug("[{}] Payload is empty!", sessionId);
            if (!isEmptyPayloadAllowed) {
                throw new AdaptorException(new IllegalArgumentException("Payload is empty!"));
            }
        }
        return payload;
    }

    private int getRequestId(String topicName, String topic) {
        return Integer.parseInt(topicName.substring(topic.length()));
    }

}
