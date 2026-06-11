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
package org.thingsboard.server.dao.sql.edge;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.edqs.fields.EdgeFields;
import org.thingsboard.server.dao.model.sql.EdgeEntity;
import org.thingsboard.server.dao.model.sql.EdgeInfoEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for edge entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface EdgeRepository extends JpaRepository<EdgeEntity, UUID> {

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                 @Param("customerId") UUID customerId,
                                                 @Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(d, c.title, c.additionalInfo) " +
            "FROM EdgeEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "WHERE d.id = :edgeId")
    /**
     * Finds edge info by id.
     *
     * @param edgeId edge id ({@link UUID})
     * @return {@link EdgeInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    EdgeInfoEntity findEdgeInfoById(@Param("edgeId") UUID edgeId);

    @Query(value = "SELECT * FROM edge_active_attribute_view edge_active",
            countQuery = "SELECT count(*) FROM edge_active_attribute_view",
            nativeQuery = true)
    /**
     * Finds active edges.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeEntity> findActiveEdges(Pageable pageable);

    @Query("SELECT d.id FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<UUID> findIdsByTenantId(@Param("tenantId") UUID tenantId,
                                 @Param("textSearch") String textSearch,
                                 Pageable pageable);

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                    @Param("textSearch") String textSearch,
                                    Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(d, c.title, c.additionalInfo) " +
            "FROM EdgeEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds edge infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeInfoEntity> findEdgeInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                 @Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                           @Param("type") String type,
                                           @Param("textSearch") String textSearch,
                                           Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(d, c.title, c.additionalInfo) " +
            "FROM EdgeEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds edge infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeInfoEntity> findEdgeInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                        @Param("type") String type,
                                                        @Param("textSearch") String textSearch,
                                                        Pageable pageable);

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                        @Param("customerId") UUID customerId,
                                                        @Param("type") String type,
                                                        @Param("textSearch") String textSearch,
                                                        Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM EdgeEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds edge infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeInfoEntity> findEdgeInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                              @Param("customerId") UUID customerId,
                                                              @Param("textSearch") String textSearch,
                                                              Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM EdgeEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds edge infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeInfoEntity> findEdgeInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                     @Param("customerId") UUID customerId,
                                                                     @Param("type") String type,
                                                                     @Param("textSearch") String textSearch,
                                                                     Pageable pageable);

    @Query("SELECT ee FROM EdgeEntity ee, RelationEntity re WHERE ee.tenantId = :tenantId " +
            "AND ee.id = re.fromId AND re.fromType = 'EDGE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.toId = :entityId AND re.toType = :entityType " +
            "AND (:textSearch IS NULL OR ilike(ee.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param entityType entity type discriminator
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeEntity> findByTenantIdAndEntityId(@Param("tenantId") UUID tenantId,
                                               @Param("entityId") UUID entityId,
                                               @Param("entityType") String entityType,
                                               @Param("textSearch") String textSearch,
                                               Pageable pageable);

    @Query("SELECT ee.id FROM EdgeEntity ee, RelationEntity re WHERE ee.tenantId = :tenantId " +
            "AND ee.id = re.fromId AND re.fromType = 'EDGE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.toId = :entityId AND re.toType = :entityType " +
            "AND (:textSearch IS NULL OR ilike(ee.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds ids by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param entityType entity type discriminator
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<UUID> findIdsByTenantIdAndEntityId(@Param("tenantId") UUID tenantId,
                                            @Param("entityId") UUID entityId,
                                            @Param("entityType") String entityType,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);
    /**
     * Finds by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link UUID})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT ee FROM EdgeEntity ee, TenantEntity te WHERE ee.tenantId = te.id AND te.tenantProfileId = :tenantProfileId ")
    Page<EdgeEntity> findByTenantProfileId(@Param("tenantProfileId") UUID tenantProfileId,
                                           Pageable pageable);
    /**
     * Finds tenant edge types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT DISTINCT d.type FROM EdgeEntity d WHERE d.tenantId = :tenantId")
    List<String> findTenantEdgeTypes(@Param("tenantId") UUID tenantId);
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT count(*) FROM EdgeEntity e WHERE e.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link EdgeEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    EdgeEntity findByTenantIdAndName(UUID tenantId, String name);
    /**
     * Finds edges by tenant id and customer id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param edgeIds edge ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EdgeEntity> findEdgesByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> edgeIds);
    /**
     * Finds edges by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeIds edge ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EdgeEntity> findEdgesByTenantIdAndIdIn(UUID tenantId, List<UUID> edgeIds);
    /**
     * Finds by routing key.
     *
     * @param routingKey routing key ({@link String})
     * @return {@link EdgeEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    EdgeEntity findByRoutingKey(String routingKey);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.EdgeFields(e.id, e.createdTime, e.tenantId, e.customerId," +
            "e.name, e.version, e.type, e.label, e.additionalInfo) FROM EdgeEntity e WHERE e.id > :id ORDER BY e.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EdgeFields> findNextBatch(@Param("id") UUID id, Limit limit);

}
