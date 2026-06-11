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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.gen.transport.TransportProtos.AttributeUpdateNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ClaimDeviceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetAttributeRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetAttributeResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.PostAttributeMsg;
import org.thingsboard.server.gen.transport.TransportProtos.PostTelemetryMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ProvisionDeviceRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ProvisionDeviceResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToDeviceRpcRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToDeviceRpcResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToServerRpcRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToServerRpcResponseMsg;
import org.thingsboard.server.transport.mqtt.session.MqttDeviceAwareSessionContext;

import java.util.Optional;

/**
 * Converts MQTT {@link io.netty.handler.codec.mqtt.MqttPublishMessage} payloads to transport protobuf messages and back.
 *
 * <p>Implementations: {@link JsonMqttAdaptor}, {@link ProtoMqttAdaptor}, {@link BackwardCompatibilityAdaptor}. Topic layout follows {@link org.thingsboard.server.common.data.device.profile.MqttTopics}.
 */
public interface MqttTransportAdaptor {

    ByteBufAllocator ALLOCATOR = new UnpooledByteBufAllocator(false);

    /**
     * Parses an inbound {@code v1/devices/me/telemetry} (or gateway equivalent) publish into
     * {@link PostTelemetryMsg} for {@link org.thingsboard.server.common.transport.TransportService}.
     *
     * @param ctx     MQTT session context (device profile, payload type)
     * @param inbound raw MQTT PUBLISH from the device
     * @return parsed telemetry protobuf message
     * @throws AdaptorException on invalid JSON/Protobuf payload or unsupported topic
     */
    PostTelemetryMsg convertToPostTelemetry(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    /**
     * Parses an inbound client attributes publish into {@link PostAttributeMsg}.
     *
     * @param ctx     MQTT session context
     * @param inbound raw MQTT PUBLISH from the device
     * @return parsed attributes protobuf message
     * @throws AdaptorException on invalid payload or topic format
     */
    PostAttributeMsg convertToPostAttributes(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    /**
     * Convert to get attributes.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return {@link GetAttributeRequestMsg}
     * @throws AdaptorException on invalid payload or topic format
     */
    GetAttributeRequestMsg convertToGetAttributes(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound, String topicBase) throws AdaptorException;

    /**
     * Convert to device rpc response.
     *
     * @param ctx MQTT session context
     * @param mqttMsg mqtt msg ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return {@link ToDeviceRpcResponseMsg}
     * @throws AdaptorException on invalid payload or topic format
     */
    ToDeviceRpcResponseMsg convertToDeviceRpcResponse(MqttDeviceAwareSessionContext ctx, MqttPublishMessage mqttMsg, String topicBase) throws AdaptorException;

    /**
     * Convert to server rpc request.
     *
     * @param ctx MQTT session context
     * @param mqttMsg mqtt msg ({@link MqttPublishMessage})
     * @param topicBase topic base ({@link String})
     * @return {@link ToServerRpcRequestMsg}
     * @throws AdaptorException on invalid payload or topic format
     */
    ToServerRpcRequestMsg convertToServerRpcRequest(MqttDeviceAwareSessionContext ctx, MqttPublishMessage mqttMsg, String topicBase) throws AdaptorException;

    /**
     * Convert to claim device.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @return {@link ClaimDeviceMsg}
     * @throws AdaptorException on invalid payload or topic format
     */
    ClaimDeviceMsg convertToClaimDevice(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param responseMsg response msg ({@link GetAttributeResponseMsg})
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, GetAttributeResponseMsg responseMsg, String topicBase) throws AdaptorException;

    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param responseMsg response msg ({@link GetAttributeResponseMsg})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, GetAttributeResponseMsg responseMsg) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param notificationMsg notification msg ({@link AttributeUpdateNotificationMsg})
     * @param topic topic ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, AttributeUpdateNotificationMsg notificationMsg, String topic) throws AdaptorException;

    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param notificationMsg notification msg ({@link AttributeUpdateNotificationMsg})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, AttributeUpdateNotificationMsg notificationMsg) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param rpcRequest rpc request ({@link ToDeviceRpcRequestMsg})
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, ToDeviceRpcRequestMsg rpcRequest, String topicBase) throws AdaptorException;

    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param rpcRequest rpc request ({@link ToDeviceRpcRequestMsg})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, ToDeviceRpcRequestMsg rpcRequest) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param rpcResponse rpc response ({@link ToServerRpcResponseMsg})
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, ToServerRpcResponseMsg rpcResponse, String topicBase) throws AdaptorException;

    /**
     * Convert to provision request msg.
     *
     * @param ctx MQTT session context
     * @param inbound inbound ({@link MqttPublishMessage})
     * @return {@link ProvisionDeviceRequestMsg}
     * @throws AdaptorException on invalid payload or topic format
     */
    ProvisionDeviceRequestMsg convertToProvisionRequestMsg(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param provisionResponse provision response ({@link ProvisionDeviceResponseMsg})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, ProvisionDeviceResponseMsg provisionResponse) throws AdaptorException;

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
    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, byte[] firmwareChunk, String requestId, int chunk, OtaPackageType firmwareType) throws AdaptorException;

    /**
     * Convert to gateway device disconnect publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param reasonCode reason code
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */
    Optional<MqttMessage> convertToGatewayDeviceDisconnectPublish(MqttDeviceAwareSessionContext ctx, String deviceName, int reasonCode) throws AdaptorException;

    /**
     * Creates mqtt publish msg.
     *
     * @param ctx MQTT session context
     * @param topic topic ({@link String})
     * @param payloadInBytes payload in bytes
     * @return {@link MqttPublishMessage}
     * @throws Exception on processing failure
     */
    default MqttPublishMessage createMqttPublishMsg(MqttDeviceAwareSessionContext ctx, String topic, byte[] payloadInBytes) {
        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, ctx.getQoSForTopic(topic), false, 0);
        MqttPublishVariableHeader header = new MqttPublishVariableHeader(topic, ctx.nextMsgId());
        ByteBuf payload = ALLOCATOR.buffer();
        payload.writeBytes(payloadInBytes);

        /**
         * Mqtt publish message.
         *
         * @return constructed MQTT publish message
         * @throws Exception on processing failure
         */
        return new MqttPublishMessage(mqttFixedHeader, header, payload);
    }

}
