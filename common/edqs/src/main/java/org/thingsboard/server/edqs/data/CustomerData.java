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
package org.thingsboard.server.edqs.data;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.CustomerFields;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory EDQS projection of customer entity fields.
 *
 * <p>Updated from {@link org.thingsboard.server.common.data.edqs.EdqsEvent} and used during query execution.
 */

public class CustomerData extends BaseEntityData<CustomerFields> {

    private final ConcurrentMap<EntityType, ConcurrentMap<UUID, EntityData<?>>> entitiesById = new ConcurrentHashMap<>();

    public CustomerData(UUID entityId) {
        super(entityId);
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.CUSTOMER;
    }
    /**
     * Returns entities.
     *
     * @param entityType entity type ({@link EntityType})
     * @return {@link Collection}
     * @throws Exception if an unexpected error occurs during processing
     */

    public Collection<EntityData<?>> getEntities(EntityType entityType) {
        var map = entitiesById.get(entityType);
        if (map == null) {
            return Collections.emptyList();
        } else {
            return map.values();
        }
    }
    /**
     * Add or update.
     *
     * @param ed ed ({@link EntityData})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addOrUpdate(EntityData<?> ed) {
        entitiesById.computeIfAbsent(ed.getEntityType(), et -> new ConcurrentHashMap<>()).put(ed.getId(), ed);
    }
    /**
     * Removes the requested data.
     *
     * @param entityType entity type ({@link EntityType})
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean remove(EntityType entityType, UUID entityId) {
        var map = entitiesById.get(entityType);
        if (map != null) {
            return map.remove(entityId) != null;
        } else {
            return false;
        }
    }

}
