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
package org.thingsboard.rule.engine.api;

import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by ashvayka on 02.04.18.
 */
/**
 * rule engine asset profile cache contract (rule engine public API contracts and services).
 */

public interface RuleEngineAssetProfileCache {
    /**
     * Returns the requested data.
     *
     * @param tenantId tenant UUID
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfile get(TenantId tenantId, AssetProfileId assetProfileId);
    /**
     * Returns the requested data.
     *
     * @param tenantId tenant UUID
     * @param assetId asset id ({@link AssetId})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfile get(TenantId tenantId, AssetId assetId);
    /**
     * Add listener.
     *
     * @param tenantId tenant UUID
     * @param listenerId listener id ({@link EntityId})
     * @param profileListener profile listener ({@link Consumer})
     * @param assetlistener assetlistener ({@link BiConsumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void addListener(TenantId tenantId, EntityId listenerId, Consumer<AssetProfile> profileListener, BiConsumer<AssetId, AssetProfile> assetlistener);
    /**
     * Removes listener.
     *
     * @param tenantId tenant UUID
     * @param listenerId listener id ({@link EntityId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeListener(TenantId tenantId, EntityId listenerId);

}
