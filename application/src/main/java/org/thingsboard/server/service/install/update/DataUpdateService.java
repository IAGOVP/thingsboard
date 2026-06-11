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
package org.thingsboard.server.service.install.update;

/**

 * Service contract for data update operations (database schema installation, upgrades, and demo data loading).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface DataUpdateService {

    void updateData() throws Exception;

    /**
     * Upgrade rule nodes.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void upgradeRuleNodes();
}
