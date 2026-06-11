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
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service API for widgets bundle persistence and domain operations.
 */
public interface WidgetsBundleService extends EntityDaoService {

    /**
     * Finds widgets bundle by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @return {@link WidgetsBundle}
     */
    WidgetsBundle findWidgetsBundleById(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    /**
     * Saves or persists widgets bundle.
     *
     * @param widgetsBundle widgets bundle ({@link WidgetsBundle})
     * @return {@link WidgetsBundle}
     */
    WidgetsBundle saveWidgetsBundle(WidgetsBundle widgetsBundle);

    /**
     * Deletes widgets bundle.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     */
    void deleteWidgetsBundle(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    /**
     * Finds widgets bundle by tenant id and alias.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param alias alias ({@link String})
     * @return {@link WidgetsBundle}
     */
    WidgetsBundle findWidgetsBundleByTenantIdAndAlias(TenantId tenantId, String alias);

    /**
     * Finds system widgets bundles by page link.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetsBundle> findSystemWidgetsBundlesByPageLink(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink);

    /**
     * Finds system widgets bundles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     */
    List<WidgetsBundle> findSystemWidgetsBundles(TenantId tenantId);

    /**
     * Finds tenant widgets bundles by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds all tenant widgets bundles by tenant id and page link.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetsBundle> findAllTenantWidgetsBundlesByTenantIdAndPageLink(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink);

    /**
     * Finds tenant widgets bundles by tenant id and page link.
     *
     * @param widgetsBundleFilter widgets bundle filter ({@link WidgetsBundleFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantIdAndPageLink(WidgetsBundleFilter widgetsBundleFilter, PageLink pageLink);

    /**
     * Finds all tenant widgets bundles by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     */
    List<WidgetsBundle> findAllTenantWidgetsBundlesByTenantId(TenantId tenantId);

    /**
     * Deletes widgets bundles by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteWidgetsBundlesByTenantId(TenantId tenantId);

    /**
     * Updates system widgets.
     *
     * @param bundles bundles ({@link Stream})
     * @param widgets widgets ({@link Stream})
     */
    void updateSystemWidgets(Stream<String> bundles, Stream<String> widgets);

    /**
     * Finds system or tenant widgets bundles by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleIds widgets bundle ids ({@link List})
     * @return {@link List}
     */
    List<WidgetsBundle> findSystemOrTenantWidgetsBundlesByIds(TenantId tenantId, List<WidgetsBundleId> widgetsBundleIds);

}
