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

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.common.data.widget.WidgetsBundleFilter;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.ImageContainerDao;

import java.util.List;
import java.util.UUID;

/**
 * Persistence contract for widgets bundle.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (widget types and widget bundles).
 */

public interface WidgetsBundleDao extends Dao<WidgetsBundle>, ExportableEntityDao<WidgetsBundleId, WidgetsBundle>, ImageContainerDao<WidgetsBundle> {

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundle widgets bundle ({@link WidgetsBundle})
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetsBundle save(TenantId tenantId, WidgetsBundle widgetsBundle);

    
    /**
     * Finds widgets bundle by tenant id and alias.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alias alias ({@link String})
     * @return {@link WidgetsBundle}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetsBundle findWidgetsBundleByTenantIdAndAlias(UUID tenantId, String alias);

    
    /**
     * Finds system widgets bundles.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetsBundle> findSystemWidgetsBundles(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink);

    
    /**
     * Finds tenant widgets bundles by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds all tenant widgets bundles by tenant id.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetsBundle> findAllTenantWidgetsBundlesByTenantId(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink);

    
    /**
     * Finds tenant widgets bundles by tenant id.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink);
    /**
     * Finds all widgets bundles.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetsBundle> findAllWidgetsBundles(PageLink pageLink);
    /**
     * Finds system or tenant widget bundles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleIds widgets bundle ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<WidgetsBundle> findSystemOrTenantWidgetBundlesByIds(UUID tenantId, List<UUID> widgetsBundleIds);

}

