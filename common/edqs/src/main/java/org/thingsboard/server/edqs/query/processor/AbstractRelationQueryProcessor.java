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
package org.thingsboard.server.edqs.query.processor;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.permission.QueryContext;
import org.thingsboard.server.common.data.query.EntityFilter;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.edqs.data.EntityData;
import org.thingsboard.server.edqs.data.RelationInfo;
import org.thingsboard.server.edqs.data.RelationsRepo;
import org.thingsboard.server.edqs.query.EdqsQuery;
import org.thingsboard.server.edqs.query.SortableEntityData;
import org.thingsboard.server.edqs.repo.TenantRepo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * EDQS query processor for abstract relation entity filters.
 *
 * <p>Evaluates {@link org.thingsboard.server.common.data.query.EntityFilter} against a {@link org.thingsboard.server.edqs.repo.TenantRepo} (EDQS microservice — entity filter query processors).
 */

public abstract class AbstractRelationQueryProcessor<T extends EntityFilter> extends AbstractQueryProcessor<T> {

    public static final int MAXIMUM_QUERY_LEVEL = 100;

    public AbstractRelationQueryProcessor(TenantRepo repo, QueryContext ctx, EdqsQuery query, T filter) {
        super(repo, ctx, query, filter);
    }
    /**
     * Returns root entities.
     *
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract Set<UUID> getRootEntities();
    /**
     * Returns direction.
     *
     * @return {@link EntitySearchDirection}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract EntitySearchDirection getDirection();
    /**
     * Returns max level.
     *
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract int getMaxLevel();
    /**
     * Is fetch last level only.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract boolean isFetchLastLevelOnly();
    /**
     * Is multi root.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected boolean isMultiRoot() {
        return false;
    }
    /**
     * Processes query.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<SortableEntityData> processQuery() {
        var relations = repository.getRelations(RelationTypeGroup.COMMON);
        var entities = getEntitiesSet(relations);
        if (ctx.isTenantUser()) {
            /** Process tenant query. */
            return processTenantQuery(entities);
        } else {
            /** Process customer query. */
            return processCustomerQuery(entities);
        }
    }
    /**
     * Counts the requested data.
     *
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public long count() {
        var relations = repository.getRelations(RelationTypeGroup.COMMON);
        var entities = getEntitiesSet(relations);
        long result = 0;

        if (ctx.isTenantUser()) {
            return entities.size();
        } else {
            var customerId = ctx.getCustomerId().getId();
            for (EntityData<?> ed : entities) {
                if (checkCustomerId(customerId, ed)) {
                    result++;
                }
            }
            return result;
        }
    }

    private List<SortableEntityData> processTenantQuery(Set<EntityData<?>> entities) {
        return entities.stream()
                .map(this::toSortData)
                .collect(Collectors.toList());
    }

    private List<SortableEntityData> processCustomerQuery(Set<EntityData<?>> entities) {
        var customerId = ctx.getCustomerId().getId();
        List<SortableEntityData> result = new ArrayList<>();
        for (EntityData<?> ed : entities) {
            if (checkCustomerId(customerId, ed)) {
                result.add(toSortData(ed));
            }
        }
        return result;
    }

    private Set<EntityData<?>> getEntitiesSet(RelationsRepo relations) {
        Set<EntityData<?>> result = new HashSet<>();
        Set<RelationSearchTask> processed = new HashSet<>();
        Queue<RelationSearchTask> tasks = new LinkedList<>();
        int maxLvl = getMaxLevel() == 0 ? MAXIMUM_QUERY_LEVEL : Math.max(1, getMaxLevel());
        for (UUID uuid : getRootEntities()) {
            tasks.add(new RelationSearchTask(uuid, 0));
        }
        while (!tasks.isEmpty()) {
            RelationSearchTask task = tasks.poll();
            if (processed.add(task)) {
                var entityLvl = task.lvl + 1;
                Set<RelationInfo> entities = EntitySearchDirection.FROM.equals(getDirection()) ? relations.getFrom(task.entityId) : relations.getTo(task.entityId);
                if (isFetchLastLevelOnly() && entities.isEmpty() && task.previous != null && check(task.previous)) {
                    result.add(task.previous.getTarget());
                }
                for (RelationInfo relationInfo : entities) {
                    var entity = relationInfo.getTarget();
                    if (entity.isEmpty()) {
                        continue;
                    }
                    var entityId = entity.getId();
                    if (isFetchLastLevelOnly()) {
                        if (entityLvl < maxLvl) {
                            tasks.add(new RelationSearchTask(entityId, entityLvl, relationInfo));
                        } else if (entityLvl == maxLvl) {
                            if (check(relationInfo)) {
                                if (isMultiRoot()) {
                                    ctx.getRelatedParentIdMap().put(entity.getId(), task.entityId);
                                }
                                result.add(entity);
                            }
                        }
                    } else {
                        if (check(relationInfo)) {
                            if (isMultiRoot()) {
                                ctx.getRelatedParentIdMap().put(entity.getId(), task.entityId);
                            }
                            result.add(entity);
                        }
                        if (entityLvl < maxLvl) {
                            tasks.add(new RelationSearchTask(entityId, entityLvl));
                        }
                    }
                }
            }
        }
        return result;
    }
    /**
     * Checks the requested data.
     *
     * @param relationInfo relation info ({@link RelationInfo})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract boolean check(RelationInfo relationInfo);
    /**
     * Relation search task (EDQS microservice — entity filter query processors).
     */

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class RelationSearchTask {
        private final UUID entityId;
        private final int lvl;
        private final RelationInfo previous;

        public RelationSearchTask(UUID entityId, int lvl) {
            this(entityId, lvl, null);
        }

    }

}
