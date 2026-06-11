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
package org.thingsboard.server.dao.sql.entityview;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.edqs.fields.EntityViewFields;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.EntityViewEntity;
import org.thingsboard.server.dao.model.sql.EntityViewInfoEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for entity view entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface EntityViewRepository extends JpaRepository<EntityViewEntity, UUID>, ExportableEntityRepository<EntityViewEntity> {

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EntityViewInfoEntity(e, c.title, c.additionalInfo) " +
            "FROM EntityViewEntity e " +
            "LEFT JOIN CustomerEntity c on c.id = e.customerId " +
            "WHERE e.id = :entityViewId")
    /**
     * Finds entity view info by id.
     *
     * @param entityViewId entity view id ({@link UUID})
     * @return {@link EntityViewInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    EntityViewInfoEntity findEntityViewInfoById(@Param("entityViewId") UUID entityViewId);

    @Query("SELECT e FROM EntityViewEntity e WHERE e.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(e.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityViewEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                          @Param("textSearch") String textSearch,
                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EntityViewInfoEntity(e, c.title, c.additionalInfo) " +
            "FROM EntityViewEntity e " +
            "LEFT JOIN CustomerEntity c on c.id = e.customerId " +
            "WHERE e.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(e.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds entity view infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityViewInfoEntity> findEntityViewInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                             @Param("textSearch") String textSearch,
                                                             Pageable pageable);

    @Query("SELECT e FROM EntityViewEntity e WHERE e.tenantId = :tenantId " +
            "AND e.type = :type " +
            "AND (:textSearch IS NULL OR ilike(e.name, CONCAT('%', :textSearch, '%')) = true)")
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
    Page<EntityViewEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                 @Param("type") String type,
                                                 @Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EntityViewInfoEntity(e, c.title, c.additionalInfo) " +
            "FROM EntityViewEntity e " +
            "LEFT JOIN CustomerEntity c on c.id = e.customerId " +
            "WHERE e.tenantId = :tenantId " +
            "AND e.type = :type " +
            "AND (:textSearch IS NULL OR ilike(e.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds entity view infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityViewInfoEntity> findEntityViewInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                                    @Param("type") String type,
                                                                    @Param("textSearch") String textSearch,
                                                                    Pageable pageable);

    @Query("SELECT e FROM EntityViewEntity e WHERE e.tenantId = :tenantId " +
            "AND e.customerId = :customerId " +
            "AND (:searchText IS NULL OR ilike(e.name, CONCAT('%', :searchText, '%')) = true)")
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
    Page<EntityViewEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                       @Param("customerId") UUID customerId,
                                                       @Param("searchText") String searchText,
                                                       Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EntityViewInfoEntity(e, c.title, c.additionalInfo) " +
            "FROM EntityViewEntity e " +
            "LEFT JOIN CustomerEntity c on c.id = e.customerId " +
            "WHERE e.tenantId = :tenantId " +
            "AND e.customerId = :customerId " +
            "AND (:searchText IS NULL OR ilike(e.name, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds entity view infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityViewInfoEntity> findEntityViewInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                          @Param("customerId") UUID customerId,
                                                                          @Param("searchText") String searchText,
                                                                          Pageable pageable);

    @Query("SELECT e FROM EntityViewEntity e WHERE e.tenantId = :tenantId " +
            "AND e.customerId = :customerId " +
            "AND e.type = :type " +
            "AND (:searchText IS NULL OR ilike(e.name, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityViewEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                              @Param("customerId") UUID customerId,
                                                              @Param("type") String type,
                                                              @Param("searchText") String searchText,
                                                              Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EntityViewInfoEntity(e, c.title, c.additionalInfo) " +
            "FROM EntityViewEntity e " +
            "LEFT JOIN CustomerEntity c on c.id = e.customerId " +
            "WHERE e.tenantId = :tenantId " +
            "AND e.customerId = :customerId " +
            "AND e.type = :type " +
            "AND (:textSearch IS NULL OR ilike(e.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds entity view infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityViewInfoEntity> findEntityViewInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                                 @Param("customerId") UUID customerId,
                                                                                 @Param("type") String type,
                                                                                 @Param("textSearch") String textSearch,
                                                                                 Pageable pageable);
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link EntityViewEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityViewEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(a.id, 'ENTITY_VIEW', a.name) " +
            "FROM EntityViewEntity a WHERE a.tenantId = :tenantId AND a.name LIKE CONCAT(:prefix, '%')")
    /**
     * Finds entity infos by name prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param prefix prefix ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findEntityInfosByNamePrefix(UUID tenantId, String prefix);
    /**
     * Finds all by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityViewEntity> findAllByTenantIdAndEntityId(UUID tenantId, UUID entityId);
    /**
     * Exists by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndEntityId(UUID tenantId, UUID entityId);
    /**
     * Finds entity views by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewIds entity view ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityViewEntity> findEntityViewsByTenantIdAndIdIn(UUID tenantId, List<UUID> entityViewIds);
    /**
     * Finds tenant entity view types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT DISTINCT ev.type FROM EntityViewEntity ev WHERE ev.tenantId = :tenantId")
    List<String> findTenantEntityViewTypes(@Param("tenantId") UUID tenantId);

    @Query("SELECT ev FROM EntityViewEntity ev, RelationEntity re WHERE ev.tenantId = :tenantId " +
            "AND ev.id = re.toId AND re.toType = 'ENTITY_VIEW' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(ev.name, CONCAT('%', :searchText, '%')) = true)")
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
    Page<EntityViewEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                               @Param("edgeId") UUID edgeId,
                                               @Param("searchText") String searchText,
                                               Pageable pageable);

    @Query("SELECT ev FROM EntityViewEntity ev, RelationEntity re WHERE ev.tenantId = :tenantId " +
            "AND ev.id = re.toId AND re.toType = 'ENTITY_VIEW' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND ev.type = :type " +
            "AND (:searchText IS NULL OR ilike(ev.name, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EntityViewEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                   @Param("edgeId") UUID edgeId,
                                                   @Param("type") String type,
                                                   @Param("searchText") String searchText,
                                                   Pageable pageable);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */
    @Query("SELECT externalId FROM EntityViewEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.EntityViewFields(e.id, e.createdTime, e.tenantId, " +
            "e.customerId, e.name, e.type, e.additionalInfo, e.version) " +
            "FROM EntityViewEntity e WHERE e.id > :id ORDER BY e.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityViewFields> findNextBatch(@Param("id") UUID id, Limit limit);
}
