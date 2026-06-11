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
package org.thingsboard.server.config.mqtt;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Binds MQTT client retransmission settings from application configuration.
 *
 * <p>These properties control how the rule-engine MQTT client retries failed publish
 * operations. Values are loaded from the {@code mqtt.client.retransmission.*} prefix
 * in {@code thingsboard.yml} (or environment variables) and consumed by
 * {@link MqttClientSettingsComponent}.
 *
 * <p>Configuration keys:
 * <ul>
 *   <li>{@code mqtt.client.retransmission.max-attempts} — maximum retry count</li>
 *   <li>{@code mqtt.client.retransmission.initial-delay-millis} — delay before first retry</li>
 *   <li>{@code mqtt.client.retransmission.jitter-factor} — randomization factor to avoid thundering herd</li>
 * </ul>
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "mqtt.client.retransmission")
public class MqttClientRetransmissionSettingsComponent {

    /** Maximum number of retransmission attempts before giving up on an MQTT publish. */
    @PositiveOrZero
    private int maxAttempts;

    /** Initial delay in milliseconds before the first retransmission attempt. */
    @PositiveOrZero
    private long initialDelayMillis;

    /**
     * Jitter factor applied to retry delays (0.0 = no jitter).
     * Spreads retry timing to reduce synchronized retries across clients.
     */
    @PositiveOrZero
    private double jitterFactor;

}
