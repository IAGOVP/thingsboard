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
import org.thingsboard.server.dao.model.sql.TbResourceInfoEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;


/**

 * Spring Data JPA repository for tb resource info entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface TbResourceInfoRepository extends JpaRepository<TbResourceInfoEntity, UUID> {

    @Query("SELECT tr FROM TbResourceInfoEntity tr WHERE " +
            "(:searchText IS NULL OR ilike(tr.title, CONCAT('%', :searchText, '%')) = true) " +
            "AND (tr.tenantId = :tenantId " +
            "OR (tr.tenantId = :systemTenantId " +
            "AND NOT EXISTS " +
            "(SELECT sr FROM TbResourceEntity sr " +
            "WHERE sr.tenantId = :tenantId " +
            "AND tr.resourceType = sr.resourceType " +
            "AND tr.resourceKey = sr.resourceKey)))" +
            "AND tr.resourceType IN :resourceTypes " +
            "AND (:resourceSubTypes IS NULL OR tr.resourceSubType IN :resourceSubTypes)")
    /**
     * Finds all tenant resources by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param systemTenantId system tenant id ({@link UUID})
     * @param resourceTypes resource types ({@link List})
     * @param resourceSubTypes resource sub types ({@link List})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<TbResourceInfoEntity> findAllTenantResourcesByTenantId(@Param("tenantId") UUID tenantId,
                                                                @Param("systemTenantId") UUID systemTenantId,
                                                                @Param("resourceTypes") List<String> resourceTypes,
                                                                @Param("resourceSubTypes") List<String> resourceSubTypes,
                                                                @Param("searchText") String searchText,
                                                                Pageable pageable);

    @Query("SELECT ri FROM TbResourceInfoEntity ri WHERE " +
            "ri.tenantId = :tenantId " +
            "AND ri.resourceType IN :resourceTypes " +
            "AND (:resourceSubTypes IS NULL OR ri.resourceSubType IN :resourceSubTypes) " +
            "AND (:searchText IS NULL OR ilike(ri.title, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds tenant resources by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceTypes resource types ({@link List})
     * @param resourceSubTypes resource sub types ({@link List})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<TbResourceInfoEntity> findTenantResourcesByTenantId(@Param("tenantId") UUID tenantId,
                                                             @Param("resourceTypes") List<String> resourceTypes,
                                                             @Param("resourceSubTypes") List<String> resourceSubTypes,
                                                             @Param("searchText") String searchText,
                                                             Pageable pageable);
    /**
     * Finds by tenant id and resource type and resource key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link String})
     * @param resourceKey resource key ({@link String})
     * @return {@link TbResourceInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceInfoEntity findByTenantIdAndResourceTypeAndResourceKey(UUID tenantId, String resourceType, String resourceKey);
    /**
     * Exists by tenant id and resource type and resource key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link String})
     * @param resourceKey resource key ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndResourceTypeAndResourceKey(UUID tenantId, String resourceType, String resourceKey);

    @Query(value = "SELECT r.resource_key FROM resource r WHERE r.tenant_id = :tenantId AND r.resource_type = :resourceType " +
            "AND starts_with(r.resource_key, :prefix)", nativeQuery = true)
    /**
     * Finds keys by tenant id and resource type and resource key starting with.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link String})
     * @param prefix prefix ({@link String})
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */
    Set<String> findKeysByTenantIdAndResourceTypeAndResourceKeyStartingWith(@Param("tenantId") UUID tenantId,
    /**
     * Finds by tenant id and etag and resource key starting with.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param etag etag ({@link String})
     * @param query filter and sort query definition
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
                                                                            @Param("resourceType") String resourceType,
                                                                            @Param("prefix") String prefix);

    List<TbResourceInfoEntity> findByTenantIdAndEtagAndResourceKeyStartingWith(UUID tenantId, String etag, String query);

    @Query(value = "SELECT * FROM resource r WHERE (r.tenant_id = '13814000-1dd2-11b2-8080-808080808080' OR r.tenant_id = :tenantId) " +
            "AND r.resource_type = :resourceType AND r.etag = :etag ORDER BY created_time, id LIMIT 1", nativeQuery = true)
    /**
     * Finds system or tenant resource by etag.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link String})
     * @param etag etag ({@link String})
     * @return {@link TbResourceInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    TbResourceInfoEntity findSystemOrTenantResourceByEtag(@Param("tenantId") UUID tenantId,
    /**
     * Exists by resource type and public resource key.
     *
     * @param resourceType resource type ({@link String})
     * @param publicResourceKey public resource key ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */
                                                          @Param("resourceType") String resourceType,
                                                          @Param("etag") String etag);

    boolean existsByResourceTypeAndPublicResourceKey(String resourceType, String publicResourceKey);
    /**
     * Finds by resource type and public resource key and is public true.
     *
     * @param resourceType resource type ({@link String})
     * @param publicResourceKey public resource key ({@link String})
     * @return {@link TbResourceInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceInfoEntity findByResourceTypeAndPublicResourceKeyAndIsPublicTrue(String resourceType, String publicResourceKey);

    @Query("SELECT tr FROM TbResourceInfoEntity tr WHERE " +
            "tr.id IN (:resourceIds) AND (tr.tenantId = :tenantId OR tr.tenantId = :systemTenantId)")
    /**
     * Finds system or tenant resources by id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param systemTenantId system tenant id ({@link UUID})
     * @param resourceIds resource ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<TbResourceInfoEntity> findSystemOrTenantResourcesByIdIn(@Param("tenantId") UUID tenantId,
                                                                 @Param("systemTenantId") UUID systemTenantId,
                                                                 @Param("resourceIds") List<UUID> resourceIds);

}
