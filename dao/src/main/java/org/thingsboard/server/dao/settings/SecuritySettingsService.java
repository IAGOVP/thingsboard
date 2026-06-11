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

import org.thingsboard.server.common.data.security.model.SecuritySettings;






















/**






 * security settings service contract (system and tenant admin settings).






 */







public interface SecuritySettingsService {
    /**
     * Returns security settings.
     *
     * @return {@link SecuritySettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    SecuritySettings getSecuritySettings();
    /**
     * Saves or persists security settings.
     *
     * @param securitySettings security settings ({@link SecuritySettings})
     * @return {@link SecuritySettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    SecuritySettings saveSecuritySettings(SecuritySettings securitySettings);

}
