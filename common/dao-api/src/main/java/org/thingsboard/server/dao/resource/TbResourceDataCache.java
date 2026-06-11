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
package org.thingsboard.server.dao.resource;

import com.google.common.util.concurrent.FluentFuture;
import org.thingsboard.server.common.data.TbResourceDataInfo;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;

/**
 * tb resource data cache contract for the DAO layer.
 */

public interface TbResourceDataCache {

    /**
     * Returns resource data info async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return {@link FluentFuture}
     */
    FluentFuture<TbResourceDataInfo> getResourceDataInfoAsync(TenantId tenantId, TbResourceId resourceId);

    /**
     * Evict resource data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     */
    void evictResourceData(TenantId tenantId, TbResourceId resourceId);
}
