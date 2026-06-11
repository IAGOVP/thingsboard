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
package org.thingsboard.server.dao.trendz;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.trendz.TrendzSettings;

/**
 * Service API for trendz settings persistence and domain operations.
 */
public interface TrendzSettingsService {

    /**
     * Saves or persists trendz settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param settings settings ({@link TrendzSettings})
     */
    void saveTrendzSettings(TenantId tenantId, TrendzSettings settings);

    /**
     * Finds trendz settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TrendzSettings}
     */
    TrendzSettings findTrendzSettings(TenantId tenantId);

    /**
     * Deletes trendz settings.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteTrendzSettings(TenantId tenantId);

}
