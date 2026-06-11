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

import org.thingsboard.server.common.data.ResourceSubType;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceDataInfo;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityWithDataDao;

import java.util.List;


/**

 * Persistence contract for tb resource.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (tenant/system resources (images, JS modules, etc.)).

 */


public interface TbResourceDao extends Dao<TbResource>, TenantEntityWithDataDao, ExportableEntityDao<TbResourceId, TbResource> {
    /**
     * Finds resource by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceId resource id ({@link String})
     * @return {@link TbResource}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResource findResourceByTenantIdAndKey(TenantId tenantId, ResourceType resourceType, String resourceId);
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TbResource> findAllByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Finds resources by tenant id and resource type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceSubType resource sub type ({@link ResourceSubType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TbResource> findResourcesByTenantIdAndResourceType(TenantId tenantId,
                                                                ResourceType resourceType,
                                                                ResourceSubType resourceSubType,
                                                                PageLink pageLink);
    /**
     * Finds resources by tenant id and resource type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceSubType resource sub type ({@link ResourceSubType})
     * @param objectIds object ids
     * @param searchText search text ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TbResource> findResourcesByTenantIdAndResourceType(TenantId tenantId,
                                                            ResourceType resourceType,
                                                            ResourceSubType resourceSubType,
                                                            String[] objectIds,
                                                            String searchText);
    /**
     * Returns resource data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return the byte[] value
     * @throws Exception if an unexpected error occurs during processing
     */

    byte[] getResourceData(TenantId tenantId, TbResourceId resourceId);
    /**
     * Returns resource preview.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return the byte[] value
     * @throws Exception if an unexpected error occurs during processing
     */

    byte[] getResourcePreview(TenantId tenantId, TbResourceId resourceId);
    /**
     * Returns resource size.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    long getResourceSize(TenantId tenantId, TbResourceId resourceId);
    /**
     * Returns resource data info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceId resource id ({@link TbResourceId})
     * @return {@link TbResourceDataInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceDataInfo getResourceDataInfo(TenantId tenantId, TbResourceId resourceId);
}
