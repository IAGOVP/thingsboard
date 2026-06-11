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
package org.thingsboard.rule.engine.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Promise;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.mqtt.MqttClient;
import org.thingsboard.mqtt.MqttClientConfig;
import org.thingsboard.mqtt.MqttConnectResult;
import org.thingsboard.rule.engine.api.MqttClientSettings;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.rule.engine.credentials.BasicCredentials;
import org.thingsboard.rule.engine.credentials.ClientCredentials;
import org.thingsboard.rule.engine.credentials.CredentialsType;
import org.thingsboard.rule.engine.external.TbAbstractExternalNode;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.plugin.ComponentClusteringMode;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * External rule node — <b>mqtt</b>.
 *
 * <p>Publish messages to the MQTT broker
 * <br>Will publish message payload to the MQTT broker with QoS AT_LEAST_ONCE.
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbMqttNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/external/mqtt/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/external/mqtt/</a>
 */
@RuleNode(
        type = ComponentType.EXTERNAL,
        name = "mqtt",
        configClazz = TbMqttNodeConfiguration.class,
        version = 2,
        clusteringMode = ComponentClusteringMode.USER_PREFERENCE,
        nodeDescription = "Publish messages to the MQTT broker",
        nodeDetails = "Will publish message payload to the MQTT broker with QoS <b>AT_LEAST_ONCE</b>.",
        configDirective = "tbExternalNodeMqttConfig",
        icon = "call_split",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/external/mqtt/"
)
public class TbMqttNode extends TbAbstractExternalNode {

    private static final int MQTT_3_MAX_CLIENT_ID_LENGTH = 23;
    private static final int MQTT_5_MAX_CLIENT_ID_LENGTH = 256;

