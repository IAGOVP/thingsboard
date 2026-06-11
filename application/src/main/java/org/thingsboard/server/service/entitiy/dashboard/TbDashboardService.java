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
package org.thingsboard.server.service.entitiy.dashboard;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.service.entitiy.SimpleTbEntityService;

import java.util.Set;

/**

 * Application-layer service API for dashboard entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbDashboardService extends SimpleTbEntityService<Dashboard> {
/**
 * Assigns dashboard to customer.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    Dashboard assignDashboardToCustomer(Dashboard dashboard, Customer customer, User user) throws ThingsboardException;
/**
 * Assigns dashboard to public customer.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard assignDashboardToPublicCustomer(Dashboard dashboard, User user) throws ThingsboardException;
/**
 * Unassigns dashboard from public customer.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard unassignDashboardFromPublicCustomer(Dashboard dashboard, User user) throws ThingsboardException;
/**
 * Updates dashboard customers.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param customerIds customer ids ({@link Set})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard updateDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws ThingsboardException;
/**
 * Add dashboard customers.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param customerIds customer ids ({@link Set})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard addDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws ThingsboardException;
/**
 * Removes dashboard customers.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param customerIds customer ids ({@link Set})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard removeDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws ThingsboardException;
/**
 * Asign dashboard to edge.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param dashboardId dashboard id ({@link DashboardId})
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard asignDashboardToEdge(TenantId tenantId, DashboardId dashboardId, Edge edge, User user) throws ThingsboardException;
/**
 * Unassigns dashboard from edge.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard unassignDashboardFromEdge(Dashboard dashboard, Edge edge, User user) throws ThingsboardException;
/**
 * Unassigns dashboard from customer.
 *
 * @param dashboard dashboard ({@link Dashboard})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Dashboard}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Dashboard unassignDashboardFromCustomer(Dashboard dashboard, Customer customer, User user) throws ThingsboardException;

}
