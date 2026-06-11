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
import org.thingsboard.server.common.data.query.EntityFilter;
import org.thingsboard.server.edqs.data.EntityData;
import org.thingsboard.server.edqs.query.EdqsQuery;
import org.thingsboard.server.edqs.query.SortableEntityData;
import org.thingsboard.server.edqs.repo.TenantRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * EDQS query processor for abstract single entity type entity filters.
 *
 * <p>Evaluates {@link org.thingsboard.server.common.data.query.EntityFilter} against a {@link org.thingsboard.server.edqs.repo.TenantRepo} (EDQS microservice — entity filter query processors).
 */

public abstract class AbstractSingleEntityTypeQueryProcessor<T extends EntityFilter> extends AbstractQueryProcessor<T> {

    public AbstractSingleEntityTypeQueryProcessor(TenantRepo repository, QueryContext ctx, EdqsQuery query, T filter) {
        super(repository, ctx, query, filter);
    }
    /**
     * Processes query.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<SortableEntityData> processQuery() {
        if (ctx.isTenantUser()) {
            /** Process tenant query. */
            return processTenantQuery();
        } else {
            return processCustomerQuery(ctx.getCustomerId().getId());
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
        AtomicLong result = new AtomicLong();
        Consumer<EntityData<?>> counter = ed -> result.incrementAndGet();

        if (ctx.isIgnorePermissionCheck()) {
            processAll(counter);
        } else if (ctx.isTenantUser()) {
            processAll(counter);
        } else {
            processCustomerQuery(ctx.getCustomerId().getId(), counter);
        }
        return result.get();
    }
    /**
     * Processes tenant query.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected List<SortableEntityData> processTenantQuery() {
        List<SortableEntityData> result = new ArrayList<>(getProbableResultSize());
        processAll(ed -> {
            result.add(toSortData(ed));
        });
        return result;
    }
    /**
     * Processes customer query.
     *
     * @param customerId customer scope for permission filtering (may be null)
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected List<SortableEntityData> processCustomerQuery(UUID customerId) {
        List<SortableEntityData> result = new ArrayList<>(getProbableResultSize());
        processCustomerQuery(customerId, ed -> {
            result.add(toSortData(ed));
        });
        return result;
    }
    /**
     * Processes customer query.
     *
     * @param customerId customer scope for permission filtering (may be null)
     * @param processor processor ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract void processCustomerQuery(UUID customerId, Consumer<EntityData<?>> processor);
    /**
     * Processes all.
     *
     * @param processor processor ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract void processAll(Consumer<EntityData<?>> processor);
    /**
     * Returns probable result size.
     *
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract int getProbableResultSize();

}
