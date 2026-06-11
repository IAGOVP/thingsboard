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
package org.thingsboard.server.dao.sql.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.WidgetTypeFields;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeFilter;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundleWidget;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.WidgetTypeDetailsEntity;
import org.thingsboard.server.dao.model.sql.WidgetTypeInfoEntity;
import org.thingsboard.server.dao.model.sql.WidgetsBundleWidgetCompositeKey;
import org.thingsboard.server.dao.model.sql.WidgetsBundleWidgetEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;
import org.thingsboard.server.dao.widget.WidgetTypeDao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;
/**
 * JPA/PostgreSQL implementation of widget type dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
public class JpaWidgetTypeDao extends JpaAbstractDao<WidgetTypeDetailsEntity, WidgetTypeDetails> implements WidgetTypeDao, TenantEntityDao<WidgetTypeDetails> {

    @Autowired
    private WidgetTypeRepository widgetTypeRepository;

    @Autowired
    private WidgetTypeInfoRepository widgetTypeInfoRepository;

    @Autowired
    private WidgetsBundleWidgetRepository widgetsBundleWidgetRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<WidgetTypeDetailsEntity> getEntityClass() {
        return WidgetTypeDetailsEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<WidgetTypeDetailsEntity, UUID> getRepository() {
        return widgetTypeRepository;
    }
    /**
     * Finds widget type by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link UUID})
     * @return {@link WidgetType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetType findWidgetTypeById(TenantId tenantId, UUID widgetTypeId) {
        return DaoUtil.getData(widgetTypeRepository.findWidgetTypeById(widgetTypeId));
    }
    /**
     * Exists by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link UUID})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean existsByTenantIdAndId(TenantId tenantId, UUID widgetTypeId) {
        return widgetTypeRepository.existsByTenantIdAndId(tenantId.getId(), widgetTypeId);
    }
    /**
     * Finds widget type info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link UUID})
     * @return {@link WidgetTypeInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeInfo findWidgetTypeInfoById(TenantId tenantId, UUID widgetTypeId) {
        return DaoUtil.getData(widgetTypeInfoRepository.findById(widgetTypeId));
    }
    /**
     * Finds system widget types.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findSystemWidgetTypes(WidgetTypeFilter widgetTypeFilter, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(widgetTypeFilter.getDeprecatedFilter());
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(widgetTypeFilter.getDeprecatedFilter());
        boolean widgetTypesEmpty = widgetTypeFilter.getWidgetTypes() == null || widgetTypeFilter.getWidgetTypes().isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findSystemWidgetTypes(
                                NULL_UUID,
                                pageLink.getTextSearch(),
                                widgetTypeFilter.isFullSearch(),
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypeFilter.getWidgetTypes() == null ? Collections.emptyList() : widgetTypeFilter.getWidgetTypes(),
                                widgetTypeFilter.isScadaFirst(),
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }
    /**
     * Finds all tenant widget types by tenant id.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findAllTenantWidgetTypesByTenantId(WidgetTypeFilter widgetTypeFilter, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(widgetTypeFilter.getDeprecatedFilter());
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(widgetTypeFilter.getDeprecatedFilter());
        boolean widgetTypesEmpty = widgetTypeFilter.getWidgetTypes() == null || widgetTypeFilter.getWidgetTypes().isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findAllTenantWidgetTypesByTenantId(
                                widgetTypeFilter.getTenantId().getId(),
                                NULL_UUID,
                                pageLink.getTextSearch(),
                                widgetTypeFilter.isFullSearch(),
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypeFilter.getWidgetTypes() == null ? Collections.emptyList() : widgetTypeFilter.getWidgetTypes(),
                                widgetTypeFilter.isScadaFirst(),
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }
    /**
     * Finds tenant widget types by tenant id.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findTenantWidgetTypesByTenantId(WidgetTypeFilter widgetTypeFilter, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(widgetTypeFilter.getDeprecatedFilter());
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(widgetTypeFilter.getDeprecatedFilter());
        boolean widgetTypesEmpty = widgetTypeFilter.getWidgetTypes() == null || widgetTypeFilter.getWidgetTypes().isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findTenantWidgetTypesByTenantId(
                                widgetTypeFilter.getTenantId().getId(),
                                pageLink.getTextSearch(),
                                widgetTypeFilter.isFullSearch(),
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypeFilter.getWidgetTypes() == null ? Collections.emptyList() : widgetTypeFilter.getWidgetTypes(),
                                widgetTypeFilter.isScadaFirst(),
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }
    /**
     * Finds widget types by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetType> findWidgetTypesByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return DaoUtil.convertDataList(widgetTypeRepository.findWidgetTypesByWidgetsBundleId(widgetsBundleId));
    }
    /**
     * Finds widget types details by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetTypeDetails> findWidgetTypesDetailsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return DaoUtil.convertDataList(widgetTypeRepository.findWidgetTypesDetailsByWidgetsBundleId(widgetsBundleId));
    }
    /**
     * Finds widget types infos by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @param fullSearch full search
     * @param deprecatedFilter deprecated filter ({@link DeprecatedFilter})
     * @param widgetTypes widget types ({@link List})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeInfo> findWidgetTypesInfosByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(deprecatedFilter);
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(deprecatedFilter);
        boolean widgetTypesEmpty = widgetTypes == null || widgetTypes.isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findWidgetTypesInfosByWidgetsBundleId(
                                widgetsBundleId,
                                Objects.toString(pageLink.getTextSearch(), ""),
                                fullSearch,
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypes == null ? Collections.emptyList() : widgetTypes,
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }
    /**
     * Finds widget fqns by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<String> findWidgetFqnsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return widgetTypeRepository.findWidgetFqnsByWidgetsBundleId(widgetsBundleId);
    }
    /**
     * Finds by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetType findByTenantIdAndFqn(UUID tenantId, String fqn) {
        return DaoUtil.getData(widgetTypeRepository.findWidgetTypeByTenantIdAndFqn(tenantId, fqn));
    }
    /**
     * Finds details by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeDetails findDetailsByTenantIdAndFqn(UUID tenantId, String fqn) {
        return DaoUtil.getData(widgetTypeRepository.findByTenantIdAndFqn(tenantId, fqn));
    }
    /**
     * Finds widget type ids by tenant id and fqns.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetFqns widget fqns ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetTypeId> findWidgetTypeIdsByTenantIdAndFqns(UUID tenantId, List<String> widgetFqns) {
        var idFqnPairs = widgetTypeRepository.findWidgetTypeIdsByTenantIdAndFqns(tenantId, widgetFqns);
        idFqnPairs.sort(Comparator.comparingInt(o -> widgetFqns.indexOf(o.getFqn())));
        return idFqnPairs.stream()
                .map(id -> new WidgetTypeId(id.getId())).collect(Collectors.toList());
    }
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeDetails findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(widgetTypeRepository.findByTenantIdAndExternalId(tenantId, externalId));
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
    public PageData<WidgetTypeDetails> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                widgetTypeRepository
                        .findTenantWidgetTypeDetailsByTenantId(
                                tenantId,
                                pageLink.getTextSearch(),
                                DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds ids by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetTypeId> findIdsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(widgetTypeRepository.findIdsByTenantId(tenantId, DaoUtil.toPageable(pageLink))
                .map(WidgetTypeId::new));
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link WidgetTypeId})
     * @return {@link WidgetTypeId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeId getExternalIdByInternal(WidgetTypeId internalId) {
        return Optional.ofNullable(widgetTypeRepository.getExternalIdById(internalId.getId()))
                .map(WidgetTypeId::new).orElse(null);
    }
    /**
     * Finds widgets bundle widgets by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetsBundleWidget> findWidgetsBundleWidgetsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return DaoUtil.convertDataList(widgetsBundleWidgetRepository.findAllByWidgetsBundleId(widgetsBundleId));
    }
    /**
     * Saves or persists widgets bundle widget.
     *
     * @param widgetsBundleWidget widgets bundle widget ({@link WidgetsBundleWidget})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void saveWidgetsBundleWidget(WidgetsBundleWidget widgetsBundleWidget) {
        widgetsBundleWidgetRepository.save(new WidgetsBundleWidgetEntity(widgetsBundleWidget));
    }
    /**
     * Removes widget type from widgets bundle.
     *
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @param widgetTypeId widget type id ({@link UUID})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeWidgetTypeFromWidgetsBundle(UUID widgetsBundleId, UUID widgetTypeId) {
        widgetsBundleWidgetRepository.deleteById(new WidgetsBundleWidgetCompositeKey(widgetsBundleId, widgetTypeId));
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
        return DaoUtil.pageToPageData(widgetTypeRepository.findAllIds(DaoUtil.toPageable(pageLink)).map(WidgetTypeId::new));
    }
    /**
     * Finds by tenant and image link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageUrl image url ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetTypeInfo> findByTenantAndImageLink(TenantId tenantId, String imageUrl, int limit) {
        return DaoUtil.convertDataList(widgetTypeInfoRepository.findByTenantAndImageUrl(tenantId.getId(), imageUrl, limit));
    }
    /**
     * Finds by image link.
     *
     * @param imageUrl image url ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetTypeInfo> findByImageLink(String imageUrl, int limit) {
        return DaoUtil.convertDataList(widgetTypeInfoRepository.findByImageUrl(imageUrl, limit));
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
    public PageData<WidgetTypeDetails> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
    }
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetTypeFields> findNextBatch(UUID id, int batchSize) {
        return widgetTypeRepository.findNextBatch(id, Limit.of(batchSize));
    }
    /**
     * Finds by tenant id and resource.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param reference reference ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<EntityInfo> findByTenantIdAndResource(TenantId tenantId, String reference, int limit) {
        return widgetTypeInfoRepository.findWidgetTypeInfosByTenantIdAndResourceLink(tenantId.getId(), reference, PageRequest.of(0, limit));
    }
    /**
     * Finds by resource.
     *
     * @param reference reference ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<EntityInfo> findByResource(String reference, int limit) {
        return widgetTypeInfoRepository.findWidgetTypeInfosByResourceLink(reference, PageRequest.of(0, limit));
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

}
