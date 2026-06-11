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
package org.thingsboard.server.dao.sql.ai;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ai.AiModel;
import org.thingsboard.server.common.data.id.AiModelId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.ai.AiModelDao;
import org.thingsboard.server.dao.model.sql.AiModelEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;
/**
 * JPA/PostgreSQL implementation of ai model dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@SqlDao
@Component
@RequiredArgsConstructor
class JpaAiModelDao extends JpaAbstractDao<AiModelEntity, AiModel> implements AiModelDao {

    private final AiModelRepository aiModelRepository;

    
    /**
     * Finds by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return optional {@link AiModel}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<AiModel> findByTenantIdAndId(TenantId tenantId, AiModelId modelId) {
        return aiModelRepository.findByTenantIdAndId(tenantId.getId(), modelId.getId()).map(DaoUtil::getData);
    }

    
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link AiModel}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AiModel findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(aiModelRepository.findByTenantIdAndName(tenantId, name));
    }

    
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link AiModel}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AiModel findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(aiModelRepository.findByTenantIdAndExternalId(tenantId, externalId));
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
    public PageData<AiModel> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
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
    public PageData<AiModel> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(aiModelRepository.findByTenantId(
                tenantId, StringUtils.defaultIfEmpty(pageLink.getTextSearch(), null), toPageRequest(pageLink))
        );
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
    public PageData<AiModelId> findIdsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(aiModelRepository.findIdsByTenantId(tenantId, toPageRequest(pageLink)).map(AiModelId::new));
    }

    private static PageRequest toPageRequest(PageLink pageLink) {
        Sort sort;
        SortOrder sortOrder = pageLink.getSortOrder();
        if (sortOrder == null) {
            sort = Sort.by(Sort.Direction.ASC, "id");
        } else {
            sort = JpaSort.unsafe(
                    Sort.Direction.fromString(sortOrder.getDirection().name()),
                    AiModelEntity.COLUMN_MAP.getOrDefault(sortOrder.getProperty(), sortOrder.getProperty())
            ).and(Sort.by(Sort.Direction.ASC, "id"));
        }
        return PageRequest.of(pageLink.getPage(), pageLink.getPageSize(), sort);
    }

    
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link AiModelId})
     * @return {@link AiModelId}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AiModelId getExternalIdByInternal(AiModelId internalId) {
        return aiModelRepository.getExternalIdById(internalId.getId()).map(AiModelId::new).orElse(null);
    }

    
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Long countByTenantId(TenantId tenantId) {
        return aiModelRepository.countByTenantId(tenantId.getId());
    }

    
    /**
     * Deletes by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public boolean deleteById(TenantId tenantId, AiModelId modelId) {
        return aiModelRepository.deleteByIdIn(Set.of(modelId.getId())) > 0;
    }

    
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Set<AiModelId> deleteByTenantId(TenantId tenantId) {
        return aiModelRepository.deleteByTenantId(tenantId.getId()).stream()
                .map(AiModelId::new)
                .collect(toSet());
    }

    
    /**
     * Deletes by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param modelId model id ({@link AiModelId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public boolean deleteByTenantIdAndId(TenantId tenantId, AiModelId modelId) {
        return aiModelRepository.deleteByTenantIdAndIdIn(tenantId.getId(), Set.of(modelId.getId())) > 0;
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.AI_MODEL;
    }

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<AiModelEntity> getEntityClass() {
        return AiModelEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<AiModelEntity, UUID> getRepository() {
        return aiModelRepository;
    }

}
