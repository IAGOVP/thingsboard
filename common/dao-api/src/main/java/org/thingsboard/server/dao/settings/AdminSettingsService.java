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
import org.thingsboard.server.common.data.id.AdminSettingsId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

/**
 * Service API for admin settings persistence and domain operations.
 */
public interface AdminSettingsService extends EntityDaoService {

    /**
     * Finds admin settings by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param adminSettingsId admin settings id ({@link AdminSettingsId})
     * @return {@link AdminSettings}
     */
    AdminSettings findAdminSettingsById(TenantId tenantId, AdminSettingsId adminSettingsId);

    /**
     * Finds admin settings by key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key key ({@link String})
     * @return {@link AdminSettings}
     */
    AdminSettings findAdminSettingsByKey(TenantId tenantId, String key);

    /**
     * Finds admin settings by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key key ({@link String})
     * @return {@link AdminSettings}
     */
    AdminSettings findAdminSettingsByTenantIdAndKey(TenantId tenantId, String key);

    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<AdminSettings> findAllByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Saves or persists admin settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param adminSettings admin settings ({@link AdminSettings})
     * @return {@link AdminSettings}
     */
    AdminSettings saveAdminSettings(TenantId tenantId, AdminSettings adminSettings);

    /**
     * Deletes admin settings by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key key ({@link String})
     * @return the boolean result
     */
    boolean deleteAdminSettingsByTenantIdAndKey(TenantId tenantId, String key);

}
