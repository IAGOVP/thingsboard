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
package org.thingsboard.server.dao.sql.customer;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.edqs.fields.CustomerFields;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.CustomerEntity;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for customer entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID>, ExportableEntityRepository<CustomerEntity> {

    @Query("SELECT c FROM CustomerEntity c WHERE c.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<CustomerEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                        @Param("textSearch") String textSearch,
                                        Pageable pageable);
    /**
     * Finds by tenant id and title.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @return {@link CustomerEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    CustomerEntity findByTenantIdAndTitle(UUID tenantId, String title);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(a.id, 'CUSTOMER', a.title) " +
            "FROM CustomerEntity a WHERE a.tenantId = :tenantId AND a.title LIKE CONCAT(:prefix, '%')")
    /**
     * Finds entity infos by name prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param prefix prefix ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findEntityInfosByNamePrefix(UUID tenantId, String prefix);

    @Query(value = "SELECT * FROM customer c WHERE c.tenant_id = :tenantId " +
            "AND c.is_public IS TRUE ORDER BY c.id ASC LIMIT 1", nativeQuery = true)
    /**
     * Finds public customer by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link CustomerEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    CustomerEntity findPublicCustomerByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */


    Long countByTenantId(UUID tenantId);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM CustomerEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query(value = "SELECT c.* FROM customer c " +
            "INNER JOIN (SELECT tenant_id, title FROM customer GROUP BY tenant_id, title HAVING COUNT(title) > 1) dc " +
            "ON c.tenant_id = dc.tenant_id AND c.title = dc.title " +
            "ORDER BY c.tenant_id, c.title, c.id",
            nativeQuery = true)
    /**
     * Finds customers with the same title.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<CustomerEntity> findCustomersWithTheSameTitle(Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.CustomerFields(c.id, c.createdTime, c.tenantId, " +
            "c.title, c.version, c.additionalInfo, c.country, c.state, c.city, c.address, c.address2, c.zip, c.phone, c.email) " +
            "FROM CustomerEntity c WHERE c.id > :id ORDER BY c.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<CustomerFields> findNextBatch(@Param("id") UUID id, Limit limit);
    /**
     * Finds customers by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerIds customer ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CustomerEntity> findCustomersByTenantIdAndIdIn(UUID tenantId, List<UUID> customerIds);
}
