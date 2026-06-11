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
package org.thingsboard.monitoring.service.transport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.monitoring.config.transport.TransportMonitoringConfig;
import org.thingsboard.monitoring.config.transport.TransportMonitoringTarget;
import org.thingsboard.monitoring.service.BaseHealthChecker;
import org.thingsboard.monitoring.service.BaseMonitoringService;
/**
 * Monitors MQTT, HTTP, CoAP, and LwM2M transports.
 *
 * <p>Registers {@link org.thingsboard.monitoring.config.transport.TransportType}-specific {@link org.thingsboard.monitoring.service.transport.TransportHealthChecker} beans from configuration.
 */


@Service
@RequiredArgsConstructor
@Slf4j
public final class TransportsMonitoringService extends BaseMonitoringService<TransportMonitoringConfig, TransportMonitoringTarget> {
    /**
     * Creates health checker.
     *
     * @param config monitoring configuration for this transport or domain
     * @param target monitoring target URL and device configuration
     * @return {@link BaseHealthChecker}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected BaseHealthChecker<?, ?> createHealthChecker(TransportMonitoringConfig config, TransportMonitoringTarget target) {
        return applicationContext.getBean(config.getTransportType().getServiceClass(), config, target);
    }
    /**
     * Creates target.
     *
     * @param baseUrl base url ({@link String})
     * @return {@link TransportMonitoringTarget}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected TransportMonitoringTarget createTarget(String baseUrl) {
        TransportMonitoringTarget target = new TransportMonitoringTarget();
        target.setBaseUrl(baseUrl);
        return target;
    }
    /**
     * Returns name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getName() {
        return "transports check";
    }

}
