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
package org.thingsboard.server.dao.sql.edge;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.edqs.fields.EdgeFields;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.edge.EdgeDao;
import org.thingsboard.server.dao.model.sql.EdgeEntity;
import org.thingsboard.server.dao.model.sql.EdgeInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.DaoUtil.convertTenantEntityTypesToDto;
/**
 * JPA/PostgreSQL implementation of edge dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@Slf4j
@SqlDao
public class JpaEdgeDao extends JpaAbstractDao<EdgeEntity, Edge> implements EdgeDao {

    @Autowired
    private EdgeRepository edgeRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<EdgeEntity> getEntityClass() {
        return EdgeEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<EdgeEntity, UUID> getRepository() {
        return edgeRepository;
    }

    
    /**
     * Finds edge info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @return {@link EdgeInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EdgeInfo findEdgeInfoById(TenantId tenantId, UUID edgeId) {
        return DaoUtil.getData(edgeRepository.findEdgeInfoById(edgeId));
    }

    
    /**
     * Finds active edges.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Edge> findActiveEdges(PageLink pageLink) {
        return DaoUtil.toPageData(edgeRepository.findActiveEdges(DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds edge ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EdgeId> findEdgeIdsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(
                edgeRepository.findIdsByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink))).mapData(EdgeId::fromUUID);
    }

    
    /**
     * Finds edges by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Edge> findEdgesByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds edges by tenant id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeIds edge ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<Edge>> findEdgesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> edgeIds) {
        return service.submit(() -> DaoUtil.convertDataList(edgeRepository.findEdgesByTenantIdAndIdIn(tenantId, edgeIds)));
    }

    
    /**
     * Finds edges by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Edge> findEdgesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds edges by tenant id customer id and ids async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param edgeIds edge ids ({@link List})
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<Edge>> findEdgesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> edgeIds) {
        return service.submit(() -> DaoUtil.convertDataList(
                edgeRepository.findEdgesByTenantIdAndCustomerIdAndIdIn(tenantId, customerId, edgeIds)));
    }

    
    /**
     * Finds edge by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return optional {@link Edge}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<Edge> findEdgeByTenantIdAndName(UUID tenantId, String name) {
        Edge edge = DaoUtil.getData(edgeRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(edge);
    }

    
    /**
     * Finds edges by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Edge> findEdgesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds edges by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Edge> findEdgesByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds edge infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findEdgeInfosByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EdgeInfoEntity.edgeInfoColumnMap)));
    }

    
    /**
     * Finds edge infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findEdgeInfosByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EdgeInfoEntity.edgeInfoColumnMap)));
    }

    
    /**
     * Finds tenant edge types async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantEdgeTypesAsync(UUID tenantId) {
        return service.submit(() -> convertTenantEntityTypesToDto(tenantId, EntityType.EDGE, edgeRepository.findTenantEdgeTypes(tenantId)));
    }

    
    /**
     * Finds edge infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EdgeInfo> findEdgeInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findEdgeInfosByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EdgeInfoEntity.edgeInfoColumnMap)));
    }

    
    /**
     * Finds edge infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EdgeInfo> findEdgeInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                edgeRepository.findEdgeInfosByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EdgeInfoEntity.edgeInfoColumnMap)));
    }

    
    /**
     * Finds by routing key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param routingKey routing key ({@link String})
     * @return optional {@link Edge}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<Edge> findByRoutingKey(UUID tenantId, String routingKey) {
        Edge edge = DaoUtil.getData(edgeRepository.findByRoutingKey(routingKey));
        return Optional.ofNullable(edge);
    }

    
    /**
     * Finds edges by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param entityType entity type discriminator
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Edge> findEdgesByTenantIdAndEntityId(UUID tenantId, UUID entityId, EntityType entityType, PageLink pageLink) {
        log.debug("Try to find edges by tenantId [{}], entityId [{}], entityType [{}], pageLink [{}]", tenantId, entityId, entityType, pageLink);
        return DaoUtil.toPageData(
                edgeRepository.findByTenantIdAndEntityId(
                        tenantId,
                        entityId,
                        entityType.name(),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds edge ids by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param entityType entity type discriminator
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EdgeId> findEdgeIdsByTenantIdAndEntityId(UUID tenantId, UUID entityId, EntityType entityType, PageLink pageLink) {
        log.debug("Try to find edge ids by tenantId [{}], entityId [{}], entityType [{}], pageLink [{}]", tenantId, entityId, entityType, pageLink);
        return DaoUtil.pageToPageData(
                edgeRepository.findIdsByTenantIdAndEntityId(
                        tenantId,
                        entityId,
                        entityType.name(),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink))).mapData(EdgeId::fromUUID);
    }

    
    /**
     * Finds edges by tenant profile id.
     *
     * @param tenantProfileId tenant profile id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Edge> findEdgesByTenantProfileId(UUID tenantProfileId, PageLink pageLink) {
        log.debug("Try to find edges by tenantProfileId [{}], pageLink [{}]", tenantProfileId, pageLink);
        return DaoUtil.toPageData(
                edgeRepository.findByTenantProfileId(
                        tenantProfileId,
                        DaoUtil.toPageable(pageLink)));
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
        return edgeRepository.countByTenantId(tenantId.getId());
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
    public PageData<Edge> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findEdgesByTenantId(tenantId.getId(), pageLink);
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
    public List<EdgeFields> findNextBatch(UUID id, int batchSize) {
        return edgeRepository.findNextBatch(id, Limit.of(batchSize));
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.EDGE;
    }

}
