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
import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundle;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleInfo;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleOauth2Client;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientInfo;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.oauth2.OAuth2ClientDao;
import org.thingsboard.server.dao.service.DataValidator;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.thingsboard.server.dao.service.Validator.checkNotNull;
/**
 * Spring {@code @Service} implementing the mobile app bundle DAO API.
 *
 * <p>Delegates to {@code *Dao} implementations and manages cache eviction (mobile apps, bundles, and QR code settings).
 */


@Slf4j
@Service
public class MobileAppBundleServiceImpl extends AbstractEntityService implements MobileAppBundleService {

    private static final String PLATFORM_TYPE_IS_REQUIRED = "Platform type is required if package name is specified";

    @Autowired
    private OAuth2ClientDao oauth2ClientDao;
    @Autowired
    private MobileAppBundleDao mobileAppBundleDao;
    @Autowired
    private DataValidator<MobileAppBundle> mobileAppBundleDataValidator;

    
    /**
     * Saves or persists mobile app bundle.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundle mobile app bundle ({@link MobileAppBundle})
     * @return {@link MobileAppBundle}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MobileAppBundle saveMobileAppBundle(TenantId tenantId, MobileAppBundle mobileAppBundle) {
        log.trace("Executing saveMobileAppBundle [{}]", mobileAppBundle);
        mobileAppBundleDataValidator.validate(mobileAppBundle, b -> tenantId);
        try {
            MobileAppBundle savedMobileApp = mobileAppBundleDao.save(tenantId, mobileAppBundle);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId).entity(savedMobileApp).build());
            return savedMobileApp;
        } catch (Exception e) {
            checkConstraintViolation(e,
                    "mobile_app_bundle_android_app_id_key", "Android mobile app is already configured in another bundle!",
                    "mobile_app_bundle_ios_app_id_key", "IOS mobile app is already configured in another bundle!");
            throw e;
        }
    }

    
    /**
     * Updates oauth2clients.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @param oAuth2ClientIds o auth2client ids ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void updateOauth2Clients(TenantId tenantId, MobileAppBundleId mobileAppBundleId, List<OAuth2ClientId> oAuth2ClientIds) {
        log.trace("Executing updateOauth2Clients, mobileAppId [{}], oAuth2ClientIds [{}]", mobileAppBundleId, oAuth2ClientIds);
        Set<MobileAppBundleOauth2Client> newClientList = oAuth2ClientIds.stream()
                .map(clientId -> new MobileAppBundleOauth2Client(mobileAppBundleId, clientId))
                .collect(Collectors.toSet());

        List<MobileAppBundleOauth2Client> existingClients = mobileAppBundleDao.findOauth2ClientsByMobileAppBundleId(tenantId, mobileAppBundleId);
        List<MobileAppBundleOauth2Client> toRemoveList = existingClients.stream()
                .filter(client -> !newClientList.contains(client))
                .toList();
        newClientList.removeIf(existingClients::contains);

        for (MobileAppBundleOauth2Client client : toRemoveList) {
            mobileAppBundleDao.removeOauth2Client(tenantId, client);
        }
        for (MobileAppBundleOauth2Client client : newClientList) {
            mobileAppBundleDao.addOauth2Client(tenantId, client);
        }
        eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId)
                .entityId(mobileAppBundleId).created(false).build());
    }

    
    /**
     * Finds mobile app bundle by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return {@link MobileAppBundle}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MobileAppBundle findMobileAppBundleById(TenantId tenantId, MobileAppBundleId mobileAppBundleId) {
        log.trace("Executing findMobileAppBundleById [{}] [{}]", tenantId, mobileAppBundleId);
        return mobileAppBundleDao.findById(tenantId, mobileAppBundleId.getId());
    }

    
    /**
     * Finds mobile app bundle infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<MobileAppBundleInfo> findMobileAppBundleInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findMobileAppBundleInfosByTenantId [{}]", tenantId);
        PageData<MobileAppBundleInfo> mobileBundles = mobileAppBundleDao.findInfosByTenantId(tenantId, pageLink);
        mobileBundles.getData().forEach(this::fetchOauth2Clients);
        return mobileBundles;
    }

    
    /**
     * Finds mobile app bundle info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppIdBundle mobile app id bundle ({@link MobileAppBundleId})
     * @return {@link MobileAppBundleInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MobileAppBundleInfo findMobileAppBundleInfoById(TenantId tenantId, MobileAppBundleId mobileAppIdBundle) {
        log.trace("Executing findMobileAppBundleInfoById [{}] [{}]", tenantId, mobileAppIdBundle);
        MobileAppBundleInfo mobileAppBundleInfo = mobileAppBundleDao.findInfoById(tenantId, mobileAppIdBundle);
        if (mobileAppBundleInfo != null) {
            fetchOauth2Clients(mobileAppBundleInfo);
        }
        return mobileAppBundleInfo;
    }

    
    /**
     * Finds mobile app bundle by pkg name and platform.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pkgName pkg name ({@link String})
     * @param platform platform ({@link PlatformType})
     * @return {@link MobileAppBundle}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MobileAppBundle findMobileAppBundleByPkgNameAndPlatform(TenantId tenantId, String pkgName, PlatformType platform) {
        log.trace("Executing findMobileAppBundleByPkgNameAndPlatform, tenantId [{}], pkgName [{}], platform [{}]", tenantId, pkgName, platform);
        checkNotNull(platform, PLATFORM_TYPE_IS_REQUIRED);
        return mobileAppBundleDao.findByPkgNameAndPlatform(tenantId, pkgName, platform);
    }

    
    /**
     * Deletes mobile app bundle by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param mobileAppBundleId mobile app bundle id ({@link MobileAppBundleId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteMobileAppBundleById(TenantId tenantId, MobileAppBundleId mobileAppBundleId) {
        log.trace("Executing deleteMobileAppBundleById [{}]", mobileAppBundleId.getId());
        mobileAppBundleDao.removeById(tenantId, mobileAppBundleId.getId());
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(mobileAppBundleId).build());
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
        log.trace("Executing deleteMobileAppsByTenantId, tenantId [{}]", tenantId);
        mobileAppBundleDao.deleteByTenantId(tenantId);
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
        return Optional.ofNullable(findMobileAppBundleById(tenantId, new MobileAppBundleId(entityId.getId())));
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
        return FluentFuture.from(mobileAppBundleDao.findByIdAsync(tenantId, entityId.getId()))
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
        deleteMobileAppBundleById(tenantId, (MobileAppBundleId) id);
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

    private void fetchOauth2Clients(MobileAppBundleInfo mobileAppBundleInfo) {
        List<OAuth2ClientInfo> clients = oauth2ClientDao.findByMobileAppBundleId(mobileAppBundleInfo.getUuidId()).stream()
                .map(OAuth2ClientInfo::new)
                .sorted(Comparator.comparing(OAuth2ClientInfo::getTitle))
                .collect(Collectors.toList());
        mobileAppBundleInfo.setOauth2ClientInfos(clients);
    }

}
