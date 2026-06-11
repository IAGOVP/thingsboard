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

import org.springframework.http.ResponseEntity;

import java.util.Objects;

/**
 * Runtime wrapper for checked API exceptions that implement {@link ToErrorResponseEntity}.
 *
 * <p>Allows {@code ToErrorResponseEntity} exceptions to propagate through APIs and lambdas
 * that do not declare checked exceptions, while preserving the original HTTP mapping via
 * delegation to the wrapped cause.
 *
 * @see ToErrorResponseEntity
 */
public class UncheckedApiException extends RuntimeException implements ToErrorResponseEntity {

    private final ToErrorResponseEntity cause;

    /**
     * Wraps a checked API exception for unchecked propagation.
     *
     * @param <T>   type of the wrapped exception, constrained to {@link Exception} and {@link ToErrorResponseEntity}
     * @param cause the original checked exception; must not be {@code null}
     */
    public <T extends Exception & ToErrorResponseEntity> UncheckedApiException(T cause) {
        super(cause.getMessage(), Objects.requireNonNull(cause));
        this.cause = cause;
    }

    /**
     * Delegates HTTP response construction to the wrapped {@link ToErrorResponseEntity} cause.
     *
     * @return response entity produced by the original checked exception
     */
    @Override
    public ResponseEntity<String> toErrorResponseEntity() {
        return cause.toErrorResponseEntity();
    }
}

