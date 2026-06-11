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
package org.thingsboard.server.service.apiusage;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.ApiFeature;
import org.thingsboard.server.common.data.ApiUsageRecordKey;
import org.thingsboard.server.common.data.ApiUsageState;
import org.thingsboard.server.common.data.ApiUsageStateValue;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.tools.SchedulerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**

 * Base api usage state (tenant API usage metering and rate-limit state).

 */

public abstract class BaseApiUsageState {
    private final Map<ApiUsageRecordKey, Long> currentCycleValues = new ConcurrentHashMap<>();
    private final Map<ApiUsageRecordKey, Long> currentHourValues = new ConcurrentHashMap<>();

    private final Map<ApiUsageRecordKey, Map<String, Long>> lastGaugesByServiceId = new HashMap<>();
    private final Map<ApiUsageRecordKey, Long> gaugesReportCycles = new HashMap<>();

    @Getter
    @Setter
    private ApiUsageState apiUsageState;
    @Getter
    private volatile long currentCycleTs;
    @Getter
    private volatile long nextCycleTs;
    @Getter
    private volatile long currentHourTs;

    @Setter
    private long gaugeReportInterval;

    public BaseApiUsageState(ApiUsageState apiUsageState) {
        this.apiUsageState = apiUsageState;
        this.currentCycleTs = SchedulerUtils.getStartOfCurrentMonth();
        this.nextCycleTs = SchedulerUtils.getStartOfNextMonth();
        this.currentHourTs = SchedulerUtils.getStartOfCurrentHour();
    }
    /**
     * Calculate.
     *
     * @param key key ({@link ApiUsageRecordKey})
     * @param value value
     * @param serviceId service id ({@link String})
     * @return {@link StatsCalculationResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    public StatsCalculationResult calculate(ApiUsageRecordKey key, long value, String serviceId) {
        long currentValue = get(key);
        long currentHourlyValue = getHourly(key);

        StatsCalculationResult result;
        if (key.isCounter()) {
            result = StatsCalculationResult.builder()
                    .newValue(currentValue + value).valueChanged(true)
                    .newHourlyValue(currentHourlyValue + value).hourlyValueChanged(true)
                    .build();
        } else {
            Long newGaugeValue = calculateGauge(key, value, serviceId);
            long newValue = newGaugeValue != null ? newGaugeValue : currentValue;
            long newHourlyValue = newGaugeValue != null ? Math.max(newGaugeValue, currentHourlyValue) : currentHourlyValue;
            result = StatsCalculationResult.builder()
                    .newValue(newValue).valueChanged(newValue != currentValue || !currentCycleValues.containsKey(key))
                    .newHourlyValue(newHourlyValue).hourlyValueChanged(newHourlyValue != currentHourlyValue || !currentHourValues.containsKey(key))
                    .build();
        }

        set(key, result.getNewValue());
        setHourly(key, result.getNewHourlyValue());
        return result;
    }

    private Long calculateGauge(ApiUsageRecordKey key, long value, String serviceId) {
        Map<String, Long> lastByServiceId = lastGaugesByServiceId.computeIfAbsent(key, k -> {
            gaugesReportCycles.put(key, System.currentTimeMillis());
            return new HashMap<>();
        });
        lastByServiceId.put(serviceId, value);

        Long gaugeReportCycle = gaugesReportCycles.get(key);
        if (gaugeReportCycle <= System.currentTimeMillis() - gaugeReportInterval) {
            long newValue = lastByServiceId.values().stream().mapToLong(Long::longValue).sum();
            lastGaugesByServiceId.remove(key);
            gaugesReportCycles.remove(key);
            return newValue;
        } else {
            return null;
        }
    }
    /**
     * Set.
     *
     * @param key key ({@link ApiUsageRecordKey})
     * @param value value ({@link Long})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void set(ApiUsageRecordKey key, Long value) {
        currentCycleValues.put(key, value);
    }
    /**
     * Returns the requested data.
     *
     * @param key key ({@link ApiUsageRecordKey})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    public long get(ApiUsageRecordKey key) {
        return currentCycleValues.getOrDefault(key, 0L);
    }
    /**
     * Set hourly.
     *
     * @param key key ({@link ApiUsageRecordKey})
     * @param value value ({@link Long})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setHourly(ApiUsageRecordKey key, Long value) {
        currentHourValues.put(key, value);
    }
    /**
     * Returns hourly.
     *
     * @param key key ({@link ApiUsageRecordKey})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    public long getHourly(ApiUsageRecordKey key) {
        return currentHourValues.getOrDefault(key, 0L);
    }
    /**
     * Set hour.
     *
     * @param currentHourTs current hour ts
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setHour(long currentHourTs) {
        this.currentHourTs = currentHourTs;
        currentHourValues.clear();
        lastGaugesByServiceId.clear();
        gaugesReportCycles.clear();
    }
    /**
     * Set cycles.
     *
     * @param currentCycleTs current cycle ts
     * @param nextCycleTs next cycle ts
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCycles(long currentCycleTs, long nextCycleTs) {
        this.currentCycleTs = currentCycleTs;
        this.nextCycleTs = nextCycleTs;
        currentCycleValues.clear();
    }
    /**
     * Handles repartition event.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void onRepartitionEvent() {
        lastGaugesByServiceId.clear();
        gaugesReportCycles.clear();
    }
    /**
     * Returns feature value.
     *
     * @param feature feature ({@link ApiFeature})
     * @return {@link ApiUsageStateValue}
     * @throws Exception if an unexpected error occurs during processing
     */

