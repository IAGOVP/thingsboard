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
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.OtaPackageEntity;
import org.thingsboard.server.dao.ota.OtaPackageDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of ota package dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
public class JpaOtaPackageDao extends JpaAbstractDao<OtaPackageEntity, OtaPackage> implements OtaPackageDao, TenantEntityDao<OtaPackage> {

    @Autowired
    private OtaPackageRepository otaPackageRepository;
    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Long sumDataSizeByTenantId(TenantId tenantId) {
        return otaPackageRepository.sumDataSizeByTenantId(tenantId.getId());
    }
    /**
     * Finds ota package by tenant id and title and version.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @param version version ({@link String})
     * @return {@link OtaPackage}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Override
    public OtaPackage findOtaPackageByTenantIdAndTitleAndVersion(TenantId tenantId, String title, String version) {
        return DaoUtil.getData(otaPackageRepository.findByTenantIdAndTitleAndVersion(tenantId.getId(), title, version));
    }
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Override
    public PageData<OtaPackage> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(otaPackageRepository.findByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Override
    public PageData<OtaPackage> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findAllByTenantId(TenantId.fromUUID(tenantId), pageLink);
    }
    /**
     * Finds ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<OtaPackageId> findIdsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(otaPackageRepository.findIdsByTenantId(tenantId, DaoUtil.toPageable(pageLink)).map(OtaPackageId::new));
    }
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link OtaPackage}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Override
    public OtaPackage findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(otaPackageRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }
    /**
     * Returns data oid by id.
     *
     * @param id entity UUID primary key
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Long getDataOidById(UUID id) {
        return otaPackageRepository.getDataOidById(id);
    }
    /**
     * Unlink large object.
     *
     * @param dataOid data oid ({@link Long})
     * @return {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Integer unlinkLargeObject(Long dataOid) {
        return otaPackageRepository.unlinkLargeObject(dataOid);
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link OtaPackageId})
     * @return {@link OtaPackageId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public OtaPackageId getExternalIdByInternal(OtaPackageId internalId) {
        return DaoUtil.toEntityId(otaPackageRepository.getExternalIdById(internalId.getId()), OtaPackageId::new);
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<OtaPackageEntity> getEntityClass() {
        return OtaPackageEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<OtaPackageEntity, UUID> getRepository() {
        return otaPackageRepository;
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.OTA_PACKAGE;
    }

}
