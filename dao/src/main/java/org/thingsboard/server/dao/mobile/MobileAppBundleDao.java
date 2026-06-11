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
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundle;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleInfo;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleOauth2Client;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;


/**

 * Persistence contract for mobile app bundle.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (mobile apps, bundles, and QR code settings).

 */


public interface MobileAppBundleDao extends Dao<MobileAppBundle> {
    /**
     * Finds infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<MobileAppBundleInfo> findInfosByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Finds info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return {@link MobileAppBundleInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    MobileAppBundleInfo findInfoById(TenantId tenantId, MobileAppBundleId mobileAppBundleId);
    /**
     * Finds oauth2clients by mobile app bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<MobileAppBundleOauth2Client> findOauth2ClientsByMobileAppBundleId(TenantId tenantId, MobileAppBundleId mobileAppBundleId);
    /**
     * Add oauth2client.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleOauth2Client mobile app bundle oauth2client ({@link MobileAppBundleOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void addOauth2Client(TenantId tenantId, MobileAppBundleOauth2Client mobileAppBundleOauth2Client);
    /**
     * Removes oauth2client.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleOauth2Client mobile app bundle oauth2client ({@link MobileAppBundleOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeOauth2Client(TenantId tenantId, MobileAppBundleOauth2Client mobileAppBundleOauth2Client);
    /**
     * Finds by pkg name and platform.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pkgName pkg name ({@link String})
     * @param platform platform ({@link PlatformType})
     * @return {@link MobileAppBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    MobileAppBundle findByPkgNameAndPlatform(TenantId tenantId, String pkgName, PlatformType platform);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByTenantId(TenantId tenantId);

}
