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
package org.thingsboard.server.dao.sql.rule;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.edqs.fields.RuleChainFields;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.RuleChainEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for rule chain entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface RuleChainRepository extends JpaRepository<RuleChainEntity, UUID>, ExportableEntityRepository<RuleChainEntity> {

    @Query("SELECT rc FROM RuleChainEntity rc WHERE rc.tenantId = :tenantId " +
            "AND (:searchText IS NULL OR ilike(rc.name, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<RuleChainEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                         @Param("searchText") String searchText,
                                         Pageable pageable);

    @Query("SELECT rc FROM RuleChainEntity rc WHERE rc.tenantId = :tenantId " +
            "AND rc.type = :type " +
            "AND (:searchText IS NULL OR ilike(rc.name, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<RuleChainEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                @Param("type") RuleChainType type,
                                                @Param("searchText") String searchText,
                                                Pageable pageable);

    @Query("SELECT rc FROM RuleChainEntity rc, RelationEntity re WHERE rc.tenantId = :tenantId " +
            "AND rc.id = re.toId AND re.toType = 'RULE_CHAIN' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(rc.name, CONCAT('%', :searchText, '%')) = true)")
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
    Page<RuleChainEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                                  @Param("edgeId") UUID edgeId,
                                                  @Param("searchText") String searchText,
                                                  Pageable pageable);

    @Query("SELECT rc FROM RuleChainEntity rc, RelationEntity re WHERE rc.tenantId = :tenantId " +
            "AND rc.id = re.toId AND re.toType = 'RULE_CHAIN' AND re.relationTypeGroup = 'EDGE_AUTO_ASSIGN_RULE_CHAIN' " +
            "AND re.relationType = 'Contains' AND re.fromId = :tenantId AND re.fromType = 'TENANT' " +
            "AND (:searchText IS NULL OR ilike(rc.name, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds auto assign by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<RuleChainEntity> findAutoAssignByTenantId(@Param("tenantId") UUID tenantId,
                                                   @Param("searchText") String searchText,
                                                   Pageable pageable);
    /**
     * Finds by tenant id and type and root is true.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainType rule chain type ({@link RuleChainType})
     * @return {@link RuleChainEntity}
     * @throws Exception if an unexpected error occurs during processing
     */


    RuleChainEntity findByTenantIdAndTypeAndRootIsTrue(UUID tenantId, RuleChainType ruleChainType);
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long countByTenantId(UUID tenantId);
    /**
     * Finds by tenant id and type and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param name entity or attribute name
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleChainEntity> findByTenantIdAndTypeAndName(UUID tenantId, RuleChainType type, String name);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM RuleChainEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(rc.id, 'RULE_CHAIN', rc.name) " +
            "FROM RuleChainEntity rc WHERE rc.tenantId = :tenantId AND EXISTS " +
            "(SELECT 1 FROM RuleNodeEntity rn WHERE rn.ruleChainId = rc.id AND cast(rn.configuration as string) LIKE CONCAT('%', :resourceId, '%'))")
    /**
     * Finds rule chains by tenant id and resource.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link String})
     * @param of of ({@link PageRequest})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findRuleChainsByTenantIdAndResource(@Param("tenantId") UUID tenantId,
                                                         @Param("resourceId") String resourceId,
                                                         PageRequest of);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(rc.id, 'RULE_CHAIN', rc.name) " +
            "FROM RuleChainEntity rc WHERE EXISTS " +
            "(SELECT 1 FROM RuleNodeEntity rn WHERE rn.ruleChainId = rc.id AND cast(rn.configuration as string) LIKE CONCAT('%', :resourceId, '%'))")
    /**
     * Finds rule chains by resource.
     *
     * @param resourceId resource id ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findRuleChainsByResource(@Param("resourceId") String resourceId,
                                              Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.RuleChainFields(r.id, r.createdTime, r.tenantId," +
            "r.name, r.version, r.additionalInfo) FROM RuleChainEntity r WHERE r.id > :id ORDER BY r.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<RuleChainFields> findNextBatch(@Param("id") UUID id, Limit limit);
    /**
     * Finds rule chains by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainIds rule chain ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleChainEntity> findRuleChainsByTenantIdAndIdIn(UUID tenantId, List<UUID> ruleChainIds);

}
