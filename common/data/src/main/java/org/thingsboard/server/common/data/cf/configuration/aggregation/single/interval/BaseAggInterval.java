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

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
/**
 * Base agg interval.
 */
public abstract class BaseAggInterval implements AggInterval {

    @NotBlank
    protected String tz;
    protected Long offsetSec; // delay seconds since start of interval
    /**
     * Returns zone id.
     *
     * @return {@link ZoneId}
     */

    @Override
    public ZoneId getZoneId() {
        return ZoneId.of(tz);
    }
    /**
     * Returns offset safe.
     *
     * @return the long result
     */

    protected long getOffsetSafe() {
        return offsetSec != null ? offsetSec : 0L;
    }
    /**
     * Returns current interval duration millis.
     *
     * @return the long result
     */

    @Override
    public long getCurrentIntervalDurationMillis() {
        return getCurrentIntervalEndTs() - getCurrentIntervalStartTs();
    }
    /**
     * Returns current interval start ts.
     *
     * @return the long result
     */

    @Override
    public long getCurrentIntervalStartTs() {
        ZoneId zoneId = getZoneId();
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return getDateTimeIntervalStartTs(now);
    }
    /**
     * Returns date time interval start ts.
     *
     * @param dateTime date time ({@link ZonedDateTime})
     * @return the long result
     */

    @Override
    public long getDateTimeIntervalStartTs(ZonedDateTime dateTime) {
        long offset = getOffsetSafe();
        ZonedDateTime shiftedNow = dateTime.minusSeconds(offset);
        ZonedDateTime alignedStart = getAlignedBoundary(shiftedNow, false);
        ZonedDateTime actualStart = alignedStart.plusSeconds(offset);
        return actualStart.toInstant().toEpochMilli();
    }
    /**
     * Returns current interval end ts.
     *
     * @return the long result
     */

    @Override
    public long getCurrentIntervalEndTs() {
        ZoneId zoneId = getZoneId();
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return getDateTimeIntervalEndTs(now);
    }
    /**
     * Returns date time interval end ts.
     *
     * @param dateTime date time ({@link ZonedDateTime})
     * @return the long result
     */

    @Override
    public long getDateTimeIntervalEndTs(ZonedDateTime dateTime) {
        long offset = getOffsetSafe();
        ZonedDateTime shiftedNow = dateTime.minusSeconds(offset);
        ZonedDateTime alignedEnd = getAlignedBoundary(shiftedNow, true);
        ZonedDateTime actualEnd = alignedEnd.plusSeconds(offset);
        return actualEnd.toInstant().toEpochMilli();
    }
    /**
     * Align to interval start.
     *
     * @param reference reference ({@link ZonedDateTime})
     * @return {@link ZonedDateTime}
     */

    protected abstract ZonedDateTime alignToIntervalStart(ZonedDateTime reference);
    /**
     * Returns aligned boundary.
     *
     * @param reference reference ({@link ZonedDateTime})
     * @param next next
     * @return {@link ZonedDateTime}
     */

    protected ZonedDateTime getAlignedBoundary(ZonedDateTime reference, boolean next) {
        ZonedDateTime base = alignToIntervalStart(reference);
        return next ? getNextIntervalStart(base) : base;
    }
    /**
     * Validates the requested data.
     *
     */

    @Override
    public void validate() {
        try {
            getZoneId();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid timezone in interval: " + ex.getMessage());
        }
        if (offsetSec != null) {
            if (offsetSec < 0) {
                throw new IllegalArgumentException("Offset cannot be negative.");
            }
            if (TimeUnit.SECONDS.toMillis(offsetSec) >= getCurrentIntervalDurationMillis()) {
                throw new IllegalArgumentException("Offset must be greater than interval duration.");
            }
        }
    }

}
