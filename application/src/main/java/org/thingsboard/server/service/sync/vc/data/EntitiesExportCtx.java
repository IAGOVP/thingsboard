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
package org.thingsboard.server.service.sync.vc.data;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportSettings;
import org.thingsboard.server.common.data.sync.vc.request.create.VersionCreateConfig;
import org.thingsboard.server.common.data.sync.vc.request.create.VersionCreateRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Data object for entities export ctx used during Git-based entity version control operations.
 */

@Slf4j
@Data
public abstract class EntitiesExportCtx<R extends VersionCreateRequest> {

    protected final User user;
    protected final CommitGitRequest commit;
    protected final R request;
    private final List<ListenableFuture<Void>> futures;
    private final Map<EntityId, EntityId> externalIdMap;

    public EntitiesExportCtx(User user, CommitGitRequest commit, R request) {
        this.user = user;
        this.commit = commit;
        this.request = request;
        this.futures = new ArrayList<>();
        this.externalIdMap = new HashMap<>();
    }

    protected <T extends R> EntitiesExportCtx(EntitiesExportCtx<T> other) {
        this.user = other.getUser();
        this.commit = other.getCommit();
        this.request = other.getRequest();
        this.futures = other.getFutures();
        this.externalIdMap = other.getExternalIdMap();
    }
    /**
     * Add.
     *
     * @param future future ({@link ListenableFuture})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void add(ListenableFuture<Void> future) {
        futures.add(future);
    }
    /**
     * Returns tenant id.
     *
     * @return {@link TenantId}
     * @throws Exception if an unexpected error occurs during processing
     */

    public TenantId getTenantId() {
        return user.getTenantId();
    }
    /**
     * Build export settings.
     *
     * @param config config ({@link VersionCreateConfig})
     * @return {@link EntityExportSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected static EntityExportSettings buildExportSettings(VersionCreateConfig config) {
        return EntityExportSettings.builder()
                .exportRelations(config.isSaveRelations())
                .exportAttributes(config.isSaveAttributes())
                .exportCredentials(config.isSaveCredentials())
                .exportCalculatedFields(config.isSaveCalculatedFields())
                .build();
    }
    /**
     * Returns settings.
     *
     * @return {@link EntityExportSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    public abstract EntityExportSettings getSettings();
    /**
     * Returns external id.
     *
     * @param internalId internal id ({@link ID})
     * @return {@link ID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @SuppressWarnings("unchecked")
    public <ID extends EntityId> ID getExternalId(ID internalId) {
        var result = externalIdMap.get(internalId);
        log.debug("[{}][{}] Local cache {} for id", internalId.getEntityType(), internalId.getId(), result != null ? "hit" : "miss");
        return (ID) result;
    }
    /**
     * Put external id.
     *
     * @param internalId internal id ({@link EntityId})
     * @param externalId external id ({@link EntityId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void putExternalId(EntityId internalId, EntityId externalId) {
        log.debug("[{}][{}] Local cache put: {}", internalId.getEntityType(), internalId.getId(), externalId);
        externalIdMap.put(internalId, externalId != null ? externalId : internalId);
    }

}
