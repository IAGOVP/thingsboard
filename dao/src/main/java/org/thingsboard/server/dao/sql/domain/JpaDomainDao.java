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
package org.thingsboard.server.dao.sql.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.domain.Domain;
import org.thingsboard.server.common.data.domain.DomainOauth2Client;
import org.thingsboard.server.common.data.id.DomainId;
import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.domain.DomainDao;
import org.thingsboard.server.dao.model.sql.DomainEntity;
import org.thingsboard.server.dao.model.sql.DomainOauth2ClientCompositeKey;
import org.thingsboard.server.dao.model.sql.DomainOauth2ClientEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of domain dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@RequiredArgsConstructor
@SqlDao
public class JpaDomainDao extends JpaAbstractDao<DomainEntity, Domain> implements DomainDao {

    private final DomainRepository domainRepository;
    private final DomainOauth2ClientRepository domainOauth2ClientRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<DomainEntity> getEntityClass() {
        return DomainEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<DomainEntity, UUID> getRepository() {
        return domainRepository;
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
    public PageData<Domain> findByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(domainRepository.findByTenantId(tenantId.getId(), pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Counts domain by tenant id and oauth2enabled.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param enabled enabled
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public int countDomainByTenantIdAndOauth2Enabled(TenantId tenantId, boolean enabled) {
        return domainRepository.countByTenantIdAndOauth2Enabled(tenantId.getId(), enabled);
    }

    
    /**
     * Finds oauth2clients by domain id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domainId domain id ({@link DomainId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<DomainOauth2Client> findOauth2ClientsByDomainId(TenantId tenantId, DomainId domainId) {
        return DaoUtil.convertDataList(domainOauth2ClientRepository.findAllByDomainId(domainId.getId()));
    }

    
    /**
     * Add oauth2client.
     *
     * @param domainOauth2Client domain oauth2client ({@link DomainOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void addOauth2Client(DomainOauth2Client domainOauth2Client) {
        domainOauth2ClientRepository.save(new DomainOauth2ClientEntity(domainOauth2Client));
    }

    
    /**
     * Removes oauth2client.
     *
     * @param domainOauth2Client domain oauth2client ({@link DomainOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void removeOauth2Client(DomainOauth2Client domainOauth2Client) {
        domainOauth2ClientRepository.deleteById(new DomainOauth2ClientCompositeKey(domainOauth2Client.getDomainId().getId(),
                domainOauth2Client.getOAuth2ClientId().getId()));
    }

    
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteByTenantId(TenantId tenantId) {
        domainRepository.deleteByTenantId(tenantId.getId());
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.DOMAIN;
    }
}

