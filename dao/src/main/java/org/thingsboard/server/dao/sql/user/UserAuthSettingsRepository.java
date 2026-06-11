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
package org.thingsboard.server.dao.sql.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sql.UserAuthSettingsEntity;

import java.util.UUID;
/**
 * Spring Data JPA repository for user auth settings entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */


@Repository
public interface UserAuthSettingsRepository extends JpaRepository<UserAuthSettingsEntity, UUID> {
    /**
     * Finds by user id.
     *
     * @param userId target user identifier
     * @return {@link UserAuthSettingsEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserAuthSettingsEntity findByUserId(UUID userId);
    /**
     * Deletes by user id.
     *
     * @param userId target user identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM UserAuthSettingsEntity e WHERE e.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT s FROM UserAuthSettingsEntity s WHERE s.userId IN (SELECT u.id FROM UserEntity u WHERE u.tenantId = :tenantId)")
    Page<UserAuthSettingsEntity> findByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

}
