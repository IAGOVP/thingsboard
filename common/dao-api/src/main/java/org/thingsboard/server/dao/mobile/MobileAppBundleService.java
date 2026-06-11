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
import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundle;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleInfo;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for mobile app bundle persistence and domain operations.
 */
public interface MobileAppBundleService extends EntityDaoService {

    /**
     * Saves or persists mobile app bundle.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundle mobile app bundle ({@link MobileAppBundle})
     * @return {@link MobileAppBundle}
     */
    MobileAppBundle saveMobileAppBundle(TenantId tenantId, MobileAppBundle mobileAppBundle);

    /**
     * Updates oauth2clients.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @param oAuth2ClientIds o auth2client ids ({@link List})
     */
    void updateOauth2Clients(TenantId tenantId, MobileAppBundleId mobileAppBundleId, List<OAuth2ClientId> oAuth2ClientIds);

    /**
     * Finds mobile app bundle by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return {@link MobileAppBundle}
     */
    MobileAppBundle findMobileAppBundleById(TenantId tenantId, MobileAppBundleId mobileAppBundleId);

    /**
     * Finds mobile app bundle infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<MobileAppBundleInfo> findMobileAppBundleInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds mobile app bundle info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return {@link MobileAppBundleInfo}
     */
    MobileAppBundleInfo findMobileAppBundleInfoById(TenantId tenantId, MobileAppBundleId mobileAppBundleId);

    /**
     * Finds mobile app bundle by pkg name and platform.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pkgName pkg name ({@link String})
     * @param platform platform ({@link PlatformType})
     * @return {@link MobileAppBundle}
     */
    MobileAppBundle findMobileAppBundleByPkgNameAndPlatform(TenantId tenantId, String pkgName, PlatformType platform);

    /**
     * Deletes mobile app bundle by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     */
    void deleteMobileAppBundleById(TenantId tenantId, MobileAppBundleId mobileAppBundleId);

}
