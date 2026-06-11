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
package org.thingsboard.server.dao.sql.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TbResourceDataInfo;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.TbResourceEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for tb resource entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface TbResourceRepository extends JpaRepository<TbResourceEntity, UUID>, ExportableEntityRepository<TbResourceEntity> {
    /**
     * Finds by tenant id and resource type and resource key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link String})
     * @param resourceKey resource key ({@link String})
     * @return {@link TbResourceEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceEntity findByTenantIdAndResourceTypeAndResourceKey(UUID tenantId, String resourceType, String resourceKey);
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<TbResourceEntity> findAllByTenantId(UUID tenantId, Pageable pageable);

    @Query("SELECT tr FROM TbResourceEntity tr " +
            "WHERE tr.resourceType = :resourceType " +
            "AND (:resourceSubType IS NULL OR tr.resourceSubType = :resourceSubType) " +
            "AND (:searchText IS NULL OR ilike(tr.searchText, CONCAT('%', :searchText, '%')) = true) " +
            "AND (tr.tenantId = :tenantId " +
            "OR (tr.tenantId = :systemAdminId " +
            "AND NOT EXISTS " +
            "(SELECT sr FROM TbResourceEntity sr " +
            "WHERE sr.tenantId = :tenantId " +
            "AND sr.resourceType = :resourceType " +
            "AND tr.resourceKey = sr.resourceKey)))")
    /**
     * Finds resources page.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param sysAdminId sys admin id ({@link UUID})
     * @param resourceType resource type ({@link String})
     * @param resourceSubType resource sub type ({@link String})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<TbResourceEntity> findResourcesPage(
            @Param("tenantId") UUID tenantId,
            @Param("systemAdminId") UUID sysAdminId,
            @Param("resourceType") String resourceType,
            @Param("resourceSubType") String resourceSubType,
            @Param("searchText") String searchText,
            Pageable pageable);

    @Query("SELECT tr FROM TbResourceEntity tr " +
            "WHERE tr.resourceType = :resourceType " +
            "AND (:resourceSubType IS NULL OR tr.resourceSubType = :resourceSubType) " +
            "AND (:searchText IS NULL OR ilike(tr.searchText, CONCAT('%', :searchText, '%')) = true) " +
            "AND (tr.tenantId = :tenantId " +
            "OR (tr.tenantId = :systemAdminId " +
            "AND NOT EXISTS " +
            "(SELECT sr FROM TbResourceEntity sr " +
            "WHERE sr.tenantId = :tenantId " +
            "AND sr.resourceType = :resourceType " +
            "AND tr.resourceKey = sr.resourceKey)))")
    /**
     * Finds resources.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param sysAdminId sys admin id ({@link UUID})
     * @param resourceType resource type ({@link String})
     * @param resourceSubType resource sub type ({@link String})
     * @param searchText search text ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<TbResourceEntity> findResources(@Param("tenantId") UUID tenantId,
                                         @Param("systemAdminId") UUID sysAdminId,
                                         @Param("resourceType") String resourceType,
                                         @Param("resourceSubType") String resourceSubType,
                                         @Param("searchText") String searchText);

    @Query("SELECT tr FROM TbResourceEntity tr " +
            "WHERE tr.resourceType = :resourceType " +
            "AND tr.resourceKey in (:resourceIds) " +
            "AND (tr.tenantId = :tenantId " +
            "OR (tr.tenantId = :systemAdminId " +
            "AND NOT EXISTS " +
            "(SELECT sr FROM TbResourceEntity sr " +
            "WHERE sr.tenantId = :tenantId " +
            "AND sr.resourceType = :resourceType " +
            "AND tr.resourceKey = sr.resourceKey)))")
    /**
     * Finds resources by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param sysAdminId sys admin id ({@link UUID})
     * @param resourceType resource type ({@link String})
     * @param objectIds object ids
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<TbResourceEntity> findResourcesByIds(@Param("tenantId") UUID tenantId,
    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */
                                              @Param("systemAdminId") UUID sysAdminId,
                                              @Param("resourceType") String resourceType,
                                              @Param("resourceIds") String[] objectIds);

    @Query(value = "SELECT COALESCE(SUM(LENGTH(r.data)), 0) FROM resource r WHERE r.tenant_id = :tenantId", nativeQuery = true)
    Long sumDataSizeByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Returns data by id.
     *
     * @param id entity UUID primary key
     * @return the byte[] value
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT r.data FROM TbResourceEntity r WHERE r.id = :id")
    byte[] getDataById(@Param("id") UUID id);
    /**
     * Returns preview by id.
     *
     * @param id entity UUID primary key
     * @return the byte[] value
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT COALESCE(preview, data) FROM resource WHERE id = :id", nativeQuery = true)
    byte[] getPreviewById(@Param("id") UUID id);
    /**
     * Returns data size by id.
     *
     * @param id entity UUID primary key
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT length(r.data) FROM resource r WHERE r.id = :id", nativeQuery = true)
    long getDataSizeById(@Param("id") UUID id);
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link UUID})
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM TbResourceInfoEntity WHERE id = :id")
    UUID getExternalIdByInternal(@Param("id") UUID internalId);
    /**
     * Finds ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT r.id FROM TbResourceInfoEntity r WHERE r.tenantId = :tenantId")
    Page<UUID> findIdsByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);
    /**
     * Returns data info by id.
     *
     * @param id entity UUID primary key
     * @return {@link TbResourceDataInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT new org.thingsboard.server.common.data.TbResourceDataInfo(r.data, r.descriptor) FROM TbResourceEntity r WHERE r.id = :id")
    TbResourceDataInfo getDataInfoById(UUID id);
}
