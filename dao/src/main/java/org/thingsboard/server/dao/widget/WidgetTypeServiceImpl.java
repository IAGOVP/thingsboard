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
package org.thingsboard.server.dao.widget;

import com.google.common.util.concurrent.FluentFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeFilter;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundleWidget;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.service.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.thingsboard.server.dao.service.Validator.validateIds;
/**
 * Spring {@code @Service} implementing the widget type DAO API.
 *
 * <p>Delegates to {@code *Dao} implementations and manages cache eviction (widget types and widget bundles).
 */


@Service("WidgetTypeDaoService")
@Slf4j
public class WidgetTypeServiceImpl implements WidgetTypeService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_WIDGETS_BUNDLE_ID = "Incorrect widgetsBundleId ";

    @Autowired
    private WidgetTypeDao widgetTypeDao;

    @Autowired
    private DataValidator<WidgetTypeDetails> widgetTypeValidator;

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Autowired
    protected ImageService imageService;

    @Autowired
    private ResourceService resourceService;
    /**
     * Finds widget type by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return {@link WidgetType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetType findWidgetTypeById(TenantId tenantId, WidgetTypeId widgetTypeId) {
        log.trace("Executing findWidgetTypeById [{}]", widgetTypeId);
        Validator.validateId(widgetTypeId, id -> "Incorrect widgetTypeId " + id);
        return widgetTypeDao.findWidgetTypeById(tenantId, widgetTypeId.getId());
    }
    /**
     * Finds widget type details by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeDetails findWidgetTypeDetailsById(TenantId tenantId, WidgetTypeId widgetTypeId) {
        log.trace("Executing findWidgetTypeDetailsById [{}]", widgetTypeId);
        Validator.validateId(widgetTypeId, id -> "Incorrect widgetTypeId " + id);
        return widgetTypeDao.findById(tenantId, widgetTypeId.getId());
    }
    /**
     * Finds widget type info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return {@link WidgetTypeInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeInfo findWidgetTypeInfoById(TenantId tenantId, WidgetTypeId widgetTypeId) {
        log.trace("Executing findWidgetTypeInfoById [{}]", widgetTypeId);
        Validator.validateId(widgetTypeId, id -> "Incorrect widgetTypeId " + id);
        return widgetTypeDao.findWidgetTypeInfoById(tenantId, widgetTypeId.getId());
    }
    /**
     * Widget type exists by tenant id and widget type id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean widgetTypeExistsByTenantIdAndWidgetTypeId(TenantId tenantId, WidgetTypeId widgetTypeId) {
        log.trace("Executing widgetTypeExistsByTenantIdAndWidgetTypeId, tenantId [{}],  widgetTypeId [{}]", tenantId, widgetTypeId);
        Validator.validateId(widgetTypeId, id -> "Incorrect widgetTypeId " + id);
        return widgetTypeDao.existsByTenantIdAndId(tenantId, widgetTypeId.getId());
    }
    /**
     * Saves or persists widget type.
     *
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeDetails saveWidgetType(WidgetTypeDetails widgetTypeDetails) {
        log.trace("Executing saveWidgetType [{}]", widgetTypeDetails);
        widgetTypeValidator.validate(widgetTypeDetails, WidgetType::getTenantId);
        try {
            TenantId tenantId = widgetTypeDetails.getTenantId();
            if (CollectionUtils.isNotEmpty(widgetTypeDetails.getResources())) {
                resourceService.importResources(tenantId, widgetTypeDetails.getResources());
            }
            imageService.updateImagesUsage(widgetTypeDetails);
            resourceService.updateResourcesUsage(tenantId, widgetTypeDetails);

            WidgetTypeDetails result = widgetTypeDao.save(tenantId, widgetTypeDetails);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId)
                    .entityId(result.getId()).created(widgetTypeDetails.getId() == null).build());
            return result;
        } catch (Exception t) {
            AbstractCachedEntityService.checkConstraintViolation(t,
                    "uq_widget_type_fqn", "Widget type with such fqn already exists!");
            AbstractCachedEntityService.checkConstraintViolation(t, "widget_type_external_id_unq_key", "Widget type with such external id already exists!");
            throw t;
        }
    }
    /**
     * Deletes widget type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void deleteWidgetType(TenantId tenantId, WidgetTypeId widgetTypeId) {
        log.trace("Executing deleteWidgetType [{}]", widgetTypeId);
        Validator.validateId(widgetTypeId, id -> "Incorrect widgetTypeId " + id);
        widgetTypeDao.removeById(tenantId, widgetTypeId.getId());
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(widgetTypeId).build());
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
        deleteWidgetType(tenantId, (WidgetTypeId) id);
    }
    /**
     * Finds system widget types by page link.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findSystemWidgetTypesByPageLink(WidgetTypeFilter widgetTypeFilter, PageLink pageLink) {
        log.trace("Executing findSystemWidgetTypesByPageLink, pageLink [{}]", pageLink);
        Validator.validatePageLink(pageLink);
        return widgetTypeDao.findSystemWidgetTypes(widgetTypeFilter, pageLink);
    }
    /**
     * Finds all tenant widget types by tenant id and page link.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findAllTenantWidgetTypesByTenantIdAndPageLink(WidgetTypeFilter widgetTypeFilter, PageLink pageLink) {
        TenantId tenantId = widgetTypeFilter.getTenantId();
        log.trace("Executing findAllTenantWidgetTypesByTenantIdAndPageLink, tenantId [{}], pageLink [{}]",
                tenantId, pageLink);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validatePageLink(pageLink);
        return widgetTypeDao.findAllTenantWidgetTypesByTenantId(widgetTypeFilter, pageLink);
    }
    /**
     * Finds tenant widget types by tenant id and page link.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findTenantWidgetTypesByTenantIdAndPageLink(WidgetTypeFilter widgetTypeFilter, PageLink pageLink) {
        TenantId tenantId = widgetTypeFilter.getTenantId();
        log.trace("Executing findTenantWidgetTypesByTenantIdAndPageLink, tenantId [{}], pageLink [{}]",
                tenantId, pageLink);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validatePageLink(pageLink);
        return widgetTypeDao.findTenantWidgetTypesByTenantId(widgetTypeFilter, pageLink);
    }
    /**
     * Finds widget types by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetType> findWidgetTypesByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId) {
        log.trace("Executing findWidgetTypesByWidgetsBundleId, tenantId [{}], widgetsBundleId [{}]", tenantId, widgetsBundleId);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validateId(widgetsBundleId, id -> INCORRECT_WIDGETS_BUNDLE_ID + id);
        return widgetTypeDao.findWidgetTypesByWidgetsBundleId(tenantId.getId(), widgetsBundleId.getId());
    }
    /**
     * Finds widget types details by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetTypeDetails> findWidgetTypesDetailsByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId) {
        log.trace("Executing findWidgetTypesDetailsByWidgetsBundleId, tenantId [{}], widgetsBundleId [{}]", tenantId, widgetsBundleId);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validateId(widgetsBundleId, id -> INCORRECT_WIDGETS_BUNDLE_ID + id);
        return widgetTypeDao.findWidgetTypesDetailsByWidgetsBundleId(tenantId.getId(), widgetsBundleId.getId());

    }
    /**
     * Finds widget types infos by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @param fullSearch full search
     * @param deprecatedFilter deprecated filter ({@link DeprecatedFilter})
     * @param widgetTypes widget types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findWidgetTypesInfosByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId, boolean fullSearch,
                                                                          DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink) {
        log.trace("Executing findWidgetTypesInfosByWidgetsBundleId, tenantId [{}], widgetsBundleId [{}], fullSearch [{}], deprecatedFilter [{}], widgetTypes [{}], pageLink [{}]",
                tenantId, widgetsBundleId, fullSearch, deprecatedFilter, widgetTypes, pageLink);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validateId(widgetsBundleId, id -> INCORRECT_WIDGETS_BUNDLE_ID + id);
        Validator.validatePageLink(pageLink);
        return widgetTypeDao.findWidgetTypesInfosByWidgetsBundleId(tenantId.getId(), widgetsBundleId.getId(), fullSearch, deprecatedFilter, widgetTypes, pageLink);
    }
    /**
     * Finds widget fqns by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<String> findWidgetFqnsByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId) {
        log.trace("Executing findWidgetTypesInfosByWidgetsBundleId, tenantId [{}], widgetsBundleId [{}]", tenantId, widgetsBundleId);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validateId(widgetsBundleId, id -> INCORRECT_WIDGETS_BUNDLE_ID + id);
        return widgetTypeDao.findWidgetFqnsByWidgetsBundleId(tenantId.getId(), widgetsBundleId.getId());
    }
    /**
     * Finds widget type by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetType findWidgetTypeByTenantIdAndFqn(TenantId tenantId, String fqn) {
        log.trace("Executing findWidgetTypeByTenantIdAndFqn, tenantId [{}], fqn [{}]", tenantId, fqn);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validateString(fqn, f -> "Incorrect fqn " + f);
        return widgetTypeDao.findByTenantIdAndFqn(tenantId.getId(), fqn);
    }
    /**
     * Finds widget type details by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeDetails findWidgetTypeDetailsByTenantIdAndFqn(TenantId tenantId, String fqn) {
        log.trace("Executing findWidgetTypeDetailsByTenantIdAndFqn, tenantId [{}], fqn [{}]", tenantId, fqn);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validateString(fqn, f -> "Incorrect fqn " + f);
        return widgetTypeDao.findDetailsByTenantIdAndFqn(tenantId.getId(), fqn);
    }
    /**
     * Updates widgets bundle widget types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @param widgetTypeIds widget type ids ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void updateWidgetsBundleWidgetTypes(TenantId tenantId, WidgetsBundleId widgetsBundleId, List<WidgetTypeId> widgetTypeIds) {
        log.trace("Executing updateWidgetsBundleWidgetTypes, tenantId [{}], widgetsBundleId [{}], widgetTypeIds [{}]", tenantId, widgetsBundleId, widgetTypeIds);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Validator.validateId(widgetsBundleId, id -> INCORRECT_WIDGETS_BUNDLE_ID + id);
        Validator.checkNotNull(widgetTypeIds, "Incorrect widgetTypeIds " + widgetTypeIds);
        if (!widgetTypeIds.isEmpty()) {
            validateIds(widgetTypeIds, ids -> "Incorrect widgetTypeIds " + ids);
        }
        List<WidgetsBundleWidget> bundleWidgets = new ArrayList<>();
        for (int index = 0; index < widgetTypeIds.size(); index++) {
            bundleWidgets.add(new WidgetsBundleWidget(widgetsBundleId, widgetTypeIds.get(index), index));
        }
        List<WidgetsBundleWidget> existingBundleWidgets = widgetTypeDao.findWidgetsBundleWidgetsByWidgetsBundleId(tenantId.getId(), widgetsBundleId.getId());
        List<WidgetTypeId> toRemove = existingBundleWidgets.stream()
                .map(WidgetsBundleWidget::getWidgetTypeId)
                .filter(widgetTypeId -> bundleWidgets.stream().noneMatch(newBundleWidget ->
                        newBundleWidget.getWidgetTypeId().equals(widgetTypeId))).toList();
        for (WidgetTypeId widgetTypeId : toRemove) {
            widgetTypeDao.removeWidgetTypeFromWidgetsBundle(widgetsBundleId.getId(), widgetTypeId.getId());
        }
        for (WidgetsBundleWidget widgetsBundleWidget : bundleWidgets) {
            widgetTypeDao.saveWidgetsBundleWidget(widgetsBundleWidget);
        }
        eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId)
                .entityId(widgetsBundleId).created(false).build());
    }
    /**
     * Updates widgets bundle widget fqns.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @param widgetFqns widget fqns ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void updateWidgetsBundleWidgetFqns(TenantId tenantId, WidgetsBundleId widgetsBundleId, List<String> widgetFqns) {
        log.trace("Executing updateWidgetsBundleWidgetFqns, tenantId [{}], widgetsBundleId [{}], widgetFqns [{}]", tenantId, widgetsBundleId, widgetFqns);
        List<WidgetTypeId> widgetTypeIds = widgetTypeDao.findWidgetTypeIdsByTenantIdAndFqns(tenantId.getId(), widgetFqns);
        this.updateWidgetsBundleWidgetTypes(tenantId, widgetsBundleId, widgetTypeIds);
    }
    /**
     * Deletes widget types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void deleteWidgetTypesByTenantId(TenantId tenantId) {
        log.trace("Executing deleteWidgetTypesByTenantId, tenantId [{}]", tenantId);
        Validator.validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        tenantWidgetTypeRemover.removeEntities(tenantId, tenantId);
    }
    /**
     * Deletes widget types by bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param bundleId bundle id ({@link WidgetsBundleId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void deleteWidgetTypesByBundleId(TenantId tenantId, WidgetsBundleId bundleId) {
        log.trace("Executing deleteWidgetTypesByBundleId, tenantId [{}], bundleId [{}]", tenantId, bundleId);
        bundleWidgetTypesRemover.removeEntities(tenantId, bundleId);
    }
    /**
     * Finds all widget types ids.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeId> findAllWidgetTypesIds(PageLink pageLink) {
        return widgetTypeDao.findAllWidgetTypesIds(pageLink);
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
        deleteWidgetTypesByTenantId(tenantId);
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
        return Optional.ofNullable(findWidgetTypeById(tenantId, new WidgetTypeId(entityId.getId())));
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
        return FluentFuture.from(widgetTypeDao.findByIdAsync(tenantId, entityId.getId()))
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
        return EntityType.WIDGET_TYPE;
    }

    private final PaginatedRemover<TenantId, WidgetTypeInfo> tenantWidgetTypeRemover = new PaginatedRemover<>() {

        @Override
        protected PageData<WidgetTypeInfo> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return widgetTypeDao.findTenantWidgetTypesByTenantId(
                    WidgetTypeFilter.builder()
                            .tenantId(id)
                            .fullSearch(false)
                            .deprecatedFilter(DeprecatedFilter.ALL)
                            .widgetTypes(null).build(),
                    pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, WidgetTypeInfo entity) {
            deleteWidgetType(tenantId, new WidgetTypeId(entity.getUuidId()));
        }

    };

    private final PaginatedRemover<WidgetsBundleId, WidgetTypeInfo> bundleWidgetTypesRemover = new PaginatedRemover<>() {

        @Override
        protected PageData<WidgetTypeInfo> findEntities(TenantId tenantId, WidgetsBundleId widgetsBundleId, PageLink pageLink) {
            return findWidgetTypesInfosByWidgetsBundleId(tenantId, widgetsBundleId, false, DeprecatedFilter.ALL, null, pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, WidgetTypeInfo widgetTypeInfo) {
            deleteWidgetType(tenantId, widgetTypeInfo.getId());
        }

    };

}
