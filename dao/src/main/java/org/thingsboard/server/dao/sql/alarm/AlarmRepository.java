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
package org.thingsboard.server.dao.sql.alarm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.model.sql.AlarmEntity;
import org.thingsboard.server.dao.model.sql.AlarmInfoEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;


/**

 * Spring Data JPA repository for alarm entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface AlarmRepository extends JpaRepository<AlarmEntity, UUID> {
    /**
     * Finds latest by originator and type.
     *
     * @param originatorId originator id ({@link UUID})
     * @param alarmType alarm type ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT a FROM AlarmEntity a WHERE a.originatorId = :originatorId AND a.type = :alarmType ORDER BY a.startTs DESC")
    List<AlarmEntity> findLatestByOriginatorAndType(@Param("originatorId") UUID originatorId,
                                                    @Param("alarmType") String alarmType,
                                                    Pageable pageable);
    /**
     * Finds latest active by originator and type.
     *
     * @param originatorId originator id ({@link UUID})
     * @param alarmType alarm type ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT a FROM AlarmEntity a WHERE a.originatorId = :originatorId AND a.type = :alarmType AND a.cleared = FALSE ORDER BY a.createdTime DESC")
    List<AlarmEntity> findLatestActiveByOriginatorAndType(@Param("originatorId") UUID originatorId,
                                                          @Param("alarmType") String alarmType,
                                                          Pageable pageable);

    @Query(value = "SELECT a " +
            "FROM AlarmInfoEntity a " +
            "LEFT JOIN EntityAlarmEntity ea ON a.id = ea.alarmId " +
            "WHERE a.tenantId = :tenantId " +
            "AND ea.tenantId = :tenantId " +
            "AND ea.entityId = :affectedEntityId " +
            "AND ea.entityType = :affectedEntityType " +
            "AND (:startTime IS NULL OR (a.createdTime >= :startTime AND ea.createdTime >= :startTime)) " +
            "AND (:endTime IS NULL OR (a.createdTime <= :endTime AND ea.createdTime <= :endTime)) " +
            "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
            "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
            "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
            "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true  " +
            "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true)) "
            ,
            countQuery = "" +
                    "SELECT count(a) " + //alarms with relations only
                    "FROM AlarmInfoEntity a " +
                    "LEFT JOIN EntityAlarmEntity ea ON a.id = ea.alarmId " +
                    "WHERE a.tenantId = :tenantId " +
                    "AND ea.tenantId = :tenantId " +
                    "AND ea.entityId = :affectedEntityId " +
                    "AND ea.entityType = :affectedEntityType " +
                    "AND (:startTime IS NULL OR (a.createdTime >= :startTime AND ea.createdTime >= :startTime)) " +
                    "AND (:endTime IS NULL OR (a.createdTime <= :endTime AND ea.createdTime <= :endTime)) " +
                    "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
                    "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
                    "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
                    "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true " +
                    "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true  " +
                    "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true))")
    /**
     * Finds alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param affectedEntityId affected entity id ({@link UUID})
     * @param affectedEntityType affected entity type ({@link String})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param clearFilterEnabled clear filter enabled
     * @param clearFilter clear filter
     * @param ackFilterEnabled ack filter enabled
     * @param ackFilter ack filter
     * @param assigneeId assignee id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AlarmInfoEntity> findAlarms(@Param("tenantId") UUID tenantId,
                                     @Param("affectedEntityId") UUID affectedEntityId,
                                     @Param("affectedEntityType") String affectedEntityType,
                                     @Param("startTime") Long startTime,
                                     @Param("endTime") Long endTime,
                                     @Param("clearFilterEnabled") boolean clearFilterEnabled,
                                     @Param("clearFilter") boolean clearFilter,
                                     @Param("ackFilterEnabled") boolean ackFilterEnabled,
                                     @Param("ackFilter") boolean ackFilter,
                                     @Param("assigneeId") UUID assigneeId,
                                     @Param("searchText") String searchText,
                                     Pageable pageable);

    @Query(value = "SELECT a " +
            "FROM AlarmInfoEntity a " +
            "LEFT JOIN EntityAlarmEntity ea ON a.id = ea.alarmId " +
            "WHERE a.tenantId = :tenantId " +
            "AND ea.tenantId = :tenantId " +
            "AND ea.entityId = :affectedEntityId " +
            "AND ea.entityType = :affectedEntityType " +
            "AND (:startTime IS NULL OR (a.createdTime >= :startTime AND ea.createdTime >= :startTime)) " +
            "AND (:endTime IS NULL OR (a.createdTime <= :endTime AND ea.createdTime <= :endTime)) " +
            "AND ((:alarmTypes) IS NULL OR a.type IN (:alarmTypes)) " +
            "AND ((:alarmSeverities) IS NULL OR a.severity IN (:alarmSeverities)) " +
            "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
            "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
            "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
            "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true  " +
            "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true)) "
            ,
            countQuery = "" +
                    "SELECT count(a) " + //alarms with relations only
                    "FROM AlarmInfoEntity a " +
                    "LEFT JOIN EntityAlarmEntity ea ON a.id = ea.alarmId " +
                    "WHERE a.tenantId = :tenantId " +
                    "AND ea.tenantId = :tenantId " +
                    "AND ea.entityId = :affectedEntityId " +
                    "AND ea.entityType = :affectedEntityType " +
                    "AND (:startTime IS NULL OR (a.createdTime >= :startTime AND ea.createdTime >= :startTime)) " +
                    "AND (:endTime IS NULL OR (a.createdTime <= :endTime AND ea.createdTime <= :endTime)) " +
                    "AND ((:alarmTypes) IS NULL OR a.type IN (:alarmTypes)) " +
                    "AND ((:alarmSeverities) IS NULL OR a.severity IN (:alarmSeverities)) " +
                    "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
                    "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
                    "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
                    "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true " +
                    "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true  " +
                    "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true))")
    /**
     * Finds alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param affectedEntityId affected entity id ({@link UUID})
     * @param affectedEntityType affected entity type ({@link String})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param alarmTypes alarm types ({@link List})
     * @param alarmSeverities alarm severities ({@link List})
     * @param clearFilterEnabled clear filter enabled
     * @param clearFilter clear filter
     * @param ackFilterEnabled ack filter enabled
     * @param ackFilter ack filter
     * @param assigneeId assignee id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AlarmInfoEntity> findAlarmsV2(@Param("tenantId") UUID tenantId,
                                       @Param("affectedEntityId") UUID affectedEntityId,
                                       @Param("affectedEntityType") String affectedEntityType,
                                       @Param("startTime") Long startTime,
                                       @Param("endTime") Long endTime,
                                       @Param("alarmTypes") List<String> alarmTypes,
                                       @Param("alarmSeverities") List<AlarmSeverity> alarmSeverities,
                                       @Param("clearFilterEnabled") boolean clearFilterEnabled,
                                       @Param("clearFilter") boolean clearFilter,
                                       @Param("ackFilterEnabled") boolean ackFilterEnabled,
                                       @Param("ackFilter") boolean ackFilter,
                                       @Param("assigneeId") UUID assigneeId,
                                       @Param("searchText") String searchText,
                                       Pageable pageable);

    @Query(value = "SELECT a " +
            "FROM AlarmInfoEntity a " +
            "WHERE a.tenantId = :tenantId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
            "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
            "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
            "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true  " +
            "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true)) ",
            countQuery = "" +
                    "SELECT count(a) " +
                    "FROM AlarmInfoEntity a " +
                    "WHERE a.tenantId = :tenantId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
                    "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
                    "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
                    "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true " +
                    "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true  " +
                    "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true))")
    /**
     * Finds all alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param clearFilterEnabled clear filter enabled
     * @param clearFilter clear filter
     * @param ackFilterEnabled ack filter enabled
     * @param ackFilter ack filter
     * @param assigneeId assignee id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AlarmInfoEntity> findAllAlarms(@Param("tenantId") UUID tenantId,
                                        @Param("startTime") Long startTime,
                                        @Param("endTime") Long endTime,
                                        @Param("clearFilterEnabled") boolean clearFilterEnabled,
                                        @Param("clearFilter") boolean clearFilter,
                                        @Param("ackFilterEnabled") boolean ackFilterEnabled,
                                        @Param("ackFilter") boolean ackFilter,
                                        @Param("assigneeId") UUID assigneeId,
                                        @Param("searchText") String searchText,
                                        Pageable pageable);

    @Query(value = "SELECT a " +
            "FROM AlarmInfoEntity a " +
            "WHERE a.tenantId = :tenantId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:alarmTypes) IS NULL OR a.type IN (:alarmTypes)) " +
            "AND ((:alarmSeverities) IS NULL OR a.severity IN (:alarmSeverities)) " +
            "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
            "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
            "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
            "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true  " +
            "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true)) ",
            countQuery = "" +
                    "SELECT count(a) " +
                    "FROM AlarmInfoEntity a " +
                    "WHERE a.tenantId = :tenantId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:alarmTypes) IS NULL OR a.type IN (:alarmTypes)) " +
                    "AND ((:alarmSeverities) IS NULL OR a.severity IN (:alarmSeverities)) " +
                    "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
                    "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
                    "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
                    "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true " +
                    "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true  " +
                    "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true))")
    /**
     * Finds all alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param alarmTypes alarm types ({@link List})
     * @param alarmSeverities alarm severities ({@link List})
     * @param clearFilterEnabled clear filter enabled
     * @param clearFilter clear filter
     * @param ackFilterEnabled ack filter enabled
     * @param ackFilter ack filter
     * @param assigneeId assignee id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AlarmInfoEntity> findAllAlarmsV2(@Param("tenantId") UUID tenantId,
                                          @Param("startTime") Long startTime,
                                          @Param("endTime") Long endTime,
                                          @Param("alarmTypes") List<String> alarmTypes,
                                          @Param("alarmSeverities") List<AlarmSeverity> alarmSeverities,
                                          @Param("clearFilterEnabled") boolean clearFilterEnabled,
                                          @Param("clearFilter") boolean clearFilter,
                                          @Param("ackFilterEnabled") boolean ackFilterEnabled,
                                          @Param("ackFilter") boolean ackFilter,
                                          @Param("assigneeId") UUID assigneeId,
                                          @Param("searchText") String searchText,
                                          Pageable pageable);

    @Query(value = "SELECT a " +
            "FROM AlarmInfoEntity a " +
            "WHERE a.tenantId = :tenantId AND a.customerId = :customerId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
            "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
            "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
            "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true  " +
            "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true)) "
            ,
            countQuery = "" +
                    "SELECT count(a) " +
                    "FROM AlarmInfoEntity a " +
                    "WHERE a.tenantId = :tenantId AND a.customerId = :customerId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
                    "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
                    "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
                    "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true " +
                    "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true  " +
                    "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true))")
    /**
     * Finds customer alarms.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param clearFilterEnabled clear filter enabled
     * @param clearFilter clear filter
     * @param ackFilterEnabled ack filter enabled
     * @param ackFilter ack filter
     * @param assigneeId assignee id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AlarmInfoEntity> findCustomerAlarms(@Param("tenantId") UUID tenantId,
                                             @Param("customerId") UUID customerId,
                                             @Param("startTime") Long startTime,
                                             @Param("endTime") Long endTime,
                                             @Param("clearFilterEnabled") boolean clearFilterEnabled,
                                             @Param("clearFilter") boolean clearFilter,
                                             @Param("ackFilterEnabled") boolean ackFilterEnabled,
                                             @Param("ackFilter") boolean ackFilter,
                                             @Param("assigneeId") UUID assigneeId,
                                             @Param("searchText") String searchText,
                                             Pageable pageable);

    @Query(value = "SELECT a " +
            "FROM AlarmInfoEntity a " +
            "WHERE a.tenantId = :tenantId AND a.customerId = :customerId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:alarmTypes) IS NULL OR a.type IN (:alarmTypes)) " +
            "AND ((:alarmSeverities) IS NULL OR a.severity IN (:alarmSeverities)) " +
            "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
            "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
            "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
            "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true  " +
            "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true)) "
            ,
            countQuery = "" +
                    "SELECT count(a) " +
                    "FROM AlarmInfoEntity a " +
                    "WHERE a.tenantId = :tenantId AND a.customerId = :customerId " +
                    "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
                    "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
                    "AND ((:alarmTypes) IS NULL OR a.type IN (:alarmTypes)) " +
                    "AND ((:alarmSeverities) IS NULL OR a.severity IN (:alarmSeverities)) " +
                    "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
                    "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
                    "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId) " +
                    "AND (:searchText IS NULL OR (ilike(a.type, CONCAT('%', :searchText, '%')) = true " +
                    "  OR ilike(a.severity, CONCAT('%', :searchText, '%')) = true  " +
                    "  OR ilike(a.status, CONCAT('%', :searchText, '%')) = true))")
    /**
     * Finds customer alarms v2.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param alarmTypes alarm types ({@link List})
     * @param alarmSeverities alarm severities ({@link List})
     * @param clearFilterEnabled clear filter enabled
     * @param clearFilter clear filter
     * @param ackFilterEnabled ack filter enabled
     * @param ackFilter ack filter
     * @param assigneeId assignee id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AlarmInfoEntity> findCustomerAlarmsV2(@Param("tenantId") UUID tenantId,
                                               @Param("customerId") UUID customerId,
                                               @Param("startTime") Long startTime,
                                               @Param("endTime") Long endTime,
                                               @Param("alarmTypes") List<String> alarmTypes,
                                               @Param("alarmSeverities") List<AlarmSeverity> alarmSeverities,
                                               @Param("clearFilterEnabled") boolean clearFilterEnabled,
                                               @Param("clearFilter") boolean clearFilter,
                                               @Param("ackFilterEnabled") boolean ackFilterEnabled,
                                               @Param("ackFilter") boolean ackFilter,
                                               @Param("assigneeId") UUID assigneeId,
                                               @Param("searchText") String searchText,
                                               Pageable pageable);

    @Query(value = "SELECT a.severity FROM AlarmEntity a " +
            "LEFT JOIN EntityAlarmEntity ea ON a.id = ea.alarmId " +
            "WHERE a.tenantId = :tenantId " +
            "AND ea.tenantId = :tenantId " +
            "AND ea.entityId = :affectedEntityId " +
            "AND ea.entityType = :affectedEntityType " +
            "AND ((:clearFilterEnabled) = FALSE OR a.cleared = :clearFilter) " +
            "AND ((:ackFilterEnabled) = FALSE OR a.acknowledged = :ackFilter) " +
            "AND (:assigneeId IS NULL OR a.assigneeId = :assigneeId)")
    /**
     * Finds alarm severities.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param affectedEntityId affected entity id ({@link UUID})
     * @param affectedEntityType affected entity type ({@link String})
     * @param clearFilterEnabled clear filter enabled
     * @param clearFilter clear filter
     * @param ackFilterEnabled ack filter enabled
     * @param ackFilter ack filter
     * @param assigneeId assignee id ({@link UUID})
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */
    Set<AlarmSeverity> findAlarmSeverities(@Param("tenantId") UUID tenantId,
    /**
     * Finds alarms ids by end ts before and tenant id.
     *
     * @param time time ({@link Long})
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
                                           @Param("affectedEntityId") UUID affectedEntityId,
                                           @Param("affectedEntityType") String affectedEntityType,
                                           @Param("clearFilterEnabled") boolean clearFilterEnabled,
                                           @Param("clearFilter") boolean clearFilter,
                                           @Param("ackFilterEnabled") boolean ackFilterEnabled,
                                           @Param("ackFilter") boolean ackFilter,
                                           @Param("assigneeId") UUID assigneeId);

    @Query("SELECT a.id FROM AlarmEntity a WHERE a.tenantId = :tenantId AND a.createdTime < :time AND a.endTs < :time")
    Page<UUID> findAlarmsIdsByEndTsBeforeAndTenantId(@Param("time") Long time, @Param("tenantId") UUID tenantId, Pageable pageable);
    /**
     * Finds alarm info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link UUID})
     * @return {@link AlarmInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT a FROM AlarmInfoEntity a WHERE a.tenantId = :tenantId AND a.id = :alarmId")
    AlarmInfoEntity findAlarmInfoById(@Param("tenantId") UUID tenantId, @Param("alarmId") UUID alarmId);

    // using Slice so that count query is not executed
    @Query("SELECT new org.thingsboard.server.common.data.util.TbPair(a.id, a.createdTime) " +
            "FROM AlarmEntity a WHERE a.tenantId = :tenantId AND a.assigneeId = :assigneeId")
    /**
     * Finds alarm ids by assignee id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assigneeId assignee id ({@link UUID})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Slice}
     * @throws Exception if an unexpected error occurs during processing
     */
    Slice<TbPair<UUID, Long>> findAlarmIdsByAssigneeId(@Param("tenantId") UUID tenantId,
                                                       @Param("assigneeId") UUID assigneeId,
                                                       Pageable pageable);

    // using Slice so that count query is not executed
    @Query("SELECT new org.thingsboard.server.common.data.util.TbPair(a.id, a.createdTime) " +
            "FROM AlarmEntity a WHERE a.tenantId = :tenantId AND a.assigneeId = :assigneeId " +
            "AND (a.createdTime > :createdTimeOffset OR " +
            "(a.createdTime = :createdTimeOffset AND a.id > :idOffset))")
    /**
     * Finds alarm ids by assignee id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assigneeId assignee id ({@link UUID})
     * @param createdTimeOffset created time offset
     * @param idOffset cursor for batch id scan (exclusive lower bound)
     * @param pageable pageable ({@link Pageable})
     * @return {@link Slice}
     * @throws Exception if an unexpected error occurs during processing
     */
    Slice<TbPair<UUID, Long>> findAlarmIdsByAssigneeId(@Param("tenantId") UUID tenantId,
                                                       @Param("assigneeId") UUID assigneeId,
                                                       @Param("createdTimeOffset") long createdTimeOffset,
                                                       @Param("idOffset") UUID idOffset,
                                                       Pageable pageable);

    // using Slice so that count query is not executed
    @Query("SELECT new org.thingsboard.server.common.data.util.TbPair(a.id, a.createdTime) " +
            "FROM AlarmEntity a WHERE a.originatorId = :originatorId " +
            "AND (a.createdTime > :createdTimeOffset OR " +
            "(a.createdTime = :createdTimeOffset AND a.id > :idOffset))")
    /**
     * Finds alarm ids by originator id.
     *
     * @param originatorId originator id ({@link UUID})
     * @param createdTimeOffset created time offset
     * @param idOffset cursor for batch id scan (exclusive lower bound)
     * @param pageable pageable ({@link Pageable})
     * @return {@link Slice}
     * @throws Exception if an unexpected error occurs during processing
     */
    Slice<TbPair<UUID, Long>> findAlarmIdsByOriginatorId(@Param("originatorId") UUID originatorId,
                                                         @Param("createdTimeOffset") long createdTimeOffset,
                                                         @Param("idOffset") UUID idOffset,
                                                         Pageable pageable);

    // using Slice so that count query is not executed
    @Query("SELECT new org.thingsboard.server.common.data.util.TbPair(a.id, a.createdTime) " +
            "FROM AlarmEntity a WHERE a.originatorId = :originatorId")
    /**
     * Finds alarm ids by originator id.
     *
     * @param originatorId originator id ({@link UUID})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Slice}
     * @throws Exception if an unexpected error occurs during processing
     */
    Slice<TbPair<UUID, Long>> findAlarmIdsByOriginatorId(@Param("originatorId") UUID originatorId,
                                                         Pageable pageable);

    @Query(value = "SELECT create_or_update_active_alarm(:t_id, :c_id, :a_id, :a_created_ts, :a_o_id, :a_o_type, :a_type, :a_severity, " +
            ":a_start_ts, :a_end_ts, :a_details, :a_propagate, :a_propagate_to_owner, " +
            ":a_propagate_to_tenant, :a_propagation_types, :a_creation_enabled)", nativeQuery = true)
    /**
     * Creates or update active alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param alarmId alarm id ({@link UUID})
     * @param createdTime created time
     * @param originatorId originator id ({@link UUID})
     * @param originatorType originator type
     * @param type type ({@link String})
     * @param severity severity ({@link String})
     * @param startTs interval start timestamp (epoch ms)
     * @param endTs interval end timestamp (epoch ms)
     * @param detailsAsString details as string ({@link String})
     * @param propagate propagate
     * @param propagateToOwner propagate to owner
     * @param propagateToTenant propagate to tenant
     * @param propagationTypes propagation types ({@link String})
     * @param alarmCreationEnabled alarm creation enabled
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */
    String createOrUpdateActiveAlarm(@Param("t_id") UUID tenantId, @Param("c_id") UUID customerId,
                                     @Param("a_id") UUID alarmId, @Param("a_created_ts") long createdTime,
                                     @Param("a_o_id") UUID originatorId, @Param("a_o_type") int originatorType,
                                     @Param("a_type") String type, @Param("a_severity") String severity,
                                     @Param("a_start_ts") long startTs, @Param("a_end_ts") long endTs, @Param("a_details") String detailsAsString,
                                     @Param("a_propagate") boolean propagate, @Param("a_propagate_to_owner") boolean propagateToOwner,
                                     @Param("a_propagate_to_tenant") boolean propagateToTenant, @Param("a_propagation_types") String propagationTypes,
                                     @Param("a_creation_enabled") boolean alarmCreationEnabled);

    @Query(value = "SELECT update_alarm(:t_id, :a_id, :a_severity, :a_start_ts, :a_end_ts, :a_details, :a_propagate, :a_propagate_to_owner, " +
            ":a_propagate_to_tenant, :a_propagation_types)", nativeQuery = true)
    /**
     * Updates alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link UUID})
     * @param severity severity ({@link String})
     * @param startTs interval start timestamp (epoch ms)
     * @param endTs interval end timestamp (epoch ms)
     * @param detailsAsString details as string ({@link String})
     * @param propagate propagate
     * @param propagateToOwner propagate to owner
     * @param propagateToTenant propagate to tenant
     * @param propagationTypes propagation types ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */
    String updateAlarm(@Param("t_id") UUID tenantId, @Param("a_id") UUID alarmId, @Param("a_severity") String severity,
    /**
     * Acknowledge alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link UUID})
     * @param ts ts
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */
                       @Param("a_start_ts") long startTs, @Param("a_end_ts") long endTs, @Param("a_details") String detailsAsString,
                       @Param("a_propagate") boolean propagate, @Param("a_propagate_to_owner") boolean propagateToOwner,
                       @Param("a_propagate_to_tenant") boolean propagateToTenant, @Param("a_propagation_types") String propagationTypes);

    @Query(value = "SELECT acknowledge_alarm(:t_id, :a_id, :a_ts)", nativeQuery = true)
    String acknowledgeAlarm(@Param("t_id") UUID tenantId, @Param("a_id") UUID alarmId, @Param("a_ts") long ts);
    /**
     * Clear alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link UUID})
     * @param ts ts
     * @param details details ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT clear_alarm(:t_id, :a_id, :a_ts, :a_details)", nativeQuery = true)
    String clearAlarm(@Param("t_id") UUID tenantId, @Param("a_id") UUID alarmId, @Param("a_ts") long ts, @Param("a_details") String details);
    /**
     * Assigns alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link UUID})
     * @param userId target user identifier
     * @param assignTime assign time
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT assign_alarm(:t_id, :a_id, :u_id, :a_ts)", nativeQuery = true)
    String assignAlarm(@Param("t_id") UUID tenantId, @Param("a_id") UUID alarmId, @Param("u_id") UUID userId, @Param("a_ts") long assignTime);
    /**
     * Unassigns alarm.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alarmId alarm id ({@link UUID})
     * @param unassignTime unassign time
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT unassign_alarm(:t_id, :a_id, :a_ts)", nativeQuery = true)
    String unassignAlarm(@Param("t_id") UUID tenantId, @Param("a_id") UUID alarmId, @Param("a_ts") long unassignTime);
    /**
     * Finds tenant alarm types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT at.type FROM alarm_types AS at WHERE at.tenant_id = :tenantId AND at.type ILIKE CONCAT('%', :searchText, '%')", nativeQuery = true)
    Page<String> findTenantAlarmTypes(@Param("tenantId") UUID tenantId, @Param("searchText") String searchText, Pageable pageable);
    /**
     * Deletes type if no alarms exist.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param types types ({@link Set})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM alarm_types AS at WHERE NOT EXISTS (SELECT 1 FROM alarm AS a WHERE a.tenant_id = at.tenant_id AND a.type = at.type) AND at.tenant_id = :tenantId AND at.type IN (:types)", nativeQuery = true)
    int deleteTypeIfNoAlarmsExist(@Param("tenantId") UUID tenantId, @Param("types") Set<String> types);

    @Query(value = "SELECT a.id FROM alarm a " +
            "WHERE a.originator_id = :originatorId " +
            "AND (COALESCE(:alarmTypes) IS NULL OR a.type IN (:alarmTypes)) " +
            "AND (COALESCE(:alarmSeverities) IS NULL OR a.severity IN (:alarmSeverities)) " +
            "AND (a.cleared = false) ORDER BY id LIMIT :limit", nativeQuery = true)
    /**
     * Finds active originator alarms.
     *
     * @param originatorId originator id ({@link UUID})
     * @param alarmTypes alarm types ({@link List})
     * @param alarmSeverities alarm severities ({@link List})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<UUID> findActiveOriginatorAlarms(@Param("originatorId") UUID originatorId,
                                          @Param("alarmTypes") List<String> alarmTypes,
                                          @Param("alarmSeverities") List<String> alarmSeverities,
                                          int limit);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<AlarmEntity> findByTenantId(UUID tenantId, Pageable pageable);

}
