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
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeFilter;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for widget type persistence and domain operations.
 */
public interface WidgetTypeService extends EntityDaoService {

    /**
     * Finds widget type by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return {@link WidgetType}
     */
    WidgetType findWidgetTypeById(TenantId tenantId, WidgetTypeId widgetTypeId);

    /**
     * Finds widget type details by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return {@link WidgetTypeDetails}
     */
    WidgetTypeDetails findWidgetTypeDetailsById(TenantId tenantId, WidgetTypeId widgetTypeId);

    /**
     * Finds widget type info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return {@link WidgetTypeInfo}
     */
    WidgetTypeInfo findWidgetTypeInfoById(TenantId tenantId, WidgetTypeId widgetTypeId);

    /**
     * Widget type exists by tenant id and widget type id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     * @return the boolean result
     */
    boolean widgetTypeExistsByTenantIdAndWidgetTypeId(TenantId tenantId, WidgetTypeId widgetTypeId);

    /**
     * Saves or persists widget type.
     *
     * @param widgetType widget type ({@link WidgetTypeDetails})
     * @return {@link WidgetTypeDetails}
     */
    WidgetTypeDetails saveWidgetType(WidgetTypeDetails widgetType);

    /**
     * Deletes widget type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeId widget type id ({@link WidgetTypeId})
     */
    void deleteWidgetType(TenantId tenantId, WidgetTypeId widgetTypeId);

    /**
     * Finds system widget types by page link.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetTypeInfo> findSystemWidgetTypesByPageLink(WidgetTypeFilter widgetTypeFilter, PageLink pageLink);

    /**
     * Finds all tenant widget types by tenant id and page link.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetTypeInfo> findAllTenantWidgetTypesByTenantIdAndPageLink(WidgetTypeFilter widgetTypeFilter, PageLink pageLink);

    /**
     * Finds tenant widget types by tenant id and page link.
     *
     * @param widgetTypeFilter widget type filter ({@link WidgetTypeFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetTypeInfo> findTenantWidgetTypesByTenantIdAndPageLink(WidgetTypeFilter widgetTypeFilter, PageLink pageLink);

    /**
     * Finds widget types by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @return {@link List}
     */
    List<WidgetType> findWidgetTypesByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    /**
     * Finds widget types details by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @return {@link List}
     */
    List<WidgetTypeDetails> findWidgetTypesDetailsByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    /**
     * Finds widget types infos by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @param fullSearch full search
     * @param deprecatedFilter deprecated filter ({@link DeprecatedFilter})
     * @param widgetTypes widget types ({@link List})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetTypeInfo> findWidgetTypesInfosByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink);

    /**
     * Finds widget fqns by widgets bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @return {@link List}
     */
    List<String> findWidgetFqnsByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    /**
     * Finds widget type by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetType}
     */
    WidgetType findWidgetTypeByTenantIdAndFqn(TenantId tenantId, String fqn);

    /**
     * Finds widget type details by tenant id and fqn.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param fqn fqn ({@link String})
     * @return {@link WidgetTypeDetails}
     */
    WidgetTypeDetails findWidgetTypeDetailsByTenantIdAndFqn(TenantId tenantId, String fqn);

    /**
     * Updates widgets bundle widget types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @param widgetTypeIds widget type ids ({@link List})
     */
    void updateWidgetsBundleWidgetTypes(TenantId tenantId, WidgetsBundleId widgetsBundleId, List<WidgetTypeId> widgetTypeIds);

    /**
     * Updates widgets bundle widget fqns.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
     * @param widgetFqns widget fqns ({@link List})
     */
    void updateWidgetsBundleWidgetFqns(TenantId tenantId, WidgetsBundleId widgetsBundleId, List<String> widgetFqns);

    /**
     * Deletes widget types by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteWidgetTypesByTenantId(TenantId tenantId);

    /**
     * Deletes widget types by bundle id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param bundleId bundle id ({@link WidgetsBundleId})
     */
    void deleteWidgetTypesByBundleId(TenantId tenantId, WidgetsBundleId bundleId);

    /**
     * Finds all widget types ids.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<WidgetTypeId> findAllWidgetTypesIds(PageLink pageLink);

}
