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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.sync.ie.EntityImportResult;
import org.thingsboard.server.common.data.sync.ie.EntityImportSettings;
import org.thingsboard.server.common.data.sync.vc.EntityTypeLoadResult;
import org.thingsboard.server.common.data.util.ThrowingRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
/**
 * Data object for entities import ctx used during Git-based entity version control operations.
 */

@Slf4j
@Data
public class EntitiesImportCtx {

    private final UUID requestId;
    private final User user;
    private final String versionId;

    private final Map<EntityType, EntityTypeLoadResult> results = new HashMap<>();
    private final Map<EntityType, Set<EntityId>> importedEntities = new HashMap<>();
    private final Map<EntityId, ReimportTask> toReimport = new HashMap<>();
    private final Map<EntityId, ThrowingRunnable> referenceCallbacks = new HashMap<>();
    private final List<ThrowingRunnable> eventCallbacks = new ArrayList<>();
    private final Map<EntityId, EntityId> externalToInternalIdMap = new HashMap<>();
    private final Set<EntityId> notFoundIds = new HashSet<>();

    private final Set<EntityRelation> relations = new LinkedHashSet<>();

    private boolean finalImportAttempt = false;
    private EntityImportSettings settings;
    private EntityImportResult<?> currentImportResult;
    private boolean rollbackOnError;

    public EntitiesImportCtx(UUID requestId, User user, String versionId) {
        this(requestId, user, versionId, null);
    }

    public EntitiesImportCtx(UUID requestId, User user, String versionId, EntityImportSettings settings) {
        this.requestId = requestId;
        this.user = user;
        this.versionId = versionId;
        this.settings = settings;
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
     * Is find existing by name.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isFindExistingByName() {
        return getSettings().isFindExistingByName();
    }
    /**
     * Is update relations.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isUpdateRelations() {
        return getSettings().isUpdateRelations();
    }
    /**
     * Is save attributes.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isSaveAttributes() {
        return getSettings().isSaveAttributes();
    }
    /**
     * Is save credentials.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isSaveCredentials() {
        return getSettings().isSaveCredentials();
    }
    /**
     * Is save calculated fields.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isSaveCalculatedFields() {
        return getSettings().isSaveCalculatedFields();
    }
    /**
     * Returns internal id.
     *
     * @param externalId external id ({@link EntityId})
     * @return {@link EntityId}
     * @throws Exception if an unexpected error occurs during processing
     */

    public EntityId getInternalId(EntityId externalId) {
        var result = externalToInternalIdMap.get(externalId);
        log.debug("[{}][{}] Local cache {} for id", externalId.getEntityType(), externalId.getId(), result != null ? "hit" : "miss");
        return result;
    }
    /**
     * Put internal id.
     *
     * @param externalId external id ({@link EntityId})
     * @param internalId internal id ({@link EntityId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void putInternalId(EntityId externalId, EntityId internalId) {
        log.debug("[{}][{}] Local cache put: {}", externalId.getEntityType(), externalId.getId(), internalId);
        externalToInternalIdMap.put(externalId, internalId);
    }
    /**
     * Register result.
     *
     * @param entityType entity type ({@link EntityType})
     * @param created created
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void registerResult(EntityType entityType, boolean created) {
        EntityTypeLoadResult result = results.computeIfAbsent(entityType, EntityTypeLoadResult::new);
        if (created) {
            result.setCreated(result.getCreated() + 1);
        } else {
            result.setUpdated(result.getUpdated() + 1);
        }
    }
    /**
     * Register deleted.
     *
     * @param entityType entity type ({@link EntityType})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void registerDeleted(EntityType entityType) {
        EntityTypeLoadResult result = results.computeIfAbsent(entityType, EntityTypeLoadResult::new);
        result.setDeleted(result.getDeleted() + 1);
    }
    /**
     * Add relations.
     *
     * @param values values ({@link Collection})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addRelations(Collection<EntityRelation> values) {
        relations.addAll(values);
    }
    /**
     * Add reference callback.
     *
     * @param externalId external id ({@link EntityId})
     * @param tr tr ({@link ThrowingRunnable})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addReferenceCallback(EntityId externalId, ThrowingRunnable tr) {
        if (tr != null) {
            referenceCallbacks.put(externalId, tr);
        }
    }
    /**
     * Add event callback.
     *
     * @param tr tr ({@link ThrowingRunnable})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addEventCallback(ThrowingRunnable tr) {
        if (tr != null) {
            eventCallbacks.add(tr);
        }
    }
    /**
     * Register not found.
     *
     * @param externalId external id ({@link EntityId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void registerNotFound(EntityId externalId) {
        notFoundIds.add(externalId);
    }
    /**
     * Is not found.
     *
     * @param externalId external id ({@link EntityId})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isNotFound(EntityId externalId) {
        return notFoundIds.contains(externalId);
    }

}
