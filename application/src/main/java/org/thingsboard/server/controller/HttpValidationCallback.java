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

import com.google.common.util.concurrent.FutureCallback;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.service.security.ValidationCallback;

/**
 * HTTP-specific {@link ValidationCallback} that bridges asynchronous security validation
 * to a Spring {@link DeferredResult} carrying an {@link ResponseEntity}.
 *
 * <p>Used by REST controllers to complete deferred HTTP responses after token or credential
 * validation finishes. Extends {@link org.thingsboard.server.service.security.ValidationCallback}.
 */
public class HttpValidationCallback extends ValidationCallback<DeferredResult<ResponseEntity>> {

    /**
     * Creates a callback that invokes the given action when validation completes.
     *
     * @param response the deferred HTTP response to populate on success or failure
     * @param action   future callback executed after validation with the same deferred result
     */
    public HttpValidationCallback(DeferredResult<ResponseEntity> response, FutureCallback<DeferredResult<ResponseEntity>> action) {
       super(response, action);
    }

}
