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

import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.mqtt.session.MqttDeviceAwareSessionContext;

import java.util.Optional;

/**
 * Delegates to {@link JsonMqttAdaptor} or {@link ProtoMqttAdaptor} based on device profile and supports legacy topic formats.
 */
@Data
@AllArgsConstructor
@Slf4j
public class BackwardCompatibilityAdaptor implements MqttTransportAdaptor {

    private MqttTransportAdaptor protoAdaptor;
    private MqttTransportAdaptor jsonAdaptor;
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
        try {
            return protoAdaptor.convertToPostTelemetry(ctx, inbound);
        } catch (AdaptorException e) {
            log.trace("[{}] failed to process post telemetry request msg: {} due to: ", ctx.getSessionId(), inbound, e);
            return jsonAdaptor.convertToPostTelemetry(ctx, inbound);
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
        try {
            return protoAdaptor.convertToPostAttributes(ctx, inbound);
        } catch (AdaptorException e) {
            log.trace("[{}] failed to process post attributes request msg: {} due to: ", ctx.getSessionId(), inbound, e);
            return jsonAdaptor.convertToPostAttributes(ctx, inbound);
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
        try {
            return protoAdaptor.convertToGetAttributes(ctx, inbound, topicBase);
        } catch (AdaptorException e) {
            log.trace("[{}] failed to process get attributes request msg: {} due to: ", ctx.getSessionId(), inbound, e);
            return jsonAdaptor.convertToGetAttributes(ctx, inbound, topicBase);
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
        try {
            return protoAdaptor.convertToDeviceRpcResponse(ctx, mqttMsg, topicBase);
        } catch (AdaptorException e) {
            log.trace("[{}] failed to process to device rpc response msg: {} due to: ", ctx.getSessionId(), mqttMsg, e);
            return jsonAdaptor.convertToDeviceRpcResponse(ctx, mqttMsg, topicBase);
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
        try {
            return protoAdaptor.convertToServerRpcRequest(ctx, mqttMsg, topicBase);
        } catch (AdaptorException e) {
            log.trace("[{}] failed to process to server rpc request msg: {} due to: ", ctx.getSessionId(), mqttMsg, e);
            return jsonAdaptor.convertToServerRpcRequest(ctx, mqttMsg, topicBase);
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
        try {
            return protoAdaptor.convertToClaimDevice(ctx, inbound);
        } catch (AdaptorException e) {
            log.trace("[{}] failed to process claim device request msg: {} due to: ", ctx.getSessionId(), inbound, e);
            return jsonAdaptor.convertToClaimDevice(ctx, inbound);
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
        log.warn("[{}] invoked not implemented adaptor method! GetAttributeResponseMsg: {} TopicBase: {}", ctx.getSessionId(), responseMsg, topicBase);
        return Optional.empty();
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
        return protoAdaptor.convertToGatewayPublish(ctx, deviceName, responseMsg);
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param notificationMsg notification msg
     * @param topic topic ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.AttributeUpdateNotificationMsg notificationMsg, String topic) throws AdaptorException {
        log.warn("[{}] invoked not implemented adaptor method! AttributeUpdateNotificationMsg: {} Topic: {}", ctx.getSessionId(), notificationMsg, topic);
        return Optional.empty();
    }
    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param notificationMsg notification msg
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, TransportProtos.AttributeUpdateNotificationMsg notificationMsg) throws AdaptorException {
        return protoAdaptor.convertToGatewayPublish(ctx, deviceName, notificationMsg);
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
        log.warn("[{}] invoked not implemented adaptor method! ToDeviceRpcRequestMsg: {} TopicBase: {}", ctx.getSessionId(), rpcRequest, topicBase);
        return Optional.empty();
    }
    /**
     * Convert to gateway publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param rpcRequest rpc request
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, TransportProtos.ToDeviceRpcRequestMsg rpcRequest) throws AdaptorException {
        return protoAdaptor.convertToGatewayPublish(ctx, deviceName, rpcRequest);
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param rpcResponse rpc response
     * @param topicBase topic base ({@link String})
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.ToServerRpcResponseMsg rpcResponse, String topicBase) throws AdaptorException {
        log.warn("[{}] invoked not implemented adaptor method! ToServerRpcResponseMsg: {} TopicBase: {}", ctx.getSessionId(), rpcResponse, topicBase);
        return Optional.empty();
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
        log.warn("[{}] invoked not implemented adaptor method! MqttPublishMessage: {}", ctx.getSessionId(), inbound);
        return null;
    }
    /**
     * Convert to publish.
     *
     * @param ctx MQTT session context
     * @param provisionResponse provision response
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, TransportProtos.ProvisionDeviceResponseMsg provisionResponse) throws AdaptorException {
        log.warn("[{}] invoked not implemented adaptor method! ProvisionDeviceResponseMsg: {}", ctx.getSessionId(), provisionResponse);
        return Optional.empty();
    }
    /**
     * Convert to gateway device disconnect publish.
     *
     * @param ctx MQTT session context
     * @param deviceName device name ({@link String})
     * @param reasonCode reason code
     * @return MQTT publish message, or empty if conversion is not applicable
     * @throws AdaptorException on invalid payload or topic format
     */

    @Override
    public Optional<MqttMessage> convertToGatewayDeviceDisconnectPublish(MqttDeviceAwareSessionContext ctx, String deviceName, int reasonCode) throws AdaptorException {
        log.warn("[{}] invoked not implemented adaptor method! Device name: {} ReasonCode: {}", ctx.getSessionId(), deviceName, reasonCode);
        return Optional.empty();
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
        return protoAdaptor.convertToPublish(ctx, firmwareChunk, requestId, chunk, firmwareType);
    }
}
