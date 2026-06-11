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

import com.google.common.util.concurrent.FutureCallback;


/**

 * Rule engine service facade for rule engine calculated field queue (rule engine public API contracts and services).

 */


public interface RuleEngineCalculatedFieldQueueService {
    /**
     * Pushes request to queue.
     *
     * @param request async service request DTO
     * @param callback completion callback for async rule engine operations
     * @throws Exception if an unexpected error occurs during processing
     */

    void pushRequestToQueue(TimeseriesSaveRequest request, FutureCallback<Void> callback);
    /**
     * Pushes request to queue.
     *
     * @param request async service request DTO
     * @param callback completion callback for async rule engine operations
     * @throws Exception if an unexpected error occurs during processing
     */

    void pushRequestToQueue(AttributesSaveRequest request, FutureCallback<Void> callback);

}
