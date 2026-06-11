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
package org.thingsboard.server.dao.sql.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ResourceSubType;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceDataInfo;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.TbResourceEntity;
import org.thingsboard.server.dao.resource.TbResourceDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of tb resource dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
public class JpaTbResourceDao extends JpaAbstractDao<TbResourceEntity, TbResource> implements TbResourceDao, TenantEntityDao<TbResource> {

    private final TbResourceRepository resourceRepository;

    public JpaTbResourceDao(TbResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<TbResourceEntity> getEntityClass() {
        return TbResourceEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<TbResourceEntity, UUID> getRepository() {
        return resourceRepository;
    }
    /**
     * Finds resource by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceKey resource key ({@link String})
     * @return {@link TbResource}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResource findResourceByTenantIdAndKey(TenantId tenantId, ResourceType resourceType, String resourceKey) {
        return DaoUtil.getData(resourceRepository.findByTenantIdAndResourceTypeAndResourceKey(tenantId.getId(), resourceType.name(), resourceKey));
    }
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TbResource> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(resourceRepository.findAllByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds resources by tenant id and resource type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceSubType resource sub type ({@link ResourceSubType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TbResource> findResourcesByTenantIdAndResourceType(TenantId tenantId,
                                                                       ResourceType resourceType,
                                                                       ResourceSubType resourceSubType,
                                                                       PageLink pageLink) {
        return DaoUtil.toPageData(resourceRepository.findResourcesPage(
                tenantId.getId(),
                TenantId.SYS_TENANT_ID.getId(),
                resourceType.name(),
                resourceSubType != null ? resourceSubType.name() : null,
                pageLink.getTextSearch(),
                DaoUtil.toPageable(pageLink)
        ));
    }
    /**
     * Finds resources by tenant id and resource type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceSubType resource sub type ({@link ResourceSubType})
     * @param objectIds object ids
     * @param searchText search text ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<TbResource> findResourcesByTenantIdAndResourceType(TenantId tenantId, ResourceType resourceType,
                                                                   ResourceSubType resourceSubType,
                                                                   String[] objectIds,
                                                                   String searchText) {
        return objectIds == null ?
                DaoUtil.convertDataList(resourceRepository.findResources(
                        tenantId.getId(),
                        TenantId.SYS_TENANT_ID.getId(),
                        resourceType.name(),
                        resourceSubType != null ? resourceSubType.name() : null,
                        searchText)) :
                DaoUtil.convertDataList(resourceRepository.findResourcesByIds(
                        tenantId.getId(),
                        TenantId.SYS_TENANT_ID.getId(),
                        resourceType.name(), objectIds));
    }
    /**
     * Returns resource data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return the byte[] value
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public byte[] getResourceData(TenantId tenantId, TbResourceId resourceId) {
        return resourceRepository.getDataById(resourceId.getId());
    }
    /**
     * Returns resource preview.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return the byte[] value
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public byte[] getResourcePreview(TenantId tenantId, TbResourceId resourceId) {
        return resourceRepository.getPreviewById(resourceId.getId());
    }
    /**
     * Returns resource size.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public long getResourceSize(TenantId tenantId, TbResourceId resourceId) {
        return resourceRepository.getDataSizeById(resourceId.getId());
    }
    /**
     * Returns resource data info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return {@link TbResourceDataInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResourceDataInfo getResourceDataInfo(TenantId tenantId, TbResourceId resourceId) {
        return resourceRepository.getDataInfoById(resourceId.getId());
    }
    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Long sumDataSizeByTenantId(TenantId tenantId) {
        return resourceRepository.sumDataSizeByTenantId(tenantId.getId());
    }
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link TbResource}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResource findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(resourceRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TbResource> findByTenantId(UUID tenantId, PageLink pageLink) {
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
    public PageData<TbResourceId> findIdsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(resourceRepository.findIdsByTenantId(tenantId, DaoUtil.toPageable(pageLink))
                .map(TbResourceId::new));
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link TbResourceId})
     * @return {@link TbResourceId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResourceId getExternalIdByInternal(TbResourceId internalId) {
        return DaoUtil.toEntityId(resourceRepository.getExternalIdByInternal(internalId.getId()), TbResourceId::new);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.TB_RESOURCE;
    }

}
