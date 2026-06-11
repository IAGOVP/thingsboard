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
package org.thingsboard.server.dao.mobile;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.app.MobileApp;
import org.thingsboard.server.common.data.mobile.qrCodeSettings.QrCodeSettings;
import org.thingsboard.server.common.data.oauth2.PlatformType;






















/**






 * qr code setting service contract (mobile apps, bundles, and QR code settings).






 */







public interface QrCodeSettingService {
    /**
     * Saves or persists qr code settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param qrCodeSettings qr code settings ({@link QrCodeSettings})
     * @return {@link QrCodeSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    QrCodeSettings saveQrCodeSettings(TenantId tenantId, QrCodeSettings qrCodeSettings);
    /**
     * Finds qr code settings.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link QrCodeSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    QrCodeSettings findQrCodeSettings(TenantId tenantId);
    /**
     * Finds app from qr code settings.
     *
     * @param sysTenantId sys tenant id ({@link TenantId})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link MobileApp}
     * @throws Exception if an unexpected error occurs during processing
     */

    MobileApp findAppFromQrCodeSettings(TenantId sysTenantId, PlatformType platformType);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByTenantId(TenantId tenantId);

}
