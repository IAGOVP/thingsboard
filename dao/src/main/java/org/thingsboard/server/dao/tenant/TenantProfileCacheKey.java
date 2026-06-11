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
package org.thingsboard.server.dao.tenant;

import lombok.Data;
import org.thingsboard.server.common.data.id.TenantProfileId;

import java.io.Serial;
import java.io.Serializable;
/**
 * Serializable cache key for tenant profile entries (tenants, tenant profiles, and profile caching).
 */







@Data
public class TenantProfileCacheKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 8220455917177676472L;

    private final TenantProfileId tenantProfileId;
    private final boolean defaultProfile;

    private TenantProfileCacheKey(TenantProfileId tenantProfileId, boolean defaultProfile) {
        this.tenantProfileId = tenantProfileId;
        this.defaultProfile = defaultProfile;
    }
    /**
     * From id.
     *
     * @param id entity UUID primary key
     * @return {@link TenantProfileCacheKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static TenantProfileCacheKey fromId(TenantProfileId id) {
        return new TenantProfileCacheKey(id, false);
    }
    /**
     * Default profile.
     *
     * @return {@link TenantProfileCacheKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static TenantProfileCacheKey defaultProfile() {
        return new TenantProfileCacheKey(null, true);
    }


    @Override
    public String toString() {
        if (defaultProfile) {
            return "default";
        } else {
            return tenantProfileId.toString();
        }
    }

}
