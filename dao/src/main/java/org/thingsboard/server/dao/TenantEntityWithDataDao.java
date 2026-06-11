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
package org.thingsboard.server.dao;

import org.thingsboard.server.common.data.id.TenantId;


/**

 * Persistence contract for tenant entity with data.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (ThingsBoard DAO layer).

 */


public interface TenantEntityWithDataDao {
    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long sumDataSizeByTenantId(TenantId tenantId);
}
