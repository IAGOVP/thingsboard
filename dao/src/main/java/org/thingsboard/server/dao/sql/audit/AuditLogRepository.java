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
package org.thingsboard.server.dao.sql.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.dao.model.sql.AuditLogEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for audit log entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    @Query("SELECT a FROM AuditLogEntity a WHERE " +
            "a.tenantId = :tenantId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:actionTypes) IS NULL OR a.actionType IN (:actionTypes)) " +
            "AND (:textSearch IS NULL OR ilike(a.entityType, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.entityName, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.userName, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionType, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionStatus, CONCAT('%', :textSearch, '%')) = true)"
    )
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param actionTypes action types ({@link List})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AuditLogEntity> findByTenantId(
                                 @Param("tenantId") UUID tenantId,
                                 @Param("textSearch") String textSearch,
                                 @Param("startTime") Long startTime,
                                 @Param("endTime") Long endTime,
                                 @Param("actionTypes") List<ActionType> actionTypes,
                                 Pageable pageable);

    @Query("SELECT a FROM AuditLogEntity a WHERE " +
            "a.tenantId = :tenantId " +
            "AND a.entityType = :entityType AND a.entityId = :entityId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:actionTypes) IS NULL OR a.actionType IN (:actionTypes)) " +
            "AND (:textSearch IS NULL OR ilike(a.entityName, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.userName, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionType, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionStatus, CONCAT('%', :textSearch, '%')) = true)"
    )
    /**
     * Finds audit logs by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityType entity type discriminator
     * @param entityId target entity identifier
     * @param textSearch text search ({@link String})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param actionTypes action types ({@link List})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AuditLogEntity> findAuditLogsByTenantIdAndEntityId(@Param("tenantId") UUID tenantId,
                                                            @Param("entityType") EntityType entityType,
                                                            @Param("entityId") UUID entityId,
                                                            @Param("textSearch") String textSearch,
                                                            @Param("startTime") Long startTime,
                                                            @Param("endTime") Long endTime,
                                                            @Param("actionTypes") List<ActionType> actionTypes,
                                                            Pageable pageable);

    @Query("SELECT a FROM AuditLogEntity a WHERE " +
            "a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:actionTypes) IS NULL OR a.actionType IN (:actionTypes)) " +
            "AND (:textSearch IS NULL OR ilike(a.entityType, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.entityName, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.userName, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionType, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionStatus, CONCAT('%', :textSearch, '%')) = true)"
    )
    /**
     * Finds audit logs by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param textSearch text search ({@link String})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param actionTypes action types ({@link List})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AuditLogEntity> findAuditLogsByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                              @Param("customerId") UUID customerId,
                                                              @Param("textSearch") String textSearch,
                                                              @Param("startTime") Long startTime,
                                                              @Param("endTime") Long endTime,
                                                              @Param("actionTypes") List<ActionType> actionTypes,
                                                              Pageable pageable);

    @Query("SELECT a FROM AuditLogEntity a WHERE " +
            "a.tenantId = :tenantId " +
            "AND a.userId = :userId " +
            "AND (:startTime IS NULL OR a.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR a.createdTime <= :endTime) " +
            "AND ((:actionTypes) IS NULL OR a.actionType IN (:actionTypes)) " +
            "AND (:textSearch IS NULL OR ilike(a.entityType, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.entityName, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionType, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(a.actionStatus, CONCAT('%', :textSearch, '%')) = true)"
    )
    /**
     * Finds audit logs by tenant id and user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @param textSearch text search ({@link String})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param actionTypes action types ({@link List})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AuditLogEntity> findAuditLogsByTenantIdAndUserId(@Param("tenantId") UUID tenantId,
                                                          @Param("userId") UUID userId,
                                                          @Param("textSearch") String textSearch,
                                                          @Param("startTime") Long startTime,
                                                          @Param("endTime") Long endTime,
                                                          @Param("actionTypes") List<ActionType> actionTypes,
                                                          Pageable pageable);

}
