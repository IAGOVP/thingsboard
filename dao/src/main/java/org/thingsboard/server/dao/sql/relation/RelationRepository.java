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
package org.thingsboard.server.dao.sql.relation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.dao.model.sql.RelationCompositeKey;
import org.thingsboard.server.dao.model.sql.RelationEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for relation entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface RelationRepository
        extends JpaRepository<RelationEntity, RelationCompositeKey>, JpaSpecificationExecutor<RelationEntity> {
    /**
     * Finds all by from id and from type and relation type group.
     *
     * @param fromId from id ({@link UUID})
     * @param fromType from type ({@link String})
     * @param relationTypeGroup relation type group ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RelationEntity> findAllByFromIdAndFromTypeAndRelationTypeGroup(UUID fromId,
                                                                        String fromType,
                                                                        String relationTypeGroup);
    /**
     * Finds all by from id and from type and relation type group in.
     *
     * @param fromId from id ({@link UUID})
     * @param fromType from type ({@link String})
     * @param relationTypeGroups relation type groups ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RelationEntity> findAllByFromIdAndFromTypeAndRelationTypeGroupIn(UUID fromId,
                                                                          String fromType,
                                                                          List<String> relationTypeGroups);
    /**
     * Finds all by from id and from type and relation type and relation type group.
     *
     * @param fromId from id ({@link UUID})
     * @param fromType from type ({@link String})
     * @param relationType relation type ({@link String})
     * @param relationTypeGroup relation type group ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RelationEntity> findAllByFromIdAndFromTypeAndRelationTypeAndRelationTypeGroup(UUID fromId,
                                                                                       String fromType,
                                                                                       String relationType,
                                                                                       String relationTypeGroup);
    /**
     * Finds all by to id and to type and relation type group.
     *
     * @param toId to id ({@link UUID})
     * @param toType to type ({@link String})
     * @param relationTypeGroup relation type group ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RelationEntity> findAllByToIdAndToTypeAndRelationTypeGroup(UUID toId,
                                                                    String toType,
                                                                    String relationTypeGroup);
    /**
     * Finds all by to id and to type and relation type group in.
     *
     * @param toId to id ({@link UUID})
     * @param toType to type ({@link String})
     * @param relationTypeGroups relation type groups ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RelationEntity> findAllByToIdAndToTypeAndRelationTypeGroupIn(UUID toId,
                                                                      String toType,
                                                                      List<String> relationTypeGroups);
    /**
     * Finds all by to id and to type and relation type and relation type group.
     *
     * @param toId to id ({@link UUID})
     * @param toType to type ({@link String})
     * @param relationType relation type ({@link String})
     * @param relationTypeGroup relation type group ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RelationEntity> findAllByToIdAndToTypeAndRelationTypeAndRelationTypeGroup(UUID toId,
                                                                                   String toType,
                                                                                   String relationType,
                                                                                   String relationTypeGroup);

    @Query("SELECT r FROM RelationEntity r WHERE " +
            "r.relationTypeGroup = 'RULE_NODE' AND r.toType = 'RULE_CHAIN' " +
            "AND r.toId in (SELECT id from RuleChainEntity where type = :ruleChainType )")
    /**
     * Finds rule node to rule chain relations.
     *
     * @param ruleChainType rule chain type ({@link RuleChainType})
     * @param page page ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<RelationEntity> findRuleNodeToRuleChainRelations(@Param("ruleChainType") RuleChainType ruleChainType, Pageable page);
    /**
     * Saves or persists the requested data.
     *
     * @param entity domain entity to persist or validate
     * @return {@link S}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    <S extends RelationEntity> S save(S entity);
    /**
     * Deletes by id.
     *
     * @param id entity UUID primary key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    void deleteById(RelationCompositeKey id);
    /**
     * Deletes by from id and from type.
     *
     * @param fromId from id ({@link UUID})
     * @param fromType from type ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM RelationEntity r where r.fromId = :fromId and r.fromType = :fromType")
    void deleteByFromIdAndFromType(@Param("fromId") UUID fromId, @Param("fromType") String fromType);
    /**
     * Deletes by to id and to type and relation type group in.
     *
     * @param toId to id ({@link UUID})
     * @param toType to type ({@link String})
     * @param relationTypeGroups relation type groups ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM RelationEntity r where r.toId = :toId and r.toType = :toType and r.relationTypeGroup in :relationTypeGroups")
    void deleteByToIdAndToTypeAndRelationTypeGroupIn(@Param("toId") UUID toId, @Param("toType") String toType, @Param("relationTypeGroups") List<String> relationTypeGroups);
    /**
     * Deletes by from id and from type and relation type group in.
     *
     * @param fromId from id ({@link UUID})
     * @param fromType from type ({@link String})
     * @param relationTypeGroups relation type groups ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM RelationEntity r where r.fromId = :fromId and r.fromType = :fromType and r.relationTypeGroup in :relationTypeGroups")
    void deleteByFromIdAndFromTypeAndRelationTypeGroupIn(@Param("fromId") UUID fromId, @Param("fromType") String fromType, @Param("relationTypeGroups") List<String> relationTypeGroups);

    @Query(value = "SELECT from_id, from_type, relation_type_group, relation_type, to_id, to_type, additional_info, version FROM relation" +
            " WHERE (from_id, from_type, relation_type_group, relation_type, to_id, to_type) > " +
            "(:fromId, :fromType, :relationTypeGroup, :relationType, :toId, :toType) ORDER BY " +
            "from_id, from_type, relation_type_group, relation_type, to_id, to_type LIMIT :batchSize", nativeQuery = true)
    /**
     * Finds next batch.
     *
     * @param fromId from id ({@link UUID})
     * @param fromType from type ({@link String})
     * @param relationTypeGroup relation type group ({@link String})
     * @param relationType relation type ({@link String})
     * @param toId to id ({@link UUID})
     * @param toType to type ({@link String})
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<RelationEntity> findNextBatch(@Param("fromId") UUID fromId,
                                       @Param("fromType") String fromType,
                                       @Param("relationTypeGroup") String relationTypeGroup,
                                       @Param("relationType") String relationType,
                                       @Param("toId") UUID toId,
                                       @Param("toType") String toType,
                                       @Param("batchSize") int batchSize);

}
