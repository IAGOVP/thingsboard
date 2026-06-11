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
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.NotificationRule;
import org.thingsboard.server.common.data.notification.rule.NotificationRuleInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;

import java.util.List;
import java.util.Optional;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
/**
 * Spring component for default notification rule service (notification templates, targets, rules, and delivery requests).
 */







@Service
@RequiredArgsConstructor
public class DefaultNotificationRuleService extends AbstractEntityService implements NotificationRuleService, EntityDaoService {

    private final NotificationRuleDao notificationRuleDao;

    
    /**
     * Saves or persists notification rule.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationRule notification rule ({@link NotificationRule})
     * @return {@link NotificationRule}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public NotificationRule saveNotificationRule(TenantId tenantId, NotificationRule notificationRule) {
        if (notificationRule.getId() != null) {
            NotificationRule oldNotificationRule = findNotificationRuleById(tenantId, notificationRule.getId());
            if (notificationRule.getTriggerType() != oldNotificationRule.getTriggerType()) {
                throw new IllegalArgumentException("Notification rule trigger type cannot be updated");
            }
        }
        try {
            NotificationRule savedRule = notificationRuleDao.saveAndFlush(tenantId, notificationRule);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId).entityId(savedRule.getId())
                    .created(notificationRule.getId() == null).build());
            return savedRule;
        } catch (Exception e) {
            checkConstraintViolation(e, "uq_notification_rule_name", "Notification rule with such name already exists");
            throw e;
        }
    }

    
    /**
     * Finds notification rule by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link NotificationRule}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public NotificationRule findNotificationRuleById(TenantId tenantId, NotificationRuleId id) {
        return notificationRuleDao.findById(tenantId, id.getId());
    }

    
    /**
     * Finds notification rule info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link NotificationRuleInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public NotificationRuleInfo findNotificationRuleInfoById(TenantId tenantId, NotificationRuleId id) {
        return notificationRuleDao.findInfoById(tenantId, id);
    }

    
    /**
     * Finds notification rules infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<NotificationRuleInfo> findNotificationRulesInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        return notificationRuleDao.findInfosByTenantIdAndPageLink(tenantId, pageLink);
    }

    
    /**
     * Finds notification rules by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<NotificationRule> findNotificationRulesByTenantId(TenantId tenantId, PageLink pageLink) {
        return notificationRuleDao.findByTenantIdAndPageLink(tenantId, pageLink);
    }

    
    /**
     * Finds enabled notification rules by tenant id and trigger type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param triggerType trigger type ({@link NotificationRuleTriggerType})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<NotificationRule> findEnabledNotificationRulesByTenantIdAndTriggerType(TenantId tenantId, NotificationRuleTriggerType triggerType) {
        return notificationRuleDao.findByTenantIdAndTriggerTypeAndEnabled(tenantId, triggerType, true);
    }

    
    /**
     * Deletes notification rule by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteNotificationRuleById(TenantId tenantId, NotificationRuleId id) {
        notificationRuleDao.removeById(tenantId, id.getId());
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(id).build());
    }

    
    /**
     * Deletes entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param force force
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        deleteNotificationRuleById(tenantId, (NotificationRuleId) id);
    }

    
    /**
     * Deletes notification rules by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteNotificationRulesByTenantId(TenantId tenantId) {
        notificationRuleDao.removeByTenantId(tenantId);
    }

    
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteByTenantId(TenantId tenantId) {
        deleteNotificationRulesByTenantId(tenantId);
    }

    
    /**
     * Finds entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return optional {@link HasId}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findNotificationRuleById(tenantId, new NotificationRuleId(entityId.getId())));
    }

    
    /**
     * Finds entity async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(notificationRuleDao.findByIdAsync(tenantId, entityId.getId()))
                .transform(Optional::ofNullable, directExecutor());
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
