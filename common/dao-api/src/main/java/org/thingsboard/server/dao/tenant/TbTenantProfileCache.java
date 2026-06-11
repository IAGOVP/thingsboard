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
package org.thingsboard.server.dao.tenant;

import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;

import java.util.function.Consumer;

/**
 * tb tenant profile cache contract for the DAO layer.
 */

public interface TbTenantProfileCache {

    /**
     * Returns the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TenantProfile}
     */
    TenantProfile get(TenantId tenantId);

    /**
     * Returns the requested data.
     *
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return {@link TenantProfile}
     */
    TenantProfile get(TenantProfileId tenantProfileId);

    /**
     * Put.
     *
     * @param profile profile ({@link TenantProfile})
     */
    void put(TenantProfile profile);

    /**
     * Evict.
     *
     * @param id id ({@link TenantProfileId})
     */
    void evict(TenantProfileId id);

    /**
     * Evict.
     *
     * @param id id ({@link TenantId})
     */
    void evict(TenantId id);

    /**
     * Add listener.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param listenerId listener id ({@link EntityId})
     * @param profileListener profile listener ({@link Consumer})
     */
    void addListener(TenantId tenantId, EntityId listenerId, Consumer<TenantProfile> profileListener);

    /**
     * Removes listener.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param listenerId listener id ({@link EntityId})
     */
    void removeListener(TenantId tenantId, EntityId listenerId);

}
