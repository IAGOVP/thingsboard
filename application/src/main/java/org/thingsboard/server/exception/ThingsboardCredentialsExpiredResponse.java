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
package org.thingsboard.server.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;

/**
 * Error response returned when user credentials have expired and must be reset.
 *
 * <p>Extends {@link ThingsboardErrorResponse} with a password-reset token that the client
 * can use to complete credential renewal. Uses {@link ThingsboardErrorCode#CREDENTIALS_EXPIRED}
 * and HTTP 401 Unauthorized.
 */
@Schema
public class ThingsboardCredentialsExpiredResponse extends ThingsboardErrorResponse {

    private final String resetToken;

    /**
     * Creates a credentials-expired response with an optional reset token.
     *
     * @param message    human-readable description of the expired credentials
     * @param resetToken one-time token for initiating password reset
     */
    protected ThingsboardCredentialsExpiredResponse(String message, String resetToken) {
        super(message, ThingsboardErrorCode.CREDENTIALS_EXPIRED, HttpStatus.UNAUTHORIZED);
        this.resetToken = resetToken;
    }

    /**
     * Factory method for constructing a credentials-expired response.
     *
     * @param message    human-readable description of the expired credentials
     * @param resetToken one-time token for initiating password reset
     * @return new {@link ThingsboardCredentialsExpiredResponse} instance
     */
    public static ThingsboardCredentialsExpiredResponse of(final String message, final String resetToken) {
        return new ThingsboardCredentialsExpiredResponse(message, resetToken);
    }

    /**
     * Returns the password-reset token issued for credential renewal.
     *
     * @return reset token string, or {@code null} if not provided
     */
    @Schema(description = "Password reset token", accessMode = Schema.AccessMode.READ_ONLY)
    public String getResetToken() {
        return resetToken;
    }
}
