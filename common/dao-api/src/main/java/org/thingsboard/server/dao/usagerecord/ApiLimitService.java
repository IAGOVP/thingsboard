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
package org.thingsboard.server.dao.usagerecord;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;

import java.util.function.Function;

/**
 * Service API for api limit persistence and domain operations.
 */
public interface ApiLimitService {

    /**
     * Checks entities limit.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityType entity type ({@link EntityType})
     * @return the boolean result
     */
    boolean checkEntitiesLimit(TenantId tenantId, EntityType entityType);

    /**
     * Returns limit.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param extractor extractor ({@link Function})
     * @return the long result
     */
    long getLimit(TenantId tenantId, Function<DefaultTenantProfileConfiguration, Number> extractor);

}
