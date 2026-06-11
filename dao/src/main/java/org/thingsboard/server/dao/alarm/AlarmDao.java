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
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatusFilter;
import org.thingsboard.server.common.data.alarm.AlarmUpdateRequest;
import org.thingsboard.server.common.data.alarm.EntityAlarm;
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
import org.thingsboard.server.dao.Dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**

 * Persistence contract for alarm.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (alarm persistence, comments, and alarm-type caching).

 */


public interface AlarmDao extends Dao<Alarm> {
    /**
     * Finds latest by originator and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    Alarm findLatestByOriginatorAndType(TenantId tenantId, EntityId originator, String type);
    /**
     * Finds latest active by originator and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    Alarm findLatestActiveByOriginatorAndType(TenantId tenantId, EntityId originator, String type);
    /**
     * Finds latest active by originator and type async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    FluentFuture<Alarm> findLatestActiveByOriginatorAndTypeAsync(TenantId tenantId, EntityId originator, String type);
    /**
     * Finds latest by originator and type async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return future completing with {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Alarm> findLatestByOriginatorAndTypeAsync(TenantId tenantId, EntityId originator, String type);
    /**
     * Finds alarm by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    Alarm findAlarmById(TenantId tenantId, UUID key);
    /**
     * Finds alarm by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return future completing with {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Alarm> findAlarmByIdAsync(TenantId tenantId, UUID key);
    /**
     * Finds alarm info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link AlarmInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmInfo findAlarmInfoById(TenantId tenantId, UUID key);
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarm alarm ({@link Alarm})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */

    Alarm save(TenantId tenantId, Alarm alarm);
    /**
     * Finds alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findAlarms(TenantId tenantId, AlarmQuery query);
    /**
     * Finds customer alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findCustomerAlarms(TenantId tenantId, CustomerId customerId, AlarmQuery query);
    /**
     * Finds alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findAlarmsV2(TenantId tenantId, AlarmQueryV2 query);
    /**
     * Finds customer alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmInfo> findCustomerAlarmsV2(TenantId tenantId, CustomerId customerId, AlarmQueryV2 query);
    /**
     * Finds alarm data by query for entities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId, AlarmDataQuery query, Collection<EntityId> orderedEntityIds);
    /**
     * Finds alarm severities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param asf asf ({@link AlarmStatusFilter})
     * @param assigneeId assignee id ({@link String})
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    Set<AlarmSeverity> findAlarmSeverities(TenantId tenantId, EntityId entityId, AlarmStatusFilter asf, String assigneeId);
    /**
     * Finds alarms ids by end ts before and tenant id.
     *
     * @param time time ({@link Long})
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmId> findAlarmsIdsByEndTsBeforeAndTenantId(Long time, TenantId tenantId, PageLink pageLink);
    /**
     * Finds alarm ids by assignee id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @param createdTimeOffset created time offset
     * @param idOffset cursor for batch id scan (exclusive lower bound)
     * @param limit maximum number of records to return
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TbPair<UUID, Long>> findAlarmIdsByAssigneeId(TenantId tenantId, UserId userId, long createdTimeOffset, AlarmId idOffset, int limit);
    /**
     * Finds alarm ids by originator id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorId originator id ({@link EntityId})
     * @param createdTimeOffset created time offset
     * @param idOffset cursor for batch id scan (exclusive lower bound)
     * @param limit maximum number of records to return
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TbPair<UUID, Long>> findAlarmIdsByOriginatorId(TenantId tenantId, EntityId originatorId, long createdTimeOffset, AlarmId idOffset, int limit);
    /**
     * Creates entity alarm record.
     *
     * @param entityAlarm entity alarm ({@link EntityAlarm})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void createEntityAlarmRecord(EntityAlarm entityAlarm);
    /**
     * Finds entity alarm records.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityAlarm> findEntityAlarmRecords(TenantId tenantId, AlarmId id);
    /**
     * Finds entity alarm records by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<EntityAlarm> findEntityAlarmRecordsByEntityId(TenantId tenantId, EntityId entityId);
    /**
     * Deletes entity alarm records.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int deleteEntityAlarmRecords(TenantId tenantId, EntityId entityId);
    /**
     * Deletes entity alarm records by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteEntityAlarmRecordsByTenantId(TenantId tenantId);
    /**
     * Creates or update active alarm.
     *
     * @param request request payload with operation parameters
     * @param alarmCreationEnabled alarm creation enabled
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult createOrUpdateActiveAlarm(AlarmCreateOrUpdateActiveRequest request, boolean alarmCreationEnabled);
    /**
     * Updates alarm.
     *
     * @param request request payload with operation parameters
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult updateAlarm(AlarmUpdateRequest request);
    /**
     * Acknowledge alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param ackTs ack ts
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult acknowledgeAlarm(TenantId tenantId, AlarmId id, long ackTs);
    /**
     * Clear alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param clearTs clear ts
     * @param details details ({@link JsonNode})
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult clearAlarm(TenantId tenantId, AlarmId alarmId, long clearTs, JsonNode details);
    /**
     * Assigns alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param assigneeId assignee id ({@link UserId})
     * @param assignTime assign time
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult assignAlarm(TenantId tenantId, AlarmId alarmId, UserId assigneeId, long assignTime);
    /**
     * Unassigns alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param unassignTime unassign time
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmApiCallResult unassignAlarm(TenantId tenantId, AlarmId alarmId, long unassignTime);
    /**
     * Counts alarms by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    long countAlarmsByQuery(TenantId tenantId, CustomerId customerId, AlarmCountQuery query, Collection<EntityId> orderedEntityIds);
    /**
     * Finds tenant alarm types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<EntitySubtype> findTenantAlarmTypes(UUID tenantId, PageLink pageLink);
    /**
     * Removes alarm types if no alarms present.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param types types ({@link Set})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean removeAlarmTypesIfNoAlarmsPresent(UUID tenantId, Set<String> types);
    /**
     * Finds active originator alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorAlarmFilter originator alarm filter ({@link OriginatorAlarmFilter})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<UUID> findActiveOriginatorAlarms(TenantId tenantId, OriginatorAlarmFilter originatorAlarmFilter, int limit);

}
