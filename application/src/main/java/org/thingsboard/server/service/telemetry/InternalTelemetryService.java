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
package org.thingsboard.server.service.telemetry;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.rule.engine.api.AttributesDeleteRequest;
import org.thingsboard.rule.engine.api.AttributesSaveRequest;
import org.thingsboard.rule.engine.api.RuleEngineTelemetryService;
import org.thingsboard.rule.engine.api.TimeseriesDeleteRequest;
import org.thingsboard.rule.engine.api.TimeseriesSaveRequest;
import org.thingsboard.server.common.data.kv.AttributesSaveResult;
import org.thingsboard.server.common.data.kv.TimeseriesSaveResult;

/**
 * Created by ashvayka on 27.03.18.
 */
public interface InternalTelemetryService extends RuleEngineTelemetryService {

    /**
     * Saves or persists timeseries internal.
     *
     * @param request request payload with operation parameters
     * @return future completing with {@link TimeseriesSaveResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<TimeseriesSaveResult> saveTimeseriesInternal(TimeseriesSaveRequest request);

    /**
     * Saves or persists attributes internal.
     *
     * @param request request payload with operation parameters
     * @return future completing with {@link AttributesSaveResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<AttributesSaveResult> saveAttributesInternal(AttributesSaveRequest request);

    /**
     * Deletes timeseries internal.
     *
     * @param request request payload with operation parameters
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteTimeseriesInternal(TimeseriesDeleteRequest request);

    /**
     * Deletes attributes internal.
     *
     * @param request request payload with operation parameters
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteAttributesInternal(AttributesDeleteRequest request);

}
