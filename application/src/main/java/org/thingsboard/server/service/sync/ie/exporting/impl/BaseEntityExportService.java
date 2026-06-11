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
package org.thingsboard.server.service.sync.ie.exporting.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.service.sync.vc.data.EntitiesExportCtx;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**

 * Exports base entity entities to portable JSON.

 *

 * <p>Used by version control and tenant migration to serialize entity graphs with dependencies.

 */

public abstract class BaseEntityExportService<I extends EntityId, E extends ExportableEntity<I>, D extends EntityExportData<E>> extends DefaultEntityExportService<I, E, D> {
    
    /**
     * Set additional export data.
     *
     * @param ctx calculated-field execution context
     * @param entity entity ({@link E})
     * @param exportData export data ({@link D})
     * @return nothing
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */


    @Override
    protected void setAdditionalExportData(EntitiesExportCtx<?> ctx, E entity, D exportData) throws ThingsboardException {
        setRelatedEntities(ctx, entity, (D) exportData);
        super.setAdditionalExportData(ctx, entity, exportData);
    }
    /**
     * Set related entities.
     *
     * @param ctx calculated-field execution context
     * @param mainEntity main entity ({@link E})
     * @param exportData export data ({@link D})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, E mainEntity, D exportData) {
    }
    /**
     * Returns supported entity types.
     *
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    public abstract Set<EntityType> getSupportedEntityTypes();
    /**
     * Replace uuids recursively.
     *
     * @param ctx calculated-field execution context
     * @param node node ({@link JsonNode})
     * @param skippedRootFields skipped root fields ({@link Set})
     * @param includedFieldsPattern included fields pattern ({@link Pattern})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void replaceUuidsRecursively(EntitiesExportCtx<?> ctx, JsonNode node, Set<String> skippedRootFields, Pattern includedFieldsPattern) {
        JacksonUtil.replaceUuidsRecursively(node, skippedRootFields, includedFieldsPattern, uuid -> getExternalIdOrElseInternalByUuid(ctx, uuid), true);
    }
    /**
     * To external ids.
     *
     * @param internalIds internal ids ({@link Collection})
     * @param entityIdCreator entity id creator ({@link Function})
     * @param ctx calculated-field execution context
     * @return {@link Stream}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected Stream<UUID> toExternalIds(Collection<UUID> internalIds, Function<UUID, EntityId> entityIdCreator,
                                         EntitiesExportCtx<?> ctx) {
        return internalIds.stream().map(entityIdCreator)
                .map(entityId -> getExternalIdOrElseInternal(ctx, entityId))
                .map(EntityId::getId);
    }

}
