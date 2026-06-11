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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.EntityFields;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.common.data.widget.WidgetsBundleFilter;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.WidgetsBundleEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;
import org.thingsboard.server.dao.widget.WidgetsBundleDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;
/**
 * JPA/PostgreSQL implementation of widgets bundle dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
public class JpaWidgetsBundleDao extends JpaAbstractDao<WidgetsBundleEntity, WidgetsBundle> implements WidgetsBundleDao, TenantEntityDao<WidgetsBundle> {

    @Autowired
    private WidgetsBundleRepository widgetsBundleRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<WidgetsBundleEntity> getEntityClass() {
        return WidgetsBundleEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<WidgetsBundleEntity, UUID> getRepository() {
        return widgetsBundleRepository;
    }
    /**
     * Finds widgets bundle by tenant id and alias.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alias alias ({@link String})
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetsBundle findWidgetsBundleByTenantIdAndAlias(UUID tenantId, String alias) {
        return DaoUtil.getData(widgetsBundleRepository.findWidgetsBundleByTenantIdAndAlias(tenantId, alias));
    }
    /**
     * Finds system widgets bundles.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetsBundle> findSystemWidgetsBundles(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink) {
        if (widgetsBundleFilter.isFullSearch()) {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findSystemWidgetsBundlesFullSearch(
                                    NULL_UUID,
                                    pageLink.getTextSearch(),
                                    DaoUtil.toPageable(pageLink)));
        } else {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findSystemWidgetsBundles(
                                    NULL_UUID,
                                    pageLink.getTextSearch(),
                                    DaoUtil.toPageable(pageLink)));
        }
    }
    /**
     * Finds tenant widgets bundles by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                widgetsBundleRepository
                        .findTenantWidgetsBundlesByTenantId(
                                tenantId,
                                pageLink.getTextSearch(),
                                DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds all tenant widgets bundles by tenant id.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetsBundle> findAllTenantWidgetsBundlesByTenantId(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink) {
        return findTenantWidgetsBundlesByTenantIds(Arrays.asList(widgetsBundleFilter.getTenantId().getId(), NULL_UUID), widgetsBundleFilter, pageLink);
    }
    /**
     * Finds tenant widgets bundles by tenant id.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink) {
        return findTenantWidgetsBundlesByTenantIds(Collections.singletonList(widgetsBundleFilter.getTenantId().getId()), widgetsBundleFilter, pageLink);
    }
    /**
     * Finds all widgets bundles.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<WidgetsBundle> findAllWidgetsBundles(PageLink pageLink) {
        return DaoUtil.toPageData(widgetsBundleRepository.findAll(DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds system or tenant widget bundles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleIds widgets bundle ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<WidgetsBundle> findSystemOrTenantWidgetBundlesByIds(UUID tenantId, List<UUID> widgetsBundleIds) {
        return DaoUtil.convertDataList(widgetsBundleRepository.findSystemOrTenantWidgetsBundlesByIdIn(tenantId, TenantId.NULL_UUID, widgetsBundleIds));
    }

    private PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantIds(List<UUID> tenantIds, WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink) {
        if (widgetsBundleFilter.isFullSearch()) {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findAllTenantWidgetsBundlesByTenantIdsFullSearch(
                                    tenantIds,
                                    pageLink.getTextSearch(),
                                    widgetsBundleFilter.isScadaFirst(),
                                    DaoUtil.toPageable(pageLink)));
        } else {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findAllTenantWidgetsBundlesByTenantIds(
                                    tenantIds,
                                    pageLink.getTextSearch(),
                                    DaoUtil.toPageable(pageLink)));
        }
    }
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetsBundle findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(widgetsBundleRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetsBundle findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(widgetsBundleRepository.findFirstByTenantIdAndTitle(tenantId, name));
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
    public PageData<WidgetsBundle> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findTenantWidgetsBundlesByTenantId(tenantId, pageLink);
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link WidgetsBundleId})
     * @return {@link WidgetsBundleId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetsBundleId getExternalIdByInternal(WidgetsBundleId internalId) {
        return Optional.ofNullable(widgetsBundleRepository.getExternalIdById(internalId.getId()))
                .map(WidgetsBundleId::new).orElse(null);
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
    public List<WidgetsBundle> findByTenantAndImageLink(TenantId tenantId, String imageUrl, int limit) {
        return DaoUtil.convertDataList(widgetsBundleRepository.findByTenantAndImageUrl(tenantId.getId(), imageUrl, limit));
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
    public List<WidgetsBundle> findByImageLink(String imageUrl, int limit) {
        return DaoUtil.convertDataList(widgetsBundleRepository.findByImageUrl(imageUrl, limit));
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
    public PageData<WidgetsBundle> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
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
    public List<? extends EntityFields> findNextBatch(UUID id, int batchSize) {
        return widgetsBundleRepository.findNextBatch(id, Limit.of(batchSize));
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGETS_BUNDLE;
    }

}
