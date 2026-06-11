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
package org.thingsboard.server.dao.relation;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntityRelationPathQuery;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.rule.RuleChainType;

import java.util.List;

/**
 * Persistence contract for relation.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (entity-to-entity relation graph persistence).
 */

public interface RelationDao {
    /**
     * Finds all by from.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findAllByFrom(TenantId tenantId, EntityId from, RelationTypeGroup typeGroup);
    /**
     * Finds all by from.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findAllByFrom(TenantId tenantId, EntityId from);
    /**
     * Finds all by from and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findAllByFromAndType(TenantId tenantId, EntityId from, String relationType, RelationTypeGroup typeGroup);
    /**
     * Finds all by to.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findAllByTo(TenantId tenantId, EntityId to, RelationTypeGroup typeGroup);
    /**
     * Finds all by to.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findAllByTo(TenantId tenantId, EntityId to);
    /**
     * Finds all by to and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findAllByToAndType(TenantId tenantId, EntityId to, String relationType, RelationTypeGroup typeGroup);
    /**
     * Checks relation async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link Boolean}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Boolean> checkRelationAsync(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);
    /**
     * Checks relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean checkRelation(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);
    /**
     * Returns relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link EntityRelation}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityRelation getRelation(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);
    /**
     * Saves or persists relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return {@link EntityRelation}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityRelation saveRelation(TenantId tenantId, EntityRelation relation);
    /**
     * Saves or persists relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relations relations ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> saveRelations(TenantId tenantId, List<EntityRelation> relations);
    /**
     * Saves or persists relation async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return future completing with {@link EntityRelation}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<EntityRelation> saveRelationAsync(TenantId tenantId, EntityRelation relation);
    /**
     * Deletes relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return {@link EntityRelation}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityRelation deleteRelation(TenantId tenantId, EntityRelation relation);
    /**
     * Deletes relation async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return future completing with {@link EntityRelation}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<EntityRelation> deleteRelationAsync(TenantId tenantId, EntityRelation relation);
    /**
     * Deletes relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link EntityRelation}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityRelation deleteRelation(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);
    /**
     * Deletes relation async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link EntityRelation}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<EntityRelation> deleteRelationAsync(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);
    /**
     * Deletes outbound relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entity domain entity to persist or validate
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> deleteOutboundRelations(TenantId tenantId, EntityId entity);
    /**
     * Deletes outbound relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entity domain entity to persist or validate
     * @param relationTypeGroup relation type group ({@link RelationTypeGroup})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> deleteOutboundRelations(TenantId tenantId, EntityId entity, RelationTypeGroup relationTypeGroup);
    /**
     * Deletes inbound relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entity domain entity to persist or validate
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> deleteInboundRelations(TenantId tenantId, EntityId entity);
    /**
     * Deletes inbound relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entity domain entity to persist or validate
     * @param relationTypeGroup relation type group ({@link RelationTypeGroup})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> deleteInboundRelations(TenantId tenantId, EntityId entity, RelationTypeGroup relationTypeGroup);
    /**
     * Finds rule node to rule chain relations.
     *
     * @param ruleChainType rule chain type ({@link RuleChainType})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findRuleNodeToRuleChainRelations(RuleChainType ruleChainType, int limit);
    /**
     * Finds by relation path query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relationPathQuery relation path query ({@link EntityRelationPathQuery})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityRelation> findByRelationPathQuery(TenantId tenantId, EntityRelationPathQuery relationPathQuery, int limit);

}
