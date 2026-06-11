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

import lombok.ToString;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.ApiUsageStateFields;

import java.util.UUID;

/**
 * In-memory EDQS projection of api usage state entity fields.
 *
 * <p>Updated from {@link org.thingsboard.server.common.data.edqs.EdqsEvent} and used during query execution.
 */

@ToString(callSuper = true)
public class ApiUsageStateData extends BaseEntityData<ApiUsageStateFields> {

    public ApiUsageStateData(UUID entityId) {
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
        return EntityType.API_USAGE_STATE;
    }
    /**
     * Returns entity name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getEntityName() {
        /** Returns the owner name. */
        return getOwnerName();
    }
    /**
     * Returns owner name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getOwnerName() {
        return repo.getOwnerEntityName(fields.getEntityId());
    }

}
