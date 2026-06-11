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
package org.thingsboard.server.dao.sql.device;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sql.DeviceCredentialsEntity;

import java.util.UUID;

/**
 * Spring Data JPA repository for device credentials entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */

public interface DeviceCredentialsRepository extends JpaRepository<DeviceCredentialsEntity, UUID> {
    /**
     * Finds by device id.
     *
     * @param deviceId target device identifier
     * @return {@link DeviceCredentialsEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentialsEntity findByDeviceId(UUID deviceId);
    /**
     * Finds by credentials id.
     *
     * @param credentialsId credentials id ({@link String})
     * @return {@link DeviceCredentialsEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentialsEntity findByCredentialsId(String credentialsId);
    /**
     * Deletes by device id.
     *
     * @param deviceId target device identifier
     * @return {@link DeviceCredentialsEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Query(value = "DELETE FROM device_credentials WHERE device_id = :deviceId RETURNING *", nativeQuery = true)
    DeviceCredentialsEntity deleteByDeviceId(@Param("deviceId") UUID deviceId);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Query("SELECT c FROM DeviceCredentialsEntity c WHERE c.deviceId IN (SELECT d.id FROM DeviceEntity d WHERE d.tenantId = :tenantId)")
    Page<DeviceCredentialsEntity> findByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

}
