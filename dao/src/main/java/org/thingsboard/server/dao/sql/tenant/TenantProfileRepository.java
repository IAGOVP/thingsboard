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
package org.thingsboard.server.dao.sql.tenant;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.edqs.fields.TenantProfileFields;
import org.thingsboard.server.dao.model.sql.TenantProfileEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for tenant profile entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface TenantProfileRepository extends JpaRepository<TenantProfileEntity, UUID> {

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(t.id, 'TENANT_PROFILE', t.name) " +
            "FROM TenantProfileEntity t " +
            "WHERE t.id = :tenantProfileId")
    /**
     * Finds tenant profile info by id.
     *
     * @param tenantProfileId tenant profile id ({@link UUID})
     * @return {@link EntityInfo}
     * @throws Exception if an unexpected error occurs during processing
     */
    EntityInfo findTenantProfileInfoById(@Param("tenantProfileId") UUID tenantProfileId);

    @Query("SELECT t FROM TenantProfileEntity t WHERE " +
            "(:textSearch IS NULL OR ilike(t.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds tenant profiles.
     *
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<TenantProfileEntity> findTenantProfiles(@Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(t.id, 'TENANT_PROFILE', t.name) " +
            "FROM TenantProfileEntity t " +
            "WHERE (:textSearch IS NULL OR ilike(t.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds tenant profile infos.
     *
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityInfo> findTenantProfileInfos(@Param("textSearch") String textSearch,
                                            Pageable pageable);

    @Query("SELECT t FROM TenantProfileEntity t " +
            "WHERE t.isDefault = true")
    /**
     * Finds by default true.
     *
     * @return {@link TenantProfileEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    TenantProfileEntity findByDefaultTrue();

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(t.id, 'TENANT_PROFILE', t.name) " +
            "FROM TenantProfileEntity t " +
            "WHERE t.isDefault = true")
    /**
     * Finds default tenant profile info.
     *
     * @return {@link EntityInfo}
     * @throws Exception if an unexpected error occurs during processing
     */
    EntityInfo findDefaultTenantProfileInfo();
    /**
     * Finds by id in.
     *
     * @param ids ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TenantProfileEntity> findByIdIn(List<UUID> ids);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.TenantProfileFields(t.id, t.createdTime, t.name," +
            "t.isDefault) FROM TenantProfileEntity t WHERE t.id > :id ORDER BY t.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<TenantProfileFields> findNextBatch(@Param("id") UUID id, Limit limit);

}
