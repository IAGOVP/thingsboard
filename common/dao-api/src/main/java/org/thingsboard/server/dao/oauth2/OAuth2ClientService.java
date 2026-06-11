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
import org.thingsboard.server.common.data.oauth2.OAuth2ClientInfo;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientLoginInfo;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.UUID;

/**
 * Service API for oauth2client persistence and domain operations.
 */
public interface OAuth2ClientService extends EntityDaoService {

    /**
     * Finds oauth2client login infos by domain name.
     *
     * @param domainName domain name ({@link String})
     * @return {@link List}
     */
    List<OAuth2ClientLoginInfo> findOAuth2ClientLoginInfosByDomainName(String domainName);

    /**
     * Finds oauth2client login infos by mobile pkg name and platform type.
     *
     * @param pkgName pkg name ({@link String})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link List}
     */
    List<OAuth2ClientLoginInfo> findOAuth2ClientLoginInfosByMobilePkgNameAndPlatformType(String pkgName, PlatformType platformType);

    /**
     * Finds oauth2clients by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     */
    List<OAuth2Client> findOAuth2ClientsByTenantId(TenantId tenantId);

    /**
     * Saves or persists oauth2client.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oAuth2Client o auth2client ({@link OAuth2Client})
     * @return {@link OAuth2Client}
     */
    OAuth2Client saveOAuth2Client(TenantId tenantId, OAuth2Client oAuth2Client);

    /**
     * Finds oauth2client by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param providerId provider id ({@link OAuth2ClientId})
     * @return {@link OAuth2Client}
     */
    OAuth2Client findOAuth2ClientById(TenantId tenantId, OAuth2ClientId providerId);

    /**
     * Finds app secret.
     *
     * @param oAuth2ClientId o auth2client id ({@link OAuth2ClientId})
     * @param pkgName pkg name ({@link String})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link String}
     */
    String findAppSecret(OAuth2ClientId oAuth2ClientId, String pkgName, PlatformType platformType);

    /**
     * Deletes oauth2client by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oAuth2ClientId o auth2client id ({@link OAuth2ClientId})
     */
    void deleteOAuth2ClientById(TenantId tenantId, OAuth2ClientId oAuth2ClientId);

    /**
     * Deletes oauth2clients by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteOauth2ClientsByTenantId(TenantId tenantId);

    /**
     * Finds oauth2client infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<OAuth2ClientInfo> findOAuth2ClientInfosByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds oauth2client infos by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oAuth2ClientIds o auth2client ids ({@link List})
     * @return {@link List}
     */
    List<OAuth2ClientInfo> findOAuth2ClientInfosByIds(TenantId tenantId, List<OAuth2ClientId> oAuth2ClientIds);

    /**
     * Is propagate oauth2client to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oAuth2ClientId o auth2client id ({@link OAuth2ClientId})
     * @return the boolean result
     */
    boolean isPropagateOAuth2ClientToEdge(TenantId tenantId, OAuth2ClientId oAuth2ClientId);

}
