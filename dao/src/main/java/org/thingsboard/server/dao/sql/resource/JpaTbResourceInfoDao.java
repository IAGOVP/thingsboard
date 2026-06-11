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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.ResourceSubType;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.TbResourceInfoFilter;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.util.CollectionsUtil;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.TbResourceInfoEntity;
import org.thingsboard.server.dao.resource.TbResourceInfoDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.DaoUtil.toUUIDs;
/**
 * JPA/PostgreSQL implementation of tb resource info dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
public class JpaTbResourceInfoDao extends JpaAbstractDao<TbResourceInfoEntity, TbResourceInfo> implements TbResourceInfoDao {

    @Autowired
    private TbResourceInfoRepository resourceInfoRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<TbResourceInfoEntity> getEntityClass() {
        return TbResourceInfoEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<TbResourceInfoEntity, UUID> getRepository() {
        return resourceInfoRepository;
    }
    /**
     * Finds all tenant resources by tenant id.
     *
     * @param filter filter ({@link TbResourceInfoFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TbResourceInfo> findAllTenantResourcesByTenantId(TbResourceInfoFilter filter, PageLink pageLink) {
        Set<ResourceType> resourceTypes = filter.getResourceTypes();
        if (CollectionsUtil.isEmpty(resourceTypes)) {
            resourceTypes = EnumSet.allOf(ResourceType.class);
        }
        Set<ResourceSubType> resourceSubTypes = filter.getResourceSubTypes();
        return DaoUtil.toPageData(resourceInfoRepository
                .findAllTenantResourcesByTenantId(
                        filter.getTenantId().getId(), TenantId.NULL_UUID,
                        resourceTypes.stream().map(Enum::name).collect(Collectors.toList()),
                        CollectionsUtil.isEmpty(resourceSubTypes) ? null :
                                resourceSubTypes.stream().map(Enum::name).collect(Collectors.toList()),
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds tenant resources by tenant id.
     *
     * @param filter filter ({@link TbResourceInfoFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<TbResourceInfo> findTenantResourcesByTenantId(TbResourceInfoFilter filter, PageLink pageLink) {
        Set<ResourceType> resourceTypes = filter.getResourceTypes();
        if (CollectionsUtil.isEmpty(resourceTypes)) {
            resourceTypes = EnumSet.allOf(ResourceType.class);
        }
        Set<ResourceSubType> resourceSubTypes = filter.getResourceSubTypes();
        return DaoUtil.toPageData(resourceInfoRepository
                .findTenantResourcesByTenantId(
                        filter.getTenantId().getId(),
                        resourceTypes.stream().map(Enum::name).collect(Collectors.toList()),
                        CollectionsUtil.isEmpty(resourceSubTypes) ? null :
                                resourceSubTypes.stream().map(Enum::name).collect(Collectors.toList()),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceKey resource key ({@link String})
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResourceInfo findByTenantIdAndKey(TenantId tenantId, ResourceType resourceType, String resourceKey) {
        return DaoUtil.getData(resourceInfoRepository.findByTenantIdAndResourceTypeAndResourceKey(tenantId.getId(), resourceType.name(), resourceKey));
    }
    /**
     * Exists by tenant id and resource type and resource key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceKey resource key ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean existsByTenantIdAndResourceTypeAndResourceKey(TenantId tenantId, ResourceType resourceType, String resourceKey) {
        return resourceInfoRepository.existsByTenantIdAndResourceTypeAndResourceKey(tenantId.getId(), resourceType.name(), resourceKey);
    }
    /**
     * Finds keys by tenant id and resource type and resource key prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param prefix prefix ({@link String})
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Set<String> findKeysByTenantIdAndResourceTypeAndResourceKeyPrefix(TenantId tenantId, ResourceType resourceType, String prefix) {
        return resourceInfoRepository.findKeysByTenantIdAndResourceTypeAndResourceKeyStartingWith(tenantId.getId(), resourceType.name(), prefix);
    }
    /**
     * Finds by tenant id and etag and key starting with.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param etag etag ({@link String})
     * @param query filter and sort query definition
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<TbResourceInfo> findByTenantIdAndEtagAndKeyStartingWith(TenantId tenantId, String etag, String query) {
        return DaoUtil.convertDataList(resourceInfoRepository.findByTenantIdAndEtagAndResourceKeyStartingWith(tenantId.getId(), etag, query));
    }
    /**
     * Finds system or tenant resource by etag.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param etag etag ({@link String})
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResourceInfo findSystemOrTenantResourceByEtag(TenantId tenantId, ResourceType resourceType, String etag) {
        return DaoUtil.getData(resourceInfoRepository.findSystemOrTenantResourceByEtag(tenantId.getId(), resourceType.name(), etag));
    }
    /**
     * Exists by public resource key.
     *
     * @param resourceType resource type ({@link ResourceType})
     * @param publicResourceKey public resource key ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean existsByPublicResourceKey(ResourceType resourceType, String publicResourceKey) {
        return resourceInfoRepository.existsByResourceTypeAndPublicResourceKey(resourceType.name(), publicResourceKey);
    }
    /**
     * Finds public resource by key.
     *
     * @param resourceType resource type ({@link ResourceType})
     * @param publicResourceKey public resource key ({@link String})
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResourceInfo findPublicResourceByKey(ResourceType resourceType, String publicResourceKey) {
        return DaoUtil.getData(resourceInfoRepository.findByResourceTypeAndPublicResourceKeyAndIsPublicTrue(resourceType.name(), publicResourceKey));
    }
    /**
     * Finds system or tenant resources by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceIds resource ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<TbResourceInfo> findSystemOrTenantResourcesByIds(TenantId tenantId, List<TbResourceId> resourceIds) {
        return DaoUtil.convertDataList(resourceInfoRepository.findSystemOrTenantResourcesByIdIn(tenantId.getId(), TenantId.NULL_UUID, toUUIDs(resourceIds)));
    }
}
