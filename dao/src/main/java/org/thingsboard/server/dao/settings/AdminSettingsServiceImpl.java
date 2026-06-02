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
package org.thingsboard.server.dao.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.FluentFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.AdminSettingsId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.Validator;

import java.util.Optional;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
/**
 * Spring service implementing admin settings API.
 */

@Service
@Slf4j
public class AdminSettingsServiceImpl implements AdminSettingsService {

    @Autowired
    private AdminSettingsDao adminSettingsDao;

    @Autowired
    private DataValidator<AdminSettings> adminSettingsValidator;

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    /**

     * Loads admin settings by id.

     */

    @Override
    public AdminSettings findAdminSettingsById(TenantId tenantId, AdminSettingsId adminSettingsId) {
        log.trace("Executing findAdminSettingsById [{}]", adminSettingsId);
        Validator.validateId(adminSettingsId, id -> "Incorrect adminSettingsId " + id);
        return adminSettingsDao.findById(tenantId, adminSettingsId.getId());
    }

    /**

     * Loads admin settings by key.

     */

    @Override
    public AdminSettings findAdminSettingsByKey(TenantId tenantId, String key) {
        log.trace("Executing findAdminSettingsByKey [{}]", key);
        Validator.validateString(key, k -> "Incorrect key " + k);
        return findAdminSettingsByTenantIdAndKey(TenantId.SYS_TENANT_ID, key);
    }

    /**

     * Loads admin settings by tenant id and key.

     */

    @Override
    public AdminSettings findAdminSettingsByTenantIdAndKey(TenantId tenantId, String key) {
        return adminSettingsDao.findByTenantIdAndKey(tenantId.getId(), key);
    }

    /**

     * Loads all by tenant id.

     */

    @Override
    public PageData<AdminSettings> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return adminSettingsDao.findAllByTenantId(tenantId, pageLink);
    }

    /**

     * Persists admin settings.

     */

    @Override
    public AdminSettings saveAdminSettings(TenantId tenantId, AdminSettings adminSettings) {
        log.trace("Executing saveAdminSettings [{}]", adminSettings);
        AdminSettings oldAdminSettings = adminSettingsValidator.validate(adminSettings, data -> tenantId);
        if (adminSettings.getKey().equals("mail")) {
            AdminSettings mailSettings = findAdminSettingsByKey(tenantId, "mail");
            if (mailSettings != null) {
                JsonNode newJsonValue = adminSettings.getJsonValue();
                JsonNode oldJsonValue = mailSettings.getJsonValue();
                if (!newJsonValue.has("password") && oldJsonValue.has("password")) {
                    ((ObjectNode) newJsonValue).put("password", oldJsonValue.get("password").asText());
                }
                if (!newJsonValue.has("refreshToken") && oldJsonValue.has("refreshToken")) {
                    ((ObjectNode) newJsonValue).put("refreshToken", oldJsonValue.get("refreshToken").asText());
                }
                dropTokenIfProviderInfoChanged(newJsonValue, oldJsonValue);
            }
        }
        if (adminSettings.getTenantId() == null) {
            adminSettings.setTenantId(TenantId.SYS_TENANT_ID);
        }
        AdminSettings savedAdminSettings = adminSettingsDao.save(tenantId, adminSettings);
        eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(savedAdminSettings.getTenantId()).entityId(savedAdminSettings.getId())
                .entity(savedAdminSettings).oldEntity(oldAdminSettings).created(adminSettings.getId() == null).build());
        return savedAdminSettings;
    }

    /**

     * Removes admin settings by tenant id and key.

     */

    @Override
    public boolean deleteAdminSettingsByTenantIdAndKey(TenantId tenantId, String key) {
        log.trace("Executing deleteAdminSettings, tenantId [{}], key [{}]", tenantId, key);
        Validator.validateString(key, k -> "Incorrect key " + k);
        return adminSettingsDao.removeByTenantIdAndKey(tenantId.getId(), key);
    }

    /**

     * Removes by tenant id.

     */

    @Override
    public void deleteByTenantId(TenantId tenantId) {
        adminSettingsDao.removeByTenantId(tenantId.getId());
    }

    /**

     * Removes entity.

     */

    @Override
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        adminSettingsDao.removeById(tenantId, id.getId());
    }

    /**

     * Loads entity.

     */

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(adminSettingsDao.findById(tenantId, entityId.getId()));
    }

    /**

     * Loads entity async.

     */

    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(adminSettingsDao.findByIdAsync(tenantId, entityId.getId()))
                .transform(Optional::ofNullable, directExecutor());
    }

    /**

     * Get entity type.

     */

    @Override
    public EntityType getEntityType() {
        return EntityType.ADMIN_SETTINGS;
    }

    private void dropTokenIfProviderInfoChanged(JsonNode newJsonValue, JsonNode oldJsonValue) {
        if (newJsonValue.has("enableOauth2") && newJsonValue.get("enableOauth2").asBoolean()) {
            if (!newJsonValue.get("providerId").equals(oldJsonValue.get("providerId")) ||
                    !newJsonValue.get("clientId").equals(oldJsonValue.get("clientId")) ||
                    !newJsonValue.get("clientSecret").equals(oldJsonValue.get("clientSecret")) ||
                    !newJsonValue.get("redirectUri").equals(oldJsonValue.get("redirectUri")) ||
                    (newJsonValue.has("providerTenantId") && !newJsonValue.get("providerTenantId").equals(oldJsonValue.get("providerTenantId")))) {
                ((ObjectNode) newJsonValue).put("tokenGenerated", false);
                ((ObjectNode) newJsonValue).remove("refreshToken");
                ((ObjectNode) newJsonValue).remove("refreshTokenExpires");
            }
        }
    }

}
