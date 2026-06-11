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
package org.thingsboard.server.dao.sql.query;

import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.AlarmCountQuery;
import org.thingsboard.server.common.data.query.AlarmData;
import org.thingsboard.server.common.data.query.AlarmDataQuery;

import java.util.Collection;


/**

 * Spring Data JPA repository for alarm query entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface AlarmQueryRepository {
    /**
     * Finds alarm data by query for entities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId,
                                                        AlarmDataQuery query, Collection<EntityId> orderedEntityIds);
    /**
     * Counts alarms by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    long countAlarmsByQuery(TenantId tenantId, CustomerId customerId, AlarmCountQuery query, Collection<EntityId> orderedEntityIds);

}
