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
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundle;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleInfo;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleOauth2Client;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.mobile.MobileAppBundleDao;
import org.thingsboard.server.dao.model.sql.MobileAppBundleEntity;
import org.thingsboard.server.dao.model.sql.MobileAppBundleOauth2ClientEntity;
import org.thingsboard.server.dao.model.sql.MobileAppOauth2ClientCompositeKey;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of mobile app bundle dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@RequiredArgsConstructor
@SqlDao
public class JpaMobileAppBundleDao extends JpaAbstractDao<MobileAppBundleEntity, MobileAppBundle> implements MobileAppBundleDao {

    private final MobileAppBundleRepository mobileAppBundleRepository;
    private final MobileAppBundleOauth2ClientRepository mobileOauth2ProviderRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<MobileAppBundleEntity> getEntityClass() {
        return MobileAppBundleEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<MobileAppBundleEntity, UUID> getRepository() {
        return mobileAppBundleRepository;
    }
    /**
     * Finds infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<MobileAppBundleInfo> findInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(mobileAppBundleRepository.findInfoByTenantId(tenantId.getId(), pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return {@link MobileAppBundleInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MobileAppBundleInfo findInfoById(TenantId tenantId, MobileAppBundleId mobileAppBundleId) {
        return DaoUtil.getData(mobileAppBundleRepository.findInfoById(mobileAppBundleId.getId()));
    }
    /**
     * Finds oauth2clients by mobile app bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<MobileAppBundleOauth2Client> findOauth2ClientsByMobileAppBundleId(TenantId tenantId, MobileAppBundleId mobileAppBundleId) {
        return DaoUtil.convertDataList(mobileOauth2ProviderRepository.findAllByMobileAppBundleId(mobileAppBundleId.getId()));
    }
    /**
     * Add oauth2client.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleOauth2Client mobile app bundle oauth2client ({@link MobileAppBundleOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void addOauth2Client(TenantId tenantId, MobileAppBundleOauth2Client mobileAppBundleOauth2Client) {
        mobileOauth2ProviderRepository.save(new MobileAppBundleOauth2ClientEntity(mobileAppBundleOauth2Client));
    }
    /**
     * Removes oauth2client.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleOauth2Client mobile app bundle oauth2client ({@link MobileAppBundleOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeOauth2Client(TenantId tenantId, MobileAppBundleOauth2Client mobileAppBundleOauth2Client) {
        mobileOauth2ProviderRepository.deleteById(new MobileAppOauth2ClientCompositeKey(mobileAppBundleOauth2Client.getMobileAppBundleId().getId(),
                mobileAppBundleOauth2Client.getOAuth2ClientId().getId()));
    }
    /**
     * Finds by pkg name and platform.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pkgName pkg name ({@link String})
     * @param platform platform ({@link PlatformType})
     * @return {@link MobileAppBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MobileAppBundle findByPkgNameAndPlatform(TenantId tenantId, String pkgName, PlatformType platform) {
        return DaoUtil.getData(mobileAppBundleRepository.findByPkgNameAndPlatformType(pkgName, platform));
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
        mobileAppBundleRepository.deleteByTenantId(tenantId.getId());
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.MOBILE_APP_BUNDLE;
    }

}

