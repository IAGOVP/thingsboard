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


/**

 * Facade for saving telemetry and attributes from rule nodes.

 */


public interface RuleEngineTelemetryService {
    /**
     * Saves or persists timeseries.
     *
     * @param request async service request DTO
     * @throws Exception if an unexpected error occurs during processing
     */

    void saveTimeseries(TimeseriesSaveRequest request);
    /**
     * Saves or persists attributes.
     *
     * @param request async service request DTO
     * @throws Exception if an unexpected error occurs during processing
     */

    void saveAttributes(AttributesSaveRequest request);
    /**
     * Deletes timeseries.
     *
     * @param request async service request DTO
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteTimeseries(TimeseriesDeleteRequest request);
    /**
     * Deletes attributes.
     *
     * @param request async service request DTO
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteAttributes(AttributesDeleteRequest request);

}
