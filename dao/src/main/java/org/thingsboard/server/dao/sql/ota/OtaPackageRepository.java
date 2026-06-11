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
package org.thingsboard.server.dao.sql.ota;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.OtaPackageEntity;

import java.util.UUID;


/**

 * Spring Data JPA repository for ota package entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface OtaPackageRepository extends JpaRepository<OtaPackageEntity, UUID>, ExportableEntityRepository<OtaPackageEntity> {
    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT COALESCE(SUM(ota.data_size), 0) FROM ota_package ota WHERE ota.tenant_id = :tenantId AND ota.data IS NOT NULL", nativeQuery = true)
    Long sumDataSizeByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<OtaPackageEntity> findByTenantId(UUID tenantId, Pageable pageable);
    /**
     * Finds by tenant id and title and version.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @param version version ({@link String})
     * @return {@link OtaPackageEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    OtaPackageEntity findByTenantIdAndTitleAndVersion(UUID tenantId, String title, String version);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM OtaPackageEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);
    /**
     * Finds ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT r.id FROM OtaPackageEntity r WHERE r.tenantId = :tenantId")
    Page<UUID> findIdsByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

    // The 'data' column is of type OID (PostgreSQL large object reference), so it returns the OID as Long
    /**
     * Returns data oid by id.
     *
     * @param id entity UUID primary key
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */
    @Query(value = "SELECT data FROM ota_package WHERE id = :id AND data IS NOT NULL", nativeQuery = true)
    Long getDataOidById(@Param("id") UUID id);
    /**
     * Unlink large object.
     *
     * @param oid oid ({@link Long})
     * @return {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Query(value = "SELECT lo_unlink(:oid)", nativeQuery = true)
    Integer unlinkLargeObject(@Param("oid") Long oid);

}
