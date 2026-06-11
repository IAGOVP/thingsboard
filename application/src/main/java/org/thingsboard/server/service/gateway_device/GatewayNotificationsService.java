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
package org.thingsboard.server.service.gateway_device;

import org.thingsboard.server.common.data.Device;

/**

 * Service contract for gateway notifications operations (gateway child-device session management).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface GatewayNotificationsService {

    void onDeviceUpdated(Device device, Device oldDevice);

    /**
     * Handles device deleted.
     *
     * @param device device ({@link Device})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onDeviceDeleted(Device device);
}
