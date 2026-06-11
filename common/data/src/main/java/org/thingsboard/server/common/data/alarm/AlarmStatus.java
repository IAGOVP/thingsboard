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
package org.thingsboard.server.common.data.alarm;

/**
 * Combined acknowledgment and clearance state of an alarm.
 */
public enum AlarmStatus {

    /** Alarm is active and not acknowledged. */
    ACTIVE_UNACK,
    /** Alarm is active and acknowledged. */
    ACTIVE_ACK,
    /** Alarm condition cleared but not acknowledged. */
    CLEARED_UNACK,
    /** Alarm cleared and acknowledged. */
    CLEARED_ACK;

    /**
     * Returns whether this status represents an acknowledged alarm.
     */
    public boolean isAck() {
        return this == ACTIVE_ACK || this == CLEARED_ACK;
    }

    /**
     * Returns whether this status represents a cleared alarm.
     */
    public boolean isCleared() {
        return this == CLEARED_ACK || this == CLEARED_UNACK;
    }

    /**
     * Maps this status to a cleared/active search dimension.
     *
     * @return {@link AlarmSearchStatus#CLEARED} or {@link AlarmSearchStatus#ACTIVE}
     */
    public AlarmSearchStatus getClearSearchStatus() {
        return this.isCleared() ? AlarmSearchStatus.CLEARED : AlarmSearchStatus.ACTIVE;
    }

    /**
     * Maps this status to an ack/unack search dimension.
     *
     * @return {@link AlarmSearchStatus#ACK} or {@link AlarmSearchStatus#UNACK}
     */
    public AlarmSearchStatus getAckSearchStatus() {
        return this.isAck() ? AlarmSearchStatus.ACK : AlarmSearchStatus.UNACK;
    }

}
