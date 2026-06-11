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

import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.util.CollectionsUtil;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.List;
/**
 * Result of calculated-field evaluation (propagation calculated field result).
 */

@Data
@Builder
public final class PropagationCalculatedFieldResult implements CalculatedFieldResult {

    private final List<EntityId> entityIds;
    private final TelemetryCalculatedFieldResult result;
    /**
     * To tb msg.
     *
     * @param entityId target entity identifier
     * @param cfName cf name ({@link String})
     * @param cfIds cf ids ({@link List})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg toTbMsg(EntityId entityId, String cfName, List<CalculatedFieldId> cfIds) {
        return result.toTbMsg(entityId, cfName, cfIds);
    }
    /**
     * String value.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String stringValue() {
        return result.stringValue();
    }
    /**
     * Is empty.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isEmpty() {
        return CollectionsUtil.isEmpty(entityIds) || result.isEmpty();
    }

}
