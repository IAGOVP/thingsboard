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
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeFilter;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundleWidget;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.ImageContainerDao;
import org.thingsboard.server.dao.ResourceContainerDao;

import java.util.List;
import java.util.UUID;

/**
 * Persistence contract for widget type.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (widget types and widget bundles).
 */

public interface WidgetTypeDao extends Dao<WidgetTypeDetails>, ExportableEntityDao<WidgetTypeId, WidgetTypeDetails>, ImageContainerDao<WidgetTypeInfo>, ResourceContainerDao<WidgetTypeInfo> {

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetTypeDetails save(TenantId tenantId, WidgetTypeDetails widgetTypeDetails);

    
    /**
     * Finds widget type by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link UUID})
     * @return {@link WidgetType}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetType findWidgetTypeById(TenantId tenantId, UUID widgetTypeId);
    /**
     * Exists by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link UUID})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndId(TenantId tenantId, UUID widgetTypeId);
    /**
     * Finds widget type info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link UUID})
     * @return {@link WidgetTypeInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetTypeInfo findWidgetTypeInfoById(TenantId tenantId, UUID widgetTypeId);
    /**
     * Finds system widget types.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetTypeInfo> findSystemWidgetTypes(WidgetTypeFilter widgetTypeFilter, PageLink pageLink);
    /**
     * Finds all tenant widget types by tenant id.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetTypeInfo> findAllTenantWidgetTypesByTenantId(WidgetTypeFilter widgetTypeFilter, PageLink pageLink);
    /**
     * Finds tenant widget types by tenant id.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetTypeInfo> findTenantWidgetTypesByTenantId(WidgetTypeFilter widgetTypeFilter, PageLink pageLink);

    
    /**
     * Finds widget types by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<WidgetType> findWidgetTypesByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId);

    
    /**
     * Finds widget types details by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<WidgetTypeDetails> findWidgetTypesDetailsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId);
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

    PageData<WidgetTypeInfo> findWidgetTypesInfosByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink);
    /**
     * Finds widget fqns by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<String> findWidgetFqnsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId);

    
    /**
     * Finds by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetType}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetType findByTenantIdAndFqn(UUID tenantId, String fqn);
    /**
     * Finds details by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetTypeDetails}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetTypeDetails findDetailsByTenantIdAndFqn(UUID tenantId, String fqn);
    /**
     * Finds widget type ids by tenant id and fqns.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetFqns widget fqns ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<WidgetTypeId> findWidgetTypeIdsByTenantIdAndFqns(UUID tenantId, List<String> widgetFqns);
    /**
     * Finds widgets bundle widgets by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<WidgetsBundleWidget> findWidgetsBundleWidgetsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId);
    /**
     * Saves or persists widgets bundle widget.
     *
     * @param widgetsBundleWidget widgets bundle widget ({@link WidgetsBundleWidget})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void saveWidgetsBundleWidget(WidgetsBundleWidget widgetsBundleWidget);
    /**
     * Removes widget type from widgets bundle.
     *
     * @param widgetsBundleId widgets bundle id ({@link UUID})
     * @param widgetTypeId widget type id ({@link UUID})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeWidgetTypeFromWidgetsBundle(UUID widgetsBundleId, UUID widgetTypeId);
    /**
     * Finds all widget types ids.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<WidgetTypeId> findAllWidgetTypesIds(PageLink pageLink);

}
