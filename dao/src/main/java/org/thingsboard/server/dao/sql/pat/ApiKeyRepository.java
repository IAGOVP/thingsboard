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
package org.thingsboard.server.dao.sql.pat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sql.ApiKeyEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;


/**

 * Spring Data JPA repository for api key entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, UUID> {
    /**
     * Finds by value.
     *
     * @param value value ({@link String})
     * @return {@link ApiKeyEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    ApiKeyEntity findByValue(String value);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<ApiKeyEntity> findByTenantId(UUID tenantId, Pageable pageable);
    /**
     * Finds by tenant id and user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<ApiKeyEntity> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    @Transactional
    @Modifying
    @Query(value = """
                DELETE FROM api_key
                WHERE tenant_id = :tenantId
                RETURNING value
            """, nativeQuery = true
    )
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */
    Set<String> deleteByTenantId(@Param("tenantId") UUID tenantId);

    @Transactional
    @Modifying
    @Query(value = """
                DELETE FROM api_key
                WHERE tenant_id = :tenantId AND user_id = :userId
                RETURNING value
            """, nativeQuery = true
    )
    /**
     * Deletes by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */
    Set<String> deleteByUserId(@Param("tenantId") UUID tenantId,
    /**
     * Deletes all by expiration time before.
     *
     * @param ts ts
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */
                               @Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ApiKeyEntity ak WHERE ak.expirationTime > 0 AND ak.expirationTime < :ts")
    int deleteAllByExpirationTimeBefore(@Param("ts") long ts);

}
