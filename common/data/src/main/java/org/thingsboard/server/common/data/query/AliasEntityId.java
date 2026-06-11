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
package org.thingsboard.server.common.data.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;

@JsonDeserialize(using = AliasEntityIdDeserializer.class)
@JsonSerialize(using = AliasEntityIdSerializer.class)
@Schema
/**
 * alias entity id contract.
 */
public interface AliasEntityId extends EntityId {

    AliasEntityType getAliasEntityType();
/**
 * Default entity id.
 *
 * @return {@link EntityId}
 */

    EntityId defaultEntityId();
/**
 * To entity id.
 *
 * @return {@link EntityId}
 */

    EntityId toEntityId();
    /**
     * Is alias entity id.
     *
     * @return the boolean result
     */

    @JsonIgnore
    default boolean isAliasEntityId() {
        /**
         * Returns alias entity type.
         *
         * @return the return value
         */
        return getAliasEntityType() != null;
    }
/**
 * From entity id.
 *
 * @param entityId target entity identifier
 * @return {@link AliasEntityId}
 */

    static AliasEntityId fromEntityId(EntityId entityId) {
        if (entityId != null) {
            /**
             * Alias entity id impl.
             *
             * @return the return new value
             */
            return new AliasEntityIdImpl(entityId);
        } else {
            return null;
        }
    }
/**
 * Resolve alias entity id.
 *
 * @param aliasEntityId alias entity id ({@link AliasEntityId})
 * @param tenantId tenant that owns the entity or operation
 * @param userId user id ({@link UserId})
 * @param userOwnerId user owner id ({@link EntityId})
 * @return {@link AliasEntityId}
 */

    static AliasEntityId resolveAliasEntityId(AliasEntityId aliasEntityId, TenantId tenantId, UserId userId, EntityId userOwnerId) {
        if (aliasEntityId != null) {
            if (aliasEntityId.isAliasEntityId()) {
                AliasEntityType aliasEntityType = aliasEntityId.getAliasEntityType();
                switch (aliasEntityType) {
                    case CURRENT_CUSTOMER -> {
                        if (EntityType.CUSTOMER.equals(userOwnerId.getEntityType())) {
                            /**
                             * From entity id.
                             *
                             * @return the return value
                             */
                            return fromEntityId(userOwnerId);
                        } else {
                            /**
                             * From entity id.
                             *
                             * @return the return value
                             */
                            return fromEntityId(aliasEntityId.defaultEntityId());
                        }
                    }
                    case CURRENT_TENANT -> {
                        /**
                         * From entity id.
                         *
                         * @return the return value
                         */
                        return fromEntityId(tenantId);
                    }
                    case CURRENT_USER -> {
                        /**
                         * From entity id.
                         *
                         * @return the return value
                         */
                        return fromEntityId(userId);
                    }
                    case CURRENT_USER_OWNER -> {
                        /**
                         * From entity id.
                         *
                         * @return the return value
                         */
                        return fromEntityId(userOwnerId);
                    }
                }
            } else {
                return aliasEntityId;
            }
        }
        return null;
    }

}
