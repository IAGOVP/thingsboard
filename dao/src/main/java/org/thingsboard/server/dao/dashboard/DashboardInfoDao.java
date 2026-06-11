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
package org.thingsboard.server.dao.dashboard;

import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ImageContainerDao;
import org.thingsboard.server.dao.ResourceContainerDao;

import java.util.List;
import java.util.UUID;

/**
 * Persistence contract for dashboard info.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (dashboard metadata, titles, and assignment).
 */

public interface DashboardInfoDao extends Dao<DashboardInfo>, ImageContainerDao<DashboardInfo>, ResourceContainerDao<DashboardInfo> {

    
    /**
     * Finds dashboards by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DashboardInfo> findDashboardsByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds mobile dashboards by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DashboardInfo> findMobileDashboardsByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds dashboards by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DashboardInfo> findDashboardsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    
    /**
     * Finds mobile dashboards by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DashboardInfo> findMobileDashboardsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    
    /**
     * Finds dashboards by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DashboardInfo> findDashboardsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink);
    /**
     * Finds first by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link DashboardInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    DashboardInfo findFirstByTenantIdAndName(UUID tenantId, String name);
    /**
     * Finds title by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link UUID})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String findTitleById(UUID tenantId, UUID dashboardId);
    /**
     * Finds dashboards by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardIds dashboard ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<DashboardInfo> findDashboardsByIds(UUID tenantId, List<UUID> dashboardIds);

}
