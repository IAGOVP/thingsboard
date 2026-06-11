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
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationRequest;
import org.thingsboard.server.common.data.notification.NotificationRequestInfo;
import org.thingsboard.server.common.data.notification.NotificationRequestStats;
import org.thingsboard.server.common.data.notification.NotificationRequestStatus;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;

/**
 * Service API for notification request persistence and domain operations.
 */
public interface NotificationRequestService {

    /**
     * Saves or persists notification request.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationRequest notification request ({@link NotificationRequest})
     * @return {@link NotificationRequest}
     */
    NotificationRequest saveNotificationRequest(TenantId tenantId, NotificationRequest notificationRequest);

    /**
     * Finds notification request by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationRequestId})
     * @return {@link NotificationRequest}
     */
    NotificationRequest findNotificationRequestById(TenantId tenantId, NotificationRequestId id);

    /**
     * Finds notification request info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link NotificationRequestId})
     * @return {@link NotificationRequestInfo}
     */
    NotificationRequestInfo findNotificationRequestInfoById(TenantId tenantId, NotificationRequestId id);

    /**
     * Finds notification requests by tenant id and originator type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationRequest> findNotificationRequestsByTenantIdAndOriginatorType(TenantId tenantId, EntityType originatorType, PageLink pageLink);

    /**
     * Finds notification requests infos by tenant id and originator type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationRequestInfo> findNotificationRequestsInfosByTenantIdAndOriginatorType(TenantId tenantId, EntityType originatorType, PageLink pageLink);

    /**
     * Finds notification requests ids by status and rule id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param requestStatus request status ({@link NotificationRequestStatus})
     * @param ruleId rule id ({@link NotificationRuleId})
     * @return {@link List}
     */
    List<NotificationRequestId> findNotificationRequestsIdsByStatusAndRuleId(TenantId tenantId, NotificationRequestStatus requestStatus, NotificationRuleId ruleId);

    /**
     * Finds notification requests by rule id and originator entity id and status.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleId rule id ({@link NotificationRuleId})
     * @param originatorEntityId originator entity id ({@link EntityId})
     * @param status status ({@link NotificationRequestStatus})
     * @return {@link List}
     */
    List<NotificationRequest> findNotificationRequestsByRuleIdAndOriginatorEntityIdAndStatus(TenantId tenantId, NotificationRuleId ruleId, EntityId originatorEntityId, NotificationRequestStatus status);

    /**
     * Deletes notification request.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param request request payload with operation parameters
     */
    void deleteNotificationRequest(TenantId tenantId, NotificationRequest request);

    /**
     * Finds scheduled notification requests.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<NotificationRequest> findScheduledNotificationRequests(PageLink pageLink);

    /**
     * Updates notification request.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param requestId request id ({@link NotificationRequestId})
     * @param requestStatus request status ({@link NotificationRequestStatus})
     * @param stats stats ({@link NotificationRequestStats})
     */
    void updateNotificationRequest(TenantId tenantId, NotificationRequestId requestId, NotificationRequestStatus requestStatus, NotificationRequestStats stats);

    /**
     * Deletes notification requests by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteNotificationRequestsByTenantId(TenantId tenantId);

}
