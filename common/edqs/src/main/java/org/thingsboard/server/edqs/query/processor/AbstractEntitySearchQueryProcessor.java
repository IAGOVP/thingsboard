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

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.permission.QueryContext;
import org.thingsboard.server.common.data.query.EntitySearchQueryFilter;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.edqs.data.EntityData;
import org.thingsboard.server.edqs.data.RelationInfo;
import org.thingsboard.server.edqs.query.EdqsQuery;
import org.thingsboard.server.edqs.repo.TenantRepo;

import java.util.Set;
import java.util.UUID;

/**
 * EDQS query processor for abstract entity search entity filters.
 *
 * <p>Evaluates {@link org.thingsboard.server.common.data.query.EntityFilter} against a {@link org.thingsboard.server.edqs.repo.TenantRepo} (EDQS microservice — entity filter query processors).
 */

public abstract class AbstractEntitySearchQueryProcessor<T extends EntitySearchQueryFilter> extends AbstractRelationQueryProcessor<T> {

    public AbstractEntitySearchQueryProcessor(TenantRepo repo, QueryContext ctx, EdqsQuery query, T filter) {
        super(repo, ctx, query, filter);
    }
    /**
     * Returns root entities.
     *
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Set<UUID> getRootEntities() {
        return Set.of(filter.getRootEntity().getId());
    }
    /**
     * Returns direction.
     *
     * @return {@link EntitySearchDirection}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntitySearchDirection getDirection() {
        return filter.getDirection();
    }
    /**
     * Returns max level.
     *
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int getMaxLevel() {
        return filter.getMaxLevel();
    }
    /**
     * Is fetch last level only.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isFetchLastLevelOnly() {
        return filter.isFetchLastLevelOnly();
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    public abstract EntityType getEntityType();
    /**
     * Checks the requested data.
     *
     * @param relationInfo relation info ({@link RelationInfo})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected boolean check(RelationInfo relationInfo) {
        EntityData<?> target = relationInfo.getTarget();
        return (filter.getRelationType() == null || relationInfo.getType().equals(filter.getRelationType())) &&
                getEntityType().equals(target.getEntityType()) && super.matches(target);
    }

}
