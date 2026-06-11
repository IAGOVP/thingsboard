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
package org.thingsboard.server.service.query;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.AttributeScope;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.AlarmCountQuery;
import org.thingsboard.server.common.data.query.AlarmData;
import org.thingsboard.server.common.data.query.AlarmDataQuery;
import org.thingsboard.server.common.data.query.AvailableEntityKeys;
import org.thingsboard.server.common.data.query.AvailableEntityKeysV2;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityData;
import org.thingsboard.server.common.data.query.EntityDataQuery;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Set;

/**

 * Service contract for entity query operations (entity query helpers for core services).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface EntityQueryService {

    long countEntitiesByQuery(SecurityUser securityUser, EntityCountQuery query);

    /**
     * Finds entity data by query.
     *
     * @param securityUser security user ({@link SecurityUser})
     * @param query query ({@link EntityDataQuery})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntityData> findEntityDataByQuery(SecurityUser securityUser, EntityDataQuery query);

    /**
     * Finds alarm data by query.
     *
     * @param securityUser security user ({@link SecurityUser})
     * @param query query ({@link AlarmDataQuery})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmData> findAlarmDataByQuery(SecurityUser securityUser, AlarmDataQuery query);

    /**
     * Counts alarms by query.
     *
     * @param securityUser security user ({@link SecurityUser})
     * @param query query ({@link AlarmCountQuery})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    long countAlarmsByQuery(SecurityUser securityUser, AlarmCountQuery query);

    /**
     * Returns keys by query.
     *
     * @param securityUser security user ({@link SecurityUser})
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link EntityDataQuery})
     * @param isTimeseries is timeseries
     * @param isAttributes is attributes
     * @param scope scope ({@link AttributeScope})
     * @return future completing with {@link AvailableEntityKeys}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<AvailableEntityKeys> getKeysByQuery(SecurityUser securityUser, TenantId tenantId, EntityDataQuery query,
                                                         boolean isTimeseries, boolean isAttributes, AttributeScope scope);

    /**
     * Finds available entity keys by query.
     *
     * @param securityUser security user ({@link SecurityUser})
     * @param query query ({@link EntityDataQuery})
     * @param includeTimeseries include timeseries
     * @param includeAttributes include attributes
     * @param scopes scopes ({@link Set})
     * @param includeSamples include samples
     * @return future completing with {@link AvailableEntityKeysV2}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<AvailableEntityKeysV2> findAvailableEntityKeysByQuery(SecurityUser securityUser, EntityDataQuery query,
                                                                           boolean includeTimeseries, boolean includeAttributes,
                                                                           Set<AttributeScope> scopes, boolean includeSamples);

}
