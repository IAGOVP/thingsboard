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
 * Checked exception indicating invalid or malformed request parameters.
 *
 * <p>When converted to an HTTP response via {@link ToErrorResponseEntity}, this exception
 * maps to {@link HttpStatus#BAD_REQUEST} (400).
 *
 * @see ToErrorResponseEntity
 */
public class InvalidParametersException extends Exception implements ToErrorResponseEntity {

    /**
     * Creates an exception with the given parameter-validation failure description.
     *
     * @param message detail message describing which parameters are invalid
     */
    public InvalidParametersException(String message) {
        super(message);
    }

    /**
     * Builds an HTTP 400 response containing the exception message as the response body.
     *
     * @return response entity with status {@link HttpStatus#BAD_REQUEST} and plain-text body
     */
    @Override
    public ResponseEntity<String> toErrorResponseEntity() {
        return new ResponseEntity<>(getMessage(), HttpStatus.BAD_REQUEST);
    }
}
