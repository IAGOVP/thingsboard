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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmApiCallResult;
import org.thingsboard.server.common.data.alarm.AlarmCreateOrUpdateActiveRequest;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.alarm.AlarmModificationRequest;
import org.thingsboard.server.common.data.alarm.AlarmQuery;
import org.thingsboard.server.common.data.alarm.AlarmQueryV2;
import org.thingsboard.server.common.data.alarm.AlarmSearchStatus;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.alarm.AlarmStatusFilter;
import org.thingsboard.server.common.data.alarm.AlarmUpdateRequest;
import org.thingsboard.server.common.data.alarm.EntityAlarm;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ApiUsageLimitsExceededException;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.query.AlarmCountQuery;
import org.thingsboard.server.common.data.query.AlarmData;
import org.thingsboard.server.common.data.query.AlarmDataQuery;
import org.thingsboard.server.common.data.query.OriginatorAlarmFilter;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntityRelationsQuery;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationsSearchParameters;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.entity.EntityService;
import org.thingsboard.server.dao.eventsourcing.ActionEntityEvent;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.dao.service.ConstraintValidator;
import org.thingsboard.server.dao.tenant.TenantService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.thingsboard.server.dao.service.Validator.validateEntityDataPageLink;
import static org.thingsboard.server.dao.service.Validator.validateId;
/**
 * Default DAO-layer service implementation for alarm.
 *
 * <p>Coordinates validation, caching, cluster events, and {@code *Dao} persistence (alarm persistence, comments, and alarm-type caching).
 */


