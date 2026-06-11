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

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
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
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.NotificationRequestEntity;
import org.thingsboard.server.dao.model.sql.NotificationRequestInfoEntity;
import org.thingsboard.server.dao.notification.NotificationRequestDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * JPA/PostgreSQL implementation of notification request dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
@RequiredArgsConstructor
public class JpaNotificationRequestDao extends JpaAbstractDao<NotificationRequestEntity, NotificationRequest> implements NotificationRequestDao {

    private final NotificationRequestRepository notificationRequestRepository;
    /**
     * Finds by tenant id and originator type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationRequest> findByTenantIdAndOriginatorTypeAndPageLink(TenantId tenantId, EntityType originatorType, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRequestRepository.findByTenantIdAndOriginatorEntityType(tenantId.getId(),
                originatorType, DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds infos by tenant id and originator type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param originatorType originator type ({@link EntityType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationRequestInfo> findInfosByTenantIdAndOriginatorTypeAndPageLink(TenantId tenantId, EntityType originatorType, PageLink pageLink) {
        return DaoUtil.pageToPageData(notificationRequestRepository.findInfosByTenantIdAndOriginatorEntityTypeAndSearchText(tenantId.getId(),
                        originatorType, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink, Map.of(
                                "templateName", "t.name"
                        ))))
                .mapData(NotificationRequestInfoEntity::toData);
    }
    /**
     * Finds ids by rule id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param requestStatus request status ({@link NotificationRequestStatus})
     * @param ruleId rule id ({@link NotificationRuleId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<NotificationRequestId> findIdsByRuleId(TenantId tenantId, NotificationRequestStatus requestStatus, NotificationRuleId ruleId) {
        return notificationRequestRepository.findAllIdsByStatusAndRuleId(requestStatus, ruleId.getId()).stream()
                .map(NotificationRequestId::new).collect(Collectors.toList());
    }
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

    @Override
    public List<NotificationRequest> findByRuleIdAndOriginatorEntityIdAndStatus(TenantId tenantId, NotificationRuleId ruleId, EntityId originatorEntityId, NotificationRequestStatus status) {
        return DaoUtil.convertDataList(notificationRequestRepository.findAllByRuleIdAndOriginatorEntityIdAndOriginatorEntityTypeAndStatus(ruleId.getId(), originatorEntityId.getId(), originatorEntityId.getEntityType(), status));
    }
    /**
     * Finds all by status.
     *
     * @param status status ({@link NotificationRequestStatus})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationRequest> findAllByStatus(NotificationRequestStatus status, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRequestRepository.findAllByStatus(status, DaoUtil.toPageable(pageLink)));
    }
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

    @Override
    public void updateById(TenantId tenantId, NotificationRequestId requestId, NotificationRequestStatus requestStatus, NotificationRequestStats stats) {
        notificationRequestRepository.updateStatusAndStatsById(requestId.getId(), requestStatus, JacksonUtil.valueToTree(stats));
    }
    /**
     * Exists by tenant id and status and target id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param status status ({@link NotificationRequestStatus})
     * @param targetId target id ({@link NotificationTargetId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean existsByTenantIdAndStatusAndTargetId(TenantId tenantId, NotificationRequestStatus status, NotificationTargetId targetId) {
        return notificationRequestRepository.existsByTenantIdAndStatusAndTargetsContaining(tenantId.getId(), status, targetId.getId().toString());
    }
    /**
     * Exists by tenant id and status and template id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param status status ({@link NotificationRequestStatus})
     * @param templateId template id ({@link NotificationTemplateId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean existsByTenantIdAndStatusAndTemplateId(TenantId tenantId, NotificationRequestStatus status, NotificationTemplateId templateId) {
        return notificationRequestRepository.existsByTenantIdAndStatusAndTemplateId(tenantId.getId(), status, templateId.getId());
    }
    /**
     * Removes by tenant id and created time before batch.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ts ts
     * @param batchSize batch size
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int removeByTenantIdAndCreatedTimeBeforeBatch(TenantId tenantId, long ts, int batchSize) {
        return notificationRequestRepository.deleteByTenantIdAndCreatedTimeBeforeBatch(tenantId.getId(), ts, batchSize);
    }
    /**
     * Finds info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link NotificationRequestInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationRequestInfo findInfoById(TenantId tenantId, NotificationRequestId id) {
        NotificationRequestInfoEntity info = notificationRequestRepository.findInfoById(id.getId());
        return info != null ? info.toData() : null;
    }
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeByTenantId(TenantId tenantId) {
        notificationRequestRepository.deleteByTenantId(tenantId.getId());
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<NotificationRequestEntity> getEntityClass() {
        return NotificationRequestEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<NotificationRequestEntity, UUID> getRepository() {
        return notificationRequestRepository;
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_REQUEST;
    }

}
