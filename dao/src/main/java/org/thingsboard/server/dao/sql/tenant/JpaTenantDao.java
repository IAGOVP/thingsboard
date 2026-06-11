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
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantInfo;
import org.thingsboard.server.common.data.edqs.fields.TenantFields;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.TenantEntity;
import org.thingsboard.server.dao.model.sql.TenantInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.tenant.TenantDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * JPA/PostgreSQL implementation of tenant dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
public class JpaTenantDao extends JpaAbstractDao<TenantEntity, Tenant> implements TenantDao {

    @Autowired
    private TenantRepository tenantRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<TenantEntity> getEntityClass() {
        return TenantEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<TenantEntity, UUID> getRepository() {
        return tenantRepository;
    }
    /**
     * Finds tenant info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link TenantInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TenantInfo findTenantInfoById(TenantId tenantId, UUID id) {
        return DaoUtil.getData(tenantRepository.findTenantInfoById(id));
    }
    /**
     * Finds tenants.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<Tenant> findTenants(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(tenantRepository
                .findTenantsNextPage(
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds tenant infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TenantInfo> findTenantInfos(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(tenantRepository
                .findTenantInfosNextPage(
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, TenantInfoEntity.tenantInfoColumnMap)));
    }
    /**
     * Finds tenants ids.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TenantId> findTenantsIds(PageLink pageLink) {
        return DaoUtil.pageToPageData(tenantRepository.findTenantsIds(DaoUtil.toPageable(pageLink))).mapData(TenantId::fromUUID);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.TENANT;
    }
    /**
     * Finds tenant ids by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link TenantProfileId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<TenantId> findTenantIdsByTenantProfileId(TenantProfileId tenantProfileId) {
        return tenantRepository.findTenantIdsByTenantProfileId(tenantProfileId.getId()).stream()
                .map(TenantId::fromUUID)
                .collect(Collectors.toList());
    }
    /**
     * Finds tenant by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link Tenant}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Tenant findTenantByName(TenantId tenantId, String name) {
        return DaoUtil.getData(tenantRepository.findFirstByTitle(name));
    }
    /**
     * Finds tenants by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param tenantIds tenant ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<Tenant> findTenantsByIds(UUID tenantId, List<UUID> tenantIds) {
        return DaoUtil.convertDataList(tenantRepository.findTenantsByIdIn(tenantIds));
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
    public List<TenantFields> findNextBatch(UUID id, int batchSize) {
        return tenantRepository.findNextBatch(id, Limit.of(batchSize));
    }

}
