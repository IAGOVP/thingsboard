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

import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

/**
 * Persistence contract for device credentials.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (devices, credentials, profiles, and connectivity).
 */

public interface DeviceCredentialsDao extends Dao<DeviceCredentials> {

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentials save(TenantId tenantId, DeviceCredentials deviceCredentials);
    /**
     * Saves or persists and flush.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceCredentials device credentials ({@link DeviceCredentials})
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentials saveAndFlush(TenantId tenantId, DeviceCredentials deviceCredentials);

    
    /**
     * Finds by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentials findByDeviceId(TenantId tenantId, UUID deviceId);

    
    /**
     * Finds by credentials id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param credentialsId credentials id ({@link String})
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentials findByCredentialsId(TenantId tenantId, String credentialsId);
    /**
     * Removes by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentials removeByDeviceId(TenantId tenantId, DeviceId deviceId);

}
