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
import org.thingsboard.server.common.data.edqs.fields.TenantFields;
import org.thingsboard.server.dao.model.sql.TenantEntity;
import org.thingsboard.server.dao.model.sql.TenantInfoEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for tenant entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {

    @Query("SELECT new org.thingsboard.server.dao.model.sql.TenantInfoEntity(t, p.name) " +
            "FROM TenantEntity t " +
            "LEFT JOIN TenantProfileEntity p on p.id = t.tenantProfileId " +
            "WHERE t.id = :tenantId")
    /**
     * Finds tenant info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TenantInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    TenantInfoEntity findTenantInfoById(@Param("tenantId") UUID tenantId);
    /**
     * Finds tenants next page.
     *
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT t FROM TenantEntity t WHERE (:textSearch IS NULL OR ilike(t.title, CONCAT('%', :textSearch, '%')) = true)")
    Page<TenantEntity> findTenantsNextPage(@Param("textSearch") String textSearch,
                                           Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.TenantInfoEntity(t, p.name) " +
            "FROM TenantEntity t " +
            "LEFT JOIN TenantProfileEntity p on p.id = t.tenantProfileId " +
            "WHERE (:textSearch IS NULL OR ilike(t.title, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds tenant infos next page.
     *
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<TenantInfoEntity> findTenantInfosNextPage(@Param("textSearch") String textSearch,
                                                   Pageable pageable);
    /**
     * Finds tenants ids.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT t.id FROM TenantEntity t")
    Page<UUID> findTenantsIds(Pageable pageable);
    /**
     * Finds tenant ids by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT t.id FROM TenantEntity t where t.tenantProfileId = :tenantProfileId")
    List<UUID> findTenantIdsByTenantProfileId(@Param("tenantProfileId") UUID tenantProfileId);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.TenantFields(t.id, t.createdTime, t.title, t.version," +
            "t.additionalInfo, t.country, t.state, t.city, t.address, t.address2, t.zip, t.phone, t.email, t.region) FROM TenantEntity t WHERE t.id > :id ORDER BY t.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<TenantFields> findNextBatch(@Param("id") UUID id, Limit limit);
    /**
     * Finds first by title.
     *
     * @param name entity or attribute name
     * @return {@link TenantEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    TenantEntity findFirstByTitle(String name);
    /**
     * Finds tenants by id in.
     *
     * @param tenantIds tenant ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TenantEntity> findTenantsByIdIn(List<UUID> tenantIds);

}
