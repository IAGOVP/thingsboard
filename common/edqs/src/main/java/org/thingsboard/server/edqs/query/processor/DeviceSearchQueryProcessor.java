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
import org.thingsboard.server.common.data.query.DeviceSearchQueryFilter;
import org.thingsboard.server.common.data.util.CollectionsUtil;
import org.thingsboard.server.edqs.data.EntityData;
import org.thingsboard.server.edqs.data.ProfileAwareData;
import org.thingsboard.server.edqs.data.RelationInfo;
import org.thingsboard.server.edqs.query.EdqsQuery;
import org.thingsboard.server.edqs.repo.TenantRepo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * EDQS query processor for device search entity filters.
 *
 * <p>Evaluates {@link org.thingsboard.server.common.data.query.EntityFilter} against a {@link org.thingsboard.server.edqs.repo.TenantRepo} (EDQS microservice — entity filter query processors).
 */

public class DeviceSearchQueryProcessor extends AbstractEntitySearchQueryProcessor<DeviceSearchQueryFilter> {

    private final Set<UUID> entityProfileIds = new HashSet<>();

    public DeviceSearchQueryProcessor(TenantRepo repo, QueryContext ctx, EdqsQuery query) {
        super(repo, ctx, query, (DeviceSearchQueryFilter) query.getEntityFilter());
        if (CollectionsUtil.isNotEmpty(filter.getDeviceTypes())) {
            var profileNamesSet = new HashSet<>(this.filter.getDeviceTypes());
            for (EntityData<?> dp : repo.getEntitySet(EntityType.DEVICE_PROFILE)) {
                if (profileNamesSet.contains(dp.getFields().getName())) {
                    entityProfileIds.add(dp.getId());
                }
            }
        }
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.DEVICE;
    }
    /**
     * Checks the requested data.
     *
     * @param relationInfo relation info ({@link RelationInfo})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected boolean check(RelationInfo relationInfo) {
        return super.check(relationInfo) &&
                (entityProfileIds.isEmpty() || entityProfileIds.contains(((ProfileAwareData<?>) relationInfo.getTarget()).getFields().getProfileId()));
    }

}
