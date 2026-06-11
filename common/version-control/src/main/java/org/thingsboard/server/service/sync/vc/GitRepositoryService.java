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

import org.eclipse.jgit.api.errors.GitAPIException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.sync.vc.BranchInfo;
import org.thingsboard.server.common.data.sync.vc.EntityVersion;
import org.thingsboard.server.common.data.sync.vc.RepositorySettings;
import org.thingsboard.server.common.data.sync.vc.VersionCreationResult;
import org.thingsboard.server.common.data.sync.vc.VersionedEntityInfo;
import org.thingsboard.server.service.sync.vc.GitRepository.Diff;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Service API for git repository persistence and domain operations.
 */
public interface GitRepositoryService {

    /** Returns the active repository tenants. */
    Set<TenantId> getActiveRepositoryTenants();

    /** Prepare commit. */
    void prepareCommit(PendingCommit pendingCommit);

    /** List versions. */
    PageData<EntityVersion> listVersions(TenantId tenantId, String branch, String path, PageLink pageLink) throws Exception;

    /** List entities at version. */
    List<VersionedEntityInfo> listEntitiesAtVersion(TenantId tenantId, String versionId, String path) throws Exception;

    /** Test repository. */
    void testRepository(TenantId tenantId, RepositorySettings settings) throws Exception;

    /** Init repository. */
    void initRepository(TenantId tenantId, RepositorySettings settings, boolean fetch) throws Exception;

    /** Returns the repository settings. */
    RepositorySettings getRepositorySettings(TenantId tenantId) throws Exception;

    /** Clear repository. */
    void clearRepository(TenantId tenantId) throws IOException;

    /** Add. */
    void add(PendingCommit commit, String relativePath, String entityDataJson) throws IOException;

    /** Delete folder content. */
    void deleteFolderContent(PendingCommit commit, String relativePath) throws IOException;

    /** Push. */
    VersionCreationResult push(PendingCommit commit);

    /** Clean up. */
    void cleanUp(PendingCommit commit);

    /** Abort. */
    void abort(PendingCommit commit);

    /** List branches. */
    List<BranchInfo> listBranches(TenantId tenantId);

    /** Returns the file content at commit. */
    String getFileContentAtCommit(TenantId tenantId, String relativePath, String versionId) throws IOException;

    /** Returns the versions diff list. */
    List<Diff> getVersionsDiffList(TenantId tenantId, String path, String versionId1, String versionId2) throws IOException;

    /** Returns the contents diff. */
    String getContentsDiff(TenantId tenantId, String content1, String content2) throws IOException;

    /** Fetch. */
    void fetch(TenantId tenantId) throws GitAPIException;

}
