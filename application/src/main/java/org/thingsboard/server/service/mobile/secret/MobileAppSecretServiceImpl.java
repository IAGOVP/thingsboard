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
package org.thingsboard.server.service.mobile.secret;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thingsboard.server.cache.TbCacheValueWrapper;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.security.model.JwtPair;
import org.thingsboard.server.dao.entity.AbstractCachedService;
import org.thingsboard.server.dao.settings.SecuritySettingsService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;

import static org.thingsboard.server.dao.settings.DefaultSecuritySettingsService.DEFAULT_MOBILE_SECRET_KEY_LENGTH;

    /**
     * Spring service component for mobile app secret service impl (mobile app bundles, secrets, and deep-link support).
     */


@Service
@Slf4j
@RequiredArgsConstructor
public class MobileAppSecretServiceImpl extends AbstractCachedService<String, JwtPair, MobileSecretEvictEvent> implements MobileAppSecretService {

    private final JwtTokenFactory tokenFactory;
    private final SecuritySettingsService securitySettingsService;
    /**
     * Generate mobile app secret.
     *
     * @param securityUser security user ({@link SecurityUser})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String generateMobileAppSecret(SecurityUser securityUser) {
        log.trace("Executing generateSecret for user [{}]", securityUser.getId());
        Integer mobileSecretKeyLength = securitySettingsService.getSecuritySettings().getMobileSecretKeyLength();
        String secret = StringUtils.generateSafeToken(mobileSecretKeyLength == null ? DEFAULT_MOBILE_SECRET_KEY_LENGTH : mobileSecretKeyLength);
        cache.put(secret, tokenFactory.createTokenPair(securityUser));
        return secret;
    }
    /**
     * Returns jwt pair.
     *
     * @param secret secret ({@link String})
     * @return {@link JwtPair}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    @Override
    public JwtPair getJwtPair(String secret) throws ThingsboardException {
        TbCacheValueWrapper<JwtPair> jwtPair = cache.get(secret);
        if (jwtPair != null) {
            return jwtPair.get();
        } else {
            throw new ThingsboardException("Jwt token not found or expired!", ThingsboardErrorCode.JWT_TOKEN_EXPIRED);
        }
    }
    /**
     * Handles evict event.
     *
     * @param event event ({@link MobileSecretEvictEvent})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @TransactionalEventListener(classes = MobileSecretEvictEvent.class)
    @Override
    public void handleEvictEvent(MobileSecretEvictEvent event) {
        cache.evict(event.getSecret());
    }

}