    public ApiUsageStateValue getFeatureValue(ApiFeature feature) {
        switch (feature) {
            case TRANSPORT:
                return apiUsageState.getTransportState();
            case RE:
                return apiUsageState.getReExecState();
            case DB:
                return apiUsageState.getDbStorageState();
            case JS:
                return apiUsageState.getJsExecState();
            case TBEL:
                return apiUsageState.getTbelExecState();
            case EMAIL:
                return apiUsageState.getEmailExecState();
            case SMS:
                return apiUsageState.getSmsExecState();
            case ALARM:
                return apiUsageState.getAlarmExecState();
            default:
                return ApiUsageStateValue.ENABLED;
        }
    }
    /**
     * Set feature value.
     *
     * @param feature feature ({@link ApiFeature})
     * @param value value ({@link ApiUsageStateValue})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean setFeatureValue(ApiFeature feature, ApiUsageStateValue value) {
        ApiUsageStateValue currentValue = getFeatureValue(feature);
        switch (feature) {
            case TRANSPORT:
                apiUsageState.setTransportState(value);
                break;
            case RE:
                apiUsageState.setReExecState(value);
                break;
            case DB:
                apiUsageState.setDbStorageState(value);
                break;
            case JS:
                apiUsageState.setJsExecState(value);
                break;
            case TBEL:
                apiUsageState.setTbelExecState(value);
                break;
            case EMAIL:
                apiUsageState.setEmailExecState(value);
                break;
            case SMS:
                apiUsageState.setSmsExecState(value);
                break;
            case ALARM:
                apiUsageState.setAlarmExecState(value);
                break;
        }
        return !currentValue.equals(value);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    public abstract EntityType getEntityType();
    /**
     * Returns tenant id.
     *
     * @return {@link TenantId}
     * @throws Exception if an unexpected error occurs during processing
     */

    public TenantId getTenantId() {
        return getApiUsageState().getTenantId();
    }
    /**
     * Returns entity id.
     *
     * @return {@link EntityId}
     * @throws Exception if an unexpected error occurs during processing
     */

    public EntityId getEntityId() {
        return getApiUsageState().getEntityId();
    }

    @Override
    public String toString() {
        return "BaseApiUsageState{" +
                "apiUsageState=" + apiUsageState +
                ", currentCycleTs=" + currentCycleTs +
                ", nextCycleTs=" + nextCycleTs +
                ", currentHourTs=" + currentHourTs +
                '}';
    }

    @Data
    @Builder
    public static class StatsCalculationResult {
        private final long newValue;
        private final boolean valueChanged;
        private final long newHourlyValue;
        private final boolean hourlyValueChanged;
    }

}
