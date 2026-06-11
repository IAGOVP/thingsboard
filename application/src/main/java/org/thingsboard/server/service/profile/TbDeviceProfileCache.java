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
package org.thingsboard.server.service.profile;

import org.thingsboard.rule.engine.api.RuleEngineDeviceProfileCache;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;

/**

 * tb device profile cache contract for device and asset profile resolution.

 */

public interface TbDeviceProfileCache extends RuleEngineDeviceProfileCache {

    void evict(TenantId tenantId, DeviceProfileId id);

    /**
     * Evict.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link DeviceId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void evict(TenantId tenantId, DeviceId id);

    /**
     * Finds the requested data.
     *
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfile find(DeviceProfileId deviceProfileId);

    /**
     * Finds or create device profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceType device type ({@link String})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfile findOrCreateDeviceProfile(TenantId tenantId, String deviceType);
}
