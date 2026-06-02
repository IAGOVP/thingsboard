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
package org.thingsboard.client.tools;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.thingsboard.server.common.data.ResourceUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

/**
 * Manual two-way TLS MQTT test client (Eclipse Paho).
 *
 * <p>Loads {@value #KEY_STORE_FILE} from the classpath for both trust and client keys,
 * connects to {@value #MQTT_URL}, publishes sample JSON to
 * {@code v1/devices/me/telemetry}, then disconnects. Intended for local SSL broker
 * verification alongside {@code client.keygen.sh} / {@code server.keygen.sh}.
 *
 * @author Valerii Sosliuk
 */
@Slf4j
public class MqttSslClient {

    /** Broker URL; default ThingsBoard MQTT SSL port when TLS terminates on 1883. */
    private static final String MQTT_URL = "ssl://localhost:1883";
    private static final String CLIENT_ID = "MQTT_SSL_JAVA_CLIENT";
    /** Keystore on classpath (generate via {@code tools/src/main/shell/client.keygen.sh}). */
    private static final String KEY_STORE_FILE = "mqttclient.jks";
    private static final String JKS = "JKS";
    private static final String TLS = "TLS";

    /**
     * Connects with mutual TLS, publishes one telemetry message, exits 0 on success.
     *
     * @param args unused
     */
    public static void main(String[] args) {

        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            KeyStore trustStore = KeyStore.getInstance(JKS);
            char[] ksPwd = new char[]{0x63, 0x6C, 0x69, 0x65, 0x6E, 0x74, 0x5F, 0x6B, 0x73, 0x5F, 0x70, 0x61, 0x73, 0x73, 0x77, 0x6F, 0x72, 0x64};
            trustStore.load(ResourceUtils.getInputStream(MqttSslClient.class.getClassLoader(), KEY_STORE_FILE), ksPwd);
            tmf.init(trustStore);
            KeyStore ks = KeyStore.getInstance(JKS);

            ks.load(ResourceUtils.getInputStream(MqttSslClient.class.getClassLoader(), KEY_STORE_FILE), ksPwd);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            char[] clientPwd = new char[]{0x63, 0x6C, 0x69, 0x65, 0x6E, 0x74, 0x5F, 0x6B, 0x65, 0x79, 0x5F, 0x70, 0x61, 0x73, 0x73, 0x77, 0x6F, 0x72, 0x64};
            kmf.init(ks, clientPwd);

            KeyManager[] km = kmf.getKeyManagers();
            TrustManager[] tm = tmf.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance(TLS);
            sslContext.init(km, tm, null);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(sslContext.getSocketFactory());
            MqttAsyncClient client = new MqttAsyncClient(MQTT_URL, CLIENT_ID, new MemoryPersistence());
            client.connect(options);
            Thread.sleep(3000);
            MqttMessage message = new MqttMessage();
            message.setPayload("{\"key1\":\"value1\", \"key2\":true, \"key3\": 3.0, \"key4\": 4}".getBytes());
            client.publish("v1/devices/me/telemetry", message);
            client.disconnect();
            log.info("Disconnected");
            System.exit(0);
        } catch (Exception e) {
            log.error("Unexpected exception occurred in MqttSslClient", e);
        }
    }
}