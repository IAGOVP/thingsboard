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
package org.thingsboard.server.dao.sql.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.edqs.fields.TenantProfileFields;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.TenantProfileEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.tenant.TenantProfileDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of tenant profile dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
public class JpaTenantProfileDao extends JpaAbstractDao<TenantProfileEntity, TenantProfile> implements TenantProfileDao {

    @Autowired
    private TenantProfileRepository tenantProfileRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<TenantProfileEntity> getEntityClass() {
        return TenantProfileEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<TenantProfileEntity, UUID> getRepository() {
        return tenantProfileRepository;
    }
    /**
     * Finds tenant profile info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantProfileId tenant profile id ({@link UUID})
     * @return {@link EntityInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityInfo findTenantProfileInfoById(TenantId tenantId, UUID tenantProfileId) {
        return tenantProfileRepository.findTenantProfileInfoById(tenantProfileId);
    }
    /**
     * Finds tenant profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TenantProfile> findTenantProfiles(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                tenantProfileRepository.findTenantProfiles(
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds tenant profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<EntityInfo> findTenantProfileInfos(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(
                tenantProfileRepository.findTenantProfileInfos(
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds default tenant profile.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link TenantProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TenantProfile findDefaultTenantProfile(TenantId tenantId) {
        return DaoUtil.getData(tenantProfileRepository.findByDefaultTrue());
    }
    /**
     * Finds default tenant profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link EntityInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityInfo findDefaultTenantProfileInfo(TenantId tenantId) {
        return tenantProfileRepository.findDefaultTenantProfileInfo();
    }
    /**
     * Finds tenant profiles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ids ids
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<TenantProfile> findTenantProfilesByIds(TenantId tenantId, UUID[] ids) {
        return DaoUtil.convertDataList(tenantProfileRepository.findByIdIn(Arrays.asList(ids)));
    }
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<TenantProfileFields> findNextBatch(UUID id, int batchSize) {
        return tenantProfileRepository.findNextBatch(id, Limit.of(batchSize));
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.TENANT_PROFILE;
    }

}
