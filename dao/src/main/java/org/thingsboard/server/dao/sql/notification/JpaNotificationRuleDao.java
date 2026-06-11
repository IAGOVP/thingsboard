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
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.NotificationRule;
import org.thingsboard.server.common.data.notification.rule.NotificationRuleInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.NotificationRuleEntity;
import org.thingsboard.server.dao.model.sql.NotificationRuleInfoEntity;
import org.thingsboard.server.dao.notification.NotificationRuleDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of notification rule dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
@RequiredArgsConstructor
public class JpaNotificationRuleDao extends JpaAbstractDao<NotificationRuleEntity, NotificationRule> implements NotificationRuleDao, TenantEntityDao<NotificationRule> {

    private final NotificationRuleRepository notificationRuleRepository;
    /**
     * Finds by tenant id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationRule> findByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRuleRepository.findByTenantIdAndSearchText(tenantId.getId(),
                pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds infos by tenant id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationRuleInfo> findInfosByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(notificationRuleRepository.findInfosByTenantIdAndSearchText(tenantId.getId(),
                        pageLink.getTextSearch(), DaoUtil.toPageable(pageLink, Map.of(
                                "templateName", "t.name"
                        ))))
                .mapData(NotificationRuleInfoEntity::toData);
    }
    /**
     * Exists by tenant id and target id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param targetId target id ({@link NotificationTargetId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean existsByTenantIdAndTargetId(TenantId tenantId, NotificationTargetId targetId) {
        return notificationRuleRepository.existsByTenantIdAndRecipientsConfigContaining(tenantId.getId(), targetId.getId().toString());
    }
    /**
     * Finds by tenant id and trigger type and enabled.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param triggerType trigger type ({@link NotificationRuleTriggerType})
     * @param enabled enabled
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<NotificationRule> findByTenantIdAndTriggerTypeAndEnabled(TenantId tenantId, NotificationRuleTriggerType triggerType, boolean enabled) {
        return DaoUtil.convertDataList(notificationRuleRepository.findAllByTenantIdAndTriggerTypeAndEnabled(tenantId.getId(), triggerType, enabled));
    }
    /**
     * Finds info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link NotificationRuleInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationRuleInfo findInfoById(TenantId tenantId, NotificationRuleId id) {
        NotificationRuleInfoEntity infoEntity = notificationRuleRepository.findInfoById(id.getId());
        return infoEntity != null ? infoEntity.toData() : null;
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
        notificationRuleRepository.deleteByTenantId(tenantId.getId());
    }
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link NotificationRule}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationRule findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(notificationRuleRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link NotificationRule}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationRule findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(notificationRuleRepository.findByTenantIdAndName(tenantId, name));
    }
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationRule> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRuleRepository.findByTenantId(tenantId, DaoUtil.toPageable(pageLink)));
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link NotificationRuleId})
     * @return {@link NotificationRuleId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationRuleId getExternalIdByInternal(NotificationRuleId internalId) {
        return DaoUtil.toEntityId(notificationRuleRepository.getExternalIdByInternal(internalId.getId()), NotificationRuleId::new);
    }
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationRule> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<NotificationRuleEntity> getEntityClass() {
        return NotificationRuleEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<NotificationRuleEntity, UUID> getRepository() {
        return notificationRuleRepository;
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_RULE;
    }

}
