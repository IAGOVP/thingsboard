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
package org.thingsboard.server.service.state;

import org.springframework.context.ApplicationListener;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.discovery.event.PartitionChangeEvent;

/**
 * Created by ashvayka on 01.05.18.
 */
public interface DeviceStateService extends ApplicationListener<PartitionChangeEvent> {

    void onDeviceConnect(TenantId tenantId, DeviceId deviceId, long lastConnectTime);

    /**
     * Handles device connect.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onDeviceConnect(TenantId tenantId, DeviceId deviceId) {
        onDeviceConnect(tenantId, deviceId, System.currentTimeMillis());
    }
/**
 * Handles device activity.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param deviceId target device identifier
 * @param lastReportedActivityTime last reported activity time
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void onDeviceActivity(TenantId tenantId, DeviceId deviceId, long lastReportedActivityTime);

    /**
     * Handles device disconnect.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param lastDisconnectTime last disconnect time
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceDisconnect(TenantId tenantId, DeviceId deviceId, long lastDisconnectTime);

    /**
     * Handles device disconnect.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onDeviceDisconnect(TenantId tenantId, DeviceId deviceId) {
        onDeviceDisconnect(tenantId, deviceId, System.currentTimeMillis());
    }
/**
 * Handles device inactivity.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param deviceId target device identifier
 * @param lastInactivityTime last inactivity time
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void onDeviceInactivity(TenantId tenantId, DeviceId deviceId, long lastInactivityTime);

    /**
     * Handles device inactivity timeout update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param inactivityTimeout inactivity timeout
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceInactivityTimeoutUpdate(TenantId tenantId, DeviceId deviceId, long inactivityTimeout);

    /**
     * Handles queue msg.
     *
     * @param proto proto
     * @param bytes bytes ({@link TbCallback})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onQueueMsg(TransportProtos.DeviceStateServiceMsgProto proto, TbCallback bytes);

}
