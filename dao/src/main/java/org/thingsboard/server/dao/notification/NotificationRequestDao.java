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
package org.thingsboard.server.dao.notification;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.NotificationTemplateId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationRequest;
import org.thingsboard.server.common.data.notification.NotificationRequestInfo;
import org.thingsboard.server.common.data.notification.NotificationRequestStats;
import org.thingsboard.server.common.data.notification.NotificationRequestStatus;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;


/**

 * Persistence contract for notification request.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (notification templates, targets, rules, and delivery requests).

 */


public interface NotificationRequestDao extends Dao<NotificationRequest> {
    /**
     * Finds by tenant id and originator type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationRequest> findByTenantIdAndOriginatorTypeAndPageLink(TenantId tenantId, EntityType originatorType, PageLink pageLink);
    /**
     * Finds infos by tenant id and originator type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationRequestInfo> findInfosByTenantIdAndOriginatorTypeAndPageLink(TenantId tenantId, EntityType originatorType, PageLink pageLink);
    /**
     * Finds ids by rule id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param requestStatus request status ({@link NotificationRequestStatus})
     * @param ruleId rule id ({@link NotificationRuleId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<NotificationRequestId> findIdsByRuleId(TenantId tenantId, NotificationRequestStatus requestStatus, NotificationRuleId ruleId);
    /**
     * Finds by rule id and originator entity id and status.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleId rule id ({@link NotificationRuleId})
     * @param originatorEntityId originator entity id ({@link EntityId})
     * @param status status ({@link NotificationRequestStatus})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<NotificationRequest> findByRuleIdAndOriginatorEntityIdAndStatus(TenantId tenantId, NotificationRuleId ruleId, EntityId originatorEntityId, NotificationRequestStatus status);
    /**
     * Finds all by status.
     *
     * @param status status ({@link NotificationRequestStatus})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<NotificationRequest> findAllByStatus(NotificationRequestStatus status, PageLink pageLink);
    /**
     * Updates by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param requestId request id ({@link NotificationRequestId})
     * @param requestStatus request status ({@link NotificationRequestStatus})
     * @param stats stats ({@link NotificationRequestStats})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void updateById(TenantId tenantId, NotificationRequestId requestId, NotificationRequestStatus requestStatus, NotificationRequestStats stats);
    /**
     * Exists by tenant id and status and target id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param status status ({@link NotificationRequestStatus})
     * @param targetId target id ({@link NotificationTargetId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndStatusAndTargetId(TenantId tenantId, NotificationRequestStatus status, NotificationTargetId targetId);
    /**
     * Exists by tenant id and status and template id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param status status ({@link NotificationRequestStatus})
     * @param templateId template id ({@link NotificationTemplateId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndStatusAndTemplateId(TenantId tenantId, NotificationRequestStatus status, NotificationTemplateId templateId);
    /**
     * Removes by tenant id and created time before batch.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ts ts
     * @param batchSize batch size
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int removeByTenantIdAndCreatedTimeBeforeBatch(TenantId tenantId, long ts, int batchSize);
    /**
     * Finds info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link NotificationRequestInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationRequestInfo findInfoById(TenantId tenantId, NotificationRequestId id);
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByTenantId(TenantId tenantId);

}