@Service("AlarmDaoService")
@Slf4j
@RequiredArgsConstructor
public class BaseAlarmService extends AbstractCachedEntityService<TenantId, PageData<EntitySubtype>, AlarmTypesCacheEvictEvent> implements AlarmService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    private static final PageLink DEFAULT_ALARM_TYPES_PAGE_LINK = new PageLink(25, 0, null, new SortOrder("type"));

    private final TenantService tenantService;
    private final AlarmDao alarmDao;
    private final EntityService entityService;

    
    /**
     * Handles evict event.
     *
     * @param event event ({@link AlarmTypesCacheEvictEvent})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @TransactionalEventListener
    public void handleEvictEvent(AlarmTypesCacheEvictEvent event) {
        TenantId tenantId = event.getTenantId();
        cache.evict(tenantId);
    }

    
    /**
     * Updates alarm.
     *
     * @param request request payload with operation parameters
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmApiCallResult updateAlarm(AlarmUpdateRequest request) {
        validateAlarmRequest(request);
        AlarmApiCallResult result = withPropagated(alarmDao.updateAlarm(request));
        if (result.getAlarm() != null) {
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(result.getAlarm().getTenantId()).entity(result)
                    .entityId(result.getAlarm().getId()).build());
        }
        return result;
    }

    
    /**
     * Creates alarm.
     *
     * @param request request payload with operation parameters
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmApiCallResult createAlarm(AlarmCreateOrUpdateActiveRequest request) {
        return createAlarm(request, true);
    }

    
    /**
     * Creates alarm.
     *
     * @param request request payload with operation parameters
     * @param alarmCreationEnabled alarm creation enabled
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmApiCallResult createAlarm(AlarmCreateOrUpdateActiveRequest request, boolean alarmCreationEnabled) {
        validateAlarmRequest(request);
        CustomerId customerId = entityService.fetchEntityCustomerId(request.getTenantId(), request.getOriginator()).orElse(null);
        if (customerId == null && request.getCustomerId() != null) {
            throw new DataValidationException("Can't assign alarm to customer. Originator is not assigned to customer!");
        } else if (customerId != null && request.getCustomerId() != null && !customerId.equals(request.getCustomerId())) {
            throw new DataValidationException("Can't assign alarm to customer. Originator belongs to different customer!");
        }
        request.setCustomerId(customerId);
        AlarmApiCallResult result = alarmDao.createOrUpdateActiveAlarm(request, alarmCreationEnabled);
        if (!result.isSuccessful() && !alarmCreationEnabled) {
            throw new ApiUsageLimitsExceededException("Alarms creation is disabled");
        }
        if (result.getAlarm() != null) {
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(result.getAlarm().getTenantId())
                    .entityId(result.getAlarm().getId()).entity(result).created(true).build());
            publishEvictEvent(new AlarmTypesCacheEvictEvent(request.getTenantId()));
        }
        return withPropagated(result);
    }

    
    /**
     * Acknowledge alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param ackTs ack ts
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmApiCallResult acknowledgeAlarm(TenantId tenantId, AlarmId alarmId, long ackTs) {
        var result = withPropagated(alarmDao.acknowledgeAlarm(tenantId, alarmId, ackTs));
        if (result.getAlarm() != null) {
            eventPublisher.publishEvent(ActionEntityEvent.builder()
                    .tenantId(tenantId)
                    .entityId(result.getAlarm().getId())
                    .entity(result.getAlarm())
                    .actionType(ActionType.ALARM_ACK)
                    .build());
        }
        return result;
    }

    
    /**
     * Clear alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param clearTs clear ts
     * @param details details ({@link JsonNode})
     * @param pushEvent push event
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmApiCallResult clearAlarm(TenantId tenantId, AlarmId alarmId, long clearTs, JsonNode details, boolean pushEvent) {
        var result = withPropagated(alarmDao.clearAlarm(tenantId, alarmId, clearTs, details));
        if (pushEvent && result.getAlarm() != null) {
            eventPublisher.publishEvent(ActionEntityEvent.builder()
                    .tenantId(tenantId)
                    .entityId(result.getAlarm().getId())
                    .entity(result.getAlarm())
                    .actionType(ActionType.ALARM_CLEAR)
                    .build());
        }
        return result;
    }

    
    /**
     * Finds latest active by originator and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Alarm findLatestActiveByOriginatorAndType(TenantId tenantId, EntityId originator, String type) {
        return alarmDao.findLatestActiveByOriginatorAndType(tenantId, originator, type);
    }

    
    /**
     * Finds latest active by originator and type async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originator originator ({@link EntityId})
     * @param type type ({@link String})
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public FluentFuture<Alarm> findLatestActiveByOriginatorAndTypeAsync(TenantId tenantId, EntityId originator, String type) {
        return alarmDao.findLatestActiveByOriginatorAndTypeAsync(tenantId, originator, type);
    }

    
    /**
     * Finds alarm data by query for entities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @param orderedEntityIds ordered entity ids ({@link Collection})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId,
                                                               AlarmDataQuery query, Collection<EntityId> orderedEntityIds) {
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateEntityDataPageLink(query.getPageLink());
        return alarmDao.findAlarmDataByQueryForEntities(tenantId, query, orderedEntityIds);
    }

    
    /**
     * Del alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public AlarmApiCallResult delAlarm(TenantId tenantId, AlarmId alarmId) {
        return delAlarm(tenantId, alarmId, true);
    }

    
    /**
     * Del alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param checkAndDeleteAlarmType check and delete alarm type
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public AlarmApiCallResult delAlarm(TenantId tenantId, AlarmId alarmId, boolean checkAndDeleteAlarmType) {
        AlarmInfo alarm = alarmDao.findAlarmInfoById(tenantId, alarmId.getId());
        return deleteAlarm(tenantId, alarm, checkAndDeleteAlarmType);
    }

    private AlarmApiCallResult deleteAlarm(TenantId tenantId, AlarmInfo alarm, boolean deleteAlarmTypes) {
        if (alarm == null) {
            return AlarmApiCallResult.builder().successful(false).build();
        } else {
            log.debug("[{}][{}] Executing deleteAlarm [{}]", tenantId, alarm.getOriginator(), alarm.getId());
            var propagationIds = getPropagationEntityIdsList(alarm);
            alarmDao.removeById(tenantId, alarm.getUuidId());
            eventPublisher.publishEvent(DeleteEntityEvent.builder()
                    .tenantId(tenantId)
                    .entityId(alarm.getId())
                    .entity(alarm)
                    .build());
            eventPublisher.publishEvent(ActionEntityEvent.builder()
                    .tenantId(tenantId)
                    .entityId(alarm.getId())
                    .entity(alarm)
                    .actionType(ActionType.ALARM_DELETE)
                    .build());
            if (deleteAlarmTypes) {
                delAlarmTypes(tenantId, Collections.singleton(alarm.getType()));
            }
            return AlarmApiCallResult.builder().alarm(alarm).deleted(true).successful(true).propagatedEntitiesList(propagationIds).build();
        }
    }

    
    /**
     * Del alarm types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param types types ({@link Set})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public void delAlarmTypes(TenantId tenantId, Set<String> types) {
        if (!types.isEmpty() && alarmDao.removeAlarmTypesIfNoAlarmsPresent(tenantId.getId(), types)) {
            publishEvictEvent(new AlarmTypesCacheEvictEvent(tenantId));
        }
    }

    private List<EntityId> createEntityAlarmRecords(Alarm alarm) throws ExecutionException, InterruptedException {
        Set<EntityId> propagatedEntitiesSet = new LinkedHashSet<>();
        propagatedEntitiesSet.add(alarm.getOriginator());
        if (alarm.isPropagate()) {
            propagatedEntitiesSet.addAll(getRelatedEntities(alarm));
        }
        if (alarm.isPropagateToOwner()) {
            propagatedEntitiesSet.add(alarm.getCustomerId() != null ? alarm.getCustomerId() : alarm.getTenantId());
        }
        if (alarm.isPropagateToTenant()) {
            propagatedEntitiesSet.add(alarm.getTenantId());
        }
        for (EntityId entityId : propagatedEntitiesSet) {
            createEntityAlarmRecord(alarm.getTenantId(), entityId, alarm);
        }
        return new ArrayList<>(propagatedEntitiesSet);
    }

    private Set<EntityId> getRelatedEntities(Alarm alarm) throws InterruptedException, ExecutionException {
        EntityRelationsQuery query = new EntityRelationsQuery();
        RelationsSearchParameters parameters = new RelationsSearchParameters(alarm.getOriginator(), EntitySearchDirection.TO, Integer.MAX_VALUE, false);
        query.setParameters(parameters);
        List<String> propagateRelationTypes = alarm.getPropagateRelationTypes();
        Stream<EntityRelation> relations = relationService.findByQuery(alarm.getTenantId(), query).get().stream();
        if (!CollectionUtils.isEmpty(propagateRelationTypes)) {
            relations = relations.filter(entityRelation -> propagateRelationTypes.contains(entityRelation.getType()));
        }
        return relations.map(EntityRelation::getFrom).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    
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


    @Override
    public AlarmApiCallResult assignAlarm(TenantId tenantId, AlarmId alarmId, UserId assigneeId, long assignTime) {
        var result = withPropagated(alarmDao.assignAlarm(tenantId, alarmId, assigneeId, assignTime));
        if (result.getAlarm() != null) {
            eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).entityId(result.getAlarm().getId())
                    .actionType(ActionType.ALARM_ASSIGNED).build());
        }
        return result;
    }

    
    /**
     * Unassigns alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @param unassignTime unassign time
     * @return {@link AlarmApiCallResult}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmApiCallResult unassignAlarm(TenantId tenantId, AlarmId alarmId, long unassignTime) {
        var result = withPropagated(alarmDao.unassignAlarm(tenantId, alarmId, unassignTime));
        if (result.getAlarm() != null) {
            eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).entityId(result.getAlarm().getId())
                    .actionType(ActionType.ALARM_UNASSIGNED).build());
        }
        return result;
    }

    
    /**
     * Finds alarm by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Alarm findAlarmById(TenantId tenantId, AlarmId alarmId) {
        log.trace("Executing findAlarmById [{}]", alarmId);
        validateId(alarmId, id -> "Incorrect alarmId " + id);
        return alarmDao.findAlarmById(tenantId, alarmId.getId());
    }

    
    /**
     * Finds alarm by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return future completing with {@link Alarm}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<Alarm> findAlarmByIdAsync(TenantId tenantId, AlarmId alarmId) {
        log.trace("Executing findAlarmByIdAsync [{}]", alarmId);
        validateId(alarmId, id -> "Incorrect alarmId " + id);
        return alarmDao.findAlarmByIdAsync(tenantId, alarmId.getId());
    }

    
    /**
     * Finds alarm info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link AlarmId})
     * @return {@link AlarmInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmInfo findAlarmInfoById(TenantId tenantId, AlarmId alarmId) {
        log.trace("Executing findAlarmInfoByIdAsync [{}]", alarmId);
        validateId(alarmId, id -> "Incorrect alarmId " + id);
        return alarmDao.findAlarmInfoById(tenantId, alarmId.getId());
    }

    
    /**
     * Finds alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AlarmInfo> findAlarms(TenantId tenantId, AlarmQuery query) {
        return alarmDao.findAlarms(tenantId, query);
    }

    
    /**
     * Finds customer alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AlarmInfo> findCustomerAlarms(TenantId tenantId, CustomerId customerId, AlarmQuery query) {
        return alarmDao.findCustomerAlarms(tenantId, customerId, query);
    }

    
    /**
     * Finds alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AlarmInfo> findAlarmsV2(TenantId tenantId, AlarmQueryV2 query) {
        return alarmDao.findAlarmsV2(tenantId, query);
    }

    
    /**
     * Finds customer alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AlarmInfo> findCustomerAlarmsV2(TenantId tenantId, CustomerId customerId, AlarmQueryV2 query) {
        return alarmDao.findCustomerAlarmsV2(tenantId, customerId, query);
    }

    
    /**
     * Finds alarm ids by assignee id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @param createdTimeOffset created time offset
     * @param idOffset cursor for batch id scan (exclusive lower bound)
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<TbPair<UUID, Long>> findAlarmIdsByAssigneeId(TenantId tenantId, UserId userId, long createdTimeOffset, AlarmId idOffset, int limit) {
        log.trace("[{}] Executing findAlarmIdsByAssigneeId [{}][{}]", tenantId, userId, idOffset);
        validateId(userId, id -> "Incorrect userId " + id);
        return alarmDao.findAlarmIdsByAssigneeId(tenantId, userId, createdTimeOffset, idOffset, limit).getData();
    }

    
    /**
     * Finds alarm ids by originator id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorId originator id ({@link EntityId})
     * @param createdTimeOffset created time offset
     * @param idOffset cursor for batch id scan (exclusive lower bound)
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<TbPair<UUID, Long>> findAlarmIdsByOriginatorId(TenantId tenantId, EntityId originatorId, long createdTimeOffset, AlarmId idOffset, int limit) {
        log.trace("[{}] Executing findAlarmIdsByOriginatorIdAndIdOffset [{}][{}]", tenantId, originatorId, idOffset);
        return alarmDao.findAlarmIdsByOriginatorId(tenantId, originatorId, createdTimeOffset, idOffset, limit).getData();
    }

    
    /**
     * Finds highest alarm severity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param alarmSearchStatus alarm search status ({@link AlarmSearchStatus})
     * @param alarmStatus alarm status ({@link AlarmStatus})
     * @param assigneeId assignee id ({@link String})
     * @return {@link AlarmSeverity}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmSeverity findHighestAlarmSeverity(TenantId tenantId, EntityId entityId, AlarmSearchStatus alarmSearchStatus,
                                                  AlarmStatus alarmStatus, String assigneeId) {
        AlarmStatusFilter asf;
        if (alarmSearchStatus != null) {
            asf = AlarmStatusFilter.from(alarmSearchStatus);
        } else if (alarmStatus != null) {
            asf = AlarmStatusFilter.from(alarmStatus);
        } else {
            asf = AlarmStatusFilter.empty();
        }

        Set<AlarmSeverity> alarmSeverities = alarmDao.findAlarmSeverities(tenantId, entityId, asf, assigneeId);
        return alarmSeverities.stream().min(AlarmSeverity::compareTo).orElse(null);
    }

    
    /**
     * Deletes entity alarm records.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public int deleteEntityAlarmRecords(TenantId tenantId, EntityId entityId) {
        log.trace("Executing deleteEntityAlarms [{}]", entityId);
        return alarmDao.deleteEntityAlarmRecords(tenantId, entityId);
    }

    
    /**
     * Deletes entity alarm records by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteEntityAlarmRecordsByTenantId(TenantId tenantId) {
        log.trace("Executing deleteEntityAlarmRecordsByTenantId [{}]", tenantId);
        alarmDao.deleteEntityAlarmRecordsByTenantId(tenantId);
    }

    
    /**
     * Counts alarms by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param query filter and sort query definition
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public long countAlarmsByQuery(TenantId tenantId, CustomerId customerId, AlarmCountQuery query) {
        return countAlarmsByQuery(tenantId, customerId, query, null);
    }

    
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


    @Override
    public long countAlarmsByQuery(TenantId tenantId, CustomerId customerId, AlarmCountQuery query, Collection<EntityId> orderedEntityIds) {
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        return alarmDao.countAlarmsByQuery(tenantId, customerId, query, orderedEntityIds);
    }

    
    /**
     * Finds alarm types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntitySubtype> findAlarmTypesByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findAlarmTypesByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        if (DEFAULT_ALARM_TYPES_PAGE_LINK.equals(pageLink)) {
            return cache.getAndPutInTransaction(tenantId, () ->
                    alarmDao.findTenantAlarmTypes(tenantId.getId(), pageLink), false);
        }
        return alarmDao.findTenantAlarmTypes(tenantId.getId(), pageLink);
    }

    
    /**
     * Finds active originator alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorAlarmFilter originator alarm filter ({@link OriginatorAlarmFilter})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<UUID> findActiveOriginatorAlarms(TenantId tenantId, OriginatorAlarmFilter originatorAlarmFilter, int limit) {
        log.trace("Executing findActiveOriginatorAlarms, tenantId [{}], originatorAlarmFilter [{}]", tenantId, originatorAlarmFilter);
        return alarmDao.findActiveOriginatorAlarms(tenantId, originatorAlarmFilter, limit);
    }

    private Alarm merge(Alarm existing, Alarm alarm) {
        if (alarm.getStartTs() > existing.getEndTs()) {
            existing.setEndTs(alarm.getStartTs());
        }
        if (alarm.getEndTs() > existing.getEndTs()) {
            existing.setEndTs(alarm.getEndTs());
        }
        if (alarm.getClearTs() > existing.getClearTs()) {
            existing.setClearTs(alarm.getClearTs());
        }
        if (alarm.getAckTs() > existing.getAckTs()) {
            existing.setAckTs(alarm.getAckTs());
        }
        if (alarm.getAssignTs() > existing.getAssignTs()) {
            existing.setAssignTs(alarm.getAssignTs());
        }
        existing.setAcknowledged(alarm.isAcknowledged());
        existing.setCleared(alarm.isCleared());
        existing.setSeverity(alarm.getSeverity());
        existing.setDetails(alarm.getDetails());
        existing.setCustomerId(alarm.getCustomerId());
        existing.setAssigneeId(alarm.getAssigneeId());
        existing.setPropagate(existing.isPropagate() || alarm.isPropagate());
        existing.setPropagateToOwner(existing.isPropagateToOwner() || alarm.isPropagateToOwner());
        existing.setPropagateToTenant(existing.isPropagateToTenant() || alarm.isPropagateToTenant());
        List<String> existingPropagateRelationTypes = existing.getPropagateRelationTypes();
        List<String> newRelationTypes = alarm.getPropagateRelationTypes();
        if (!CollectionUtils.isEmpty(newRelationTypes)) {
            if (!CollectionUtils.isEmpty(existingPropagateRelationTypes)) {
                existing.setPropagateRelationTypes(Stream.concat(existingPropagateRelationTypes.stream(), newRelationTypes.stream())
                        .distinct()
                        .collect(Collectors.toList()));
            } else {
                existing.setPropagateRelationTypes(newRelationTypes);
            }
        }
        return existing;
    }

    private List<EntityId> getPropagationEntityIdsList(Alarm alarm) {
        return new ArrayList<>(getPropagationEntityIds(alarm));
    }

    private Set<EntityId> getPropagationEntityIds(Alarm alarm) {
        if (alarm.isPropagate() || alarm.isPropagateToOwner() || alarm.isPropagateToTenant()) {
            List<EntityAlarm> entityAlarms = alarmDao.findEntityAlarmRecords(alarm.getTenantId(), alarm.getId());
            return entityAlarms.stream().map(EntityAlarm::getEntityId).collect(Collectors.toSet());
        } else {
            return Collections.singleton(alarm.getOriginator());
        }
    }

    private void createEntityAlarmRecord(TenantId tenantId, EntityId entityId, Alarm alarm) {
        EntityAlarm entityAlarm = new EntityAlarm(tenantId, entityId, alarm.getCreatedTime(), alarm.getType(), alarm.getCustomerId(), null, alarm.getId());
        try {
            alarmDao.createEntityAlarmRecord(entityAlarm);
        } catch (Exception e) {
            log.warn("[{}] Failed to create entity alarm record: {}", tenantId, entityAlarm, e);
        }
    }

    
    /**
     * Finds entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return optional {@link HasId}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findAlarmById(tenantId, new AlarmId(entityId.getId())));
    }

    
    /**
     * Finds entity async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(findAlarmByIdAsync(tenantId, new AlarmId(entityId.getId())))
                .transform(Optional::ofNullable, directExecutor());
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.ALARM;
    }

    //TODO: refactor to use efficient caching.
    private AlarmApiCallResult withPropagated(AlarmApiCallResult result) {
        if (result.isSuccessful() && result.getAlarm() != null) {
            List<EntityId> propagationEntities;
            if (result.isPropagationChanged()) {
                try {
                    propagationEntities = createEntityAlarmRecords(result.getAlarm());
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                propagationEntities = getPropagationEntityIdsList(result.getAlarm());
            }
            return new AlarmApiCallResult(result, propagationEntities);
        } else {
            return result;
        }
    }

    private void validateAlarmRequest(AlarmModificationRequest request) {
        ConstraintValidator.validateFields(request);
        if (request.getEndTs() > 0 && request.getStartTs() > request.getEndTs()) {
            throw new DataValidationException("Alarm start ts can't be greater then alarm end ts!");
        }
        if (!tenantService.tenantExists(request.getTenantId())) {
            throw new DataValidationException("Alarm is referencing to non-existent tenant!");
        }
        if (request.getStartTs() == 0L) {
            request.setStartTs(System.currentTimeMillis());
        }
        if (request.getEndTs() == 0L) {
            request.setEndTs(request.getStartTs());
        }
    }

}
