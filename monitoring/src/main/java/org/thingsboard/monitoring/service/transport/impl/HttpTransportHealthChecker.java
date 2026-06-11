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
package org.thingsboard.monitoring.service.transport.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.thingsboard.monitoring.config.transport.HttpTransportMonitoringConfig;
import org.thingsboard.monitoring.config.transport.TransportMonitoringTarget;
import org.thingsboard.monitoring.config.transport.TransportType;
import org.thingsboard.monitoring.service.transport.TransportHealthChecker;

import java.time.Duration;
/**
 * POSTs test telemetry over the HTTP device API and validates WebSocket echo.
 */


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class HttpTransportHealthChecker extends TransportHealthChecker<HttpTransportMonitoringConfig> {

    private RestTemplate restTemplate;

    protected HttpTransportHealthChecker(HttpTransportMonitoringConfig config, TransportMonitoringTarget target) {
        super(config, target);
    }
    /**
     * Init client.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void initClient() throws Exception {
        if (restTemplate == null) {
            restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofMillis(config.getRequestTimeoutMs()))
                    .setReadTimeout(Duration.ofMillis(config.getRequestTimeoutMs()))
                    .build();
            log.debug("Initialized HTTP client");
        }
    }
    /**
     * Send test payload.
     *
     * @param payload payload ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void sendTestPayload(String payload) throws Exception {
        String accessToken = target.getDevice().getCredentials().getCredentialsId();
        restTemplate.postForObject(target.getBaseUrl() + "/api/v1/" + accessToken + "/telemetry", payload, String.class);
    }
    /**
     * Destroy client.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void destroyClient() throws Exception {}
    /**
     * Returns transport type.
     *
     * @return {@link TransportType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected TransportType getTransportType() {
        return TransportType.HTTP;
    }

}
