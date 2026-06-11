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
package org.thingsboard.server.service.ws.telemetry.cmd.v2;

import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.AggregationParams;
import org.thingsboard.server.common.data.kv.IntervalType;

import java.util.List;

/**
 * Get ts cmd contract for the ThingsBoard WebSocket layer.
 */

public interface GetTsCmd {

    /**
     * Returns start ts.
     * @return numeric result
     */

    long getStartTs();

    /**
     * Returns end ts.
     * @return numeric result
     */

    long getEndTs();

    /**
     * Returns keys.
     * @return {@link List}
     */

    List<String> getKeys();

    /**
     * Returns interval type.
     * @return {@link IntervalType}
     */

    IntervalType getIntervalType();

    /**
     * Returns interval.
     * @return numeric result
     */

    long getInterval();

    /**
     * Returns time zone id.
     * @return string value
     */

    String getTimeZoneId();

    /**
     * Returns limit.
     * @return numeric result
     */

    int getLimit();

    /**
     * Returns agg.
     * @return {@link Aggregation}
     */

    Aggregation getAgg();

    /**
     * Is fetch latest previous point.
     * @return {@code true} when the condition holds
     */

    boolean isFetchLatestPreviousPoint();

    /**
     * To aggregation params.
     * @return {@link AggregationParams}
     */

    default AggregationParams toAggregationParams() {
        var agg = getAgg();
        var intervalType = getIntervalType();
        if (agg == null || Aggregation.NONE.equals(agg)) {
            return AggregationParams.none();
        } else if (intervalType == null || IntervalType.MILLISECONDS.equals(intervalType)) {
            return AggregationParams.milliseconds(agg, getInterval());
        } else {
            return AggregationParams.calendar(agg, intervalType, getTimeZoneId());
        }
    }

}
