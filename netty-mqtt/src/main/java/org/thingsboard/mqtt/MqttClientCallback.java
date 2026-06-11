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
package org.thingsboard.mqtt;

import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;

/**
 * Client lifecycle callbacks for connection loss and reconnect.
 *
 * <p>Optional hooks: {@link #connectionLost(Throwable)}, reconnect attempts, and successful reconnect.
 */

public interface MqttClientCallback {

    
    /**
     * Called when the TCP/MQTT connection is lost unexpectedly.
     *
     * @param cause failure that closed the connection or caused the error
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void connectionLost(Throwable cause);

    
    /**
     * Called after a successful automatic reconnect.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onSuccessfulReconnect();
    /**
     * Handles conn ack.
     *
     * @param connAckMessage conn ack message ({@link MqttConnAckMessage})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onConnAck(MqttConnAckMessage connAckMessage) {
    }
    /**
     * Handles pub ack.
     *
     * @param pubAckMessage pub ack message ({@link MqttPubAckMessage})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onPubAck(MqttPubAckMessage pubAckMessage) {
    }
    /**
     * Handles sub ack.
     *
     * @param pubAckMessage pub ack message ({@link MqttSubAckMessage})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onSubAck(MqttSubAckMessage pubAckMessage) {
    }
    /**
     * Handles unsub ack.
     *
     * @param unsubAckMessage unsub ack message ({@link MqttUnsubAckMessage})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onUnsubAck(MqttUnsubAckMessage unsubAckMessage) {
    }
    /**
     * Handles disconnect.
     *
     * @param mqttDisconnectMessage mqtt disconnect message ({@link MqttMessage})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onDisconnect(MqttMessage mqttDisconnectMessage) {
    }

}
