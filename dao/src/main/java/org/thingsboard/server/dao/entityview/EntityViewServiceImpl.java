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
package org.thingsboard.server.dao.entityview;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.EntityViewInfo;
import org.thingsboard.server.common.data.NameConflictPolicy;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.entityview.EntityViewSearchQuery;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.dao.entity.CachedVersionedEntityService;
import org.thingsboard.server.dao.eventsourcing.ActionEntityEvent;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.service.validator.EntityViewDataValidator;
import org.thingsboard.server.dao.sql.JpaExecutorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.thingsboard.server.dao.DaoUtil.toUUIDs;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;
import static org.thingsboard.server.dao.service.Validator.validateString;
/**
 * Spring {@code @Service} implementing the entity view DAO API.
 *
 * <p>Delegates to {@code *Dao} implementations and manages cache eviction (ThingsBoard DAO layer).
 */


@Service("EntityViewDaoService")
@Slf4j
public class EntityViewServiceImpl extends CachedVersionedEntityService<EntityViewCacheKey, EntityViewCacheValue, EntityViewEvictEvent> implements EntityViewService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_CUSTOMER_ID = "Incorrect customerId ";
    public static final String INCORRECT_ENTITY_VIEW_ID = "Incorrect entityViewId ";
    public static final String INCORRECT_EDGE_ID = "Incorrect edgeId ";

    @Autowired
    private EntityViewDao entityViewDao;

    @Autowired
    private EntityViewDataValidator entityViewValidator;

    @Autowired
    protected JpaExecutorService service;

    
    /**
     * Handles evict event.
     *
     * @param event event ({@link EntityViewEvictEvent})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @TransactionalEventListener(classes = EntityViewEvictEvent.class)
    @Override
    public void handleEvictEvent(EntityViewEvictEvent event) {
        List<EntityViewCacheKey> toEvict = new ArrayList<>(5);
        toEvict.add(EntityViewCacheKey.byName(event.getTenantId(), event.getNewName()));
        if (event.getSavedEntityView() != null) {
            cache.put(EntityViewCacheKey.byId(event.getSavedEntityView().getId()), new EntityViewCacheValue(event.getSavedEntityView(), null));
        } else if (event.getEntityViewId() != null) {
            toEvict.add(EntityViewCacheKey.byId(event.getEntityViewId()));
        }
        toEvict.add(EntityViewCacheKey.byEntityId(event.getTenantId(), event.getNewEntityId()));
        if (event.getOldEntityId() != null && !event.getOldEntityId().equals(event.getNewEntityId())) {
            toEvict.add(EntityViewCacheKey.byEntityId(event.getTenantId(), event.getOldEntityId()));
        }
        if (StringUtils.isNotEmpty(event.getOldName()) && !event.getOldName().equals(event.getNewName())) {
            toEvict.add(EntityViewCacheKey.byName(event.getTenantId(), event.getOldName()));
        }
        cache.evict(toEvict);
    }

    
    /**
     * Saves or persists entity view.
     *
     * @param entityView entity view ({@link EntityView})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView saveEntityView(EntityView entityView) {
        return saveEntityView(entityView, true);
    }

    
    /**
     * Saves or persists entity view.
     *
     * @param entityView entity view ({@link EntityView})
     * @param nameConflictStrategy name conflict strategy ({@link NameConflictStrategy})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView saveEntityView(EntityView entityView, NameConflictStrategy nameConflictStrategy) {
        return saveEntityView(entityView, true, nameConflictStrategy);
    }

    
    /**
     * Saves or persists entity view.
     *
     * @param entityView entity view ({@link EntityView})
     * @param doValidate do validate
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView saveEntityView(EntityView entityView, boolean doValidate) {
        return saveEntityView(entityView, doValidate, NameConflictStrategy.DEFAULT);
    }

    private EntityView saveEntityView(EntityView entityView, boolean doValidate, NameConflictStrategy nameConflictStrategy) {
        log.trace("Executing save entity view [{}]", entityView);
        EntityView old = (entityView.getId() != null) ? entityViewDao.findById(entityView.getTenantId(), entityView.getId().getId()) : null;
        if (nameConflictStrategy.policy() == NameConflictPolicy.UNIQUIFY && (old == null || !entityView.getName().equals(old.getName()))) {
            uniquifyEntityName(entityView, old, entityView::setName, EntityType.ENTITY_VIEW, nameConflictStrategy);
        }
        if (doValidate) {
            entityViewValidator.validate(entityView, EntityView::getTenantId);
        }
        try {
            EntityView saved = entityViewDao.save(entityView.getTenantId(), entityView);
            publishEvictEvent(new EntityViewEvictEvent(saved.getTenantId(), saved.getId(), saved.getEntityId(), old != null ? old.getEntityId() : null, saved.getName(), old != null ? old.getName() : null, saved));
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(saved.getTenantId())
                    .entityId(saved.getId()).entity(saved).created(entityView.getId() == null).build());
            return saved;
        } catch (Exception t) {
            checkConstraintViolation(t,
                    "entity_view_external_id_unq_key", "Entity View with such external id already exists!");
            throw t;
        }
    }

    
    /**
     * Assigns entity view to customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param customerId target customer identifier
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView assignEntityViewToCustomer(TenantId tenantId, EntityViewId entityViewId, CustomerId customerId) {
        EntityView entityView = findEntityViewById(tenantId, entityViewId);
        if (customerId.equals(entityView.getCustomerId())) {
            return entityView;
        }
        entityView.setCustomerId(customerId);
        return saveEntityView(entityView);
    }

    
    /**
     * Unassigns entity view from customer.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView unassignEntityViewFromCustomer(TenantId tenantId, EntityViewId entityViewId) {
        EntityView entityView = findEntityViewById(tenantId, entityViewId);
        if (entityView.getCustomerId() == null) {
            return entityView;
        }
        entityView.setCustomerId(null);
        return saveEntityView(entityView);
    }

    
    /**
     * Unassigns customer entity views.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void unassignCustomerEntityViews(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing unassignCustomerEntityViews, tenantId [{}], customerId [{}]", tenantId, customerId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(customerId, id -> INCORRECT_CUSTOMER_ID + id);
        customerEntityViewsRemover.removeEntities(tenantId, customerId);
    }

    
    /**
     * Finds entity view info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return {@link EntityViewInfo}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityViewInfo findEntityViewInfoById(TenantId tenantId, EntityViewId entityViewId) {
        log.trace("Executing findEntityViewInfoById [{}]", entityViewId);
        validateId(entityViewId, id -> INCORRECT_ENTITY_VIEW_ID + id);
        return entityViewDao.findEntityViewInfoById(tenantId, entityViewId.getId());
    }

    
    /**
     * Finds entity view by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView findEntityViewById(TenantId tenantId, EntityViewId entityViewId) {
        return findEntityViewById(tenantId, entityViewId, true);
    }

    
    /**
     * Finds entity view by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param putInCache put in cache
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView findEntityViewById(TenantId tenantId, EntityViewId entityViewId, boolean putInCache) {
        log.trace("Executing findEntityViewById [{}]", entityViewId);
        validateId(entityViewId, id -> INCORRECT_ENTITY_VIEW_ID + id);
        return findEntityViewByIdInternal(tenantId, entityViewId, putInCache);
    }

    
    /**
     * Finds entity view by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return future completing with {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<EntityView> findEntityViewByIdAsync(TenantId tenantId, EntityViewId entityViewId) {
        log.trace("Executing findEntityViewByIdAsync [{}]", entityViewId);
        validateId(entityViewId, id -> INCORRECT_ENTITY_VIEW_ID + id);
        return service.submit(() -> findEntityViewByIdInternal(tenantId, entityViewId, true));
    }

    private EntityView findEntityViewByIdInternal(TenantId tenantId, EntityViewId entityViewId, boolean putInCache) {
        EntityViewCacheValue value = cache.get(EntityViewCacheKey.byId(entityViewId), () -> {
            EntityView entityView = entityViewDao.findById(tenantId, entityViewId.getId());
            return new EntityViewCacheValue(entityView, null);
        }, putInCache);
        return value != null ? value.getEntityView() : null;
    }

    
    /**
     * Finds entity view by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView findEntityViewByTenantIdAndName(TenantId tenantId, String name) {
        log.trace("Executing findEntityViewByTenantIdAndName [{}][{}]", tenantId, name);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        return cache.getAndPutInTransaction(EntityViewCacheKey.byName(tenantId, name),
                () -> entityViewDao.findEntityViewByTenantIdAndName(tenantId.getId(), name).orElse(null)
                , EntityViewCacheValue::getEntityView, v -> new EntityViewCacheValue(v, null), true);
    }

    
    /**
     * Finds entity view by tenant id and name async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return future completing with {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<EntityView> findEntityViewByTenantIdAndNameAsync(TenantId tenantId, String name) {
        log.trace("Executing findEntityViewByTenantIdAndNameAsync [{}][{}]", tenantId, name);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        return service.submit(() -> findEntityViewByTenantIdAndName(tenantId, name));
    }

    
    /**
     * Finds entity view by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findEntityViewsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validatePageLink(pageLink);
        return entityViewDao.findEntityViewsByTenantId(tenantId.getId(), pageLink);
    }

    
    /**
     * Finds entity view infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findEntityViewInfosByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validatePageLink(pageLink);
        return entityViewDao.findEntityViewInfosByTenantId(tenantId.getId(), pageLink);
    }

    
    /**
     * Finds entity view by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @param type type ({@link String})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewByTenantIdAndType(TenantId tenantId, PageLink pageLink, String type) {
        log.trace("Executing findEntityViewByTenantIdAndType, tenantId [{}], pageLink [{}], type [{}]", tenantId, pageLink, type);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validatePageLink(pageLink);
        validateString(type, t -> "Incorrect type " + t);
        return entityViewDao.findEntityViewsByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    
    /**
     * Finds entity view infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        log.trace("Executing findEntityViewInfosByTenantIdAndType, tenantId [{}], pageLink [{}], type [{}]", tenantId, pageLink, type);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validatePageLink(pageLink);
        validateString(type, t -> "Incorrect type " + t);
        return entityViewDao.findEntityViewInfosByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    
    /**
     * Finds entity views by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId,
                                                                       PageLink pageLink) {
        log.trace("Executing findEntityViewByTenantIdAndCustomerId, tenantId [{}], customerId [{}]," +
                " pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(customerId, id -> INCORRECT_CUSTOMER_ID + id);
        validatePageLink(pageLink);
        return entityViewDao.findEntityViewsByTenantIdAndCustomerId(tenantId.getId(),
                customerId.getId(), pageLink);
    }

    
    /**
     * Finds entity views by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewIds entity view ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityView> findEntityViewsByTenantIdAndIds(TenantId tenantId, List<EntityViewId> entityViewIds) {
        log.trace("Executing findEntityViewsByTenantIdAndIds, tenantId [{}], entityViewIds [{}]", tenantId, entityViewIds);
        return entityViewDao.findEntityViewsByTenantIdAndIds(tenantId.getId(), toUUIDs(entityViewIds));
    }

    
    /**
     * Finds entity view infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
        log.trace("Executing findEntityViewInfosByTenantIdAndCustomerId, tenantId [{}], customerId [{}]," +
                " pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(customerId, id -> INCORRECT_CUSTOMER_ID + id);
        validatePageLink(pageLink);
        return entityViewDao.findEntityViewInfosByTenantIdAndCustomerId(tenantId.getId(),
                customerId.getId(), pageLink);
    }

    
    /**
     * Finds entity views by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @param type type ({@link String})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, PageLink pageLink, String type) {
        log.trace("Executing findEntityViewsByTenantIdAndCustomerIdAndType, tenantId [{}], customerId [{}]," +
                " pageLink [{}], type [{}]", tenantId, customerId, pageLink, type);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(customerId, id -> INCORRECT_CUSTOMER_ID + id);
        validatePageLink(pageLink);
        validateString(type, t -> "Incorrect type " + t);
        return entityViewDao.findEntityViewsByTenantIdAndCustomerIdAndType(tenantId.getId(),
                customerId.getId(), type, pageLink);
    }

    
    /**
     * Finds entity view infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink) {
        log.trace("Executing findEntityViewInfosByTenantIdAndCustomerIdAndType, tenantId [{}], customerId [{}]," +
                " pageLink [{}], type [{}]", tenantId, customerId, pageLink, type);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(customerId, id -> INCORRECT_CUSTOMER_ID + id);
        validatePageLink(pageLink);
        validateString(type, t -> "Incorrect type " + t);
        return entityViewDao.findEntityViewInfosByTenantIdAndCustomerIdAndType(tenantId.getId(),
                customerId.getId(), type, pageLink);
    }

    
    /**
     * Finds entity views by query.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param query filter and sort query definition
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<EntityView>> findEntityViewsByQuery(TenantId tenantId, EntityViewSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(tenantId, query.toEntitySearchQuery());
        ListenableFuture<List<EntityView>> entityViews = Futures.transformAsync(relations, r -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<EntityView>> futures = new ArrayList<>();
            for (EntityRelation relation : r) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.ENTITY_VIEW) {
                    futures.add(findEntityViewByIdAsync(tenantId, new EntityViewId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        }, MoreExecutors.directExecutor());

        entityViews = Futures.transform(entityViews, new Function<List<EntityView>, List<EntityView>>() {
            /**
             * Apply.
             */
            @Nullable
            @Override
            public List<EntityView> apply(@Nullable List<EntityView> entityViewList) {
                return entityViewList == null ? Collections.emptyList() : entityViewList.stream().filter(entityView -> query.getEntityViewTypes().contains(entityView.getType())).collect(Collectors.toList());
            }
        }, MoreExecutors.directExecutor());

        return entityViews;
    }

    
    /**
     * Finds entity views by tenant id and entity id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<EntityView>> findEntityViewsByTenantIdAndEntityIdAsync(TenantId tenantId, EntityId entityId) {
        log.trace("Executing findEntityViewsByTenantIdAndEntityIdAsync, tenantId [{}], entityId [{}]", tenantId, entityId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(entityId.getId(), id -> "Incorrect entityId" + id);

        return service.submit(() -> cache.getAndPutInTransaction(EntityViewCacheKey.byEntityId(tenantId, entityId),
                () -> entityViewDao.findEntityViewsByTenantIdAndEntityId(tenantId.getId(), entityId.getId()),
                EntityViewCacheValue::getEntityViews, v -> new EntityViewCacheValue(null, v), true));
    }

    
    /**
     * Finds entity views by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<EntityView> findEntityViewsByTenantIdAndEntityId(TenantId tenantId, EntityId entityId) {
        log.trace("Executing findEntityViewsByTenantIdAndEntityId, tenantId [{}], entityId [{}]", tenantId, entityId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(entityId.getId(), id -> "Incorrect entityId" + id);

        return cache.getAndPutInTransaction(EntityViewCacheKey.byEntityId(tenantId, entityId),
                () -> entityViewDao.findEntityViewsByTenantIdAndEntityId(tenantId.getId(), entityId.getId()),
                EntityViewCacheValue::getEntityViews, v -> new EntityViewCacheValue(null, v), true);
    }

    
    /**
     * Exists by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public boolean existsByTenantIdAndEntityId(TenantId tenantId, EntityId entityId) {
        return entityViewDao.existsByTenantIdAndEntityId(tenantId.getId(), entityId.getId());
    }

    
    /**
     * Deletes entity view.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public void deleteEntityView(TenantId tenantId, EntityViewId entityViewId) {
        log.trace("Executing deleteEntityView [{}]", entityViewId);
        validateId(entityViewId, id -> INCORRECT_ENTITY_VIEW_ID + id);
        EntityView entityView = entityViewDao.findById(tenantId, entityViewId.getId());
        if (entityView == null) {
            return;
        }
        entityViewDao.removeById(tenantId, entityViewId.getId());
        publishEvictEvent(new EntityViewEvictEvent(entityView.getTenantId(), entityView.getId(), entityView.getEntityId(), null, entityView.getName(), null));
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(entityViewId).build());
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
    @Transactional
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        deleteEntityView(tenantId, (EntityViewId) id);
    }

    
    /**
     * Deletes entity views by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteEntityViewsByTenantId(TenantId tenantId) {
        log.trace("Executing deleteEntityViewsByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        tenantEntityViewRemover.removeEntities(tenantId, tenantId);
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
        deleteEntityViewsByTenantId(tenantId);
    }

    
    /**
     * Finds entity view types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<List<EntitySubtype>> findEntityViewTypesByTenantId(TenantId tenantId) {
        log.trace("Executing findEntityViewTypesByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        ListenableFuture<List<EntitySubtype>> tenantEntityViewTypes = entityViewDao.findTenantEntityViewTypesAsync(tenantId.getId());
        return Futures.transform(tenantEntityViewTypes,
                entityViewTypes -> {
                    entityViewTypes.sort(Comparator.comparing(EntitySubtype::getType));
                    return entityViewTypes;
                }, MoreExecutors.directExecutor());
    }

    
    /**
     * Assigns entity view to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView assignEntityViewToEdge(TenantId tenantId, EntityViewId entityViewId, EdgeId edgeId) {
        EntityView entityView = findEntityViewById(tenantId, entityViewId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't assign entityView to non-existent edge!");
        }
        if (!edge.getTenantId().getId().equals(entityView.getTenantId().getId())) {
            throw new DataValidationException("Can't assign entityView to edge from different tenant!");
        }

        boolean relationExists = relationService.checkRelation(tenantId, edgeId, entityView.getEntityId(),
                EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE);
        if (!relationExists) {
            throw new DataValidationException("Can't assign entity view to edge because related device/asset doesn't assigned to edge!");
        }

        try {
            createRelation(tenantId, new EntityRelation(edgeId, entityViewId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            log.warn("[{}] Failed to create entityView relation. Edge Id: [{}]", entityViewId, edgeId);
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(entityViewId)
                .actionType(ActionType.ASSIGNED_TO_EDGE).build());
        return entityView;
    }

    
    /**
     * Unassigns entity view from edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityViewId entity view id ({@link EntityViewId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link EntityView}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityView unassignEntityViewFromEdge(TenantId tenantId, EntityViewId entityViewId, EdgeId edgeId) {
        EntityView entityView = findEntityViewById(tenantId, entityViewId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't unassign entityView from non-existent edge!");
        }
        try {
            deleteRelation(tenantId, new EntityRelation(edgeId, entityViewId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            log.warn("[{}] Failed to delete entityView relation. Edge Id: [{}]", entityViewId, edgeId);
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(entityViewId)
                .actionType(ActionType.UNASSIGNED_FROM_EDGE).build());
        return entityView;
    }

    
    /**
     * Finds entity views by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink) {
        log.trace("Executing findEntityViewsByTenantIdAndEdgeId, tenantId [{}], edgeId [{}], pageLink [{}]", tenantId, edgeId, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(edgeId, id -> INCORRECT_EDGE_ID + id);
        validatePageLink(pageLink);
        return entityViewDao.findEntityViewsByTenantIdAndEdgeId(tenantId.getId(), edgeId.getId(), pageLink);
    }

    
    /**
     * Finds entity views by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink) {
        log.trace("Executing findEntityViewsByTenantIdAndEdgeIdAndType, tenantId [{}], edgeId [{}], type [{}], pageLink [{}]", tenantId, edgeId, type, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(edgeId, id -> INCORRECT_EDGE_ID + id);
        validateString(type, t -> "Incorrect type " + t);
        validatePageLink(pageLink);
        return entityViewDao.findEntityViewsByTenantIdAndEdgeIdAndType(tenantId.getId(), edgeId.getId(), type, pageLink);
    }

    private final PaginatedRemover<TenantId, EntityView> tenantEntityViewRemover = new PaginatedRemover<>() {
        /**
         * Loads entities.
         */
        @Override
        protected PageData<EntityView> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return entityViewDao.findEntityViewsByTenantId(id.getId(), pageLink);
        }

        /**

         * Removes entity.

         */

        @Override
        protected void removeEntity(TenantId tenantId, EntityView entity) {
            deleteEntityView(tenantId, new EntityViewId(entity.getUuidId()));
        }
    };

    private final PaginatedRemover<CustomerId, EntityView> customerEntityViewsRemover = new PaginatedRemover<>() {
        /**
         * Loads entities.
         */
        @Override
        protected PageData<EntityView> findEntities(TenantId tenantId, CustomerId id, PageLink pageLink) {
            return entityViewDao.findEntityViewsByTenantIdAndCustomerId(tenantId.getId(), id.getId(), pageLink);
        }

        /**

         * Removes entity.

         */

        @Override
        protected void removeEntity(TenantId tenantId, EntityView entity) {
            unassignEntityViewFromCustomer(tenantId, new EntityViewId(entity.getUuidId()));
        }
    };

    
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
        return Optional.ofNullable(findEntityViewById(tenantId, new EntityViewId(entityId.getId())));
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
        return FluentFuture.from(findEntityViewByIdAsync(tenantId, new EntityViewId(entityId.getId())))
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
        return EntityType.ENTITY_VIEW;
    }

}
