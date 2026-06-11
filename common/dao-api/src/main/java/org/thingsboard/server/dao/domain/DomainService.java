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
package org.thingsboard.server.dao.domain;

import org.thingsboard.server.common.data.domain.Domain;
import org.thingsboard.server.common.data.domain.DomainInfo;
import org.thingsboard.server.common.data.id.DomainId;
import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for domain persistence and domain operations.
 */
public interface DomainService extends EntityDaoService {

    /**
     * Saves or persists domain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domain domain ({@link Domain})
     * @return {@link Domain}
     */
    Domain saveDomain(TenantId tenantId, Domain domain);

    /**
     * Deletes domain by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domainId domain id ({@link DomainId})
     */
    void deleteDomainById(TenantId tenantId, DomainId domainId);

    /**
     * Finds domain by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domainId domain id ({@link DomainId})
     * @return {@link Domain}
     */
    Domain findDomainById(TenantId tenantId, DomainId domainId);

    /**
     * Finds domain infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<DomainInfo> findDomainInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds domain info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domainId domain id ({@link DomainId})
     * @return {@link DomainInfo}
     */
    DomainInfo findDomainInfoById(TenantId tenantId, DomainId domainId);

    /**
     * Is oauth2enabled.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the boolean result
     */
    boolean isOauth2Enabled(TenantId tenantId);

    /**
     * Updates oauth2clients.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domainId domain id ({@link DomainId})
     * @param oAuth2ClientIds o auth2client ids ({@link List})
     */
    void updateOauth2Clients(TenantId tenantId, DomainId domainId, List<OAuth2ClientId> oAuth2ClientIds);

    /**
     * Deletes domains by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteDomainsByTenantId(TenantId tenantId);
}
