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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.plugin.ComponentLifecycleEvent;
import org.thingsboard.server.common.msg.plugin.ComponentLifecycleMsg;
import org.thingsboard.server.dao.asset.AssetProfileService;
import org.thingsboard.server.dao.asset.AssetService;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

    /**
     * Default Spring implementation for tb asset profile cache (device and asset profile resolution).
     *
     * <p>Registered as a {@code @Service} or {@code @Component} bean.
     */

@Service
@Slf4j
public class DefaultTbAssetProfileCache implements TbAssetProfileCache {

    private final Lock assetProfileFetchLock = new ReentrantLock();
    private final AssetProfileService assetProfileService;
    private final AssetService assetService;

    private final ConcurrentMap<AssetProfileId, AssetProfile> assetProfilesMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<AssetId, AssetProfileId> assetsMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<TenantId, ConcurrentMap<EntityId, Consumer<AssetProfile>>> profileListeners = new ConcurrentHashMap<>();
    private final ConcurrentMap<TenantId, ConcurrentMap<EntityId, BiConsumer<AssetId, AssetProfile>>> assetProfileListeners = new ConcurrentHashMap<>();

    public DefaultTbAssetProfileCache(AssetProfileService assetProfileService, AssetService assetService) {
        this.assetProfileService = assetProfileService;
        this.assetService = assetService;
    }
    /**
     * Returns the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AssetProfile get(TenantId tenantId, AssetProfileId assetProfileId) {
        AssetProfile profile = assetProfilesMap.get(assetProfileId);
        if (profile == null) {
            assetProfileFetchLock.lock();
            try {
                profile = assetProfilesMap.get(assetProfileId);
                if (profile == null) {
                    profile = assetProfileService.findAssetProfileById(tenantId, assetProfileId);
                    if (profile != null) {
                        assetProfilesMap.put(assetProfileId, profile);
                        log.debug("[{}] Fetch asset profile into cache: {}", profile.getId(), profile);
                    }
                }
            } finally {
                assetProfileFetchLock.unlock();
            }
        }
        log.trace("[{}] Found asset profile in cache: {}", assetProfileId, profile);
        return profile;
    }
    /**
     * Returns the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AssetProfile get(TenantId tenantId, AssetId assetId) {
        AssetProfileId profileId = assetsMap.get(assetId);
        if (profileId == null) {
            Asset asset = assetService.findAssetById(tenantId, assetId);
            if (asset != null) {
                profileId = asset.getAssetProfileId();
                assetsMap.put(assetId, profileId);
            } else {
                return null;
            }
        }
        return get(tenantId, profileId);
    }
    /**
     * Evict.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileId profile id ({@link AssetProfileId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void evict(TenantId tenantId, AssetProfileId profileId) {
        AssetProfile oldProfile = assetProfilesMap.remove(profileId);
        log.debug("[{}] evict asset profile from cache: {}", profileId, oldProfile);
        AssetProfile newProfile = get(tenantId, profileId);
        if (newProfile != null) {
            notifyProfileListeners(newProfile);
        }
    }
    /**
     * Evict.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetId asset id ({@link AssetId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void evict(TenantId tenantId, AssetId assetId) {
        AssetProfileId old = assetsMap.remove(assetId);
        if (old != null) {
            AssetProfile newProfile = get(tenantId, assetId);
            if (newProfile == null || !old.equals(newProfile.getId())) {
                notifyAssetListeners(tenantId, assetId, newProfile);
            }
        }
    }
    /**
     * Add listener.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param listenerId listener id ({@link EntityId})
     * @param profileListener profile listener ({@link Consumer})
     * @param assetListener asset listener ({@link BiConsumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void addListener(TenantId tenantId, EntityId listenerId,
                            Consumer<AssetProfile> profileListener,
                            BiConsumer<AssetId, AssetProfile> assetListener) {
        if (profileListener != null) {
            profileListeners.computeIfAbsent(tenantId, id -> new ConcurrentHashMap<>()).put(listenerId, profileListener);
        }
        if (assetListener != null) {
            assetProfileListeners.computeIfAbsent(tenantId, id -> new ConcurrentHashMap<>()).put(listenerId, assetListener);
        }
    }
    /**
     * Finds the requested data.
     *
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AssetProfile find(AssetProfileId assetProfileId) {
        return assetProfileService.findAssetProfileById(TenantId.SYS_TENANT_ID, assetProfileId);
    }
    /**
     * Finds or create asset profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileName profile name ({@link String})
     * @return {@link AssetProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AssetProfile findOrCreateAssetProfile(TenantId tenantId, String profileName) {
        return assetProfileService.findOrCreateAssetProfile(tenantId, profileName);
    }
    /**
     * Removes listener.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param listenerId listener id ({@link EntityId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeListener(TenantId tenantId, EntityId listenerId) {
        ConcurrentMap<EntityId, Consumer<AssetProfile>> tenantListeners = profileListeners.get(tenantId);
        if (tenantListeners != null) {
            tenantListeners.remove(listenerId);
        }
        ConcurrentMap<EntityId, BiConsumer<AssetId, AssetProfile>> assetListeners = assetProfileListeners.get(tenantId);
        if (assetListeners != null) {
            assetListeners.remove(listenerId);
        }
    }
    /**
     * Handles component lifecycle event.
     *
     * @param event event ({@link ComponentLifecycleMsg})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @EventListener(ComponentLifecycleMsg.class)
    public void onComponentLifecycleEvent(ComponentLifecycleMsg event) {
        switch (event.getEntityId().getEntityType()) {
            case TENANT:
                if (event.getEvent() == ComponentLifecycleEvent.DELETED) {
                    TenantId tenantId = event.getTenantId();
                    Set<AssetProfileId> toRemove = assetProfilesMap.values().stream()
                            .filter(assetProfile -> assetProfile.getTenantId().equals(tenantId))
                            .map(AssetProfile::getId)
                            .collect(Collectors.toSet());
                    assetProfilesMap.keySet().removeAll(toRemove);
                    assetsMap.entrySet().removeIf(entry -> toRemove.contains(entry.getValue()));
                    profileListeners.remove(tenantId);
                    assetProfileListeners.remove(tenantId);
                }
                break;
        }
    }

    private void notifyProfileListeners(AssetProfile profile) {
        ConcurrentMap<EntityId, Consumer<AssetProfile>> tenantListeners = profileListeners.get(profile.getTenantId());
        if (tenantListeners != null) {
            tenantListeners.forEach((id, listener) -> listener.accept(profile));
        }
    }

    private void notifyAssetListeners(TenantId tenantId, AssetId assetId, AssetProfile profile) {
        if (profile != null) {
            ConcurrentMap<EntityId, BiConsumer<AssetId, AssetProfile>> tenantListeners = assetProfileListeners.get(tenantId);
            if (tenantListeners != null) {
                tenantListeners.forEach((id, listener) -> listener.accept(assetId, profile));
            }
        }
    }

}
