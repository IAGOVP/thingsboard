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
package org.thingsboard.server.dao.asset;

import lombok.Data;
import org.thingsboard.server.cache.VersionedCacheKey;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.TenantId;

import java.io.Serial;
/**
 * Serializable cache key for asset profile entries (asset and asset-profile DAO services and caches).
 */







@Data
public class AssetProfileCacheKey implements VersionedCacheKey {

    @Serial
    private static final long serialVersionUID = 8220455917177676472L;

    private final TenantId tenantId;
    private final String name;
    private final AssetProfileId assetProfileId;
    private final boolean defaultProfile;

    private AssetProfileCacheKey(TenantId tenantId, String name, AssetProfileId assetProfileId, boolean defaultProfile) {
        this.tenantId = tenantId;
        this.name = name;
        this.assetProfileId = assetProfileId;
        this.defaultProfile = defaultProfile;
    }
    /**
     * For name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link AssetProfileCacheKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static AssetProfileCacheKey forName(TenantId tenantId, String name) {
        return new AssetProfileCacheKey(tenantId, name, null, false);
    }
    /**
     * For id.
     *
     * @param id entity UUID primary key
     * @return {@link AssetProfileCacheKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static AssetProfileCacheKey forId(AssetProfileId id) {
        return new AssetProfileCacheKey(null, null, id, false);
    }
    /**
     * For default profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfileCacheKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static AssetProfileCacheKey forDefaultProfile(TenantId tenantId) {
        return new AssetProfileCacheKey(tenantId, null, null, true);
    }

    @Override
    public String toString() {
        if (assetProfileId != null) {
            return assetProfileId.toString();
        } else if (defaultProfile) {
            return tenantId.toString();
        } else {
            return tenantId + "_" + name;
        }
    }
    /**
     * Is versioned.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isVersioned() {
        return assetProfileId != null;
    }

}
