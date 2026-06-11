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

import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.monitoring.config.transport.TransportInfo;
import org.thingsboard.monitoring.config.transport.TransportMonitoringConfig;
import org.thingsboard.monitoring.config.transport.TransportMonitoringTarget;
import org.thingsboard.monitoring.config.transport.TransportType;
import org.thingsboard.monitoring.service.BaseHealthChecker;
/**
 * Marker interface for transport-specific {@link BaseHealthChecker} Spring beans.
 */


@Slf4j
public abstract class TransportHealthChecker<C extends TransportMonitoringConfig> extends BaseHealthChecker<C, TransportMonitoringTarget> {

    @Value("${monitoring.calculated_fields.enabled:true}")
    private boolean calculatedFieldsMonitoringEnabled;

    public TransportHealthChecker(C config, TransportMonitoringTarget target) {
        super(config, target);
    }
    /**
     * Initialize.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void initialize() {
        entityService.checkEntities(config, target);
    }
    /**
     * Creates test payload.
     *
     * @param testValue test value ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String createTestPayload(String testValue) {
        return JacksonUtil.newObjectNode().set(TEST_TELEMETRY_KEY, new TextNode(testValue)).toString();
    }
    /**
     * Returns info.
     *
     * @return {@link Object}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Object getInfo() {
        return new TransportInfo(getTransportType(), target);
    }
    /**
     * Returns key.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getKey() {
        return getTransportType().name().toLowerCase() + (target.getQueue().equals("Main") ? "" : target.getQueue()) + "Transport";
    }
    /**
     * Returns transport type.
     *
     * @return {@link TransportType}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract TransportType getTransportType();
    /**
     * Is cf monitoring enabled.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected boolean isCfMonitoringEnabled() {
        return calculatedFieldsMonitoringEnabled;
    }

}
