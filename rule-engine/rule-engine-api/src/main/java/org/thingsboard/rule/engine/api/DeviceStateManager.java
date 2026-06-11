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
package org.thingsboard.rule.engine.api;

import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.TbCallback;

/**

 * Updates device active/inactive state.

 */


/**

 * Updates device active/inactive connectivity state from rule nodes.

 */


public interface DeviceStateManager {
    /**
     * Handles device connect.
     *
     * @param tenantId tenant UUID
     * @param deviceId device UUID
     * @param connectTime connect time
     * @param callback completion callback for async rule engine operations
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceConnect(TenantId tenantId, DeviceId deviceId, long connectTime, TbCallback callback);
    /**
     * Handles device activity.
     *
     * @param tenantId tenant UUID
     * @param deviceId device UUID
     * @param activityTime activity time
     * @param callback completion callback for async rule engine operations
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceActivity(TenantId tenantId, DeviceId deviceId, long activityTime, TbCallback callback);
    /**
     * Handles device disconnect.
     *
     * @param tenantId tenant UUID
     * @param deviceId device UUID
     * @param disconnectTime disconnect time
     * @param callback completion callback for async rule engine operations
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceDisconnect(TenantId tenantId, DeviceId deviceId, long disconnectTime, TbCallback callback);
    /**
     * Handles device inactivity.
     *
     * @param tenantId tenant UUID
     * @param deviceId device UUID
     * @param inactivityTime inactivity time
     * @param callback completion callback for async rule engine operations
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceInactivity(TenantId tenantId, DeviceId deviceId, long inactivityTime, TbCallback callback);
    /**
     * Handles device inactivity timeout update.
     *
     * @param tenantId tenant UUID
     * @param deviceId device UUID
     * @param inactivityTimeout inactivity timeout
     * @param callback completion callback for async rule engine operations
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceInactivityTimeoutUpdate(TenantId tenantId, DeviceId deviceId, long inactivityTimeout, TbCallback callback);

}
