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
package org.thingsboard.rule.engine.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.mail.javamail.JavaMailSender;
import org.thingsboard.server.common.data.ApiFeature;
import org.thingsboard.server.common.data.ApiUsageRecordState;
import org.thingsboard.server.common.data.ApiUsageStateValue;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;


/**

 * Facade for sending email from rule engine mail nodes.

 */


public interface MailService {
    /**
     * Updates mail configuration.
     *
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void updateMailConfiguration();
    /**
     * Send email.
     *
     * @param tenantId tenant UUID
     * @param email email ({@link String})
     * @param subject subject ({@link String})
     * @param message message ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendEmail(TenantId tenantId, String email, String subject, String message) throws ThingsboardException;
    /**
     * Send test mail.
     *
     * @param config deserialized node configuration POJO
     * @param email email ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendTestMail(JsonNode config, String email) throws ThingsboardException;
    /**
     * Send activation email.
     *
     * @param activationLink activation link ({@link String})
     * @param ttlMs ttl ms
     * @param email email ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendActivationEmail(String activationLink, long ttlMs, String email) throws ThingsboardException;
    /**
     * Send account activated email.
     *
     * @param loginLink login link ({@link String})
     * @param email email ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendAccountActivatedEmail(String loginLink, String email) throws ThingsboardException;
    /**
     * Send reset password email.
     *
     * @param passwordResetLink password reset link ({@link String})
     * @param ttlMs ttl ms
     * @param email email ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendResetPasswordEmail(String passwordResetLink, long ttlMs, String email) throws ThingsboardException;
    /**
     * Send reset password email async.
     *
     * @param passwordResetLink password reset link ({@link String})
     * @param ttlMs ttl ms
     * @param email email ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendResetPasswordEmailAsync(String passwordResetLink, long ttlMs, String email);
    /**
     * Send password was reset email.
     *
     * @param loginLink login link ({@link String})
     * @param email email ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendPasswordWasResetEmail(String loginLink, String email) throws ThingsboardException;
    /**
     * Send account lockout email.
     *
     * @param lockoutEmail lockout email ({@link String})
     * @param email email ({@link String})
     * @param maxFailedLoginAttempts max failed login attempts ({@link Integer})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendAccountLockoutEmail(String lockoutEmail, String email, Integer maxFailedLoginAttempts) throws ThingsboardException;
    /**
     * Send two fa verification email.
     *
     * @param email email ({@link String})
     * @param verificationCode verification code ({@link String})
     * @param expirationTimeSeconds expiration time seconds
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendTwoFaVerificationEmail(String email, String verificationCode, int expirationTimeSeconds) throws ThingsboardException;
    /**
     * Send.
     *
     * @param tenantId tenant UUID
     * @param customerId customer id ({@link CustomerId})
     * @param tbEmail tb email ({@link TbEmail})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail) throws ThingsboardException;
    /**
     * Send.
     *
     * @param tenantId tenant UUID
     * @param customerId customer id ({@link CustomerId})
     * @param tbEmail tb email ({@link TbEmail})
     * @param javaMailSender java mail sender ({@link JavaMailSender})
     * @param timeout timeout
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail, JavaMailSender javaMailSender, long timeout) throws ThingsboardException;
    /**
     * Send api feature state email.
     *
     * @param apiFeature api feature ({@link ApiFeature})
     * @param stateValue state value ({@link ApiUsageStateValue})
     * @param email email ({@link String})
     * @param recordState record state ({@link ApiUsageRecordState})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendApiFeatureStateEmail(ApiFeature apiFeature, ApiUsageStateValue stateValue, String email, ApiUsageRecordState recordState) throws ThingsboardException;
    /**
     * Test connection.
     *
     * @param tenantId tenant UUID
     * @throws Exception if an unexpected error occurs during processing
     */

    void testConnection(TenantId tenantId) throws Exception;
    /**
     * Is configured.
     *
     * @param tenantId tenant UUID
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean isConfigured(TenantId tenantId);

}
