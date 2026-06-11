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
package org.thingsboard.server.common.data;

import lombok.Getter;

/**
 * Keys for tenant API usage metering counters and limits.
 *
 * <p>Each constant maps to {@link ApiFeature} counters stored in {@link ApiUsageState}
 * and compared against tenant profile limits. Used by the usage reporting service.
 */
public enum ApiUsageRecordKey {

    /** Transport protocol messages processed. */
    TRANSPORT_MSG_COUNT(ApiFeature.TRANSPORT, "transportMsgCount", "transportMsgLimit", "message"),
    /** Transport telemetry data points ingested. */
    TRANSPORT_DP_COUNT(ApiFeature.TRANSPORT, "transportDataPointsCount", "transportDataPointsLimit", "data point"),
    /** Time-series data points persisted to storage. */
    STORAGE_DP_COUNT(ApiFeature.DB, "storageDataPointsCount", "storageDataPointsLimit", "data point"),
    /** Rule engine message executions. */
    RE_EXEC_COUNT(ApiFeature.RE, "ruleEngineExecutionCount", "ruleEngineExecutionLimit", "Rule Engine execution"),
    /** JavaScript function invocations in rule nodes. */
    JS_EXEC_COUNT(ApiFeature.JS, "jsExecutionCount", "jsExecutionLimit", "JavaScript execution"),
    /** TBEL expression executions in rule nodes. */
    TBEL_EXEC_COUNT(ApiFeature.TBEL, "tbelExecutionCount", "tbelExecutionLimit", "Tbel execution"),
    /** Outbound email messages sent. */
    EMAIL_EXEC_COUNT(ApiFeature.EMAIL, "emailCount", "emailLimit", "email message", true, true),
    /** Outbound SMS messages sent. */
    SMS_EXEC_COUNT(ApiFeature.SMS, "smsCount", "smsLimit", "SMS message", true, true),
    /** Alarms created within the billing period. */
    CREATED_ALARMS_COUNT(ApiFeature.ALARM, "createdAlarmsCount", "createdAlarmsLimit", "alarm"),
    /** Devices currently reporting as active. */
    ACTIVE_DEVICES("activeDevicesCount"),
    /** Devices currently reporting as inactive. */
    INACTIVE_DEVICES("inactiveDevicesCount");

    private static final ApiUsageRecordKey[] JS_RECORD_KEYS = {JS_EXEC_COUNT};
    private static final ApiUsageRecordKey[] TBEL_RECORD_KEYS = {TBEL_EXEC_COUNT};
    private static final ApiUsageRecordKey[] RE_RECORD_KEYS = {RE_EXEC_COUNT};
    private static final ApiUsageRecordKey[] DB_RECORD_KEYS = {STORAGE_DP_COUNT};
    private static final ApiUsageRecordKey[] TRANSPORT_RECORD_KEYS = {TRANSPORT_MSG_COUNT, TRANSPORT_DP_COUNT};
    private static final ApiUsageRecordKey[] EMAIL_RECORD_KEYS = {EMAIL_EXEC_COUNT};
    private static final ApiUsageRecordKey[] SMS_RECORD_KEYS = {SMS_EXEC_COUNT};
    private static final ApiUsageRecordKey[] ALARM_RECORD_KEYS = {CREATED_ALARMS_COUNT};

    @Getter
    private final ApiFeature apiFeature;
    @Getter
    private final String apiCountKey;
    @Getter
    private final String apiLimitKey;
    @Getter
    private final String unitLabel;
    @Getter
    private final boolean counter;
    @Getter
    private final boolean urgent; // urgent keys are reported at a shorter interval for quicker usage state updates

    ApiUsageRecordKey(ApiFeature apiFeature, String apiCountKey, String apiLimitKey, String unitLabel) {
        this(apiFeature, apiCountKey, apiLimitKey, unitLabel, true, false);
    }

    ApiUsageRecordKey(String apiCountKey) {
        this(null, apiCountKey, null, null, false, false);
    }

    ApiUsageRecordKey(ApiFeature apiFeature, String apiCountKey, String apiLimitKey, String unitLabel, boolean counter, boolean urgent) {
        this.apiFeature = apiFeature;
        this.apiCountKey = apiCountKey;
        this.apiLimitKey = apiLimitKey;
        this.unitLabel = unitLabel;
        this.counter = counter;
        this.urgent = urgent;
    }
    /**
     * Returns keys.
     *
     * @param feature feature ({@link ApiFeature})
     * @return the ApiUsageRecordKey[] value
     */

    public static ApiUsageRecordKey[] getKeys(ApiFeature feature) {
        switch (feature) {
            case TRANSPORT:
                return TRANSPORT_RECORD_KEYS;
            case DB:
                return DB_RECORD_KEYS;
            case RE:
                return RE_RECORD_KEYS;
            case JS:
                return JS_RECORD_KEYS;
            case TBEL:
                return TBEL_RECORD_KEYS;
            case EMAIL:
                return EMAIL_RECORD_KEYS;
            case SMS:
                return SMS_RECORD_KEYS;
            case ALARM:
                return ALARM_RECORD_KEYS;
            default:
                return new ApiUsageRecordKey[]{};
        }
    }

}
