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
import org.thingsboard.server.common.data.domain.DomainOauth2Client;
import org.thingsboard.server.common.data.id.DomainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;


/**

 * Persistence contract for domain.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (tenant domain and OAuth2 client bindings).

 */


public interface DomainDao extends Dao<Domain> {
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Domain> findByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Counts domain by tenant id and oauth2enabled.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oauth2Enabled oauth2enabled
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int countDomainByTenantIdAndOauth2Enabled(TenantId tenantId, boolean oauth2Enabled);
    /**
     * Finds oauth2clients by domain id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domainId domain id ({@link DomainId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<DomainOauth2Client> findOauth2ClientsByDomainId(TenantId tenantId, DomainId domainId);
    /**
     * Add oauth2client.
     *
     * @param domainOauth2Client domain oauth2client ({@link DomainOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void addOauth2Client(DomainOauth2Client domainOauth2Client);
    /**
     * Removes oauth2client.
     *
     * @param domainOauth2Client domain oauth2client ({@link DomainOauth2Client})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeOauth2Client(DomainOauth2Client domainOauth2Client);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByTenantId(TenantId tenantId);
}
