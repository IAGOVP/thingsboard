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
package org.thingsboard.server.transport.mqtt;

import io.netty.handler.ssl.SslHandler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.transport.TransportContext;
import org.thingsboard.server.common.transport.TransportTenantProfileCache;
import org.thingsboard.server.transport.mqtt.adaptors.JsonMqttAdaptor;
import org.thingsboard.server.transport.mqtt.adaptors.ProtoMqttAdaptor;
import org.thingsboard.server.transport.mqtt.gateway.GatewayMetricsService;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MQTT transport configuration: SSL, proxy IP filters, topic filters, and session limits.
 */
@Slf4j
@Component
@TbMqttTransportComponent
public class MqttTransportContext extends TransportContext {

    @Getter
    @Autowired(required = false)
    private MqttSslHandlerProvider sslHandlerProvider;

    @Getter
    @Autowired
    private JsonMqttAdaptor jsonMqttAdaptor;

    @Getter
    @Autowired
    private ProtoMqttAdaptor protoMqttAdaptor;

    @Getter
    @Autowired
    private TransportTenantProfileCache tenantProfileCache;

    @Getter
    @Autowired
    private GatewayMetricsService gatewayMetricsService;

    @Getter
    @Value("${transport.mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;

    @Getter
    @Value("${transport.mqtt.ssl.skip_validity_check_for_client_cert:false}")
    private boolean skipValidityCheckForClientCert;

    @Getter
    @Setter
    private SslHandler sslHandler;

    @Getter
    @Value("${transport.mqtt.msg_queue_size_per_device_limit:100}")
    private int messageQueueSizePerDeviceLimit;

    @Getter
    @Value("${transport.mqtt.timeout:10000}")
    private long timeout;

    @Getter
    @Value("${transport.mqtt.disconnect_timeout:1000}")
    private long disconnectTimeout;

    @Getter
    @Value("${transport.mqtt.proxy_enabled:false}")
    private boolean proxyEnabled;

    private final AtomicInteger connectionsActiveCounterMQTT = new AtomicInteger();
    private final AtomicInteger connectionsActiveCounterMQTTS = new AtomicInteger();
    /**
     * Init.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @PostConstruct
    public void init() {
        super.init();
        transportService.createGaugeStats("connections_active", connectionsActiveCounterMQTT, "protocol", "MQTT");
        transportService.createGaugeStats("connections_active", connectionsActiveCounterMQTTS, "protocol", "MQTTS");
    }
    /**
     * Channel registered.
     *
     * @param isSSL is ssl
     * @return nothing
     * @throws Exception on processing failure
     */
    public void channelRegistered(boolean isSSL) {
        if (isSSL) {
            connectionsActiveCounterMQTTS.incrementAndGet();
        } else {
            connectionsActiveCounterMQTT.incrementAndGet();
        }
    }
    /**
     * Channel unregistered.
     *
     * @param isSSL is ssl
     * @return nothing
     * @throws Exception on processing failure
     */
    public void channelUnregistered(boolean isSSL) {
        if (isSSL) {
            connectionsActiveCounterMQTTS.decrementAndGet();
        } else {
            connectionsActiveCounterMQTT.decrementAndGet();
        }
    }
    /**
     * Checks address.
     *
     * @param address address ({@link InetSocketAddress})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public boolean checkAddress(InetSocketAddress address) {
        return rateLimitService.checkAddress(address);
    }
    /**
     * Handles auth success.
     *
     * @param address address ({@link InetSocketAddress})
     * @return nothing
     * @throws Exception on processing failure
     */
    public void onAuthSuccess(InetSocketAddress address) {
        rateLimitService.onAuthSuccess(address);
    }
    /**
     * Handles auth failure.
     *
     * @param address address ({@link InetSocketAddress})
     * @return nothing
     * @throws Exception on processing failure
     */
    public void onAuthFailure(InetSocketAddress address) {
        rateLimitService.onAuthFailure(address);
    }

}
