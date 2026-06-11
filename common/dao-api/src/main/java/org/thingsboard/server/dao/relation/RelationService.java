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
import org.thingsboard.server.common.data.relation.EntityRelationInfo;
import org.thingsboard.server.common.data.relation.EntityRelationPathQuery;
import org.thingsboard.server.common.data.relation.EntityRelationsQuery;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.rule.RuleChainType;

import java.util.List;
import java.util.function.Predicate;

/**
 * Service API for relation persistence and domain operations.
 */
public interface RelationService {

    /**
     * Checks relation async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link Boolean}
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
     */
    EntityRelation getRelation(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    /**
     * Saves or persists relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return {@link EntityRelation}
     */
    EntityRelation saveRelation(TenantId tenantId, EntityRelation relation);

    /**
     * Saves or persists relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relations relations ({@link List})
     */
    void saveRelations(TenantId tenantId, List<EntityRelation> relations);

    /**
     * Saves or persists relation async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return future completing with {@link Boolean}
     */
    ListenableFuture<Boolean> saveRelationAsync(TenantId tenantId, EntityRelation relation);

    /**
     * Deletes relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return the boolean result
     */
    boolean deleteRelation(TenantId tenantId, EntityRelation relation);

    /**
     * Deletes relation async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relation relation ({@link EntityRelation})
     * @return future completing with {@link Boolean}
     */
    ListenableFuture<Boolean> deleteRelationAsync(TenantId tenantId, EntityRelation relation);

    /**
     * Deletes relation.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link EntityRelation}
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
     * @return future completing with {@link Boolean}
     */
    ListenableFuture<Boolean> deleteRelationAsync(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    /**
     * Deletes entity relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entity entity ({@link EntityId})
     */
    void deleteEntityRelations(TenantId tenantId, EntityId entity);

    /**
     * Deletes entity common relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entity entity ({@link EntityId})
     */
    void deleteEntityCommonRelations(TenantId tenantId, EntityId entity);

    /**
     * Finds by from.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     */
    List<EntityRelation> findByFrom(TenantId tenantId, EntityId from, RelationTypeGroup typeGroup);

    /**
     * Finds by from async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelation>> findByFromAsync(TenantId tenantId, EntityId from, RelationTypeGroup typeGroup);

    /**
     * Finds info by from.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelationInfo>> findInfoByFrom(TenantId tenantId, EntityId from, RelationTypeGroup typeGroup);

    /**
     * Finds by from and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     */
    List<EntityRelation> findByFromAndType(TenantId tenantId, EntityId from, String relationType, RelationTypeGroup typeGroup);

    /**
     * Finds by from and type async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param from from ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelation>> findByFromAndTypeAsync(TenantId tenantId, EntityId from, String relationType, RelationTypeGroup typeGroup);

    /**
     * Finds by to.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     */
    List<EntityRelation> findByTo(TenantId tenantId, EntityId to, RelationTypeGroup typeGroup);

    /**
     * Finds by to async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelation>> findByToAsync(TenantId tenantId, EntityId to, RelationTypeGroup typeGroup);

    /**
     * Finds info by to.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelationInfo>> findInfoByTo(TenantId tenantId, EntityId to, RelationTypeGroup typeGroup);

    /**
     * Finds by to and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return {@link List}
     */
    List<EntityRelation> findByToAndType(TenantId tenantId, EntityId to, String relationType, RelationTypeGroup typeGroup);

    /**
     * Finds by to and type async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param to to ({@link EntityId})
     * @param relationType relation type ({@link String})
     * @param typeGroup type group ({@link RelationTypeGroup})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelation>> findByToAndTypeAsync(TenantId tenantId, EntityId to, String relationType, RelationTypeGroup typeGroup);

    /**
     * Finds by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link EntityRelationsQuery})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelation>> findByQuery(TenantId tenantId, EntityRelationsQuery query);

    /**
     * Finds info by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link EntityRelationsQuery})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelationInfo>> findInfoByQuery(TenantId tenantId, EntityRelationsQuery query);

    /**
     * Removes relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     */
    void removeRelations(TenantId tenantId, EntityId entityId);

    /**
     * Finds rule node to rule chain relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainType rule chain type ({@link RuleChainType})
     * @param limit limit
     * @return {@link List}
     */
    List<EntityRelation> findRuleNodeToRuleChainRelations(TenantId tenantId, RuleChainType ruleChainType, int limit);

    /**
     * Finds by relation path query async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relationPathQuery relation path query ({@link EntityRelationPathQuery})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelation>> findByRelationPathQueryAsync(TenantId tenantId, EntityRelationPathQuery relationPathQuery);

    /**
     * Finds filtered relations by path query async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relationPathQuery relation path query ({@link EntityRelationPathQuery})
     * @param relationFilter relation filter ({@link Predicate})
     * @return future completing with {@link List}
     */
    ListenableFuture<List<EntityRelation>> findFilteredRelationsByPathQueryAsync(TenantId tenantId, EntityRelationPathQuery relationPathQuery, Predicate<EntityRelation> relationFilter);

    /**
     * Finds by relation path query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param relationPathQuery relation path query ({@link EntityRelationPathQuery})
     * @return {@link List}
     */
    List<EntityRelation> findByRelationPathQuery(TenantId tenantId, EntityRelationPathQuery relationPathQuery);

//    TODO: This method may be useful for some validations in the future
//    ListenableFuture<Boolean> checkRecursiveRelation(EntityId from, EntityId to);

}
