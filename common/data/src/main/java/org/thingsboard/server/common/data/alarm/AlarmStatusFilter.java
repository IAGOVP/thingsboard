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

import java.util.Collection;
import java.util.Optional;

/**
 * Filter predicate matching alarm ack/clear status combinations.
 */
public class AlarmStatusFilter {

    private static final AlarmStatusFilter EMPTY = new AlarmStatusFilter(Optional.empty(), Optional.empty());

    private final Optional<Boolean> clearFilter;
    private final Optional<Boolean> ackFilter;

    private AlarmStatusFilter(Optional<Boolean> clearFilter, Optional<Boolean> ackFilter) {
        this.clearFilter = clearFilter;
        this.ackFilter = ackFilter;
    }
    /**
     * From.
     *
     * @param query query ({@link AlarmQuery})
     * @return {@link AlarmStatusFilter}
     */

    public static AlarmStatusFilter from(AlarmQuery query) {
        if (query.getSearchStatus() != null) {
            return AlarmStatusFilter.from(query.getSearchStatus());
        } else if (query.getStatus() != null) {
            return AlarmStatusFilter.from(query.getStatus());
        }
        return AlarmStatusFilter.empty();
    }
    /**
     * From.
     *
     * @param alarmSearchStatus alarm search status ({@link AlarmSearchStatus})
     * @return {@link AlarmStatusFilter}
     */

    public static AlarmStatusFilter from(AlarmSearchStatus alarmSearchStatus) {
        switch (alarmSearchStatus) {
            case ACK:
                return new AlarmStatusFilter(Optional.empty(), Optional.of(true));
            case UNACK:
                return new AlarmStatusFilter(Optional.empty(), Optional.of(false));
            case ACTIVE:
                return new AlarmStatusFilter(Optional.of(false), Optional.empty());
            case CLEARED:
                return new AlarmStatusFilter(Optional.of(true), Optional.empty());
            default:
                return EMPTY;
        }
    }
    /**
     * From.
     *
     * @param alarmStatus alarm status ({@link AlarmStatus})
     * @return {@link AlarmStatusFilter}
     */

    public static AlarmStatusFilter from(AlarmStatus alarmStatus) {
        switch (alarmStatus) {
            case ACTIVE_UNACK:
                return new AlarmStatusFilter(Optional.of(false), Optional.of(false));
            case ACTIVE_ACK:
                return new AlarmStatusFilter(Optional.of(false), Optional.of(true));
            case CLEARED_UNACK:
                return new AlarmStatusFilter(Optional.of(true), Optional.of(false));
            case CLEARED_ACK:
                return new AlarmStatusFilter(Optional.of(true), Optional.of(true));
            default:
                return EMPTY;
        }
    }
    /**
     * Empty.
     *
     * @return {@link AlarmStatusFilter}
     */

    public static AlarmStatusFilter empty() {
        return EMPTY;
    }
    /**
     * Has any filter.
     *
     * @return the boolean result
     */

    public boolean hasAnyFilter() {
        return clearFilter.isPresent() || ackFilter.isPresent();
    }
    /**
     * Has clear filter.
     *
     * @return the boolean result
     */

    public boolean hasClearFilter() {
        return clearFilter.isPresent();
    }
    /**
     * Has ack filter.
     *
     * @return the boolean result
     */

    public boolean hasAckFilter() {
        return ackFilter.isPresent();
    }
    /**
     * Returns clear filter.
     *
     * @return the boolean result
     */

    public boolean getClearFilter() {
        return clearFilter.orElseThrow(() -> new RuntimeException("Clear filter is not set! Use `hasClearFilter` to check."));
    }
    /**
     * Returns ack filter.
     *
     * @return the boolean result
     */

    public boolean getAckFilter() {
        return ackFilter.orElseThrow(() -> new RuntimeException("Ack filter is not set! Use `hasAckFilter` to check."));
    }
    /**
     * From.
     *
     * @param statuses statuses ({@link Collection})
     * @return {@link AlarmStatusFilter}
     */


    public static AlarmStatusFilter from(Collection<AlarmSearchStatus> statuses) {
        if (statuses == null || statuses.isEmpty() || statuses.contains(AlarmSearchStatus.ANY)) {
            return EMPTY;
        }
        boolean clearFilter = statuses.contains(AlarmSearchStatus.CLEARED);
        boolean activeFilter = statuses.contains(AlarmSearchStatus.ACTIVE);
        Optional<Boolean> clear = Optional.empty();
        if (clearFilter && !activeFilter || !clearFilter && activeFilter) {
            clear = Optional.of(clearFilter);
        }

        boolean ackFilter = statuses.contains(AlarmSearchStatus.ACK);
        boolean unackFilter = statuses.contains(AlarmSearchStatus.UNACK);
        Optional<Boolean> ack = Optional.empty();
        if (ackFilter && !unackFilter || !ackFilter && unackFilter) {
            ack = Optional.of(ackFilter);
        }
        return new AlarmStatusFilter(clear, ack);
    }
    /**
     * Matches.
     *
     * @param alarm alarm ({@link Alarm})
     * @return the boolean result
     */

    public boolean matches(Alarm alarm) {
        return ackFilter.map(ackFilter -> ackFilter.equals(alarm.isAcknowledged())).orElse(true) &&
                clearFilter.map(clearedFilter -> clearedFilter.equals(alarm.isCleared())).orElse(true);
    }

}
