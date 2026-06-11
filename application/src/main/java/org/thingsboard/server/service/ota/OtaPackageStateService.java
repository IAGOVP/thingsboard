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
package org.thingsboard.server.service.ota;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.gen.transport.TransportProtos.ToOtaPackageStateServiceMsg;

/**

 * Service contract for ota package state operations (over-the-air firmware/software package handling).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface OtaPackageStateService {

    void update(Device device, Device oldDevice);

    /**
     * Updates the requested data.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @param isFirmwareChanged is firmware changed
     * @param isSoftwareChanged is software changed
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void update(DeviceProfile deviceProfile, boolean isFirmwareChanged, boolean isSoftwareChanged);

    /**
     * Processes the requested data.
     *
     * @param msg msg ({@link ToOtaPackageStateServiceMsg})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean process(ToOtaPackageStateServiceMsg msg);

}
