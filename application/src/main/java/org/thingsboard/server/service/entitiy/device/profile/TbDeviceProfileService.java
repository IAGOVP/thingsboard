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
package org.thingsboard.server.service.entitiy.device.profile;

import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.service.entitiy.SimpleTbEntityService;

/**

 * Application-layer service API for device profile entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbDeviceProfileService extends SimpleTbEntityService<DeviceProfile> {
/**
 * Set default device profile.
 *
 * @param deviceProfile device profile ({@link DeviceProfile})
 * @param previousDefaultDeviceProfile previous default device profile ({@link DeviceProfile})
 * @param user authenticated user performing the action
 * @return {@link DeviceProfile}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    DeviceProfile setDefaultDeviceProfile(DeviceProfile deviceProfile, DeviceProfile previousDefaultDeviceProfile, User user) throws ThingsboardException;
}
