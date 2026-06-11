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
package org.thingsboard.server.dao.entity;

import com.google.common.util.concurrent.FluentFuture;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.Optional;

/**
 * Common CRUD contract for tenant-scoped entities (devices, assets, dashboards, …).
 *
 * <p>Extended by domain services such as {@link org.thingsboard.server.dao.device.DeviceService}.
 */
public interface EntityDaoService {

    /**
     * Finds entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return optional {@link HasId}, empty if not found
     */
    Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId);

    /**
     * Finds entity async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return {@link FluentFuture}
     */
    FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId);

    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return the long result
     */
    default long countByTenantId(TenantId tenantId) {
        /**
         * Illegal argument exception.
         *
         * @param getEntityType() get entity type()
         * @return the throw new value
         */
        throw new IllegalArgumentException("Not implemented for " + getEntityType());
    }

    /**
     * Deletes entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link EntityId})
     * @param force force
     */
    default void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        /**
         * Illegal argument exception.
         *
         * @param supported" supported"
         * @return the throw new value
         */
        throw new IllegalArgumentException(getEntityType().getNormalName() + " deletion not supported");
    }

    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    default void deleteByTenantId(TenantId tenantId) {
        /**
         * Illegal argument exception.
         *
         * @param getEntityType().getNormalName() get entity type().get normal name()
         * @return the throw new value
         */
        throw new IllegalArgumentException("Deletion by tenant id not supported for " + getEntityType().getNormalName());
    }

    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     */
    EntityType getEntityType();

}
