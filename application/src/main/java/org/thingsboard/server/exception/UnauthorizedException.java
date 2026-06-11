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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Checked exception indicating that the caller is not authenticated or lacks valid credentials.
 *
 * <p>When converted to an HTTP response via {@link ToErrorResponseEntity}, this exception
 * maps to {@link HttpStatus#UNAUTHORIZED} (401).
 *
 * @see ToErrorResponseEntity
 */
public class UnauthorizedException extends Exception implements ToErrorResponseEntity {

    /**
     * Creates an exception with the given authentication-failure description.
     *
     * @param message detail message describing the authorization failure
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Builds an HTTP 401 response containing the exception message as the response body.
     *
     * @return response entity with status {@link HttpStatus#UNAUTHORIZED} and plain-text body
     */
    @Override
    public ResponseEntity<String> toErrorResponseEntity() {
        return new ResponseEntity<>(getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
