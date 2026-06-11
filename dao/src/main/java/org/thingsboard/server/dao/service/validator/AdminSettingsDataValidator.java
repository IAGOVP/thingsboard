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
package org.thingsboard.server.dao.service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.settings.AdminSettingsService;
/**
 * Validates admin settings entities before persistence.
 *
 * <p>Enforces constraints, uniqueness, and referential integrity at the DAO layer.
 */


@Component
@AllArgsConstructor
public class AdminSettingsDataValidator extends DataValidator<AdminSettings> {

    private final AdminSettingsService adminSettingsService;

    
    /**
     * Validates create.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param adminSettings admin settings ({@link AdminSettings})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void validateCreate(TenantId tenantId, AdminSettings adminSettings) {
        AdminSettings existingSettings = adminSettingsService.findAdminSettingsByTenantIdAndKey(tenantId, adminSettings.getKey());
        if (existingSettings != null) {
            throw new DataValidationException("Admin settings with such name already exists!");
        }
    }

    
    /**
     * Validates update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param adminSettings admin settings ({@link AdminSettings})
     * @return {@link AdminSettings}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected AdminSettings validateUpdate(TenantId tenantId, AdminSettings adminSettings) {
        AdminSettings existentAdminSettings = adminSettingsService.findAdminSettingsById(tenantId, adminSettings.getId());
        if (existentAdminSettings != null) {
            if (!existentAdminSettings.getKey().equals(adminSettings.getKey())) {
                throw new DataValidationException("Changing key of admin settings entry is prohibited!");
            }
        }
        return existentAdminSettings;
    }

    
    /**
     * Validates data impl.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param adminSettings admin settings ({@link AdminSettings})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void validateDataImpl(TenantId tenantId, AdminSettings adminSettings) {
        validateString("Key", adminSettings.getKey());
        if (adminSettings.getJsonValue() == null) {
            throw new DataValidationException("Json value should be specified!");
        }
    }
}
