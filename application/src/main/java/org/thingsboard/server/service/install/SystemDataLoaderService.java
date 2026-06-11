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
package org.thingsboard.server.service.install;

/**

 * Service contract for system data loader operations (database schema installation, upgrades, and demo data loading).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface SystemDataLoaderService {

    void createSysAdmin() throws Exception;

    /**
     * Creates default tenant profiles.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void createDefaultTenantProfiles() throws Exception;

    /**
     * Creates admin settings.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void createAdminSettings() throws Exception;

    /**
     * Creates random jwt settings.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void createRandomJwtSettings() throws Exception;

    /**
     * Updates security settings.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void updateSecuritySettings() throws Exception;

    /**
     * Creates oauth2templates.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void createOAuth2Templates() throws Exception;

    /**
     * Loads system widgets.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void loadSystemWidgets() throws Exception;

    /**
     * Loads demo data.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void loadDemoData() throws Exception;

    /**
     * Creates queues.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void createQueues();

    /**
     * Creates default notification configs.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void createDefaultNotificationConfigs();

    /**
     * Updates default notification configs.
     *
     * @param updateTenants update tenants
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void updateDefaultNotificationConfigs(boolean updateTenants);

}
