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
package org.thingsboard.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.NotificationTemplateId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.notification.NotificationTemplateService;
import org.thingsboard.server.dao.service.ConstraintValidator;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;
/**
 * Imports notification template entities from export JSON.
 *
 * <p>Resolves references, applies conflict strategy, and persists through DAO services.
 */

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class NotificationTemplateImportService extends BaseEntityImportService<NotificationTemplateId, NotificationTemplate, EntityExportData<NotificationTemplate>> {

    private final NotificationTemplateService notificationTemplateService;
    /**
     * Set owner.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationTemplate notification template ({@link NotificationTemplate})
     * @param idProvider id provider ({@link IdProvider})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void setOwner(TenantId tenantId, NotificationTemplate notificationTemplate, IdProvider idProvider) {
        notificationTemplate.setTenantId(tenantId);
    }
    /**
     * Prepare.
     *
     * @param ctx calculated-field execution context
     * @param notificationTemplate notification template ({@link NotificationTemplate})
     * @param oldEntity old entity ({@link NotificationTemplate})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @return {@link NotificationTemplate}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected NotificationTemplate prepare(EntitiesImportCtx ctx, NotificationTemplate notificationTemplate, NotificationTemplate oldEntity, EntityExportData<NotificationTemplate> exportData, IdProvider idProvider) {
        return notificationTemplate;
    }
    /**
     * Saves or updates the requested data.
     *
     * @param ctx calculated-field execution context
     * @param notificationTemplate notification template ({@link NotificationTemplate})
     * @param exportData export data ({@link EntityExportData})
     * @param idProvider id provider ({@link IdProvider})
     * @param compareResult compare result ({@link CompareResult})
     * @return {@link NotificationTemplate}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected NotificationTemplate saveOrUpdate(EntitiesImportCtx ctx, NotificationTemplate notificationTemplate, EntityExportData<NotificationTemplate> exportData, IdProvider idProvider, CompareResult compareResult) {
        ConstraintValidator.validateFields(notificationTemplate);
        return notificationTemplateService.saveNotificationTemplate(ctx.getTenantId(), notificationTemplate);
    }
    /**
     * Handles entity saved.
     *
     * @param user authenticated user performing the action
     * @param savedEntity saved entity ({@link NotificationTemplate})
     * @param oldEntity old entity ({@link NotificationTemplate})
     * @return nothing
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    @Override
    protected void onEntitySaved(User user, NotificationTemplate savedEntity, NotificationTemplate oldEntity) throws ThingsboardException {
        entityActionService.logEntityAction(user, savedEntity.getId(), savedEntity, null,
                oldEntity == null ? ActionType.ADDED : ActionType.UPDATED, null);
    }
    /**
     * Deep copy.
     *
     * @param notificationTemplate notification template ({@link NotificationTemplate})
     * @return {@link NotificationTemplate}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected NotificationTemplate deepCopy(NotificationTemplate notificationTemplate) {
        return new NotificationTemplate(notificationTemplate);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_TEMPLATE;
    }

}
