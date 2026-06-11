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
package org.thingsboard.server.service.security.auth.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.model.JwtPair;
import org.thingsboard.server.service.security.auth.MfaAuthenticationToken;
import org.thingsboard.server.service.security.auth.MfaConfigurationToken;
import org.thingsboard.server.service.security.auth.mfa.config.TwoFaConfigManager;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
/**
 * Handles success responses for username/password REST login.
 *
 * <p><b>Responsibilities:</b> Spring-managed service component.
 */

@Slf4j @Component(value = "defaultAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class RestAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenFactory tokenFactory;
    private final TwoFaConfigManager twoFaConfigManager;

    /**
     * On authentication success.
     *
     * @param request request (HttpServletRequest)
     * @param response response (HttpServletResponse)
     * @param authentication authentication (Authentication)
     * @throws IOException if the operation fails
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        JwtPair tokenPair;

        if (authentication instanceof MfaAuthenticationToken) {
            tokenPair = createMfaTokenPair(securityUser, Authority.PRE_VERIFICATION_TOKEN);
        } else if (authentication instanceof MfaConfigurationToken) {
            tokenPair = createMfaTokenPair(securityUser, Authority.MFA_CONFIGURATION_TOKEN);
        } else {
            tokenPair = tokenFactory.createTokenPair(securityUser);
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        JacksonUtil.writeValue(response.getWriter(), tokenPair);

        clearAuthenticationAttributes(request);
    }

    /**
     * Creates or persists mfa token pair.
     *
     * @param securityUser security user (SecurityUser)
     * @param scope scope (Authority)
     * @return {@link JwtPair} result
     */

    public JwtPair createMfaTokenPair(SecurityUser securityUser, Authority scope) {
        log.debug("[{}][{}] Creating {} token", securityUser.getTenantId(), securityUser.getId(), scope);
        JwtPair tokenPair = new JwtPair();
        int preVerificationTokenLifetime = twoFaConfigManager.getPlatformTwoFaSettings(securityUser.getTenantId(), true)
                .flatMap(settings -> Optional.ofNullable(settings.getTotalAllowedTimeForVerification())
                        .filter(time -> time > 0))
                .orElse((int) TimeUnit.MINUTES.toSeconds(30));
        tokenPair.setToken(tokenFactory.createMfaToken(securityUser, scope, preVerificationTokenLifetime).token());
        tokenPair.setRefreshToken(null);
        tokenPair.setScope(scope);
        return tokenPair;
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     *
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}
