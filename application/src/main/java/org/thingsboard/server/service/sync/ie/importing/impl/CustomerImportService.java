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
package org.thingsboard.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports customer entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class CustomerImportService extends BaseEntityImportService<CustomerId, Customer, EntityExportData<Customer>> {

    private final CustomerService customerService;
    private final CustomerDao customerDao;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customer customer ({@link Customer})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, Customer customer, IdProvider idProvider) {
        customer.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param customer customer ({@link Customer})
     * @param old old ({@link Customer})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link Customer}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Customer prepare(EntitiesImportCtx ctx, Customer customer, Customer old, EntityExportData<Customer> exportData, IdProvider idProvider) {
        if (customer.isPublic()) {
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(ctx.getTenantId());
            publicCustomer.setExternalId(customer.getExternalId());
            return publicCustomer;
        } else {
            return customer;
        }
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param customer customer ({@link Customer})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link Customer}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Customer saveOrUpdate(EntitiesImportCtx ctx, Customer customer, EntityExportData<Customer> exportData, IdProvider idProvider, CompareResult compareResult) {
        if (!customer.isPublic()) {
            return customerService.saveCustomer(customer);
        } else {
            return customerDao.save(ctx.getTenantId(), customer);
        }
    }
    /**
     * Deep copy.
     *
     * @param customer customer ({@link Customer})
     * @return {@link Customer}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Customer deepCopy(Customer customer) {
        return new Customer(customer);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.CUSTOMER;
    }

}
