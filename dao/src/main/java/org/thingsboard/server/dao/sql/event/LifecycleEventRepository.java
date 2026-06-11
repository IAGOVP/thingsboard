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
package org.thingsboard.server.dao.sql.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.event.LifecycleEvent;
import org.thingsboard.server.dao.model.sql.LifecycleEventEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for lifecycle event entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface LifecycleEventRepository extends EventRepository<LifecycleEventEntity, LifecycleEvent>, JpaRepository<LifecycleEventEntity, UUID> {
    /**
     * Finds latest events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    @Query(nativeQuery = true, value = "SELECT * FROM lc_event e WHERE e.tenant_id = :tenantId AND e.entity_id = :entityId ORDER BY e.ts DESC LIMIT :limit")
    List<LifecycleEventEntity> findLatestEvents(@Param("tenantId") UUID tenantId, @Param("entityId") UUID entityId, @Param("limit") int limit);


    @Query("SELECT e FROM LifecycleEventEntity e WHERE " +
            "e.tenantId = :tenantId " +
            "AND e.entityId = :entityId " +
            "AND (:startTime IS NULL OR e.ts >= :startTime) " +
            "AND (:endTime IS NULL OR e.ts <= :endTime)"
    )
    /**
     * Finds events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<LifecycleEventEntity> findEvents(@Param("tenantId") UUID tenantId,
                                          @Param("entityId") UUID entityId,
                                          @Param("startTime") Long startTime,
                                          @Param("endTime") Long endTime,
                                          Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT * FROM lc_event e WHERE " +
                    "e.tenant_id = :tenantId " +
                    "AND e.entity_id = :entityId " +
                    "AND (:startTime IS NULL OR e.ts >= :startTime) " +
                    "AND (:endTime IS NULL OR e.ts <= :endTime) " +
                    "AND (:serviceId IS NULL OR e.service_id ILIKE concat('%', :serviceId, '%')) " +
                    "AND (:eventType IS NULL OR e.e_type ILIKE concat('%', :eventType, '%')) " +
                    "AND ((:statusFilterEnabled = FALSE) OR e.e_success = :statusFilter) " +
                    "AND (:error IS NULL OR e.e_error ILIKE concat('%', :error, '%'))"
            ,
            countQuery = "SELECT count(*) FROM lc_event e WHERE " +
                    "e.tenant_id = :tenantId " +
                    "AND e.entity_id = :entityId " +
                    "AND (:startTime IS NULL OR e.ts >= :startTime) " +
                    "AND (:endTime IS NULL OR e.ts <= :endTime) " +
                    "AND (:serviceId IS NULL OR e.service_id ILIKE concat('%', :serviceId, '%')) " +
                    "AND (:eventType IS NULL OR e.e_type ILIKE concat('%', :eventType, '%')) " +
                    "AND ((:statusFilterEnabled = FALSE) OR e.e_success = :statusFilter) " +
                    "AND (:error IS NULL OR e.e_error ILIKE concat('%', :error, '%'))"
    )
    /**
     * Finds events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param server server ({@link String})
     * @param eventType event type ({@link String})
     * @param statusFilterEnabled status filter enabled
     * @param statusFilter status filter
     * @param error error ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<LifecycleEventEntity> findEvents(@Param("tenantId") UUID tenantId,
                                          @Param("entityId") UUID entityId,
                                          @Param("startTime") Long startTime,
                                          @Param("endTime") Long endTime,
                                          @Param("serviceId") String server,
                                          @Param("eventType") String eventType,
                                          @Param("statusFilterEnabled") boolean statusFilterEnabled,
                                          @Param("statusFilter") boolean statusFilter,
                                          @Param("error") String error,
                                          Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM LifecycleEventEntity e WHERE " +
            "e.tenantId = :tenantId " +
            "AND e.entityId = :entityId " +
            "AND (:startTime IS NULL OR e.ts >= :startTime) " +
            "AND (:endTime IS NULL OR e.ts <= :endTime)"
    )
    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */
    void removeEvents(@Param("tenantId") UUID tenantId,
                      @Param("entityId") UUID entityId,
                      @Param("startTime") Long startTime,
                      @Param("endTime") Long endTime);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM lc_event e WHERE " +
                    "e.tenant_id = :tenantId " +
                    "AND e.entity_id = :entityId " +
                    "AND (:startTime IS NULL OR e.ts >= :startTime) " +
                    "AND (:endTime IS NULL OR e.ts <= :endTime) " +
                    "AND (:serviceId IS NULL OR e.service_id ILIKE concat('%', :serviceId, '%')) " +
                    "AND (:eventType IS NULL OR e.e_type ILIKE concat('%', :eventType, '%')) " +
                    "AND ((:statusFilterEnabled = FALSE) OR e.e_success = :statusFilter) " +
                    "AND (:error IS NULL OR e.e_error ILIKE concat('%', :error, '%'))"
    )
    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param server server ({@link String})
     * @param eventType event type ({@link String})
     * @param statusFilterEnabled status filter enabled
     * @param statusFilter status filter
     * @param error error ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */
    void removeEvents(@Param("tenantId") UUID tenantId,
                      @Param("entityId") UUID entityId,
                      @Param("startTime") Long startTime,
                      @Param("endTime") Long endTime,
                      @Param("serviceId") String server,
                      @Param("eventType") String eventType,
                      @Param("statusFilterEnabled") boolean statusFilterEnabled,
                      @Param("statusFilter") boolean statusFilter,
                      @Param("error") String error);
}
