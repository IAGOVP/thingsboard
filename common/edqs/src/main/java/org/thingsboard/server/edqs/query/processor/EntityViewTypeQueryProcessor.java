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
import org.thingsboard.server.common.data.query.EntityViewTypeFilter;
import org.thingsboard.server.edqs.query.EdqsQuery;
import org.thingsboard.server.edqs.repo.TenantRepo;

import java.util.List;

/**
 * EDQS query processor for entity view type entity filters.
 *
 * <p>Evaluates {@link org.thingsboard.server.common.data.query.EntityFilter} against a {@link org.thingsboard.server.edqs.repo.TenantRepo} (EDQS microservice — entity filter query processors).
 */

public class EntityViewTypeQueryProcessor extends AbstractEntityProfileNameQueryProcessor<EntityViewTypeFilter> {

    public EntityViewTypeQueryProcessor(TenantRepo repo, QueryContext ctx, EdqsQuery query) {
        super(repo, ctx, query, (EntityViewTypeFilter) query.getEntityFilter(), EntityType.ENTITY_VIEW);
    }
    /**
     * Returns entity name filter.
     *
     * @param filter entity filter definition (type, relations, search text, etc.)
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getEntityNameFilter(EntityViewTypeFilter filter) {
        return filter.getEntityViewNameFilter();
    }
    /**
     * Returns profile names.
     *
     * @param filter entity filter definition (type, relations, search text, etc.)
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected List<String> getProfileNames(EntityViewTypeFilter filter) {
        return filter.getEntityViewTypes();
    }

}
