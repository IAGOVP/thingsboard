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
package org.thingsboard.server.common.data.kv;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.StringUtils;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
/**
 * Aggregation params.
 */
public class AggregationParams {
    private static final Map<String, String> TZ_LINKS = Map.of("EST", "America/New_York", "GMT+0", "GMT", "GMT-0", "GMT", "HST", "US/Hawaii", "MST", "America/Phoenix", "ROC", "Asia/Taipei");
    @Getter
    private final Aggregation aggregation;
    @Getter
    private final IntervalType intervalType;
    @Getter
    private final ZoneId tzId;

    private final long interval;
    /**
     * None.
     *
     * @return {@link AggregationParams}
     */

    public static AggregationParams none() {
        return new AggregationParams(Aggregation.NONE, null, null, 0L);
    }
    /**
     * Milliseconds.
     *
     * @param aggregationType aggregation type ({@link Aggregation})
     * @param aggregationIntervalMs aggregation interval ms
     * @return {@link AggregationParams}
     */

    public static AggregationParams milliseconds(Aggregation aggregationType, long aggregationIntervalMs) {
        return new AggregationParams(aggregationType, IntervalType.MILLISECONDS, null, aggregationIntervalMs);
    }
    /**
     * Calendar.
     *
     * @param aggregationType aggregation type ({@link Aggregation})
     * @param intervalType interval type ({@link IntervalType})
     * @param tzIdStr tz id str ({@link String})
     * @return {@link AggregationParams}
     */

    public static AggregationParams calendar(Aggregation aggregationType, IntervalType intervalType, String tzIdStr) {
        return calendar(aggregationType, intervalType, getZoneId(tzIdStr));
    }
    /**
     * Calendar.
     *
     * @param aggregationType aggregation type ({@link Aggregation})
     * @param intervalType interval type ({@link IntervalType})
     * @param tzId tz id ({@link ZoneId})
     * @return {@link AggregationParams}
     */

    public static AggregationParams calendar(Aggregation aggregationType, IntervalType intervalType, ZoneId tzId) {
        return new AggregationParams(aggregationType, intervalType, tzId, 0L);
    }
    /**
     * Of.
     *
     * @param aggregation aggregation ({@link Aggregation})
     * @param intervalType interval type ({@link IntervalType})
     * @param tzId tz id ({@link ZoneId})
     * @param interval interval
     * @return {@link AggregationParams}
     */

    public static AggregationParams of(Aggregation aggregation, IntervalType intervalType, ZoneId tzId, long interval) {
        return new AggregationParams(aggregation, intervalType, tzId, interval);
    }
    /**
     * Returns interval.
     *
     * @return the long result
     */

    public long getInterval() {
        if (intervalType == null) {
            return 0L;
        } else {
            switch (intervalType) {
                case WEEK:
                case WEEK_ISO:
                    return TimeUnit.DAYS.toMillis(7);
                case MONTH:
                    return TimeUnit.DAYS.toMillis(30);
                case QUARTER:
                    return TimeUnit.DAYS.toMillis(90);
                default:
                    return interval;
            }
        }
    }

    private static ZoneId getZoneId(String tzIdStr) {
        if (StringUtils.isEmpty(tzIdStr)) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(tzIdStr, TZ_LINKS);
        } catch (DateTimeException e) {
            log.warn("[{}] Failed to convert the time zone. Fallback to default.", tzIdStr);
            return ZoneId.systemDefault();
        }
    }
}
