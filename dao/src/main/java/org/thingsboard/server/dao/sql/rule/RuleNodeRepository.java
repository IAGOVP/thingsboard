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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sql.RuleNodeEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for rule node entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface RuleNodeRepository extends JpaRepository<RuleNodeEntity, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM rule_node r WHERE r.rule_chain_id in " +
            "(select id from rule_chain rc WHERE rc.tenant_id = :tenantId) AND r.type = :ruleType " +
            " AND (:searchText IS NULL OR r.configuration ILIKE CONCAT('%', :searchText, '%'))")
    /**
     * Finds rule nodes by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleType rule type ({@link String})
     * @param searchText search text ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<RuleNodeEntity> findRuleNodesByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                        @Param("ruleType") String ruleType,
                                                        @Param("searchText") String searchText);

    @Query(nativeQuery = true, value = "SELECT * FROM rule_node r WHERE r.type = :ruleType " +
            " AND (:searchText IS NULL OR r.configuration ILIKE CONCAT('%', :searchText, '%'))")
    /**
     * Finds all rule nodes by type.
     *
     * @param ruleType rule type ({@link String})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<RuleNodeEntity> findAllRuleNodesByType(@Param("ruleType") String ruleType,
                                                @Param("searchText") String searchText,
                                                Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM rule_node r WHERE r.type = :ruleType " +
            " AND r.configuration_version < :version " +
            " AND (:searchText IS NULL OR r.configuration ILIKE CONCAT('%', :searchText, '%'))")
    /**
     * Finds all rule nodes by type and version less than.
     *
     * @param ruleType rule type ({@link String})
     * @param version version
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<RuleNodeEntity> findAllRuleNodesByTypeAndVersionLessThan(@Param("ruleType") String ruleType,
                                                                  @Param("version") int version,
                                                                  @Param("searchText") String searchText,
                                                                  Pageable pageable);
    /**
     * Finds all rule node ids by type and version less than.
     *
     * @param ruleType rule type ({@link String})
     * @param version version
     * @param pageable pageable ({@link Pageable})
     * @return {@link Slice}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT r.id FROM RuleNodeEntity r WHERE r.type = :ruleType AND r.configurationVersion < :version")
    Slice<UUID> findAllRuleNodeIdsByTypeAndVersionLessThan(@Param("ruleType") String ruleType,
                                                           @Param("version") int version,
                                                           Pageable pageable);
    /**
     * Finds rule nodes by rule chain id and external id in.
     *
     * @param ruleChainId rule chain id ({@link UUID})
     * @param externalIds external ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleNodeEntity> findRuleNodesByRuleChainIdAndExternalIdIn(UUID ruleChainId, List<UUID> externalIds);
    /**
     * Deletes by id in.
     *
     * @param ids ids ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM RuleNodeEntity e where e.id in :ids")
    void deleteByIdIn(@Param("ids") List<UUID> ids);

}
