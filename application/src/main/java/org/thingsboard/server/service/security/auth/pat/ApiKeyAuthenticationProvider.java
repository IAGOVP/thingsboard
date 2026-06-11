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
package org.thingsboard.server.service.security.auth.pat;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.dao.pat.ApiKeyService;
import org.thingsboard.server.service.security.auth.AbstractAuthenticationProvider;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.token.ApiKeyAuthRequest;
import org.thingsboard.server.service.user.cache.UserAuthDetailsCache;
/**
 * Spring Security authentication provider for personal access token (API key) authentication.
 *
 * <p><b>Responsibilities:</b> Spring-managed service component. Integrates with Spring Security filter chain.
 */

@Component
public class ApiKeyAuthenticationProvider extends AbstractAuthenticationProvider {

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthenticationProvider(ApiKeyService apiKeyService, UserAuthDetailsCache userAuthDetailsCache) {
        super(null, userAuthDetailsCache);
        this.apiKeyService = apiKeyService;
    }
    /**
     * Authenticates credentials and returns a populated security principal.
     *
     * @param authentication authentication (Authentication)
     * @return {@link Authentication} result
     * @throws AuthenticationException if the operation fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiKeyAuthRequest apiKeyAuthRequest = (ApiKeyAuthRequest) authentication.getCredentials();
        SecurityUser securityUser = authenticate(apiKeyAuthRequest.apiKey());
        return new ApiKeyAuthenticationToken(securityUser);
    }

    /**
     * Authenticates credentials and returns a populated security principal.
     *
     * @param key key (String)
     * @return {@link SecurityUser} result
     */

    public SecurityUser authenticate(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BadCredentialsException("Empty API key");
        }
        ApiKey apiKey = apiKeyService.findApiKeyByValue(key);
        if (apiKey == null) {
            throw new BadCredentialsException("User not found for the provided API key");
        }
        if (!apiKey.isEnabled()) {
            throw new DisabledException("API key auth is not active");
        }
        if (apiKey.getExpirationTime() != 0 && apiKey.getExpirationTime() < System.currentTimeMillis()) {
            throw new CredentialsExpiredException("API key is expired");
        }
        return super.authenticateByUserId(apiKey.getTenantId(), apiKey.getUserId());
    }
    /**
     * Indicates whether this provider can authenticate the given authentication token type.
     *
     * @param authentication authentication (Class<?>)
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
