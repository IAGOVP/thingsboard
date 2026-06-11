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

/**
 * hash map observer mbean contract (MQTT transport adaptor and topic handling (ThingsBoard common module)).
 */
public interface HashMapObserverMBean {
    int getSize();

    /**
     * Returns gateway count.
     *
     * @param unused unused ({@link String})
     * @return the long result
     * @throws Exception on processing failure
     */
    long getGatewayCount(String unused);

    /**
     * Returns non gateway count.
     *
     * @param unused unused ({@link String})
     * @return the long result
     * @throws Exception on processing failure
     */
    long getNonGatewayCount(String unused);

    /**
     * Returns session by uuid.
     *
     * @param key key ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getSessionByUUID(String key);

    /**
     * Returns all sessions.
     *
     * @param key key ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getAllSessions(String key);

    /**
     * Returns subscribed sessions.
     *
     * @param unused unused ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getSubscribedSessions(String unused);

    /**
     * Returns non active sessions.
     *
     * @param unused unused ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getNonActiveSessions(String unused);

    /**
     * Returns active sessions.
     *
     * @param unused unused ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getActiveSessions(String unused);

    /**
     * Returns gateway device session context connected sessions.
     *
     * @param unused unused ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getGatewayDeviceSessionContextConnectedSessions(String unused);

    /**
     * Returns device aware session context not connected sessions.
     *
     * @param unused unused ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getDeviceAwareSessionContextNotConnectedSessions(String unused);
}
