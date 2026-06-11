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
package org.thingsboard.server.dao.alarm;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.FluentFuture;
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
import org.thingsboard.server.common.data.query.AlarmCountQuery;
import org.thingsboard.server.common.data.query.AlarmData;
import org.thingsboard.server.common.data.query.AlarmDataQuery;
import org.thingsboard.server.common.data.query.OriginatorAlarmFilter;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Service API for alarm persistence and domain operations.
 */
public interface AlarmService extends EntityDaoService {

    /**
     * Designed for atomic operations over active alarms.
     * Only one active alarm may exist for the pair {originatorId, alarmType}
     */
    AlarmApiCallResult createAlarm(AlarmCreateOrUpdateActiveRequest request);

    /**
     * Designed for atomic operations over active alarms.
     * Only one active alarm may exist for the pair {originatorId, alarmType}
     */
    AlarmApiCallResult createAlarm(AlarmCreateOrUpdateActiveRequest request, boolean alarmCreationEnabled);

    /**
     * Designed to update existing alarm. Accepts only part of the alarm fields.
     */
    AlarmApiCallResult updateAlarm(AlarmUpdateRequest request);

    /**
     * Acknowledge alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param ackTs ack ts
     * @return {@link AlarmApiCallResult}
     */
    AlarmApiCallResult acknowledgeAlarm(TenantId tenantId, AlarmId alarmId, long ackTs);

    /**
     * Clear alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param clearTs clear ts
     * @param details details ({@link JsonNode})
     * @param pushEvent push event
     * @return {@link AlarmApiCallResult}
     */
    AlarmApiCallResult clearAlarm(TenantId tenantId, AlarmId alarmId, long clearTs, JsonNode details, boolean pushEvent);

    /**
     * Assigns alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param assigneeId assignee id ({@link UserId})
     * @param ts ts
     * @return {@link AlarmApiCallResult}
     */
    AlarmApiCallResult assignAlarm(TenantId tenantId, AlarmId alarmId, UserId assigneeId, long ts);

    /**
     * Unassigns alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param ts ts
     * @return {@link AlarmApiCallResult}
     */
    AlarmApiCallResult unassignAlarm(TenantId tenantId, AlarmId alarmId, long ts);

    /**
     * Del alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link AlarmApiCallResult}
     */
    AlarmApiCallResult delAlarm(TenantId tenantId, AlarmId alarmId);

    /**
     * Del alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param checkAndDeleteAlarmType check and delete alarm type
     * @return {@link AlarmApiCallResult}
     */
    AlarmApiCallResult delAlarm(TenantId tenantId, AlarmId alarmId, boolean checkAndDeleteAlarmType);

    /**
     * Del alarm types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param types types ({@link Set})
     */
    void delAlarmTypes(TenantId tenantId, Set<String> types);

    // Other API
    /**
     * Finds alarm by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link Alarm}
     */
    Alarm findAlarmById(TenantId tenantId, AlarmId alarmId);

    /**
     * Finds alarm by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return future completing with {@link Alarm}
     */
    ListenableFuture<Alarm> findAlarmByIdAsync(TenantId tenantId, AlarmId alarmId);

    /**
     * Finds alarm info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link AlarmInfo}
     */
    AlarmInfo findAlarmInfoById(TenantId tenantId, AlarmId alarmId);

    /**
     * Finds alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link AlarmQuery})
     * @return {@link PageData}
     */
    PageData<AlarmInfo> findAlarms(TenantId tenantId, AlarmQuery query);

    /**
     * Finds customer alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param query query ({@link AlarmQuery})
     * @return {@link PageData}
     */
    PageData<AlarmInfo> findCustomerAlarms(TenantId tenantId, CustomerId customerId, AlarmQuery query);

    /**
     * Finds alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link AlarmQueryV2})
     * @return {@link PageData}
     */
    PageData<AlarmInfo> findAlarmsV2(TenantId tenantId, AlarmQueryV2 query);

    /**
     * Finds customer alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param query query ({@link AlarmQueryV2})
     * @return {@link PageData}
     */
    PageData<AlarmInfo> findCustomerAlarmsV2(TenantId tenantId, CustomerId customerId, AlarmQueryV2 query);

    /**
     * Finds highest alarm severity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param alarmSearchStatus alarm search status ({@link AlarmSearchStatus})
     * @param alarmStatus alarm status ({@link AlarmStatus})
     * @param assigneeId assignee id ({@link String})
     * @return {@link AlarmSeverity}
     */
    AlarmSeverity findHighestAlarmSeverity(TenantId tenantId, EntityId entityId, AlarmSearchStatus alarmSearchStatus,
                                           AlarmStatus alarmStatus, String assigneeId);

    /**
     * Finds latest active by originator and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return {@link Alarm}
     */
    Alarm findLatestActiveByOriginatorAndType(TenantId tenantId, EntityId originator, String type);

    /**
     * Finds latest active by originator and type async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return {@link FluentFuture}
     */
    FluentFuture<Alarm> findLatestActiveByOriginatorAndTypeAsync(TenantId tenantId, EntityId originator, String type);

    /**
     * Finds alarm data by query for entities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query query ({@link AlarmDataQuery})
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return {@link PageData}
     */
    PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId,
                                                        AlarmDataQuery query, Collection<EntityId> orderedEntityIds);

    /**
     * Finds alarm ids by assignee id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId user id ({@link UserId})
     * @param createdTimeOffset created time offset
     * @param idOffset id offset ({@link AlarmId})
     * @param limit limit
     * @return {@link List}
     */
    List<TbPair<UUID, Long>> findAlarmIdsByAssigneeId(TenantId tenantId, UserId userId, long createdTimeOffset, AlarmId idOffset, int limit);

    /**
     * Finds alarm ids by originator id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorId originator id ({@link EntityId})
     * @param createdTimeOffset created time offset
     * @param idOffset id offset ({@link AlarmId})
     * @param limit limit
     * @return {@link List}
     */
    List<TbPair<UUID, Long>> findAlarmIdsByOriginatorId(TenantId tenantId, EntityId originatorId, long createdTimeOffset, AlarmId idOffset, int limit);

    /**
     * Deletes entity alarm records.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return the int result
     */
    int deleteEntityAlarmRecords(TenantId tenantId, EntityId entityId);

    /**
     * Deletes entity alarm records by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteEntityAlarmRecordsByTenantId(TenantId tenantId);

    /**
     * Counts alarms by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param query query ({@link AlarmCountQuery})
     * @return the long result
     */
    long countAlarmsByQuery(TenantId tenantId, CustomerId customerId, AlarmCountQuery query);

    /**
     * Counts alarms by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId customer to assign or filter by
     * @param query query ({@link AlarmCountQuery})
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return the long result
     */
    long countAlarmsByQuery(TenantId tenantId, CustomerId customerId, AlarmCountQuery query, Collection<EntityId> orderedEntityIds);

    /**
     * Finds alarm types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EntitySubtype> findAlarmTypesByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds active originator alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorAlarmFilter originator alarm filter ({@link OriginatorAlarmFilter})
     * @param limit limit
     * @return {@link List}
     */
    List<UUID> findActiveOriginatorAlarms(TenantId tenantId, OriginatorAlarmFilter originatorAlarmFilter, int limit);

}
