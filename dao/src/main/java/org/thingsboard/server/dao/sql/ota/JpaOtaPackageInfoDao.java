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
package org.thingsboard.server.dao.sql.ota;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.OtaPackageInfoEntity;
import org.thingsboard.server.dao.ota.OtaPackageInfoDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of ota package info dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
public class JpaOtaPackageInfoDao extends JpaAbstractDao<OtaPackageInfoEntity, OtaPackageInfo> implements OtaPackageInfoDao {

    @Autowired
    private OtaPackageInfoRepository otaPackageInfoRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<OtaPackageInfoEntity> getEntityClass() {
        return OtaPackageInfoEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<OtaPackageInfoEntity, UUID> getRepository() {
        return otaPackageInfoRepository;
    }
    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link OtaPackageInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public OtaPackageInfo findById(TenantId tenantId, UUID id) {
        return DaoUtil.getData(otaPackageInfoRepository.findOtaPackageInfoById(id));
    }
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param otaPackageInfo ota package info ({@link OtaPackageInfo})
     * @return {@link OtaPackageInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Override
    public OtaPackageInfo save(TenantId tenantId, OtaPackageInfo otaPackageInfo) {
        OtaPackageInfo savedOtaPackage = super.save(tenantId, otaPackageInfo);
        if (otaPackageInfo.getId() == null) {
            return savedOtaPackage;
        } else {
            return findById(tenantId, savedOtaPackage.getId().getId());
        }
    }
    /**
     * Finds ota package info by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<OtaPackageInfo> findOtaPackageInfoByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(otaPackageInfoRepository
                .findAllByTenantId(
                        tenantId.getId(),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds ota package info by tenant id and device profile id and type and has data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @param otaPackageType ota package type ({@link OtaPackageType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<OtaPackageInfo> findOtaPackageInfoByTenantIdAndDeviceProfileIdAndTypeAndHasData(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType, PageLink pageLink) {
        return DaoUtil.toPageData(otaPackageInfoRepository
                .findAllByTenantIdAndTypeAndDeviceProfileIdAndHasData(
                        tenantId.getId(),
                        deviceProfileId.getId(),
                        otaPackageType,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Is ota package used.
     *
     * @param otaPackageId ota package id ({@link OtaPackageId})
     * @param otaPackageType ota package type ({@link OtaPackageType})
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isOtaPackageUsed(OtaPackageId otaPackageId, OtaPackageType otaPackageType, DeviceProfileId deviceProfileId) {
        return otaPackageInfoRepository.isOtaPackageUsed(otaPackageId.getId(), deviceProfileId.getId(), otaPackageType.name());
    }

}
