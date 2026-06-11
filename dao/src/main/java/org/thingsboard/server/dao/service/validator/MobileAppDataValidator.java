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
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.app.MobileApp;
import org.thingsboard.server.common.data.mobile.app.MobileAppStatus;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
/**
 * Validates mobile app entities before persistence.
 *
 * <p>Enforces constraints, uniqueness, and referential integrity at the DAO layer.
 */


@Component
@AllArgsConstructor
public class MobileAppDataValidator extends DataValidator<MobileApp> {

    
    /**
     * Validates data impl.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileApp mobile app ({@link MobileApp})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void validateDataImpl(TenantId tenantId, MobileApp mobileApp) {
        if (mobileApp.getStatus() == MobileAppStatus.PUBLISHED) {
            if (mobileApp.getStoreInfo() == null) {
                throw new DataValidationException("Store info is required for published apps");
            }
            if (mobileApp.getPlatformType() == PlatformType.ANDROID &&
                    (mobileApp.getStoreInfo().getSha256CertFingerprints() == null || mobileApp.getStoreInfo().getStoreLink() == null)) {
                throw new DataValidationException("Sha256CertFingerprints and store link are required");
            } else if (mobileApp.getPlatformType() == PlatformType.IOS &&
                    (mobileApp.getStoreInfo().getAppId() == null || mobileApp.getStoreInfo().getStoreLink() == null)) {
                throw new DataValidationException("AppId and store link are required");
            }
        }
    }
}
