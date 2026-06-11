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
package org.thingsboard.server.dao.sql.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.CustomerFields;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.model.sql.CustomerEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA/PostgreSQL implementation of customer dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */

@Component
@SqlDao
public class JpaCustomerDao extends JpaAbstractDao<CustomerEntity, Customer> implements CustomerDao {

    @Autowired
    private CustomerRepository customerRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<CustomerEntity> getEntityClass() {
        return CustomerEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<CustomerEntity, UUID> getRepository() {
        return customerRepository;
    }

    
    /**
     * Finds customers by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Customer> findCustomersByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(customerRepository.findByTenantId(
                tenantId,
                pageLink.getTextSearch(),
                DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds customer by tenant id and title.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @return optional {@link Customer}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<Customer> findCustomerByTenantIdAndTitle(UUID tenantId, String title) {
        return Optional.ofNullable(DaoUtil.getData(customerRepository.findByTenantIdAndTitle(tenantId, title)));
    }

    
    /**
     * Finds public customer by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return optional {@link Customer}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<Customer> findPublicCustomerByTenantId(UUID tenantId) {
        return Optional.ofNullable(DaoUtil.getData(customerRepository.findPublicCustomerByTenantId(tenantId)));
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
        return customerRepository.countByTenantId(tenantId.getId());
    }

    
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link Customer}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Customer findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(customerRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }

    
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link Customer}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Customer findByTenantIdAndName(UUID tenantId, String name) {
        return findCustomerByTenantIdAndTitle(tenantId, name).orElse(null);
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
    public PageData<Customer> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findCustomersByTenantId(tenantId, pageLink);
    }

    
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link CustomerId})
     * @return {@link CustomerId}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public CustomerId getExternalIdByInternal(CustomerId internalId) {
        return Optional.ofNullable(customerRepository.getExternalIdById(internalId.getId()))
                .map(CustomerId::new).orElse(null);
    }

    
    /**
     * Finds customers with the same title.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Customer> findCustomersWithTheSameTitle(PageLink pageLink) {
        return DaoUtil.toPageData(
                customerRepository.findCustomersWithTheSameTitle(DaoUtil.toPageable(pageLink))
        );
    }

    
    /**
     * Finds customers by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerIds customer ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<Customer> findCustomersByTenantIdAndIds(UUID tenantId, List<UUID> customerIds) {
        return DaoUtil.convertDataList(customerRepository.findCustomersByTenantIdAndIdIn(tenantId, customerIds));
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
    public PageData<Customer> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
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
    public List<CustomerFields> findNextBatch(UUID id, int batchSize) {
        return customerRepository.findNextBatch(id, Limit.of(batchSize));
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
        return customerRepository.findEntityInfosByNamePrefix(tenantId.getId(), name);
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
