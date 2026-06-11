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
package org.thingsboard.server.dao.sql.mobile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sql.QrCodeSettingsEntity;

import java.util.UUID;




/**


 * Spring Data JPA repository for qr code settings entities.


 *


 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.


 */



public interface QrCodeSettingsRepository extends JpaRepository<QrCodeSettingsEntity, UUID> {
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link QrCodeSettingsEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    QrCodeSettingsEntity findByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM QrCodeSettingsEntity r WHERE r.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);
}
