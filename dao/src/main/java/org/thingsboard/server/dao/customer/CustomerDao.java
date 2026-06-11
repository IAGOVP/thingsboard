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

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for customer.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (customer entity persistence and caching).
 */

public interface CustomerDao extends Dao<Customer>, TenantEntityDao<Customer>, ExportableEntityDao<CustomerId, Customer> {

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customer customer ({@link Customer})
     * @return {@link Customer}
     * @throws Exception if an unexpected error occurs during processing
     */

    Customer save(TenantId tenantId, Customer customer);

    
    /**
     * Finds customers by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Customer> findCustomersByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds customer by tenant id and title.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @return optional {@link Customer}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<Customer> findCustomerByTenantIdAndTitle(UUID tenantId, String title);

    
    /**
     * Finds public customer by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return optional {@link Customer}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<Customer> findPublicCustomerByTenantId(UUID tenantId);


    
    /**
     * Finds customers with the same title.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Customer> findCustomersWithTheSameTitle(PageLink pageLink);
    /**
     * Finds customers by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerIds customer ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<Customer> findCustomersByTenantIdAndIds(UUID tenantId, List<UUID> customerIds);

}
