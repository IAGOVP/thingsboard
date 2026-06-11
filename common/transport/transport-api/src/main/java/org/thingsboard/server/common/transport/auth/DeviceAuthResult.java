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
package org.thingsboard.server.common.transport.auth;

import org.thingsboard.server.common.data.id.DeviceId;

/**
 * Device auth result.
 */
public class DeviceAuthResult {

    private final boolean success;
    private final DeviceId deviceId;
    private final String errorMsg;
    /**
     * Of.
     *
     * @param deviceId target device identifier
     * @return {@link DeviceAuthResult}
     * @throws Exception on processing failure
     */
    public static DeviceAuthResult of(DeviceId deviceId) {
        return new DeviceAuthResult(true, deviceId, null);
    }
    /**
     * Of.
     *
     * @param errorMsg error msg ({@link String})
     * @return {@link DeviceAuthResult}
     * @throws Exception on processing failure
     */
    public static DeviceAuthResult of(String errorMsg) {
        return new DeviceAuthResult(false, null, errorMsg);
    }

    private DeviceAuthResult(boolean success, DeviceId deviceId, String errorMsg) {
        super();
        this.success = success;
        this.deviceId = deviceId;
        this.errorMsg = errorMsg;
    }
    /**
     * Is success.
     *
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public boolean isSuccess() {
        return success;
    }
    /**
     * Returns device id.
     *
     * @return {@link DeviceId}
     * @throws Exception on processing failure
     */
    public DeviceId getDeviceId() {
        return deviceId;
    }
    /**
     * Returns error msg.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public String toString() {
        return "DeviceAuthResult [success=" + success + ", deviceId=" + deviceId + ", errorMsg=" + errorMsg + "]";
    }

}
