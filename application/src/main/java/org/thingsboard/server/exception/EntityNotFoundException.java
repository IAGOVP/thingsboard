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
 * Checked exception indicating that a requested entity does not exist.
 *
 * <p>When converted to an HTTP response via {@link ToErrorResponseEntity}, this exception
 * maps to {@link HttpStatus#NOT_FOUND} (404).
 *
 * @see ToErrorResponseEntity
 */
public class EntityNotFoundException extends Exception implements ToErrorResponseEntity {

    /**
     * Creates an exception with the given human-readable description of the missing entity.
     *
     * @param message detail message describing which entity was not found
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Builds an HTTP 404 response containing the exception message as the response body.
     *
     * @return response entity with status {@link HttpStatus#NOT_FOUND} and plain-text body
     */
    @Override
    public ResponseEntity<String> toErrorResponseEntity() {
        return new ResponseEntity<>(getMessage(), HttpStatus.NOT_FOUND);
    }
}
