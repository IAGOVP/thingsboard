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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmApiCallResult;
import org.thingsboard.server.common.data.alarm.AlarmCreateOrUpdateActiveRequest;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.alarm.AlarmQuery;
import org.thingsboard.server.common.data.alarm.AlarmQueryV2;
import org.thingsboard.server.common.data.alarm.AlarmSearchStatus;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.alarm.AlarmUpdateRequest;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.query.AlarmData;
import org.thingsboard.server.common.data.query.AlarmDataQuery;

import java.util.Collection;


/**

 * Facade for creating, updating, and clearing alarms from rule nodes.

 */


public interface RuleEngineAlarmService {

    /*
     *  New API, since 3.5.
     */

    
    /**
     * Creates alarm.
     *
     * @param request async service request DTO
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult createAlarm(AlarmCreateOrUpdateActiveRequest request);

    
    /**
     * Updates alarm.
     *
     * @param request async service request DTO
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult updateAlarm(AlarmUpdateRequest request);
    /**
     * Acknowledge alarm.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @param ackTs ack ts
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult acknowledgeAlarm(TenantId tenantId, AlarmId alarmId, long ackTs);
    /**
     * Clear alarm.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @param clearTs clear ts
     * @param details details ({@link JsonNode})
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult clearAlarm(TenantId tenantId, AlarmId alarmId, long clearTs, JsonNode details);
    /**
     * Clear alarm.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @param clearTs clear ts
     * @param details details ({@link JsonNode})
     * @param pushEvent push event
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult clearAlarm(TenantId tenantId, AlarmId alarmId, long clearTs, JsonNode details, boolean pushEvent);
    /**
     * Assigns alarm.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @param assigneeId assignee id ({@link UserId})
     * @param assignTs assign ts
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult assignAlarm(TenantId tenantId, AlarmId alarmId, UserId assigneeId, long assignTs);
    /**
     * Unassigns alarm.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @param assignTs assign ts
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult unassignAlarm(TenantId tenantId, AlarmId alarmId, long assignTs);

    // Other API
    /**
     * Deletes alarm.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */
    boolean deleteAlarm(TenantId tenantId, AlarmId alarmId);
    /**
     * Finds alarm by id async.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @return future completing with {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Alarm> findAlarmByIdAsync(TenantId tenantId, AlarmId alarmId);
    /**
     * Finds alarm by id.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    Alarm findAlarmById(TenantId tenantId, AlarmId alarmId);
    /**
     * Finds latest active by originator and type.
     *
     * @param tenantId tenant UUID
     * @param originator message originator entity id
     * @param type type ({@link String})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    Alarm findLatestActiveByOriginatorAndType(TenantId tenantId, EntityId originator, String type);
    /**
     * Finds latest active by originator and type async.
     *
     * @param tenantId tenant UUID
     * @param originator message originator entity id
     * @param type type ({@link String})
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    FluentFuture<Alarm> findLatestActiveByOriginatorAndTypeAsync(TenantId tenantId, EntityId originator, String type);
    /**
     * Finds latest by originator and type.
     *
     * @param tenantId tenant UUID
     * @param originator message originator entity id
     * @param type type ({@link String})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    Alarm findLatestByOriginatorAndType(TenantId tenantId, EntityId originator, String type);
    /**
     * Finds alarm info by id.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link AlarmInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmInfo findAlarmInfoById(TenantId tenantId, AlarmId alarmId);
    /**
     * Finds alarm info by id async.
     *
     * @param tenantId tenant UUID
     * @param alarmId alarm id ({@link AlarmId})
     * @return future completing with {@link AlarmInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    default ListenableFuture<AlarmInfo> findAlarmInfoByIdAsync(TenantId tenantId, AlarmId alarmId) {
        return Futures.immediateFuture(findAlarmInfoById(tenantId, alarmId));
    }
    /**
     * Finds alarms.
     *
     * @param tenantId tenant UUID
     * @param query query ({@link AlarmQuery})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findAlarms(TenantId tenantId, AlarmQuery query);
    /**
     * Finds customer alarms.
     *
     * @param tenantId tenant UUID
     * @param customerId customer id ({@link CustomerId})
     * @param query query ({@link AlarmQuery})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findCustomerAlarms(TenantId tenantId, CustomerId customerId, AlarmQuery query);
    /**
     * Finds alarms v2.
     *
     * @param tenantId tenant UUID
     * @param query query ({@link AlarmQueryV2})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findAlarmsV2(TenantId tenantId, AlarmQueryV2 query);
    /**
     * Finds customer alarms v2.
     *
     * @param tenantId tenant UUID
     * @param customerId customer id ({@link CustomerId})
     * @param query query ({@link AlarmQueryV2})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findCustomerAlarmsV2(TenantId tenantId, CustomerId customerId, AlarmQueryV2 query);
    /**
     * Finds highest alarm severity.
     *
     * @param tenantId tenant UUID
     * @param entityId target entity identifier
     * @param alarmSearchStatus alarm search status ({@link AlarmSearchStatus})
     * @param alarmStatus alarm status ({@link AlarmStatus})
     * @param assigneeId assignee id ({@link String})
     * @return {@link AlarmSeverity}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmSeverity findHighestAlarmSeverity(TenantId tenantId, EntityId entityId, AlarmSearchStatus alarmSearchStatus, AlarmStatus alarmStatus, String assigneeId);
    /**
     * Finds alarm data by query for entities.
     *
     * @param tenantId tenant UUID
     * @param query query ({@link AlarmDataQuery})
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId, AlarmDataQuery query, Collection<EntityId> orderedEntityIds);
    /**
     * Finds alarm types by tenant id.
     *
     * @param tenantId tenant UUID
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntitySubtype> findAlarmTypesByTenantId(TenantId tenantId, PageLink pageLink);

}
