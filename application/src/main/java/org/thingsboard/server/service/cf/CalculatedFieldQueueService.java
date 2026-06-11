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
package org.thingsboard.server.service.cf;

import com.google.common.util.concurrent.FutureCallback;
import org.thingsboard.rule.engine.api.AttributesDeleteRequest;
import org.thingsboard.rule.engine.api.AttributesSaveRequest;
import org.thingsboard.rule.engine.api.RuleEngineCalculatedFieldQueueService;
import org.thingsboard.rule.engine.api.TimeseriesDeleteRequest;
import org.thingsboard.rule.engine.api.TimeseriesSaveRequest;
import org.thingsboard.server.common.data.kv.AttributesSaveResult;
import org.thingsboard.server.common.data.kv.TimeseriesSaveResult;

import java.util.List;

/**

 * Kafka queue integration for calculated-field telemetry and lifecycle messages.

 */

public interface CalculatedFieldQueueService extends RuleEngineCalculatedFieldQueueService {

        
    /**
     * Pushes request to queue.
     *
     * @param request request payload with operation parameters
     * @param result result ({@link TimeseriesSaveResult})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void pushRequestToQueue(TimeseriesSaveRequest request, TimeseriesSaveResult result, FutureCallback<Void> callback);
/**
 * Pushes request to queue.
 *
 * @param request request payload with operation parameters
 * @param result result ({@link AttributesSaveResult})
 * @param callback queue callback invoked when processing completes
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void pushRequestToQueue(AttributesSaveRequest request, AttributesSaveResult result, FutureCallback<Void> callback);
/**
 * Pushes request to queue.
 *
 * @param request request payload with operation parameters
 * @param result result ({@link List})
 * @param callback queue callback invoked when processing completes
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void pushRequestToQueue(AttributesDeleteRequest request, List<String> result, FutureCallback<Void> callback);
/**
 * Pushes request to queue.
 *
 * @param request request payload with operation parameters
 * @param result result ({@link List})
 * @param callback queue callback invoked when processing completes
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void pushRequestToQueue(TimeseriesDeleteRequest request, List<String> result, FutureCallback<Void> callback);

}
