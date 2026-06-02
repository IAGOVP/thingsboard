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

import com.google.common.util.concurrent.FluentFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationRequest;
import org.thingsboard.server.common.data.notification.NotificationRequestInfo;
import org.thingsboard.server.common.data.notification.NotificationRequestStats;
import org.thingsboard.server.common.data.notification.NotificationRequestStatus;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.service.DataValidator;

import java.util.List;
import java.util.Optional;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
/**
 * Default notification request service.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultNotificationRequestService implements NotificationRequestService, EntityDaoService {

    private final NotificationRequestDao notificationRequestDao;
    private final NotificationDao notificationDao;

    private final ApplicationEventPublisher eventPublisher;

    private final NotificationRequestValidator notificationRequestValidator = new NotificationRequestValidator();

    /**

     * Persists notification request.

     */

    @Override
    public NotificationRequest saveNotificationRequest(TenantId tenantId, NotificationRequest notificationRequest) {
        notificationRequestValidator.validate(notificationRequest, NotificationRequest::getTenantId);
        return notificationRequestDao.save(tenantId, notificationRequest);
    }

    /**

     * Loads notification request by id.

     */

    @Override
    public NotificationRequest findNotificationRequestById(TenantId tenantId, NotificationRequestId id) {
        return notificationRequestDao.findById(tenantId, id.getId());
    }

    /**

     * Loads notification request info by id.

     */

    @Override
    public NotificationRequestInfo findNotificationRequestInfoById(TenantId tenantId, NotificationRequestId id) {
        return notificationRequestDao.findInfoById(tenantId, id);
    }

    /**

     * Loads notification requests by tenant id and originator type.

     */

    @Override
    public PageData<NotificationRequest> findNotificationRequestsByTenantIdAndOriginatorType(TenantId tenantId, EntityType originatorType, PageLink pageLink) {
        return notificationRequestDao.findByTenantIdAndOriginatorTypeAndPageLink(tenantId, originatorType, pageLink);
    }

    /**

     * Loads notification requests infos by tenant id and originator type.

     */

    @Override
    public PageData<NotificationRequestInfo> findNotificationRequestsInfosByTenantIdAndOriginatorType(TenantId tenantId, EntityType originatorType, PageLink pageLink) {
        return notificationRequestDao.findInfosByTenantIdAndOriginatorTypeAndPageLink(tenantId, originatorType, pageLink);
    }

    /**

     * Loads notification requests ids by status and rule id.

     */

    @Override
    public List<NotificationRequestId> findNotificationRequestsIdsByStatusAndRuleId(TenantId tenantId, NotificationRequestStatus requestStatus, NotificationRuleId ruleId) {
        return notificationRequestDao.findIdsByRuleId(tenantId, requestStatus, ruleId);
    }

    /**

     * Loads notification requests by rule id and originator entity id and status.

     */

    @Override
    public List<NotificationRequest> findNotificationRequestsByRuleIdAndOriginatorEntityIdAndStatus(TenantId tenantId, NotificationRuleId ruleId, EntityId originatorEntityId, NotificationRequestStatus status) {
        return notificationRequestDao.findByRuleIdAndOriginatorEntityIdAndStatus(tenantId, ruleId, originatorEntityId, status);
    }

    /**

     * Removes notification request.

     */

    @Override
    public void deleteNotificationRequest(TenantId tenantId, NotificationRequest request) {
        notificationRequestDao.removeById(tenantId, request.getUuidId());
        notificationDao.deleteByRequestId(tenantId, request.getId());
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entity(request).entityId(request.getId()).build());
    }

    /**

     * Removes entity.

     */

    @Override
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        if (force) {
            notificationRequestDao.removeById(tenantId, id.getId());
        } else {
            NotificationRequest notificationRequest = findNotificationRequestById(tenantId, (NotificationRequestId) id);
            deleteNotificationRequest(tenantId, notificationRequest);
        }
    }

    /**

     * Loads scheduled notification requests.

     */

    @Override
    public PageData<NotificationRequest> findScheduledNotificationRequests(PageLink pageLink) {
        return notificationRequestDao.findAllByStatus(NotificationRequestStatus.SCHEDULED, pageLink);
    }

    /**

     * Updates notification request.

     */

    @Override
    public void updateNotificationRequest(TenantId tenantId, NotificationRequestId requestId, NotificationRequestStatus requestStatus, NotificationRequestStats stats) {
        notificationRequestDao.updateById(tenantId, requestId, requestStatus, stats);
    }

    // notifications themselves are left in the database until removed by ttl
    /**
     * Removes notification requests by tenant id.
     */
    @Override
    public void deleteNotificationRequestsByTenantId(TenantId tenantId) {
        notificationRequestDao.removeByTenantId(tenantId);
    }

    /**

     * Removes by tenant id.

     */

    @Override
    public void deleteByTenantId(TenantId tenantId) {
        deleteNotificationRequestsByTenantId(tenantId);
    }

    /**

     * Loads entity.

     */

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findNotificationRequestById(tenantId, new NotificationRequestId(entityId.getId())));
    }

    /**

     * Loads entity async.

     */

    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(notificationRequestDao.findByIdAsync(tenantId, entityId.getId()))
                .transform(Optional::ofNullable, directExecutor());
    }

    /**

     * Get entity type.

     */

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_REQUEST;
    }

    private static class NotificationRequestValidator extends DataValidator<NotificationRequest> {}

}
