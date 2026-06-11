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

import org.thingsboard.server.common.data.id.MobileAppBundleId;
import org.thingsboard.server.common.data.id.MobileAppId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.app.MobileApp;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

/**
 * Service API for mobile app persistence and domain operations.
 */
public interface MobileAppService extends EntityDaoService {

    /**
     * Saves or persists mobile app.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileApp mobile app ({@link MobileApp})
     * @return {@link MobileApp}
     */
    MobileApp saveMobileApp(TenantId tenantId, MobileApp mobileApp);

    /**
     * Finds mobile app by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppId mobile app id ({@link MobileAppId})
     * @return {@link MobileApp}
     */
    MobileApp findMobileAppById(TenantId tenantId, MobileAppId mobileAppId);

    /**
     * Finds mobile apps by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param platformType platform type ({@link PlatformType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<MobileApp> findMobileAppsByTenantId(TenantId tenantId, PlatformType platformType, PageLink pageLink);

    /**
     * Finds by bundle id and platform type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link MobileApp}
     */
    MobileApp findByBundleIdAndPlatformType(TenantId tenantId, MobileAppBundleId mobileAppBundleId, PlatformType platformType);

    /**
     * Finds mobile app by pkg name and platform type.
     *
     * @param pkgName pkg name ({@link String})
     * @param platform platform ({@link PlatformType})
     * @return {@link MobileApp}
     */
    MobileApp findMobileAppByPkgNameAndPlatformType(String pkgName, PlatformType platform);

    /**
     * Deletes mobile app by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppId mobile app id ({@link MobileAppId})
     */
    void deleteMobileAppById(TenantId tenantId, MobileAppId mobileAppId);

}
