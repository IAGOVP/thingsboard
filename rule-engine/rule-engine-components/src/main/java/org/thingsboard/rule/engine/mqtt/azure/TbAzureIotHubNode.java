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
package org.thingsboard.rule.engine.mqtt.azure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.thingsboard.common.util.AzureIotHubUtil;
import org.thingsboard.mqtt.MqttClient;
import org.thingsboard.mqtt.MqttClientConfig;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.rule.engine.credentials.CertPemCredentials;
import org.thingsboard.rule.engine.credentials.ClientCredentials;
import org.thingsboard.rule.engine.credentials.CredentialsType;
import org.thingsboard.rule.engine.mqtt.TbMqttNode;
import org.thingsboard.rule.engine.mqtt.TbMqttNodeConfiguration;
import org.thingsboard.server.common.data.plugin.ComponentClusteringMode;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.util.TbPair;

import java.time.Clock;

/**
 * External rule node — <b>azure iot hub</b>.
 *
 * <p>Publish messages to the Azure IoT Hub
 * <br>Will publish message payload to the Azure IoT Hub with QoS AT_LEAST_ONCE.
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbAzureIotHubNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/external/azure-iot-hub/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/external/azure-iot-hub/</a>
 */
@RuleNode(
        type = ComponentType.EXTERNAL,
        name = "azure iot hub",
        configClazz = TbAzureIotHubNodeConfiguration.class,
        version = 1,
        clusteringMode = ComponentClusteringMode.SINGLETON,
        nodeDescription = "Publish messages to the Azure IoT Hub",
        nodeDetails = "Will publish message payload to the Azure IoT Hub with QoS <b>AT_LEAST_ONCE</b>.",
        configDirective = "tbExternalNodeAzureIotHubConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/external/azure-iot-hub/"
)
public class TbAzureIotHubNode extends TbMqttNode {

    private Clock clock = Clock.systemUTC();
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
            mqttNodeConfiguration.setPort(8883);
            mqttNodeConfiguration.setCleanSession(true);
            ClientCredentials credentials = mqttNodeConfiguration.getCredentials();
            if (CredentialsType.CERT_PEM == credentials.getType()) {
                CertPemCredentials pemCredentials = (CertPemCredentials) credentials;
                if (pemCredentials.getCaCert() == null || pemCredentials.getCaCert().isEmpty()) {
                    pemCredentials.setCaCert(AzureIotHubUtil.getDefaultCaCert());
                }
            }
            this.mqttClient = initAzureClient(ctx);
        } catch (Exception e) {
            throw new TbNodeException(e);
        }
    }
    /**
     * Prepare mqtt client config.
     *
     * @param config deserialized node configuration POJO
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void prepareMqttClientConfig(MqttClientConfig config) {
        config.setUsername(AzureIotHubUtil.buildUsername(mqttNodeConfiguration.getHost(), config.getClientId()));
        ClientCredentials credentials = mqttNodeConfiguration.getCredentials();
        if (CredentialsType.SAS == credentials.getType()) {
            config.setPassword(AzureIotHubUtil.buildSasToken(mqttNodeConfiguration.getHost(), ((AzureIotHubSasCredentials) credentials).getSasKey(), clock));
        }
    }

    MqttClient initAzureClient(TbContext ctx) throws Exception {
        return initClient(ctx);
    }

    @VisibleForTesting
    void setClock(Clock clock) {
        this.clock = clock;
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
                String protocolVersion = "protocolVersion";
                if (!oldConfiguration.has(protocolVersion)) {
                    hasChanges = true;
                    ((ObjectNode) oldConfiguration).put(protocolVersion, MqttVersion.MQTT_3_1_1.name());
                }
                break;
            default:
                break;
        }
        return new TbPair<>(hasChanges, oldConfiguration);
    }

}
