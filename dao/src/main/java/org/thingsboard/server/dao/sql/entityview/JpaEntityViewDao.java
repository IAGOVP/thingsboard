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
package org.thingsboard.server.dao.sql.entityview;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.EntityViewInfo;
import org.thingsboard.server.common.data.edqs.fields.EntityViewFields;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.entityview.EntityViewDao;
import org.thingsboard.server.dao.model.sql.EntityViewEntity;
import org.thingsboard.server.dao.model.sql.EntityViewInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.DaoUtil.convertTenantEntityTypesToDto;
/**
 * JPA/PostgreSQL implementation of entity view dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@Slf4j
@SqlDao
public class JpaEntityViewDao extends JpaAbstractDao<EntityViewEntity, EntityView> implements EntityViewDao, TenantEntityDao<EntityView> {

    @Autowired
    private EntityViewRepository entityViewRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<EntityViewEntity> getEntityClass() {
        return EntityViewEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<EntityViewEntity, UUID> getRepository() {
        return entityViewRepository;
    }

    
    /**
     * Finds entity view info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link UUID})
     * @return {@link EntityViewInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityViewInfo findEntityViewInfoById(TenantId tenantId, UUID entityViewId) {
        return DaoUtil.getData(entityViewRepository.findEntityViewInfoById(entityViewId));
    }

    
    /**
     * Finds entity views by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds entity view infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    
    /**
     * Finds entity views by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds entity view infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    
    /**
     * Finds entity view by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return optional {@link EntityView}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<EntityView> findEntityViewByTenantIdAndName(UUID tenantId, String name) {
        return Optional.ofNullable(
                DaoUtil.getData(entityViewRepository.findByTenantIdAndName(tenantId, name)));
    }

    
    /**
     * Finds entity views by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndCustomerId(UUID tenantId,
                                                                       UUID customerId,
                                                                       PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)
                ));
    }

    
    /**
     * Finds entity view infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    
    /**
     * Finds entity views by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)
                ));
    }

    
    /**
     * Finds entity view infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    
    /**
     * Finds entity views by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityView> findEntityViewsByTenantIdAndEntityId(UUID tenantId, UUID entityId) {
        return DaoUtil.convertDataList(
                entityViewRepository.findAllByTenantIdAndEntityId(tenantId, entityId));
    }

    
    /**
     * Exists by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public boolean existsByTenantIdAndEntityId(UUID tenantId, UUID entityId) {
        return entityViewRepository.existsByTenantIdAndEntityId(tenantId, entityId);
    }

    
    /**
     * Finds tenant entity view types async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantEntityViewTypesAsync(UUID tenantId) {
        return service.submit(() -> convertTenantEntityTypesToDto(tenantId, EntityType.ENTITY_VIEW, entityViewRepository.findTenantEntityViewTypes(tenantId)));
    }

    
    /**
     * Finds entity views by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink) {
        log.debug("Try to find entity views by tenantId [{}], edgeId [{}] and pageLink [{}]", tenantId, edgeId, pageLink);
        return DaoUtil.toPageData(entityViewRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds entity views by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewIds entity view ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityView> findEntityViewsByTenantIdAndIds(UUID tenantId, List<UUID> entityViewIds) {
        return DaoUtil.convertDataList(entityViewRepository.findEntityViewsByTenantIdAndIdIn(tenantId, entityViewIds));
    }

    
    /**
     * Finds entity views by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink) {
        log.debug("Try to find entity views by tenantId [{}], edgeId [{}], type [{}] and pageLink [{}]", tenantId, edgeId, type, pageLink);
        return DaoUtil.toPageData(entityViewRepository
                .findByTenantIdAndEdgeIdAndType(
                        tenantId,
                        edgeId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(entityViewRepository.findByTenantIdAndExternalId(tenantId, externalId));
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
    public PageData<EntityView> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findEntityViewsByTenantId(tenantId, pageLink);
    }

    
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link EntityViewId})
     * @return {@link EntityViewId}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityViewId getExternalIdByInternal(EntityViewId internalId) {
        return Optional.ofNullable(entityViewRepository.getExternalIdById(internalId.getId()))
                .map(EntityViewId::new).orElse(null);
    }

    
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView findByTenantIdAndName(UUID tenantId, String name) {
        return findEntityViewByTenantIdAndName(tenantId, name).orElse(null);
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
    public PageData<EntityView> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
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
    public List<EntityViewFields> findNextBatch(UUID id, int batchSize) {
        return entityViewRepository.findNextBatch(id, Limit.of(batchSize));
    }

    
    /**
     * Finds entity infos by name prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityInfo> findEntityInfosByNamePrefix(TenantId tenantId, String name) {
        return entityViewRepository.findEntityInfosByNamePrefix(tenantId.getId(), name);
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.ENTITY_VIEW;
    }

}
