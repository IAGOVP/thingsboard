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
package org.thingsboard.server.dao.customer;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.Optional;

/**
 * Service API for customer persistence and domain operations.
 */
public interface CustomerService extends EntityDaoService {

    /**
     * Finds customer by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @return {@link Customer}
     */
    Customer findCustomerById(TenantId tenantId, CustomerId customerId);

    /**
     * Finds customer by tenant id and title.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @return optional {@link Customer}, empty if not found
     */
    Optional<Customer> findCustomerByTenantIdAndTitle(TenantId tenantId, String title);

    /**
     * Finds customer by tenant id and title async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @return future completing with optional {@link Customer}, empty if not found
     */
    ListenableFuture<Optional<Customer>> findCustomerByTenantIdAndTitleAsync(TenantId tenantId, String title);

    /**
     * Finds customer by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @return future completing with {@link Customer}
     */
    ListenableFuture<Customer> findCustomerByIdAsync(TenantId tenantId, CustomerId customerId);

    /**
     * Saves or persists customer.
     *
     * @param customer customer ({@link Customer})
     * @return {@link Customer}
     */
    Customer saveCustomer(Customer customer);

    /**
     * Saves or persists customer.
     *
     * @param customer customer ({@link Customer})
     * @param nameConflictStrategy behavior when an entity with the same name already exists
     * @return {@link Customer}
     */
    Customer saveCustomer(Customer customer, NameConflictStrategy nameConflictStrategy);

    /**
     * Deletes customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     */
    void deleteCustomer(TenantId tenantId, CustomerId customerId);

    /**
     * Finds or create public customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Customer}
     */
    Customer findOrCreatePublicCustomer(TenantId tenantId);

    /**
     * Finds public customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Customer}
     */
    Customer findPublicCustomer(TenantId tenantId);

    /**
     * Finds customers by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Customer> findCustomersByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Deletes customers by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteCustomersByTenantId(TenantId tenantId);

    /**
     * Finds customers by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerIds customer ids ({@link List})
     * @return {@link List}
     */
    List<Customer> findCustomersByTenantIdAndIds(TenantId tenantId, List<CustomerId> customerIds);

}
