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
package org.thingsboard.server.cache.device;

import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;

/**
 * Cluster broadcast event evicting Device cache entries after create/update/delete.
 *
 * <p>Published to all ThingsBoard nodes so {@link DeviceCaffeineCache} and
 * {@link DeviceRedisCache} stay consistent. Handlers evict old and new key variants
 * when identifiers change (e.g. rename).
 */
@Data
public class DeviceCacheEvictEvent {

    /** Tenant scope of the evicted device. */
    private final TenantId tenantId;

    /** Device identifier whose cache entry is invalidated. */
    private final DeviceId deviceId;

    /** New device name after rename; used to evict the updated name-based key. */
    private final String newName;

    /** Previous device name; used to evict the stale name-based key. */
    private final String oldName;

    /** Optionally carries the saved device for repopulating cache on the handling node. */
    private Device savedDevice;

}
