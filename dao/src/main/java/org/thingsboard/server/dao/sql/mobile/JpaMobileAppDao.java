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
package org.thingsboard.server.dao.sql.mobile;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.MobileAppBundleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.app.MobileApp;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.mobile.MobileAppDao;
import org.thingsboard.server.dao.model.sql.MobileAppEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of mobile app dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@RequiredArgsConstructor
@SqlDao
public class JpaMobileAppDao extends JpaAbstractDao<MobileAppEntity, MobileApp> implements MobileAppDao {

    private final MobileAppRepository mobileAppRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<MobileAppEntity> getEntityClass() {
        return MobileAppEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<MobileAppEntity, UUID> getRepository() {
        return mobileAppRepository;
    }
    /**
     * Finds by bundle id and platform type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link MobileApp}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MobileApp findByBundleIdAndPlatformType(TenantId tenantId, MobileAppBundleId mobileAppBundleId, PlatformType platformType) {
        return switch (platformType) {
            case ANDROID -> DaoUtil.getData(mobileAppRepository.findAndroidAppByBundleId(mobileAppBundleId.getId()));
            case IOS -> DaoUtil.getData(mobileAppRepository.findIOSAppByBundleId(mobileAppBundleId.getId()));
            default -> null;
        };
    }
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param platformType platform type ({@link PlatformType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<MobileApp> findByTenantId(TenantId tenantId, PlatformType platformType, PageLink pageLink) {
        return DaoUtil.toPageData(mobileAppRepository.findByTenantId(tenantId.getId(), platformType, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void deleteByTenantId(TenantId tenantId) {
        mobileAppRepository.deleteByTenantId(tenantId.getId());
    }
    /**
     * Finds by pkg name and platform type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pkgName pkg name ({@link String})
     * @param platform platform ({@link PlatformType})
     * @return {@link MobileApp}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MobileApp findByPkgNameAndPlatformType(TenantId tenantId, String pkgName, PlatformType platform) {
        return DaoUtil.getData(mobileAppRepository.findByPkgNameAndPlatformType(pkgName, platform));
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.MOBILE_APP;
    }

}

