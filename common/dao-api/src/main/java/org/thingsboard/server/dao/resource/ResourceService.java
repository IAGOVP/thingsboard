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
package org.thingsboard.server.dao.resource;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.ResourceExportData;
import org.thingsboard.server.common.data.ResourceSubType;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceDataInfo;
import org.thingsboard.server.common.data.TbResourceDeleteResult;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.TbResourceInfoFilter;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.Collection;
import java.util.List;

/**
 * Service API for resource persistence and domain operations.
 */
public interface ResourceService extends EntityDaoService {

    /**
     * Saves or persists resource.
     *
     * @param resource resource ({@link TbResource})
     * @return {@link TbResource}
     */
    TbResource saveResource(TbResource resource);

    /**
     * Saves or persists resource.
     *
     * @param resource resource ({@link TbResource})
     * @param doValidate whether to run validation before persist
     * @return {@link TbResource}
     */
    TbResource saveResource(TbResource resource, boolean doValidate);

    /**
     * Finds resource by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceKey resource key ({@link String})
     * @return {@link TbResource}
     */
    TbResource findResourceByTenantIdAndKey(TenantId tenantId, ResourceType resourceType, String resourceKey);

    /**
     * Finds resource by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return {@link TbResource}
     */
    TbResource findResourceById(TenantId tenantId, TbResourceId resourceId);

    /**
     * Returns resource data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return the byte[] value
     */
    byte[] getResourceData(TenantId tenantId, TbResourceId resourceId);

    /**
     * Returns resource data info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return {@link TbResourceDataInfo}
     */
    TbResourceDataInfo getResourceDataInfo(TenantId tenantId, TbResourceId resourceId);

    /**
     * Exports resource.
     *
     * @param resourceInfo resource info ({@link TbResourceInfo})
     * @return {@link ResourceExportData}
     */
    ResourceExportData exportResource(TbResourceInfo resourceInfo);

    /**
     * Exports resources.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resources resources ({@link Collection})
     * @return {@link List}
     */
    List<ResourceExportData> exportResources(TenantId tenantId, Collection<TbResourceInfo> resources);

    /**
     * To resource.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param exportData export data ({@link ResourceExportData})
     * @return {@link TbResource}
     */
    TbResource toResource(TenantId tenantId, ResourceExportData exportData);

    /**
     * Imports resources.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resources resources ({@link List})
     */
    void importResources(TenantId tenantId, List<ResourceExportData> resources);

    /**
     * Finds resource info by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo findResourceInfoById(TenantId tenantId, TbResourceId resourceId);

    /**
     * Finds resource info by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceKey resource key ({@link String})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo findResourceInfoByTenantIdAndKey(TenantId tenantId, ResourceType resourceType, String resourceKey);

    /**
     * Finds all tenant resources.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TbResource> findAllTenantResources(TenantId tenantId, PageLink pageLink);

    /**
     * Finds resource info by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return future completing with {@link TbResourceInfo}
     */
    ListenableFuture<TbResourceInfo> findResourceInfoByIdAsync(TenantId tenantId, TbResourceId resourceId);

    /**
     * Finds all tenant resources by tenant id.
     *
     * @param filter filter ({@link TbResourceInfoFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TbResourceInfo> findAllTenantResourcesByTenantId(TbResourceInfoFilter filter, PageLink pageLink);

    /**
     * Finds tenant resources by tenant id.
     *
     * @param filter filter ({@link TbResourceInfoFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TbResourceInfo> findTenantResourcesByTenantId(TbResourceInfoFilter filter, PageLink pageLink);

    /**
     * Finds tenant resources by resource type and object ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param lwm2mModel lwm2m model ({@link ResourceType})
     * @param objectIds object ids
     * @return {@link List}
     */
    List<TbResource> findTenantResourcesByResourceTypeAndObjectIds(TenantId tenantId, ResourceType lwm2mModel, String[] objectIds);

    /**
     * Finds tenant resources by resource type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param lwm2mModel lwm2m model ({@link ResourceType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TbResource> findTenantResourcesByResourceTypeAndPageLink(TenantId tenantId, ResourceType lwm2mModel, PageLink pageLink);

    /**
     * Deletes resource.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @param force force
     * @return {@link TbResourceDeleteResult}
     */
    TbResourceDeleteResult deleteResource(TenantId tenantId, TbResourceId resourceId, boolean force);

    /**
     * Deletes resources by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteResourcesByTenantId(TenantId tenantId);

    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the long result
     */
    long sumDataSizeByTenantId(TenantId tenantId);

    /**
     * Calculate etag.
     *
     * @param data data
     * @return {@link String}
     */
    String calculateEtag(byte[] data);

    /**
     * Finds system or tenant resource by etag.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param etag etag ({@link String})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo findSystemOrTenantResourceByEtag(TenantId tenantId, ResourceType resourceType, String etag);

    /**
     * Updates resources usage.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboard dashboard ({@link Dashboard})
     * @return the boolean result
     */
    boolean updateResourcesUsage(TenantId tenantId, Dashboard dashboard);

    /**
     * Updates resources usage.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @return the boolean result
     */
    boolean updateResourcesUsage(TenantId tenantId, WidgetTypeDetails widgetTypeDetails);

    /**
     * Returns used resources.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param dashboard dashboard ({@link Dashboard})
     * @return {@link Collection}
     */
    Collection<TbResourceInfo> getUsedResources(TenantId tenantId, Dashboard dashboard);

    /**
     * Returns used resources.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @return {@link Collection}
     */
    Collection<TbResourceInfo> getUsedResources(TenantId tenantId, WidgetTypeDetails widgetTypeDetails);

    /**
     * Creates or update system resource.
     *
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceSubType resource sub type ({@link ResourceSubType})
     * @param resourceKey resource key ({@link String})
     * @param data data
     * @return {@link TbResource}
     */
    TbResource createOrUpdateSystemResource(ResourceType resourceType, ResourceSubType resourceSubType, String resourceKey, byte[] data);

    /**
     * Finds system or tenant resources by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceIds resource ids ({@link List})
     * @return {@link List}
     */
    List<TbResourceInfo> findSystemOrTenantResourcesByIds(TenantId tenantId, List<TbResourceId> resourceIds);

}
