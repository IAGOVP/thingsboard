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
package org.thingsboard.server.service.security.auth.mfa.provider.impl;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.model.mfa.account.EmailTwoFaAccountConfig;
import org.thingsboard.server.common.data.security.model.mfa.provider.EmailTwoFaProviderConfig;
import org.thingsboard.server.common.data.security.model.mfa.provider.TwoFaProviderType;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
/**
 * Email two fa provider for two-factor authentication (MFA).
 *
 * <p><b>Responsibilities:</b> Spring-managed service component.
 */

@Service
@TbCoreComponent
public class EmailTwoFaProvider extends OtpBasedTwoFaProvider<EmailTwoFaProviderConfig, EmailTwoFaAccountConfig> {

    private final MailService mailService;

    protected EmailTwoFaProvider(CacheManager cacheManager, MailService mailService) {
        super(cacheManager);
        this.mailService = mailService;
    }
    /**
     * Generate new account config.
     *
     * @param user user (User)
     * @param providerConfig provider config (EmailTwoFaProviderConfig)
     * @return {@link EmailTwoFaAccountConfig} result
     */
    @Override
    public EmailTwoFaAccountConfig generateNewAccountConfig(User user, EmailTwoFaProviderConfig providerConfig) {
        EmailTwoFaAccountConfig config = new EmailTwoFaAccountConfig();
        config.setEmail(user.getEmail());
        return config;
    }
    /**
     * Check.
     *
     * @param tenantId tenant id (TenantId)
     * @throws ThingsboardException if the operation fails
     */
    @Override
    public void check(TenantId tenantId) throws ThingsboardException {
        try {
            mailService.testConnection(tenantId);
        } catch (Exception e) {
            throw new ThingsboardException("Mail service is not set up", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    /**
     * Send verification code.
     *
     * @param user user (SecurityUser)
     * @param verificationCode verification code (String)
     * @param providerConfig provider config (EmailTwoFaProviderConfig)
     * @param accountConfig account config (EmailTwoFaAccountConfig)
     * @throws ThingsboardException if the operation fails
     */
    @Override
    protected void sendVerificationCode(SecurityUser user, String verificationCode, EmailTwoFaProviderConfig providerConfig, EmailTwoFaAccountConfig accountConfig) throws ThingsboardException {
        try {
            mailService.sendTwoFaVerificationEmail(accountConfig.getEmail(), verificationCode, providerConfig.getVerificationCodeLifetime());
        } catch (Exception e) {
            throw new ThingsboardException("Couldn't send 2FA verification email", ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * Returns type.
     *
     */
    @Override
    public TwoFaProviderType getType() {
        return TwoFaProviderType.EMAIL;
    }

}
