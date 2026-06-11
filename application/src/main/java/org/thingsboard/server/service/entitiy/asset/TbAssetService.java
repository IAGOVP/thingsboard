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
package org.thingsboard.server.service.entitiy.asset;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.TenantId;

/**

 * Application-layer service API for asset entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbAssetService {
/**
 * Saves or persists the requested data.
 *
 * @param asset asset ({@link Asset})
 * @param user authenticated user performing the action
 * @return {@link Asset}
 * @throws Exception if an unexpected error occurs during processing
 */



    Asset save(Asset asset, User user) throws Exception;
/**
 * Saves or persists the requested data.
 *
 * @param asset asset ({@link Asset})
 * @param nameConflictStrategy name conflict strategy ({@link NameConflictStrategy})
 * @param user authenticated user performing the action
 * @return {@link Asset}
 * @throws Exception if an unexpected error occurs during processing
 */

    Asset save(Asset asset, NameConflictStrategy nameConflictStrategy, User user) throws Exception;
/**
 * Deletes the requested data.
 *
 * @param asset asset ({@link Asset})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void delete(Asset asset, User user);
/**
 * Assigns asset to customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param assetId asset id ({@link AssetId})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Asset}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Asset assignAssetToCustomer(TenantId tenantId, AssetId assetId, Customer customer, User user) throws ThingsboardException;
/**
 * Unassigns asset to customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param assetId asset id ({@link AssetId})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Asset}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Asset unassignAssetToCustomer(TenantId tenantId, AssetId assetId, Customer customer, User user) throws ThingsboardException;
/**
 * Assigns asset to public customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param assetId asset id ({@link AssetId})
 * @param user authenticated user performing the action
 * @return {@link Asset}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Asset assignAssetToPublicCustomer(TenantId tenantId, AssetId assetId, User user) throws ThingsboardException;
/**
 * Assigns asset to edge.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param assetId asset id ({@link AssetId})
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link Asset}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Asset assignAssetToEdge(TenantId tenantId, AssetId assetId, Edge edge, User user) throws ThingsboardException;
/**
 * Unassigns asset from edge.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param asset asset ({@link Asset})
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link Asset}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Asset unassignAssetFromEdge(TenantId tenantId, Asset asset, Edge edge, User user) throws ThingsboardException;

}
