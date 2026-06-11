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
package org.thingsboard.server.service.sync.vc.autocommit;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.vc.AutoCommitSettings;

/**

 * Manages automatic version commits when entities change.

 */

public interface TbAutoCommitSettingsService {
/**
 * Returns the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return {@link AutoCommitSettings}
 * @throws Exception if an unexpected error occurs during processing
 */



    AutoCommitSettings get(TenantId tenantId);
/**
 * Saves or persists the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param settings settings ({@link AutoCommitSettings})
 * @return {@link AutoCommitSettings}
 * @throws Exception if an unexpected error occurs during processing
 */

    AutoCommitSettings save(TenantId tenantId, AutoCommitSettings settings);
/**
 * Deletes the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean delete(TenantId tenantId);

}
