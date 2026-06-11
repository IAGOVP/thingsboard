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
import com.google.gson.JsonParser;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.common.adaptor.JsonConverter;
import org.thingsboard.server.common.adaptor.ProtoConverter;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.device.profile.MqttTopics;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.gen.transport.TransportApiProtos;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.mqtt.session.DeviceSessionCtx;
import org.thingsboard.server.transport.mqtt.session.MqttDeviceAwareSessionContext;

import java.util.Optional;

import static org.thingsboard.server.common.data.device.profile.MqttTopics.DEVICE_SOFTWARE_FIRMWARE_RESPONSES_TOPIC_FORMAT;

/**
 * Protobuf payload adaptor for MQTT device API when device profile transport type is PROTOBUF.
 */
@Component
@Slf4j
public class ProtoMqttAdaptor implements MqttTransportAdaptor {
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
        DeviceSessionCtx deviceSessionCtx = (DeviceSessionCtx) ctx;
        byte[] bytes = toBytes(inbound.payload());
        Descriptors.Descriptor telemetryDynamicMsgDescriptor = ProtoConverter.validateDescriptor(deviceSessionCtx.getTelemetryDynamicMessageDescriptor());
        try {
            return JsonConverter.convertToTelemetryProto(JsonParser.parseString(ProtoConverter.dynamicMsgToJson(bytes, telemetryDynamicMsgDescriptor)));
        } catch (Exception e) {
            log.debug("Failed to decode post telemetry request", e);
            throw new AdaptorException(e);
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
        DeviceSessionCtx deviceSessionCtx = (DeviceSessionCtx) ctx;
        byte[] bytes = toBytes(inbound.payload());
        Descriptors.Descriptor attributesDynamicMessageDescriptor = ProtoConverter.validateDescriptor(deviceSessionCtx.getAttributesDynamicMessageDescriptor());
        try {
            return JsonConverter.convertToAttributesProto(JsonParser.parseString(ProtoConverter.dynamicMsgToJson(bytes, attributesDynamicMessageDescriptor)));
        } catch (Exception e) {
            log.debug("Failed to decode post attributes request", e);
            throw new AdaptorException(e);
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
        byte[] bytes = toBytes(inbound.payload());
        try {
            return ProtoConverter.convertToClaimDeviceProto(ctx.getDeviceId(), bytes);
        } catch (InvalidProtocolBufferException e) {
            log.debug("Failed to decode claim device request", e);
            throw new AdaptorException(e);
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
        byte[] bytes = toBytes(inbound.payload());
        String topicName = inbound.variableHeader().topicName();
        try {
            int requestId = getRequestId(topicName, topicBase);
            return ProtoConverter.convertToGetAttributeRequestMessage(bytes, requestId);
        } catch (InvalidProtocolBufferException e) {
            log.debug("Failed to decode get attributes request", e);
            throw new AdaptorException(e);
        }
    }
    /**
     * Convert to device rpc response.
     *
     * @param ctx MQTT session context
     * @param mqttMsg mqtt msg ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return the TransportProtos.ToDeviceRpcResponseMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ToDeviceRpcResponseMsg convertToDeviceRpcResponse(MqttDeviceAwareSessionContext ctx, MqttPublishMessage mqttMsg, String topicBase) throws AdaptorException {
        DeviceSessionCtx deviceSessionCtx = (DeviceSessionCtx) ctx;
        String topicName = mqttMsg.variableHeader().topicName();
        byte[] bytes = toBytes(mqttMsg.payload());
        Descriptors.Descriptor rpcResponseDynamicMessageDescriptor = ProtoConverter.validateDescriptor(deviceSessionCtx.getRpcResponseDynamicMessageDescriptor());
        try {
            int requestId = getRequestId(topicName, topicBase);
            JsonElement response = JsonParser.parseString(ProtoConverter.dynamicMsgToJson(bytes, rpcResponseDynamicMessageDescriptor));
            return TransportProtos.ToDeviceRpcResponseMsg.newBuilder().setRequestId(requestId).setPayload(response.toString()).build();
        } catch (Exception e) {
            log.debug("Failed to decode rpc response", e);
            throw new AdaptorException(e);
        }
    }
    /**
     * Convert to server rpc request.
     *
     * @param ctx MQTT session context
     * @param mqttMsg mqtt msg ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return the TransportProtos.ToServerRpcRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ToServerRpcRequestMsg convertToServerRpcRequest(MqttDeviceAwareSessionContext ctx, MqttPublishMessage mqttMsg, String topicBase) throws AdaptorException {
        byte[] bytes = toBytes(mqttMsg.payload());
        String topicName = mqttMsg.variableHeader().topicName();
        try {
            int requestId = getRequestId(topicName, topicBase);
            return ProtoConverter.convertToServerRpcRequest(bytes, requestId);
        } catch (InvalidProtocolBufferException e) {
            log.debug("Failed to decode to server rpc request", e);
            throw new AdaptorException(e);
        }
    }
    /**
     * Convert to provision request msg.
     *
     * @param ctx MQTT session context
     * @param mqttMsg mqtt msg ({@link MqttPublishMessage})
     * @return the TransportProtos.ProvisionDeviceRequestMsg value
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public TransportProtos.ProvisionDeviceRequestMsg convertToProvisionRequestMsg(MqttDeviceAwareSessionContext ctx, MqttPublishMessage mqttMsg) throws AdaptorException {
        byte[] bytes = toBytes(mqttMsg.payload());
        try {
            return ProtoConverter.convertToProvisionRequestMsg(bytes);
        } catch (InvalidProtocolBufferException ex) {
            log.debug("Failed to decode provision request", ex);
            throw new AdaptorException(ex);
        }
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
        if (!StringUtils.isEmpty(responseMsg.getError())) {
            throw new AdaptorException(responseMsg.getError());
        } else {
            int requestId = responseMsg.getRequestId();
            if (requestId >= 0) {
                return Optional.of(createMqttPublishMsg(ctx, topicBase + requestId, responseMsg.toByteArray()));
            }
            return Optional.empty();
        }
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param rpcRequest rpc request
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.ToDeviceRpcRequestMsg rpcRequest, String topicBase) throws AdaptorException {
        DeviceSessionCtx deviceSessionCtx = (DeviceSessionCtx) ctx;
        DynamicMessage.Builder rpcRequestDynamicMessageBuilder = deviceSessionCtx.getRpcRequestDynamicMessageBuilder();
        if (rpcRequestDynamicMessageBuilder == null) {
            throw new AdaptorException("Failed to get rpcRequestDynamicMessageBuilder!");
        } else {
            return Optional.of(createMqttPublishMsg(ctx, topicBase + rpcRequest.getRequestId(), ProtoConverter.convertToRpcRequest(rpcRequest, rpcRequestDynamicMessageBuilder)));
        }
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
        return Optional.of(createMqttPublishMsg(ctx, topicBase + rpcResponse.getRequestId(), rpcResponse.toByteArray()));
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
        return Optional.of(createMqttPublishMsg(ctx, topic, notificationMsg.toByteArray()));
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
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.DEVICE_PROVISION_RESPONSE_TOPIC, provisionResponse.toByteArray()));
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
        TransportProtos.GatewayDisconnectDeviceMsg gatewayDeviceDisconnectMsg = TransportProtos.GatewayDisconnectDeviceMsg.newBuilder()
                .setDeviceName(deviceName)
                .setReasonCode(reasonCode)
                .build();
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_DISCONNECT_TOPIC, gatewayDeviceDisconnectMsg.toByteArray()));
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
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, byte[] firmwareChunk, String requestId, int chunk, OtaPackageType firmwareType) throws AdaptorException {
        return Optional.of(createMqttPublishMsg(ctx, String.format(DEVICE_SOFTWARE_FIRMWARE_RESPONSES_TOPIC_FORMAT, firmwareType.getKeyPrefix(), requestId, chunk), firmwareChunk));
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
        if (!StringUtils.isEmpty(responseMsg.getError())) {
            throw new AdaptorException(responseMsg.getError());
        } else {
            TransportApiProtos.GatewayAttributeResponseMsg.Builder responseMsgBuilder = TransportApiProtos.GatewayAttributeResponseMsg.newBuilder();
            responseMsgBuilder.setDeviceName(deviceName);
            responseMsgBuilder.setResponseMsg(responseMsg);
            byte[] payloadBytes = responseMsgBuilder.build().toByteArray();
            return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_ATTRIBUTES_RESPONSE_TOPIC, payloadBytes));
        }
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
        TransportApiProtos.GatewayAttributeUpdateNotificationMsg.Builder builder = TransportApiProtos.GatewayAttributeUpdateNotificationMsg.newBuilder();
        builder.setDeviceName(deviceName);
        builder.setNotificationMsg(notificationMsg);
        byte[] payloadBytes = builder.build().toByteArray();
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_ATTRIBUTES_TOPIC, payloadBytes));
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
        TransportApiProtos.GatewayDeviceRpcRequestMsg.Builder builder = TransportApiProtos.GatewayDeviceRpcRequestMsg.newBuilder();
        builder.setDeviceName(deviceName);
        builder.setRpcRequestMsg(rpcRequest);
        byte[] payloadBytes = builder.build().toByteArray();
        return Optional.of(createMqttPublishMsg(ctx, MqttTopics.GATEWAY_RPC_TOPIC, payloadBytes));
    }
    /**
     * To bytes.
     *
     * @param inbound inbound ({@link ByteBuf})
     * @return the byte[] value
     * @throws Exception on processing failure
     */
    public static byte[] toBytes(ByteBuf inbound) {
        byte[] bytes = new byte[inbound.readableBytes()];
        int readerIndex = inbound.readerIndex();
        inbound.getBytes(readerIndex, bytes);
        return bytes;
    }

    private int getRequestId(String topicName, String topic) {
        return Integer.parseInt(topicName.substring(topic.length()));
    }

}
