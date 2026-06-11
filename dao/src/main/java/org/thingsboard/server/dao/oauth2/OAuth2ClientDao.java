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
package org.thingsboard.server.dao.oauth2;

import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.oauth2.OAuth2Client;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for oauth2client.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (OAuth2 client registration templates).

 */


public interface OAuth2ClientDao extends Dao<OAuth2Client> {
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<OAuth2Client> findByTenantId(UUID tenantId, PageLink pageLink);
    /**
     * Finds enabled by domain name.
     *
     * @param domainName domain name ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<OAuth2Client> findEnabledByDomainName(String domainName);
    /**
     * Finds enabled by pkg name and platform type.
     *
     * @param pkgName pkg name ({@link String})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<OAuth2Client> findEnabledByPkgNameAndPlatformType(String pkgName, PlatformType platformType);
    /**
     * Finds by domain id.
     *
     * @param domainId domain id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<OAuth2Client> findByDomainId(UUID domainId);
    /**
     * Finds by mobile app bundle id.
     *
     * @param mobileAppBundleId mobile app bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<OAuth2Client> findByMobileAppBundleId(UUID mobileAppBundleId);
    /**
     * Finds app secret.
     *
     * @param id entity UUID primary key
     * @param pkgName pkg name ({@link String})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String findAppSecret(UUID id, String pkgName, PlatformType platformType);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByTenantId(UUID tenantId);
    /**
     * Finds by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oAuth2ClientIds o auth2client ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<OAuth2Client> findByIds(UUID tenantId, List<OAuth2ClientId> oAuth2ClientIds);
    /**
     * Is propagate to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oAuth2ClientId o auth2client id ({@link UUID})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean isPropagateToEdge(TenantId tenantId, UUID oAuth2ClientId);

}