    protected TbMqttNodeConfiguration mqttNodeConfiguration;
    protected MqttClient mqttClient;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        super.init(ctx);
        this.mqttNodeConfiguration = TbNodeUtils.convert(configuration, TbMqttNodeConfiguration.class);
        try {
            this.mqttClient = initClient(ctx);
        } catch (TbNodeException e) {
            throw e;
        } catch (Exception e) {
            throw new TbNodeException(e);
        }
    }
    /**
     * Processes one incoming {@link org.thingsboard.server.common.msg.TbMsg} and routes the result via {@link TbContext}.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param msg incoming or outgoing rule engine message
     * @throws TbNodeException if configuration or processing fails
     */

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        String topic = TbNodeUtils.processPattern(mqttNodeConfiguration.getTopicPattern(), msg);
        var tbMsg = ackIfNeeded(ctx, msg);
        this.mqttClient.publish(topic, Unpooled.wrappedBuffer(getData(tbMsg, mqttNodeConfiguration.isParseToPlainText()).getBytes(StandardCharsets.UTF_8)),
                        MqttQoS.AT_LEAST_ONCE, mqttNodeConfiguration.isRetainedMessage())
                .addListener(future -> {
                            if (future.isSuccess()) {
                                tellSuccess(ctx, tbMsg);
                            } else {
                                tellFailure(ctx, processException(tbMsg, future.cause()), future.cause());
                            }
                        }
                );
    }

    private TbMsg processException(TbMsg origMsg, Throwable e) {
        TbMsgMetaData metaData = origMsg.getMetaData().copy();
        metaData.putValue("error", e.getClass() + ": " + e.getMessage());
        return origMsg.transform()
                .metaData(metaData)
                .build();
    }
    /**
     * Releases resources held by the node (script engines, clients, thread pools).
     *
     */

    @Override
    public void destroy() {
        if (mqttClient != null) {
            mqttClient.disconnect();
        }
    }

    String getOwnerId(TbContext ctx) {
        return "Tenant[" + ctx.getTenantId().getId() + "]RuleNode[" + ctx.getSelf().getId().getId() + "]";
    }
    /**
     * Init client.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @return {@link MqttClient}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected MqttClient initClient(TbContext ctx) throws Exception {
        MqttClientConfig config = new MqttClientConfig(getSslContext());
        config.setOwnerId(getOwnerId(ctx));
        if (!StringUtils.isEmpty(mqttNodeConfiguration.getClientId())) {
            config.setClientId(getClientId(ctx));
        }
        config.setCleanSession(mqttNodeConfiguration.isCleanSession());
        config.setProtocolVersion(mqttNodeConfiguration.getProtocolVersion());

        MqttClientSettings mqttClientSettings = ctx.getMqttClientSettings();
        config.setRetransmissionConfig(new MqttClientConfig.RetransmissionConfig(
                mqttClientSettings.getRetransmissionMaxAttempts(),
                mqttClientSettings.getRetransmissionInitialDelayMillis(),
                mqttClientSettings.getRetransmissionJitterFactor()
        ));

        prepareMqttClientConfig(config);
        MqttClient client = getMqttClient(ctx, config);
        client.setEventLoop(ctx.getSharedEventLoop());
        Promise<MqttConnectResult> connectFuture = client.connect(mqttNodeConfiguration.getHost(), mqttNodeConfiguration.getPort());
        MqttConnectResult result;
        try {
            result = connectFuture.get(mqttNodeConfiguration.getConnectTimeoutSec(), TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            connectFuture.cancel(true);
            client.disconnect();
            String hostPort = mqttNodeConfiguration.getHost() + ":" + mqttNodeConfiguration.getPort();
            throw new RuntimeException(String.format("Failed to connect to MQTT broker at %s.", hostPort));
        }
        if (!result.isSuccess()) {
            connectFuture.cancel(true);
            client.disconnect();
            String hostPort = mqttNodeConfiguration.getHost() + ":" + mqttNodeConfiguration.getPort();
            throw new RuntimeException(String.format("Failed to connect to MQTT broker at %s. Result code is: %s", hostPort, result.getReturnCode()));
        }
        return client;
    }

    private String getClientId(TbContext ctx) throws TbNodeException {
        String clientId = mqttNodeConfiguration.isAppendClientIdSuffix() ?
                mqttNodeConfiguration.getClientId() + "_" + ctx.getServiceId() :
                mqttNodeConfiguration.getClientId();
        int maxLength = mqttNodeConfiguration.getProtocolVersion() == MqttVersion.MQTT_3_1 ? MQTT_3_MAX_CLIENT_ID_LENGTH : MQTT_5_MAX_CLIENT_ID_LENGTH;
        if (clientId.length() > maxLength) {
            throw new TbNodeException("The length of Client ID cannot be longer than " + maxLength + ", but current length is " + clientId.length() + ".", true);
        }
        return clientId;
    }

    MqttClient getMqttClient(TbContext ctx, MqttClientConfig config) {
        return MqttClient.create(config, null, ctx.getExternalCallExecutor());
    }
    /**
     * Prepare mqtt client config.
     *
     * @param config deserialized node configuration POJO
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void prepareMqttClientConfig(MqttClientConfig config) {
        ClientCredentials credentials = mqttNodeConfiguration.getCredentials();
        if (credentials.getType() == CredentialsType.BASIC) {
            BasicCredentials basicCredentials = (BasicCredentials) credentials;
            config.setUsername(basicCredentials.getUsername());
            config.setPassword(basicCredentials.getPassword());
        }
    }

    private SslContext getSslContext() throws SSLException {
        return mqttNodeConfiguration.isSsl() ? mqttNodeConfiguration.getCredentials().initSslContext() : null;
    }

    private String getData(TbMsg tbMsg, boolean parseToPlainText) {
        if (parseToPlainText) {
            return JacksonUtil.toPlainText(tbMsg.getData());
        }
        return tbMsg.getData();
    }
    /**
     * Upgrades persisted node configuration from an older {@link RuleNode#version()} to the current schema.
     *
     * @param fromVersion configuration schema version stored in the database
     * @param oldConfiguration previous JSON configuration to upgrade
     * @return {@link TbPair}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public TbPair<Boolean, JsonNode> upgrade(int fromVersion, JsonNode oldConfiguration) throws TbNodeException {
        boolean hasChanges = false;
        switch (fromVersion) {
            case 0:
                String parseToPlainText = "parseToPlainText";
                if (!oldConfiguration.has(parseToPlainText)) {
                    hasChanges = true;
                    ((ObjectNode) oldConfiguration).put(parseToPlainText, false);
                }
            case 1:
                String protocolVersion = "protocolVersion";
                if (!oldConfiguration.has(protocolVersion)) {
                    hasChanges = true;
                    ((ObjectNode) oldConfiguration).put(protocolVersion, MqttVersion.MQTT_3_1.name());
                }
                break;
            default:
                break;
        }
        return new TbPair<>(hasChanges, oldConfiguration);
    }

}
