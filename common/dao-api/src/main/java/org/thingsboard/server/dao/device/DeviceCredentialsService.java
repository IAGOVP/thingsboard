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
package org.thingsboard.server.dao.device;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.DeviceCredentials;

/**
 * Service API for device credentials persistence and domain operations.
 */
public interface DeviceCredentialsService {

    /**
     * Finds device credentials by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link DeviceCredentials}
     */
    DeviceCredentials findDeviceCredentialsByDeviceId(TenantId tenantId, DeviceId deviceId);

    /**
     * Finds device credentials by credentials id.
     *
     * @param credentialsId credentials id ({@link String})
     * @return {@link DeviceCredentials}
     */
    DeviceCredentials findDeviceCredentialsByCredentialsId(String credentialsId);

    /**
     * Updates device credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     * @return {@link DeviceCredentials}
     */
    DeviceCredentials updateDeviceCredentials(TenantId tenantId, DeviceCredentials deviceCredentials);

    /**
     * Creates device credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     * @return {@link DeviceCredentials}
     */
    DeviceCredentials createDeviceCredentials(TenantId tenantId, DeviceCredentials deviceCredentials);

    /**
     * Format credentials.
     *
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     */
    void formatCredentials(DeviceCredentials deviceCredentials);

    /**
     * To credentials info.
     *
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     * @return {@link JsonNode}
     */
    JsonNode toCredentialsInfo(DeviceCredentials deviceCredentials);

    /**
     * Deletes device credentials.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     */
    void deleteDeviceCredentials(TenantId tenantId, DeviceCredentials deviceCredentials);

    /**
     * Deletes device credentials by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     */
    void deleteDeviceCredentialsByDeviceId(TenantId tenantId, DeviceId deviceId);

}
