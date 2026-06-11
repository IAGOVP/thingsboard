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
package org.thingsboard.server.dao.sql.dashboard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.dao.model.sql.DashboardInfoEntity;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for dashboard info entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */

public interface DashboardInfoRepository extends JpaRepository<DashboardInfoEntity, UUID> {
    /**
     * Finds first by tenant id and title.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @return {@link DashboardInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    DashboardInfoEntity findFirstByTenantIdAndTitle(UUID tenantId, String title);

    @Query("SELECT di FROM DashboardInfoEntity di WHERE di.tenantId = :tenantId " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DashboardInfoEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                             @Param("searchText") String searchText,
                                             Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di WHERE di.tenantId = :tenantId " +
            "AND di.mobileHide = false " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds mobile by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DashboardInfoEntity> findMobileByTenantId(@Param("tenantId") UUID tenantId,
                                                   @Param("searchText") String searchText,
                                                   Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di, RelationEntity re WHERE di.tenantId = :tenantId " +
            "AND di.id = re.toId AND re.toType = 'DASHBOARD' AND re.relationTypeGroup = 'DASHBOARD' " +
            "AND re.relationType = 'Contains' AND re.fromId = :customerId AND re.fromType = 'CUSTOMER' " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DashboardInfoEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                          @Param("customerId") UUID customerId,
                                                          @Param("searchText") String searchText,
                                                          Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di, RelationEntity re WHERE di.tenantId = :tenantId " +
            "AND di.mobileHide = false " +
            "AND di.id = re.toId AND re.toType = 'DASHBOARD' AND re.relationTypeGroup = 'DASHBOARD' " +
            "AND re.relationType = 'Contains' AND re.fromId = :customerId AND re.fromType = 'CUSTOMER' " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds mobile by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DashboardInfoEntity> findMobileByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                          @Param("customerId") UUID customerId,
                                                          @Param("searchText") String searchText,
                                                          Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di, RelationEntity re WHERE di.tenantId = :tenantId " +
            "AND di.id = re.toId AND re.toType = 'DASHBOARD' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DashboardInfoEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                                      @Param("edgeId") UUID edgeId,
                                                      @Param("searchText") String searchText,
                                                      Pageable pageable);
    /**
     * Finds title by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link UUID})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT di.title FROM DashboardInfoEntity di WHERE di.tenantId = :tenantId AND di.id = :dashboardId")
    String findTitleByTenantIdAndId(@Param("tenantId") UUID tenantId, @Param("dashboardId") UUID dashboardId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM dashboard d WHERE d.tenant_id = :tenantId " +
                    "and (d.image = :imageLink or d.configuration ILIKE CONCAT('%\"', :imageLink, '\"%')) limit :limit"
    )
    /**
     * Finds by tenant and image link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageLink image link ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<DashboardInfoEntity> findByTenantAndImageLink(@Param("tenantId") UUID tenantId, @Param("imageLink") String imageLink, @Param("limit") int limit);

    @Query(nativeQuery = true,
            value = "SELECT * FROM dashboard d WHERE d.image = :imageLink or d.configuration ILIKE CONCAT('%\"', :imageLink, '\"%') limit :limit"
    )
    /**
     * Finds by image link.
     *
     * @param imageLink image link ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<DashboardInfoEntity> findByImageLink(@Param("imageLink") String imageLink, @Param("limit") int limit);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(d.id, 'DASHBOARD', d.title) " +
            "FROM DashboardEntity d WHERE d.tenantId = :tenantId AND ilike(cast(d.configuration as string), CONCAT('%', :link, '%')) = true")
    /**
     * Finds dashboard infos by tenant id and resource link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param link link ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findDashboardInfosByTenantIdAndResourceLink(@Param("tenantId") UUID tenantId,
                                                                 @Param("link") String link,
                                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(d.id, 'DASHBOARD', d.title) " +
            "FROM DashboardEntity d WHERE ilike(cast(d.configuration as string), CONCAT('%', :link, '%')) = true")
    /**
     * Finds dashboard infos by resource link.
     *
     * @param link link ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findDashboardInfosByResourceLink(@Param("link") String link,
                                                      Pageable pageable);
    /**
     * Finds by id in.
     *
     * @param dashboardIds dashboard ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<DashboardInfoEntity> findByIdIn(List<UUID> dashboardIds);

}
