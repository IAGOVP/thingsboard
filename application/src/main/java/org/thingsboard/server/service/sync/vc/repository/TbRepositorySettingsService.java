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
package org.thingsboard.server.service.sync.vc.repository;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.vc.RepositorySettings;


/**

 * Service contract for tb repository settings operations (Git-based entity version control (entity version control, Git repository sync, and import/export)).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */


public interface TbRepositorySettingsService {
/**
 * Restore.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionControlSettings version control settings ({@link RepositorySettings})
 * @return {@link RepositorySettings}
 * @throws Exception if an unexpected error occurs during processing
 */



    RepositorySettings restore(TenantId tenantId, RepositorySettings versionControlSettings);
/**
 * Returns the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return {@link RepositorySettings}
 * @throws Exception if an unexpected error occurs during processing
 */

    RepositorySettings get(TenantId tenantId);
/**
 * Saves or persists the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionControlSettings version control settings ({@link RepositorySettings})
 * @return {@link RepositorySettings}
 * @throws Exception if an unexpected error occurs during processing
 */

    RepositorySettings save(TenantId tenantId, RepositorySettings versionControlSettings);
/**
 * Deletes the requested data.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean delete(TenantId tenantId);

}
