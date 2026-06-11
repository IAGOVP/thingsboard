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
package org.thingsboard.monitoring.data.notification;


/**

 * Short display names for monitored services in Slack and log output.

 */


public interface ShortNameProvider {
    /**
     * Returns a compact display name for the monitored service key.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String getShortName();

}
