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

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for dashboard persistence and domain operations.
 */
public interface DashboardService extends EntityDaoService {

    /**
     * Finds dashboard by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @return {@link Dashboard}
     */
    Dashboard findDashboardById(TenantId tenantId, DashboardId dashboardId);

    /**
     * Finds dashboard by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @return future completing with {@link Dashboard}
     */
    ListenableFuture<Dashboard> findDashboardByIdAsync(TenantId tenantId, DashboardId dashboardId);

    /**
     * Finds dashboard info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @return {@link DashboardInfo}
     */
    DashboardInfo findDashboardInfoById(TenantId tenantId, DashboardId dashboardId);

    /**
     * Finds dashboard title by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @return {@link String}
     */
    String findDashboardTitleById(TenantId tenantId, DashboardId dashboardId);

    /**
     * Finds dashboard info by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @return future completing with {@link DashboardInfo}
     */
    ListenableFuture<DashboardInfo> findDashboardInfoByIdAsync(TenantId tenantId, DashboardId dashboardId);

    /**
     * Saves or persists dashboard.
     *
     * @param dashboard dashboard ({@link Dashboard})
     * @param doValidate whether to run validation before persist
     * @return {@link Dashboard}
     */
    Dashboard saveDashboard(Dashboard dashboard, boolean doValidate);

    /**
     * Saves or persists dashboard.
     *
     * @param dashboard dashboard ({@link Dashboard})
     * @return {@link Dashboard}
     */
    Dashboard saveDashboard(Dashboard dashboard);

    /**
     * Assigns dashboard to customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @param customerId customer to assign or filter by
     * @return {@link Dashboard}
     */
    Dashboard assignDashboardToCustomer(TenantId tenantId, DashboardId dashboardId, CustomerId customerId);

    /**
     * Unassigns dashboard from customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @param customerId customer to assign or filter by
     * @return {@link Dashboard}
     */
    Dashboard unassignDashboardFromCustomer(TenantId tenantId, DashboardId dashboardId, CustomerId customerId);

    /**
     * Deletes dashboard.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     */
    void deleteDashboard(TenantId tenantId, DashboardId dashboardId);

    /**
     * Finds dashboards by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DashboardInfo> findDashboardsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds mobile dashboards by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DashboardInfo> findMobileDashboardsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Deletes dashboards by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteDashboardsByTenantId(TenantId tenantId);

    /**
     * Finds dashboards by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DashboardInfo> findDashboardsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Finds mobile dashboards by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DashboardInfo> findMobileDashboardsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    /**
     * Unassigns customer dashboards.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void unassignCustomerDashboards(TenantId tenantId, CustomerId customerId);

    /**
     * Updates customer dashboards.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void updateCustomerDashboards(TenantId tenantId, CustomerId customerId);

    /**
     * Assigns dashboard to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Dashboard}
     */
    Dashboard assignDashboardToEdge(TenantId tenantId, DashboardId dashboardId, EdgeId edgeId);

    /**
     * Unassigns dashboard from edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link Dashboard}
     */
    Dashboard unassignDashboardFromEdge(TenantId tenantId, DashboardId dashboardId, EdgeId edgeId);

    /**
     * Finds dashboards by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DashboardInfo> findDashboardsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    /**
     * Finds first dashboard info by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link DashboardInfo}
     */
    DashboardInfo findFirstDashboardInfoByTenantIdAndName(TenantId tenantId, String name);

    /**
     * Finds first dashboard info by tenant id and name async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @return future completing with {@link DashboardInfo}
     */
    ListenableFuture<DashboardInfo> findFirstDashboardInfoByTenantIdAndNameAsync(TenantId tenantId, String name);

    /**
     * Finds tenant dashboards by title.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @return {@link List}
     */
    List<Dashboard> findTenantDashboardsByTitle(TenantId tenantId, String title);

    /**
     * Exists by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardId dashboard id ({@link DashboardId})
     * @return the boolean result
     */
    boolean existsById(TenantId tenantId, DashboardId dashboardId);

    /**
     * Finds all dashboards ids.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DashboardId> findAllDashboardsIds(PageLink pageLink);

    /**
     * Finds dashboard info by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboardIds dashboard ids ({@link List})
     * @return {@link List}
     */
    List<DashboardInfo> findDashboardInfoByIds(TenantId tenantId, List<DashboardId> dashboardIds);

}
