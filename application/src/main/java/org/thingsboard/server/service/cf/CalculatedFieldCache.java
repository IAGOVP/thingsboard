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
package org.thingsboard.server.service.cf;

import org.thingsboard.server.common.data.cf.CalculatedField;
import org.thingsboard.server.common.data.cf.CalculatedFieldLink;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.service.cf.ctx.state.CalculatedFieldCtx;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**

 * calculated field cache contract for calculated fields (calculated-field argument resolution, runtime state, and result processing).

 */

public interface CalculatedFieldCache {
/**
 * Returns calculated field.
 *
 * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
 * @return {@link CalculatedField}
 * @throws Exception if an unexpected error occurs during processing
 */



    CalculatedField getCalculatedField(CalculatedFieldId calculatedFieldId);
/**
 * Returns calculated fields by entity id.
 *
 * @param entityId target entity identifier
 * @return {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    List<CalculatedField> getCalculatedFieldsByEntityId(EntityId entityId);
/**
 * Returns calculated field links by entity id.
 *
 * @param entityId target entity identifier
 * @return {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    List<CalculatedFieldLink> getCalculatedFieldLinksByEntityId(EntityId entityId);
/**
 * Returns calculated field ctx.
 *
 * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
 * @return {@link CalculatedFieldCtx}
 * @throws Exception if an unexpected error occurs during processing
 */

    CalculatedFieldCtx getCalculatedFieldCtx(CalculatedFieldId calculatedFieldId);
/**
 * Returns calculated field ctxs by entity id.
 *
 * @param entityId target entity identifier
 * @return {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    List<CalculatedFieldCtx> getCalculatedFieldCtxsByEntityId(EntityId entityId);
/**
 * Returns calculated field ctxs by type.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param cfType cf type ({@link CalculatedFieldType})
 * @return {@link Stream}
 * @throws Exception if an unexpected error occurs during processing
 */

    Stream<CalculatedFieldCtx> getCalculatedFieldCtxsByType(TenantId tenantId, CalculatedFieldType cfType);
/**
 * Has calculated fields.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @param filter filter ({@link Predicate})
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean hasCalculatedFields(TenantId tenantId, EntityId entityId, Predicate<CalculatedFieldCtx> filter);
/**
 * Add calculated field.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void addCalculatedField(TenantId tenantId, CalculatedFieldId calculatedFieldId);
/**
 * Updates calculated field.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void updateCalculatedField(TenantId tenantId, CalculatedFieldId calculatedFieldId);
/**
 * Evict.
 *
 * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void evict(CalculatedFieldId calculatedFieldId);
/**
 * Handles tenant profile update.
 *
 * @param tenantProfileId tenant profile id ({@link TenantProfileId})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void handleTenantProfileUpdate(TenantProfileId tenantProfileId);
/**
 * Returns profile id.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @return {@link EntityId}
 * @throws Exception if an unexpected error occurs during processing
 */

    EntityId getProfileId(TenantId tenantId, EntityId entityId);
/**
 * Returns dynamic entities.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @return {@link Set}
 * @throws Exception if an unexpected error occurs during processing
 */

    Set<EntityId> getDynamicEntities(TenantId tenantId, EntityId entityId);
/**
 * Updates owner entity.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void updateOwnerEntity(TenantId tenantId, EntityId entityId);
/**
 * Add owner entity.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void addOwnerEntity(TenantId tenantId, EntityId entityId);
/**
 * Evict owner entity.
 *
 * @param entityId target entity identifier
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void evictOwnerEntity(EntityId entityId);
/**
 * Evict owner.
 *
 * @param owner owner ({@link EntityId})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void evictOwner(EntityId owner);

}
