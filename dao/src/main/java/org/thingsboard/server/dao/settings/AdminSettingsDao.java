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
package org.thingsboard.server.dao.settings;

import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;


/**

 * Persistence contract for admin settings.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (system and tenant admin settings).

 */


public interface AdminSettingsDao extends Dao<AdminSettings> {
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param adminSettings admin settings ({@link AdminSettings})
     * @return {@link AdminSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    AdminSettings save(TenantId tenantId, AdminSettings adminSettings);
    /**
     * Finds by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link AdminSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    AdminSettings findByTenantIdAndKey(UUID tenantId, String key);
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AdminSettings> findAllByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Removes by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean removeByTenantIdAndKey(UUID tenantId, String key);
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByTenantId(UUID tenantId);

}
