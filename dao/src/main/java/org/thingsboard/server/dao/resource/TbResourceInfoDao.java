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

import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.TbResourceInfoFilter;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.Set;


/**

 * Persistence contract for tb resource info.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (tenant/system resources (images, JS modules, etc.)).

 */


public interface TbResourceInfoDao extends Dao<TbResourceInfo> {
    /**
     * Finds all tenant resources by tenant id.
     *
     * @param filter filter ({@link TbResourceInfoFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TbResourceInfo> findAllTenantResourcesByTenantId(TbResourceInfoFilter filter, PageLink pageLink);
    /**
     * Finds tenant resources by tenant id.
     *
     * @param filter filter ({@link TbResourceInfoFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<TbResourceInfo> findTenantResourcesByTenantId(TbResourceInfoFilter filter, PageLink pageLink);
    /**
     * Finds by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceKey resource key ({@link String})
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceInfo findByTenantIdAndKey(TenantId tenantId, ResourceType resourceType, String resourceKey);
    /**
     * Exists by tenant id and resource type and resource key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param resourceKey resource key ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndResourceTypeAndResourceKey(TenantId tenantId, ResourceType resourceType, String resourceKey);
    /**
     * Finds keys by tenant id and resource type and resource key prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param prefix prefix ({@link String})
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    Set<String> findKeysByTenantIdAndResourceTypeAndResourceKeyPrefix(TenantId tenantId, ResourceType resourceType, String prefix);
    /**
     * Finds by tenant id and etag and key starting with.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param etag etag ({@link String})
     * @param query filter and sort query definition
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TbResourceInfo> findByTenantIdAndEtagAndKeyStartingWith(TenantId tenantId, String etag, String query);
    /**
     * Finds system or tenant resource by etag.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceType resource type ({@link ResourceType})
     * @param etag etag ({@link String})
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceInfo findSystemOrTenantResourceByEtag(TenantId tenantId, ResourceType resourceType, String etag);
    /**
     * Exists by public resource key.
     *
     * @param resourceType resource type ({@link ResourceType})
     * @param publicResourceKey public resource key ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByPublicResourceKey(ResourceType resourceType, String publicResourceKey);
    /**
     * Finds public resource by key.
     *
     * @param resourceType resource type ({@link ResourceType})
     * @param publicResourceKey public resource key ({@link String})
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceInfo findPublicResourceByKey(ResourceType resourceType, String publicResourceKey);
    /**
     * Finds system or tenant resources by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param resourceIds resource ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<TbResourceInfo> findSystemOrTenantResourcesByIds(TenantId tenantId, List<TbResourceId> resourceIds);
}
