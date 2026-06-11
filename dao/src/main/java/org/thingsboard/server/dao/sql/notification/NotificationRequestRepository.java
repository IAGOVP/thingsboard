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
package org.thingsboard.server.dao.sql.notification;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.notification.NotificationRequestStatus;
import org.thingsboard.server.dao.model.sql.NotificationRequestEntity;
import org.thingsboard.server.dao.model.sql.NotificationRequestInfoEntity;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for notification request entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */


@Repository
public interface NotificationRequestRepository extends JpaRepository<NotificationRequestEntity, UUID> {

    String REQUEST_INFO_QUERY = "SELECT new org.thingsboard.server.dao.model.sql.NotificationRequestInfoEntity(r, t.name, t.configuration) " +
            "FROM NotificationRequestEntity r LEFT JOIN NotificationTemplateEntity t ON r.templateId = t.id";
    /**
     * Finds by tenant id and originator entity type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<NotificationRequestEntity> findByTenantIdAndOriginatorEntityType(UUID tenantId, EntityType originatorType, Pageable pageable);

    @Query(REQUEST_INFO_QUERY + " WHERE r.tenantId = :tenantId AND r.originatorEntityType = :originatorType " +
            "AND (:searchText is NULL OR (t.name IS NOT NULL AND ilike(t.name, concat('%', :searchText, '%')) = true))")
    /**
     * Finds infos by tenant id and originator entity type and search text.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<NotificationRequestInfoEntity> findInfosByTenantIdAndOriginatorEntityTypeAndSearchText(@Param("tenantId") UUID tenantId,
                                                                                                @Param("originatorType") EntityType originatorType,
                                                                                                @Param("searchText") String searchText,
                                                                                                Pageable pageable);
    /**
     * Finds info by id.
     *
     * @param id entity UUID primary key
     * @return {@link NotificationRequestInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(REQUEST_INFO_QUERY + " WHERE r.id = :id")
    NotificationRequestInfoEntity findInfoById(@Param("id") UUID id);
    /**
     * Finds all ids by status and rule id.
     *
     * @param status status ({@link NotificationRequestStatus})
     * @param ruleId rule id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT r.id FROM NotificationRequestEntity r WHERE r.status = :status AND r.ruleId = :ruleId")
    List<UUID> findAllIdsByStatusAndRuleId(@Param("status") NotificationRequestStatus status,
    /**
     * Finds all by rule id and originator entity id and originator entity type and status.
     *
     * @param ruleId rule id ({@link UUID})
     * @param originatorEntityId originator entity id ({@link UUID})
     * @param originatorEntityType originator entity type ({@link EntityType})
     * @param status status ({@link NotificationRequestStatus})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
                                           @Param("ruleId") UUID ruleId);

    List<NotificationRequestEntity> findAllByRuleIdAndOriginatorEntityIdAndOriginatorEntityTypeAndStatus(UUID ruleId, UUID originatorEntityId, EntityType originatorEntityType, NotificationRequestStatus status);
    /**
     * Finds all by status.
     *
     * @param status status ({@link NotificationRequestStatus})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<NotificationRequestEntity> findAllByStatus(NotificationRequestStatus status, Pageable pageable);
    /**
     * Updates status and stats by id.
     *
     * @param id entity UUID primary key
     * @param status status ({@link NotificationRequestStatus})
     * @param stats stats ({@link JsonNode})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Modifying
    @Transactional
    @Query("UPDATE NotificationRequestEntity r SET r.status = :status, r.stats = :stats WHERE r.id = :id")
    void updateStatusAndStatsById(@Param("id") UUID id,
    /**
     * Exists by tenant id and status and targets containing.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param status status ({@link NotificationRequestStatus})
     * @param targetIdStr target id str ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */
                                  @Param("status") NotificationRequestStatus status,
                                  @Param("stats") JsonNode stats);

    boolean existsByTenantIdAndStatusAndTargetsContaining(UUID tenantId, NotificationRequestStatus status, String targetIdStr);
    /**
     * Exists by tenant id and status and template id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param status status ({@link NotificationRequestStatus})
     * @param templateId template id ({@link UUID})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndStatusAndTemplateId(UUID tenantId, NotificationRequestStatus status, UUID templateId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM notification_request WHERE id IN " +
            "(SELECT id FROM notification_request WHERE tenant_id = :tenantId AND created_time < :ts LIMIT :batchSize)",
            nativeQuery = true)
    /**
     * Deletes by tenant id and created time before batch.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ts ts
     * @param batchSize batch size
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */
    int deleteByTenantIdAndCreatedTimeBeforeBatch(@Param("tenantId") UUID tenantId,
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */
                                                  @Param("ts") long ts,
                                                  @Param("batchSize") int batchSize);

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationRequestEntity r WHERE r.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);

}
