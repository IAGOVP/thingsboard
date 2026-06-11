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
package org.thingsboard.server.utils;

import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.cf.configuration.Argument;
import org.thingsboard.server.common.data.cf.configuration.aggregation.AggMetric;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.KvEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.service.cf.ctx.state.ArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.CalculatedFieldCtx;
import org.thingsboard.server.service.cf.ctx.state.CalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.ScriptCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.SimpleCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.SingleValueArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.aggregation.RelatedEntitiesAggregationCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.aggregation.single.AggIntervalEntry;
import org.thingsboard.server.service.cf.ctx.state.aggregation.single.AggIntervalEntryStatus;
import org.thingsboard.server.service.cf.ctx.state.aggregation.single.EntityAggregationArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.aggregation.single.EntityAggregationCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.alarm.AlarmCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.geofencing.GeofencingCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.propagation.PropagationCalculatedFieldState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.thingsboard.server.service.cf.ctx.state.SingleValueArgumentEntry.DEFAULT_VERSION;

/**
 * Factory and transformation helpers for calculated-field argument entries and initial state objects.
 *
 * <p>Converts telemetry, attribute, and aggregation inputs into {@link ArgumentEntry} instances
 * and creates type-appropriate {@link CalculatedFieldState} objects for new field contexts.
 */
public class CalculatedFieldArgumentUtils {

    /**
     * Wraps a single key-value entry as a calculated-field argument.
     *
     * @param kvEntry telemetry or attribute value; must not be {@code null}
     * @return {@link SingleValueArgumentEntry} with the value, or an empty entry if the value is {@code null}
     */
    public static ArgumentEntry transformSingleValueArgument(@NonNull KvEntry kvEntry) {
        return kvEntry.getValue() != null ? ArgumentEntry.createSingleValueArgument(kvEntry) : new SingleValueArgumentEntry();
    }

    /**
     * Builds a time-series rolling-window argument from historical telemetry samples.
     *
     * @param tsRolling      list of time-series key-value entries within the window
     * @param limit          maximum number of records to retain
     * @param argTimeWindow  rolling window duration in milliseconds
     * @return {@link ArgumentEntry} representing the rolling time-series argument
     */
    public static ArgumentEntry transformTsRollingArgument(List<TsKvEntry> tsRolling, int limit, long argTimeWindow) {
        return ArgumentEntry.createTsRollingArgument(tsRolling, limit, argTimeWindow);
    }

    /**
     * Builds an aggregation metric argument from time-series data, falling back to defaults when empty.
     *
     * @param timeSeries list of time-series entries for the metric; may be empty
     * @param argKey     argument key name
     * @param aggMetric  aggregation metric definition including optional default value
     * @return single-value or default metric {@link ArgumentEntry}
     */
    public static ArgumentEntry transformAggMetricArgument(List<TsKvEntry> timeSeries, String argKey, AggMetric aggMetric) {
        if (timeSeries == null || timeSeries.isEmpty()) {
            return createDefaultMetricArgumentEntry(argKey, aggMetric);
        }
        return ArgumentEntry.createSingleValueArgument(timeSeries.get(0));
    }

    /**
     * Creates a default metric argument entry using the metric's configured default value.
     *
     * @param argKey  argument key name
     * @param metric  aggregation metric with optional {@link AggMetric#getDefaultValue()}
     * @return {@link SingleValueArgumentEntry} with default double value, or empty entry if no default
     */
    public static ArgumentEntry createDefaultMetricArgumentEntry(String argKey, AggMetric metric) {
        Double defaultValue = metric.getDefaultValue();
        if (defaultValue != null) {
            return ArgumentEntry.createSingleValueArgument(new DoubleDataEntry(argKey, defaultValue));
        }
        return new SingleValueArgumentEntry();
    }

    /**
     * Builds an entity-aggregation interval argument from time-series data for a given window.
     *
     * @param timeSeries       time-series entries for the interval; may be empty
     * @param startIntervalTs  interval start timestamp
     * @param endIntervalTs    interval end timestamp
     * @return {@link EntityAggregationArgumentEntry} with interval status tracking
     */
    public static ArgumentEntry transformAggregationArgument(List<TsKvEntry> timeSeries, long startIntervalTs, long endIntervalTs) {
        Map<AggIntervalEntry, AggIntervalEntryStatus> aggIntervals = new HashMap<>();
        AggIntervalEntry aggIntervalEntry = new AggIntervalEntry(startIntervalTs, endIntervalTs);
        if (timeSeries == null || timeSeries.isEmpty()) {
            aggIntervals.put(aggIntervalEntry, new AggIntervalEntryStatus());
        } else {
            aggIntervals.put(aggIntervalEntry, new AggIntervalEntryStatus(System.currentTimeMillis()));
        }
        return new EntityAggregationArgumentEntry(aggIntervals);
    }

    private static KvEntry createDefaultKvEntry(Argument argument) {
        String key = argument.getRefEntityKey().getKey();
        String defaultValue = argument.getDefaultValue();
        if (StringUtils.isBlank(defaultValue)) {
            return new StringDataEntry(key, null);
        }
        if (NumberUtils.isParsable(defaultValue)) {
            return new DoubleDataEntry(key, Double.parseDouble(defaultValue));
        }
        if ("true".equalsIgnoreCase(defaultValue) || "false".equalsIgnoreCase(defaultValue)) {
            return new BooleanDataEntry(key, Boolean.parseBoolean(defaultValue));
        }
        return new StringDataEntry(key, defaultValue);
    }

    /**
     * Creates a default time-series key-value entry from an argument's configured default value.
     *
     * @param argument calculated-field argument definition with optional default
     * @param ts       timestamp to assign to the entry
     * @return {@link BasicTsKvEntry} with typed default value
     */
    public static TsKvEntry createDefaultTsKvEntry(Argument argument, long ts) {
        return new BasicTsKvEntry(ts, createDefaultKvEntry(argument), DEFAULT_VERSION);
    }

    /**
     * Creates a default attribute key-value entry from an argument's configured default value.
     *
     * @param argument calculated-field argument definition with optional default
     * @param ts       timestamp to assign to the entry
     * @return {@link BaseAttributeKvEntry} with typed default value
     */
    public static AttributeKvEntry createDefaultAttributeEntry(Argument argument, long ts) {
        return new BaseAttributeKvEntry(createDefaultKvEntry(argument), ts, DEFAULT_VERSION);
    }

    /**
     * Instantiates an empty {@link CalculatedFieldState} appropriate for the field type in the context.
     *
     * @param ctx      calculated-field runtime context providing the field type
     * @param entityId target entity for which state is created
     * @return new state object matching {@link CalculatedFieldCtx#getCfType()}
     */
    public static CalculatedFieldState createStateByType(CalculatedFieldCtx ctx, EntityId entityId) {
        return switch (ctx.getCfType()) {
            case SIMPLE -> new SimpleCalculatedFieldState(entityId);
            case SCRIPT -> new ScriptCalculatedFieldState(entityId);
            case GEOFENCING -> new GeofencingCalculatedFieldState(entityId);
            case ALARM -> new AlarmCalculatedFieldState(entityId);
            case PROPAGATION -> new PropagationCalculatedFieldState(entityId);
            case RELATED_ENTITIES_AGGREGATION -> new RelatedEntitiesAggregationCalculatedFieldState(entityId);
            case ENTITY_AGGREGATION -> new EntityAggregationCalculatedFieldState(entityId);
        };
    }

}
