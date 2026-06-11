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
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.common.data.sync.vc.BranchInfo;
import org.thingsboard.server.common.data.sync.vc.EntityVersion;
import org.thingsboard.server.common.data.sync.vc.EntityVersionsDiff;
import org.thingsboard.server.common.data.sync.vc.RepositorySettings;
import org.thingsboard.server.common.data.sync.vc.VersionCreationResult;
import org.thingsboard.server.common.data.sync.vc.VersionedEntityInfo;
import org.thingsboard.server.common.data.sync.vc.request.create.VersionCreateRequest;
import org.thingsboard.server.gen.transport.TransportProtos.VersionControlResponseMsg;
import org.thingsboard.server.service.sync.vc.data.CommitGitRequest;

import java.util.List;

/**

 * Git repository integration for version control queue service.

 */

public interface GitVersionControlQueueService {
/**
 * Prepare commit.
 *
 * @param user authenticated user performing the action
 * @param request request payload with operation parameters
 * @return future completing with {@link CommitGitRequest}
 * @throws Exception if an unexpected error occurs during processing
 */



    ListenableFuture<CommitGitRequest> prepareCommit(User user, VersionCreateRequest request);
/**
 * Add to commit.
 *
 * @param commit commit ({@link CommitGitRequest})
 * @param entityData entity data ({@link EntityExportData})
 * @return future completing with {@link Void}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<Void> addToCommit(CommitGitRequest commit, EntityExportData<ExportableEntity<EntityId>> entityData);
/**
 * Deletes all.
 *
 * @param pendingCommit pending commit ({@link CommitGitRequest})
 * @param entityType entity type ({@link EntityType})
 * @return future completing with {@link Void}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<Void> deleteAll(CommitGitRequest pendingCommit, EntityType entityType);
/**
 * Pushes the requested data.
 *
 * @param commit commit ({@link CommitGitRequest})
 * @return future completing with {@link VersionCreationResult}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<VersionCreationResult> push(CommitGitRequest commit);
/**
 * Lists versions.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param branch Git branch name
 * @param pageLink pagination and sort parameters
 * @return future completing with {@link PageData}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<PageData<EntityVersion>> listVersions(TenantId tenantId, String branch, PageLink pageLink);
/**
 * Lists versions.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param branch Git branch name
 * @param entityType entity type ({@link EntityType})
 * @param pageLink pagination and sort parameters
 * @return future completing with {@link PageData}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<PageData<EntityVersion>> listVersions(TenantId tenantId, String branch, EntityType entityType, PageLink pageLink);
/**
 * Lists versions.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param branch Git branch name
 * @param entityId target entity identifier
 * @param pageLink pagination and sort parameters
 * @return future completing with {@link PageData}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<PageData<EntityVersion>> listVersions(TenantId tenantId, String branch, EntityId entityId, PageLink pageLink);
/**
 * Lists entities at version.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionId entity version identifier in the repository
 * @param entityType entity type ({@link EntityType})
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<VersionedEntityInfo>> listEntitiesAtVersion(TenantId tenantId, String versionId, EntityType entityType);
/**
 * Lists entities at version.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionId entity version identifier in the repository
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<VersionedEntityInfo>> listEntitiesAtVersion(TenantId tenantId, String versionId);
/**
 * Lists branches.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<BranchInfo>> listBranches(TenantId tenantId);
/**
 * Returns entity.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionId entity version identifier in the repository
 * @param entityId target entity identifier
 * @return future completing with {@link EntityExportData}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<EntityExportData> getEntity(TenantId tenantId, String versionId, EntityId entityId);
/**
 * Returns entities.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param versionId entity version identifier in the repository
 * @param entityType entity type ({@link EntityType})
 * @param offset offset
 * @param limit limit
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<EntityExportData>> getEntities(TenantId tenantId, String versionId, EntityType entityType, int offset, int limit);
/**
 * Returns versions diff.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityType entity type ({@link EntityType})
 * @param externalId external id ({@link EntityId})
 * @param versionId1 version id1 ({@link String})
 * @param versionId2 version id2 ({@link String})
 * @return future completing with {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<List<EntityVersionsDiff>> getVersionsDiff(TenantId tenantId, EntityType entityType, EntityId externalId, String versionId1, String versionId2);
/**
 * Init repository.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param settings settings ({@link RepositorySettings})
 * @return future completing with {@link Void}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<Void> initRepository(TenantId tenantId, RepositorySettings settings);
/**
 * Test repository.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param settings settings ({@link RepositorySettings})
 * @return future completing with {@link Void}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<Void> testRepository(TenantId tenantId, RepositorySettings settings);
/**
 * Clear repository.
 *
 * @param tenantId tenant that owns the entity or operation
 * @return future completing with {@link Void}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<Void> clearRepository(TenantId tenantId);
/**
 * Processes response.
 *
 * @param vcResponseMsg vc response msg ({@link VersionControlResponseMsg})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void processResponse(VersionControlResponseMsg vcResponseMsg);
}
