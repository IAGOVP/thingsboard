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

import com.google.common.util.concurrent.FluentFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.MobileAppBundleId;
import org.thingsboard.server.common.data.id.MobileAppId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.app.MobileApp;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.Validator;

import java.util.Optional;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
/**
 * Spring {@code @Service} implementing the mobile app DAO API.
 *
 * <p>Delegates to {@code *Dao} implementations and manages cache eviction (mobile apps, bundles, and QR code settings).
 */


@Slf4j
@Service
public class MobileAppServiceImpl extends AbstractEntityService implements MobileAppService {

    private static final String PLATFORM_TYPE_IS_REQUIRED = "Platform type is required if package name is specified";

    @Autowired
    private MobileAppDao mobileAppDao;
    @Autowired
    private DataValidator<MobileApp> mobileAppDataValidator;

    
    /**
     * Saves or persists mobile app.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileApp mobile app ({@link MobileApp})
     * @return {@link MobileApp}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MobileApp saveMobileApp(TenantId tenantId, MobileApp mobileApp) {
        log.trace("Executing saveMobileApp [{}]", mobileApp);
        mobileAppDataValidator.validate(mobileApp, a -> tenantId);
        try {
            MobileApp savedMobileApp = mobileAppDao.save(tenantId, mobileApp);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId).entity(savedMobileApp).build());
            return savedMobileApp;
        } catch (Exception e) {
            checkConstraintViolation(e, "mobile_app_pkg_name_platform_unq_key", "Mobile app with such package name and platform already exists!");
            throw e;
        }
    }

    
    /**
     * Deletes mobile app by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppId mobile app id ({@link MobileAppId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteMobileAppById(TenantId tenantId, MobileAppId mobileAppId) {
        log.trace("Executing deleteMobileAppById [{}]", mobileAppId.getId());
        mobileAppDao.removeById(tenantId, mobileAppId.getId());
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(mobileAppId).build());
    }

    
    /**
     * Finds mobile app by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppId mobile app id ({@link MobileAppId})
     * @return {@link MobileApp}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MobileApp findMobileAppById(TenantId tenantId, MobileAppId mobileAppId) {
        log.trace("Executing findMobileAppById [{}] [{}]", tenantId, mobileAppId);
        return mobileAppDao.findById(tenantId, mobileAppId.getId());
    }

    
    /**
     * Finds mobile apps by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param platformType platform type ({@link PlatformType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<MobileApp> findMobileAppsByTenantId(TenantId tenantId, PlatformType platformType, PageLink pageLink) {
        log.trace("Executing findMobileAppInfosByTenantId [{}]", tenantId);
        return mobileAppDao.findByTenantId(tenantId, platformType, pageLink);
    }

    
    /**
     * Finds entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return optional {@link HasId}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findMobileAppById(tenantId, new MobileAppId(entityId.getId())));
    }

    
    /**
     * Finds entity async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(mobileAppDao.findByIdAsync(tenantId, entityId.getId()))
                .transform(Optional::ofNullable, directExecutor());
    }

    
    /**
     * Deletes entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param force force
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        deleteMobileAppById(tenantId, (MobileAppId) id);
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
        log.trace("Executing findAndroidQrConfig, tenantId [{}], mobileAppBundleId [{}]", tenantId, mobileAppBundleId);
        return mobileAppDao.findByBundleIdAndPlatformType(tenantId, mobileAppBundleId, platformType);
    }

    
    /**
     * Finds mobile app by pkg name and platform type.
     *
     * @param pkgName pkg name ({@link String})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link MobileApp}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MobileApp findMobileAppByPkgNameAndPlatformType(String pkgName, PlatformType platformType) {
        log.trace("Executing findMobileAppByPkgNameAndPlatformType, pkgName [{}], platform [{}]", pkgName, platformType);
        Validator.checkNotNull(platformType, PLATFORM_TYPE_IS_REQUIRED);
        return mobileAppDao.findByPkgNameAndPlatformType(TenantId.SYS_TENANT_ID, pkgName, platformType);
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
        log.trace("Executing deleteByTenantId, tenantId [{}]", tenantId);
        mobileAppDao.deleteByTenantId(tenantId);
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
