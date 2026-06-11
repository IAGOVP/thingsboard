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
package org.thingsboard.server.service.sync.vc;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.sync.vc.BranchInfo;
import org.thingsboard.server.common.data.sync.vc.EntityDataDiff;
import org.thingsboard.server.common.data.sync.vc.EntityDataInfo;
import org.thingsboard.server.common.data.sync.vc.EntityVersion;
import org.thingsboard.server.common.data.sync.vc.RepositorySettings;
import org.thingsboard.server.common.data.sync.vc.VersionCreationResult;
import org.thingsboard.server.common.data.sync.vc.VersionLoadResult;
import org.thingsboard.server.common.data.sync.vc.VersionedEntityInfo;
import org.thingsboard.server.common.data.sync.vc.request.create.VersionCreateRequest;
import org.thingsboard.server.common.data.sync.vc.request.load.VersionLoadRequest;

import java.util.List;
import java.util.UUID;

/**

 * Service API for Git-based entity version control.

 *

 * <p>Creates versions, loads historical snapshots, compares entity data, and manages repository settings per tenant.

 */

public interface EntitiesVersionControlService {
/**
 * Saves or persists entities version.
 *
 * @param user authenticated user performing the action
 * @param request request payload with operation parameters
 * @return future completing with {@link UUID}
 * @throws Exception if an unexpected error occurs during processing
 */



    ListenableFuture<UUID> saveEntitiesVersion(User user, VersionCreateRequest request) throws Exception;
/**
 * Returns version create status.
 *
 * @param user authenticated user performing the action
 * @param requestId request id ({@link UUID})
 * @return {@link VersionCreationResult}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    VersionCreationResult getVersionCreateStatus(User user, UUID requestId) throws ThingsboardException;
/**
 * Lists entity versions.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param branch Git branch name
 * @param externalId external id ({@link EntityId})
 * @param pageLink pagination and sort parameters
 * @return future completing with {@link PageData}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<PageData<EntityVersion>> listEntityVersions(TenantId tenantId, String branch, EntityId externalId, PageLink pageLink) throws Exception;
/**
 * Lists entity type versions.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param branch Git branch name
 * @param entityType entity type ({@link EntityType})
 * @param pageLink pagination and sort parameters
 * @return future completing with {@link PageData}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<PageData<EntityVersion>> listEntityTypeVersions(TenantId tenantId, String branch, EntityType entityType, PageLink pageLink) throws Exception;
/**
 * Lists versions.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param branch Git branch name
 * @param pageLink pagination and sort parameters
 * @return future completing with {@link PageData}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<PageData<EntityVersion>> listVersions(TenantId tenantId, String branch, PageLink pageLink) throws Exception;
/**
 * Lists entities at version.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionId entity version identifier in the repository
 * @param entityType entity type ({@link EntityType})
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<VersionedEntityInfo>> listEntitiesAtVersion(TenantId tenantId, String versionId, EntityType entityType) throws Exception;
/**
 * Lists all entities at version.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionId entity version identifier in the repository
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<VersionedEntityInfo>> listAllEntitiesAtVersion(TenantId tenantId, String versionId) throws Exception;
/**
 * Loads entities version.
 *
 * @param user authenticated user performing the action
 * @param request request payload with operation parameters
 * @return {@link UUID}
 * @throws Exception if an unexpected error occurs during processing
 */

    UUID loadEntitiesVersion(User user, VersionLoadRequest request) throws Exception;
/**
 * Returns version load status.
 *
 * @param user authenticated user performing the action
 * @param requestId request id ({@link UUID})
 * @return {@link VersionLoadResult}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    VersionLoadResult getVersionLoadStatus(User user, UUID requestId) throws ThingsboardException;
/**
 * Compares entity data to version.
 *
 * @param user authenticated user performing the action
 * @param entityId target entity identifier
 * @param versionId entity version identifier in the repository
 * @return future completing with {@link EntityDataDiff}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<EntityDataDiff> compareEntityDataToVersion(User user, EntityId entityId, String versionId) throws Exception;
/**
 * Lists branches.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<BranchInfo>> listBranches(TenantId tenantId) throws Exception;
/**
 * Returns version control settings.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return {@link RepositorySettings}
 * @throws Exception if an unexpected error occurs during processing
 */

    RepositorySettings getVersionControlSettings(TenantId tenantId);
/**
 * Saves or persists version control settings.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionControlSettings version control settings ({@link RepositorySettings})
 * @return future completing with {@link RepositorySettings}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<RepositorySettings> saveVersionControlSettings(TenantId tenantId, RepositorySettings versionControlSettings);
/**
 * Deletes version control settings.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return future completing with {@link Void}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<Void> deleteVersionControlSettings(TenantId tenantId);
/**
 * Checks version control access.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param settings settings ({@link RepositorySettings})
 * @return future completing with {@link Void}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<Void> checkVersionControlAccess(TenantId tenantId, RepositorySettings settings) throws Exception;
/**
 * Triggers auto-commit for the requested data.
 *
 * @param user authenticated user performing the action
 * @param entityId target entity identifier
 * @return future completing with {@link UUID}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<UUID> autoCommit(User user, EntityId entityId);
/**
 * Triggers auto-commit for the requested data.
 *
 * @param user authenticated user performing the action
 * @param entityType entity type ({@link EntityType})
 * @param entityIds entity ids ({@link List})
 * @return future completing with {@link UUID}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<UUID> autoCommit(User user, EntityType entityType, List<UUID> entityIds);
/**
 * Returns entity data info.
 *
 * @param user authenticated user performing the action
 * @param entityId target entity identifier
 * @param versionId entity version identifier in the repository
 * @return future completing with {@link EntityDataInfo}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<EntityDataInfo> getEntityDataInfo(User user, EntityId entityId, String versionId);

}
