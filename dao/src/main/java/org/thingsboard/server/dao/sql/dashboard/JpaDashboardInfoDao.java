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
package org.thingsboard.server.dao.sql.dashboard;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.dashboard.DashboardInfoDao;
import org.thingsboard.server.dao.model.sql.DashboardInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA/PostgreSQL implementation of dashboard info dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */

@Slf4j
@Component
@SqlDao
public class JpaDashboardInfoDao extends JpaAbstractDao<DashboardInfoEntity, DashboardInfo> implements DashboardInfoDao {

    @Autowired
    private DashboardInfoRepository dashboardInfoRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<DashboardInfoEntity> getEntityClass() {
        return DashboardInfoEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<DashboardInfoEntity, UUID> getRepository() {
        return dashboardInfoRepository;
    }

    
    /**
     * Finds dashboards by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DashboardInfo> findDashboardsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(dashboardInfoRepository
                .findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds mobile dashboards by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DashboardInfo> findMobileDashboardsByTenantId(UUID tenantId, PageLink pageLink) {
        List<SortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(new SortOrder("mobileOrder", SortOrder.Direction.ASC));
        if (pageLink.getSortOrder() != null) {
            sortOrders.add(pageLink.getSortOrder());
        }
        return DaoUtil.toPageData(dashboardInfoRepository
                .findMobileByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, sortOrders)));
    }

    
    /**
     * Finds dashboards by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DashboardInfo> findDashboardsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(dashboardInfoRepository
                .findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds mobile dashboards by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DashboardInfo> findMobileDashboardsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        List<SortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(new SortOrder("mobileOrder", SortOrder.Direction.ASC));
        if (pageLink.getSortOrder() != null) {
            sortOrders.add(pageLink.getSortOrder());
        }
        return DaoUtil.toPageData(dashboardInfoRepository
                .findMobileByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, sortOrders)));
    }

    
    /**
     * Finds dashboards by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DashboardInfo> findDashboardsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink) {
        log.debug("Try to find dashboards by tenantId [{}], edgeId [{}] and pageLink [{}]", tenantId, edgeId, pageLink);
        return DaoUtil.toPageData(dashboardInfoRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds first by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link DashboardInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DashboardInfo findFirstByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(dashboardInfoRepository.findFirstByTenantIdAndTitle(tenantId, name));
    }

    
    /**
     * Finds title by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link UUID})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public String findTitleById(UUID tenantId, UUID dashboardId) {
        return dashboardInfoRepository.findTitleByTenantIdAndId(tenantId, dashboardId);
    }

    
    /**
     * Finds dashboards by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardIds dashboard ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<DashboardInfo> findDashboardsByIds(UUID tenantId, List<UUID> dashboardIds) {
        return DaoUtil.convertDataList(dashboardInfoRepository.findByIdIn(dashboardIds));
    }

    
    /**
     * Finds by tenant and image link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageLink image link ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<DashboardInfo> findByTenantAndImageLink(TenantId tenantId, String imageLink, int limit) {
        return DaoUtil.convertDataList(dashboardInfoRepository.findByTenantAndImageLink(tenantId.getId(), imageLink, limit));
    }

    
    /**
     * Finds by image link.
     *
     * @param imageLink image link ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<DashboardInfo> findByImageLink(String imageLink, int limit) {
        return DaoUtil.convertDataList(dashboardInfoRepository.findByImageLink(imageLink, limit));
    }

    
    /**
     * Finds by tenant id and resource.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param reference reference ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityInfo> findByTenantIdAndResource(TenantId tenantId, String reference, int limit) {
        return dashboardInfoRepository.findDashboardInfosByTenantIdAndResourceLink(tenantId.getId(), reference, PageRequest.of(0, limit));
    }

    
    /**
     * Finds by resource.
     *
     * @param reference reference ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityInfo> findByResource(String reference, int limit) {
        return dashboardInfoRepository.findDashboardInfosByResourceLink(reference, PageRequest.of(0, limit));
    }

}
