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
 * Checked exception indicating an unexpected internal server error.
 *
 * <p>When converted to an HTTP response via {@link ToErrorResponseEntity}, this exception
 * maps to {@link HttpStatus#INTERNAL_SERVER_ERROR} (500).
 *
 * @see ToErrorResponseEntity
 */
public class InternalErrorException extends Exception implements ToErrorResponseEntity {

    /**
     * Creates an exception with the given internal-error description.
     *
     * @param message detail message describing the internal failure
     */
    public InternalErrorException(String message) {
        super(message);
    }

    /**
     * Builds an HTTP 500 response containing the exception message as the response body.
     *
     * @return response entity with status {@link HttpStatus#INTERNAL_SERVER_ERROR} and plain-text body
     */
    @Override
    public ResponseEntity<String> toErrorResponseEntity() {
        return new ResponseEntity<>(getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
