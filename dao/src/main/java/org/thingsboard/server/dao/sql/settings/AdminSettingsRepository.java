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
package org.thingsboard.server.dao.sql.settings;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.dao.model.sql.AdminSettingsEntity;

import java.util.UUID;


/**

 * Spring Data JPA repository for admin settings entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface AdminSettingsRepository extends JpaRepository<AdminSettingsEntity, UUID> {
    /**
     * Finds by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link AdminSettingsEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    AdminSettingsEntity findByTenantIdAndKey(UUID tenantId, String key);
    /**
     * Deletes by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByTenantIdAndKey(UUID tenantId, String key);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByTenantId(UUID tenantId);
    /**
     * Exists by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndKey(UUID tenantId, String key);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<AdminSettingsEntity> findByTenantId(UUID tenantId, Pageable pageable);

}
