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
package org.thingsboard.server.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.security.model.JwtPair;
import org.thingsboard.server.common.data.security.model.mfa.PlatformTwoFaSettings;
import org.thingsboard.server.common.data.security.model.mfa.account.EmailTwoFaAccountConfig;
import org.thingsboard.server.common.data.security.model.mfa.account.SmsTwoFaAccountConfig;
import org.thingsboard.server.common.data.security.model.mfa.provider.TwoFaProviderType;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.auth.mfa.TwoFactorAuthService;
import org.thingsboard.server.service.security.auth.mfa.config.TwoFaConfigManager;
import org.thingsboard.server.service.security.auth.rest.RestAuthenticationDetails;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;
import org.thingsboard.server.service.security.system.SystemSecurityService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.NEW_LINE;

/**
 * REST API for the second-factor authentication step during login.
 *
 * <p>Base path: {@code /api/auth/2fa}. Endpoints operate on short-lived tokens issued after
 * primary authentication when 2FA is enabled ({@code PRE_VERIFICATION_TOKEN} or
 * {@code MFA_CONFIGURATION_TOKEN}), not regular user JWTs.
 *
 * @see TwoFactorAuthConfigController
 * @see org.thingsboard.server.service.security.auth.mfa.TwoFactorAuthService
 */
