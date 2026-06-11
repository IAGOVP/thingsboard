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
package org.thingsboard.server.common.data.cf.configuration.aggregation.single.interval;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Schema(
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "HOUR", schema = HourInterval.class),
                @DiscriminatorMapping(value = "DAY", schema = DayInterval.class),
                @DiscriminatorMapping(value = "WEEK", schema = WeekInterval.class),
                @DiscriminatorMapping(value = "WEEK_SUN_SAT", schema = WeekSunSatInterval.class),
                @DiscriminatorMapping(value = "MONTH", schema = MonthInterval.class),
                @DiscriminatorMapping(value = "QUARTER", schema = QuarterInterval.class),
                @DiscriminatorMapping(value = "YEAR", schema = YearInterval.class),
                @DiscriminatorMapping(value = "CUSTOM", schema = CustomInterval.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HourInterval.class, name = "HOUR"),
        @JsonSubTypes.Type(value = DayInterval.class, name = "DAY"),
        @JsonSubTypes.Type(value = WeekInterval.class, name = "WEEK"),
        @JsonSubTypes.Type(value = WeekSunSatInterval.class, name = "WEEK_SUN_SAT"),
        @JsonSubTypes.Type(value = MonthInterval.class, name = "MONTH"),
        @JsonSubTypes.Type(value = QuarterInterval.class, name = "QUARTER"),
        @JsonSubTypes.Type(value = YearInterval.class, name = "YEAR"),
        @JsonSubTypes.Type(value = CustomInterval.class, name = "CUSTOM")
})
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * agg interval contract.
 */
public interface AggInterval {
    /**
     * Returns type.
     *
     * @return {@link AggIntervalType}
     */

    @JsonIgnore
    AggIntervalType getType();
    /**
     * Returns zone id.
     *
     * @return {@link ZoneId}
     */

    @JsonIgnore
    ZoneId getZoneId();
    /**
     * Returns current interval duration millis.
     *
     * @return the long result
     */

    @JsonIgnore
    long getCurrentIntervalDurationMillis();
    /**
     * Returns current interval start ts.
     *
     * @return the long result
     */

    @JsonIgnore
    long getCurrentIntervalStartTs();
/**
 * Returns date time interval start ts.
 *
 * @param dateTime date time ({@link ZonedDateTime})
 * @return the long result
 */

    long getDateTimeIntervalStartTs(ZonedDateTime dateTime);
    /**
     * Returns current interval end ts.
     *
     * @return the long result
     */

    @JsonIgnore
    long getCurrentIntervalEndTs();
/**
 * Returns date time interval end ts.
 *
 * @param dateTime date time ({@link ZonedDateTime})
 * @return the long result
 */

    long getDateTimeIntervalEndTs(ZonedDateTime dateTime);
/**
 * Returns next interval start.
 *
 * @param currentStart current start ({@link ZonedDateTime})
 * @return {@link ZonedDateTime}
 */

    ZonedDateTime getNextIntervalStart(ZonedDateTime currentStart);
/**
 * Validates the requested data.
 *
 */

    void validate();

}
