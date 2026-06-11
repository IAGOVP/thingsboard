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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.NotificationRuleEntity;
import org.thingsboard.server.dao.model.sql.NotificationRuleInfoEntity;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for notification rule entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */


@Repository
public interface NotificationRuleRepository extends JpaRepository<NotificationRuleEntity, UUID>, ExportableEntityRepository<NotificationRuleEntity> {

    String RULE_INFO_QUERY = "SELECT new org.thingsboard.server.dao.model.sql.NotificationRuleInfoEntity(r, t.name, t.configuration) " +
            "FROM NotificationRuleEntity r INNER JOIN NotificationTemplateEntity t ON r.templateId = t.id";

    @Query("SELECT r FROM NotificationRuleEntity r WHERE r.tenantId = :tenantId " +
            "AND (:searchText is NULL OR ilike(r.name, concat('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and search text.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<NotificationRuleEntity> findByTenantIdAndSearchText(@Param("tenantId") UUID tenantId,
                                                             @Param("searchText") String searchText,
                                                             Pageable pageable);

    @Query("SELECT count(r) > 0 FROM NotificationRuleEntity r WHERE r.tenantId = :tenantId " +
            "AND CAST(r.recipientsConfig AS text) LIKE concat('%', :searchString, '%')")
    /**
     * Exists by tenant id and recipients config containing.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchString search string ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */
    boolean existsByTenantIdAndRecipientsConfigContaining(@Param("tenantId") UUID tenantId,
    /**
     * Finds all by tenant id and trigger type and enabled.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param triggerType trigger type ({@link NotificationRuleTriggerType})
     * @param enabled enabled
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
                                                          @Param("searchString") String searchString);

    List<NotificationRuleEntity> findAllByTenantIdAndTriggerTypeAndEnabled(UUID tenantId, NotificationRuleTriggerType triggerType, boolean enabled);
    /**
     * Finds info by id.
     *
     * @param id entity UUID primary key
     * @return {@link NotificationRuleInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(RULE_INFO_QUERY + " WHERE r.id = :id")
    NotificationRuleInfoEntity findInfoById(@Param("id") UUID id);
    /**
     * Finds infos by tenant id and search text.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(RULE_INFO_QUERY + " WHERE r.tenantId = :tenantId AND (:searchText IS NULL OR ilike(r.name, concat('%', :searchText, '%')) = true)")
    Page<NotificationRuleInfoEntity> findInfosByTenantIdAndSearchText(@Param("tenantId") UUID tenantId,
                                                                      @Param("searchText") String searchText,
                                                                      Pageable pageable);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationRuleEntity r WHERE r.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link NotificationRuleEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationRuleEntity findByTenantIdAndName(UUID tenantId, String name);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<NotificationRuleEntity> findByTenantId(UUID tenantId, Pageable pageable);
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link UUID})
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM NotificationRuleEntity WHERE id = :id")
    UUID getExternalIdByInternal(@Param("id") UUID internalId);

}
