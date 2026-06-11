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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.cache.VersionedCacheKey;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;

import java.io.Serial;

/**
 * Composite {@link VersionedCacheKey} for {@link org.thingsboard.server.common.data.Device} lookups.
 *
 * <p>Supports three lookup shapes: by {@link org.thingsboard.server.common.data.id.DeviceId} alone,
 * by tenant+deviceId (versioned), or by tenant+deviceName (non-versioned name index).
 *
 * @see DeviceCaffeineCache
 * @see DeviceRedisCache
 * @see DeviceCacheEvictEvent
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class DeviceCacheKey implements VersionedCacheKey {

    @Serial
    private static final long serialVersionUID = 6366389552842340207L;

    private final TenantId tenantId;
    private final DeviceId deviceId;
    private final String deviceName;

    /**
     * Key for global device-id lookup (no tenant scope).
     *
     * @param deviceId device identifier
     */
    public DeviceCacheKey(DeviceId deviceId) {
        this(null, deviceId, null);
    }

    /**
     * Versioned tenant-scoped key by device id.
     *
     * @param tenantId tenant scope
     * @param deviceId device identifier
     */
    public DeviceCacheKey(TenantId tenantId, DeviceId deviceId) {
        this(tenantId, deviceId, null);
    }

    /**
     * Non-versioned tenant-scoped key by device name.
     *
     * @param tenantId   tenant scope
     * @param deviceName device name within tenant
     */
    public DeviceCacheKey(TenantId tenantId, String deviceName) {
        this(tenantId, null, deviceName);
    }

/**
         * @return key suffix encoding tenant, id, or name lookup variant
         */
    @Override
    public String toString() {
        if (deviceId == null) {
            return tenantId + "_n_" + deviceName;
        } else if (tenantId == null) {
            return deviceId.toString();
        } else {
            return tenantId + "_" + deviceId;
        }
    }

/**
         * @return {@code true} when keyed by device id (versioned storage enabled)
         */
    @Override
    public boolean isVersioned() {
        return deviceId != null;
    }

}