@RestController
@RequestMapping("/api/auth/2fa")
@TbCoreComponent
@RequiredArgsConstructor
public class TwoFactorAuthController extends BaseController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final TwoFaConfigManager twoFaConfigManager;
    private final JwtTokenFactory tokenFactory;
    private final SystemSecurityService systemSecurityService;
    private final UserService userService;

    /**
     * Sends a 2FA verification code for the selected provider.
     *
     * <p><b>HTTP:</b> {@code POST /api/auth/2fa/verification/send?providerType=&lt;type&gt;}
     * <p><b>Auth:</b> {@code PRE_VERIFICATION_TOKEN} (issued after username/password login when 2FA is required)
     * <p>Rate-limited per {@link org.thingsboard.server.common.data.security.model.mfa.PlatformTwoFaSettings}.
     *
     * @param providerType 2FA provider ({@code EMAIL}, {@code SMS}, {@code TOTP}, etc.)
     * @throws Exception if the provider is not configured or rate limits are exceeded
     */
    @ApiOperation(value = "Request 2FA verification code (requestTwoFaVerificationCode)",
            notes = "Request 2FA verification code." + NEW_LINE +
                    "To make a request to this endpoint, you need an access token with the scope of PRE_VERIFICATION_TOKEN, " +
                    "which is issued on username/password auth if 2FA is enabled." + NEW_LINE +
                    "The API method is rate limited (using rate limit config from TwoFactorAuthSettings). " +
                    "Will return a Bad Request error if provider is not configured for usage, " +
                    "and Too Many Requests error if rate limits are exceeded.")
    @PostMapping("/verification/send")
    @PreAuthorize("hasAuthority('PRE_VERIFICATION_TOKEN')")
    public void requestTwoFaVerificationCode(@RequestParam TwoFaProviderType providerType) throws Exception {
        SecurityUser user = getCurrentUser();
        twoFactorAuthService.prepareVerificationCode(user, providerType, true);
    }

    /**
     * Validates a 2FA verification code and returns a full JWT pair on success.
     *
     * <p><b>HTTP:</b> {@code POST /api/auth/2fa/verification/check?providerType=&lt;type&gt;&amp;verificationCode=&lt;code&gt;}
     * <p><b>Auth:</b> {@code PRE_VERIFICATION_TOKEN}
     * <p>Rate-limited; repeated failures may lock the user per platform 2FA settings.
     *
     * @param providerType 2FA provider used to send or generate the code
     * @param verificationCode user-supplied verification code
     * @param servletRequest HTTP request used for login audit logging
     * @return access and refresh token pair when verification succeeds
     * @throws Exception if verification fails, provider is invalid, or limits are exceeded
     */
    @ApiOperation(value = "Check 2FA verification code (checkTwoFaVerificationCode)",
            notes = "Checks 2FA verification code, and if it is correct the method returns a regular access and refresh token pair." + NEW_LINE +
                    "The API method is rate limited (using rate limit config from TwoFactorAuthSettings), and also will block a user " +
                    "after X unsuccessful verification attempts if such behavior is configured (in TwoFactorAuthSettings)." + NEW_LINE +
                    "Will return a Bad Request error if provider is not configured for usage, " +
                    "and Too Many Requests error if rate limits are exceeded.")
    @PostMapping("/verification/check")
    @PreAuthorize("hasAuthority('PRE_VERIFICATION_TOKEN')")
    public JwtPair checkTwoFaVerificationCode(@RequestParam TwoFaProviderType providerType,
                                              @RequestParam String verificationCode, HttpServletRequest servletRequest) throws Exception {
        SecurityUser user = getCurrentUser();
        boolean verificationSuccess = twoFactorAuthService.checkVerificationCode(user, providerType, verificationCode, true);
        if (verificationSuccess) {
            logLogInAction(servletRequest, user, null);
            return createTokenPair(user);
        } else {
            IllegalArgumentException error = new IllegalArgumentException("Verification code is incorrect");
            logLogInAction(servletRequest, user, error);
            throw error;
        }
    }

    /**
     * Lists 2FA providers configured for the current user with obfuscated contact hints.
     *
     * <p><b>HTTP:</b> {@code GET /api/auth/2fa/providers}
     * <p><b>Auth:</b> {@code PRE_VERIFICATION_TOKEN}
     *
     * @return list of {@link TwoFaProviderInfo} entries (type, default flag, masked contact)
     * @throws ThingsboardException if platform 2FA settings cannot be loaded
     */
    @ApiOperation(value = "Get available 2FA providers (getAvailableTwoFaProviderInfos)", notes =
            "Get the list of 2FA provider infos available for user to use. Example:\n" +
            "```\n[\n" +
            "  {\n    \"type\": \"EMAIL\",\n    \"default\": true,\n    \"contact\": \"ab*****ko@gmail.com\"\n  },\n" +
            "  {\n    \"type\": \"TOTP\",\n    \"default\": false,\n    \"contact\": null\n  },\n" +
            "  {\n    \"type\": \"SMS\",\n    \"default\": false,\n    \"contact\": \"+38********12\"\n  }\n" +
            "]\n```")
    @GetMapping("/providers")
    @PreAuthorize("hasAuthority('PRE_VERIFICATION_TOKEN')")
    public List<TwoFaProviderInfo> getAvailableTwoFaProviderInfos() throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        Optional<PlatformTwoFaSettings> platformTwoFaSettings = twoFaConfigManager.getPlatformTwoFaSettings(user.getTenantId(), true);
        return twoFaConfigManager.getAccountTwoFaSettings(user.getTenantId(), user)
                .map(settings -> settings.getConfigs().values()).orElse(Collections.emptyList())
                .stream().map(config -> {
                    String contact = null;
                    switch (config.getProviderType()) {
                        case SMS:
                            String phoneNumber = ((SmsTwoFaAccountConfig) config).getPhoneNumber();
                            contact = StringUtils.obfuscate(phoneNumber, 2, '*', phoneNumber.indexOf('+') + 1, phoneNumber.length());
                            break;
                        case EMAIL:
                            String email = ((EmailTwoFaAccountConfig) config).getEmail();
                            contact = StringUtils.obfuscate(email, 2, '*', 0, email.indexOf('@'));
                            break;
                    }
                    return TwoFaProviderInfo.builder()
                            .type(config.getProviderType())
                            .isDefault(config.isUseByDefault())
                            .contact(contact)
                            .minVerificationCodeSendPeriod(platformTwoFaSettings.get().getMinVerificationCodeSendPeriod())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Completes login after mandatory 2FA setup using an MFA configuration token.
     *
     * <p><b>HTTP:</b> {@code POST /api/auth/2fa/login}
     * <p><b>Auth:</b> {@code MFA_CONFIGURATION_TOKEN} (issued when 2FA must be configured before full access)
     *
     * @param servletRequest HTTP request used for login audit logging
     * @return access and refresh token pair when 2FA is configured for the account
     * @throws ThingsboardException if 2FA is not yet configured
     */
    @ApiOperation(value = "Get regular token pair after successfully configuring 2FA",
            notes = "Checks 2FA is configured, returning token pair on success.")
    @PostMapping("/login")
    @PreAuthorize("hasAuthority('MFA_CONFIGURATION_TOKEN')")
    public JwtPair authenticateByTwoFaConfigurationToken(HttpServletRequest servletRequest) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        if (twoFactorAuthService.isTwoFaEnabled(user.getTenantId(), user)) {
            logLogInAction(servletRequest, user, null);
            return createTokenPair(user);
        } else {
            IllegalArgumentException error = new IllegalArgumentException("2FA is not configured");
            logLogInAction(servletRequest, user, error);
            throw error;
        }
    }

    private JwtPair createTokenPair(SecurityUser user) {
        log.debug("[{}][{}] Creating token pair for user", user.getTenantId(), user.getId());
        user = new SecurityUser(userService.findUserById(user.getTenantId(), user.getId()), true, user.getUserPrincipal());
        return tokenFactory.createTokenPair(user);
    }

    private void logLogInAction(HttpServletRequest servletRequest, SecurityUser user, Exception error) {
        systemSecurityService.logLoginAction(user, new RestAuthenticationDetails(servletRequest), ActionType.LOGIN, error);
    }

    /**
     * JSON view of an available 2FA provider during the login verification flow.
     *
     * <p>Contact values are obfuscated for email and SMS providers.
     */
    @Data
    @AllArgsConstructor
    @Builder
    public static class TwoFaProviderInfo {
        private TwoFaProviderType type;
        @JsonProperty("default")
        private boolean isDefault;
        private String contact;
        private Integer minVerificationCodeSendPeriod;
    }

}
