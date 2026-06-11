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

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.AttributeScope;
import org.thingsboard.server.common.data.cf.configuration.OutputStrategy;
import org.thingsboard.server.common.data.cf.configuration.OutputType;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.List;

import static org.thingsboard.server.common.data.DataConstants.CF_NAME_METADATA_KEY;
import static org.thingsboard.server.common.data.DataConstants.SCOPE;
/**
 * Result of calculated-field evaluation (telemetry calculated field result).
 */

@Data
@Builder
public final class TelemetryCalculatedFieldResult implements CalculatedFieldResult {

    private final OutputType type;
    private final AttributeScope scope;
    private final OutputStrategy outputStrategy;
    private final JsonNode result;

    public static final TelemetryCalculatedFieldResult EMPTY = TelemetryCalculatedFieldResult.builder().result(null).build();
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
        TbMsgType msgType = switch (type) {
            case ATTRIBUTES -> TbMsgType.POST_ATTRIBUTES_REQUEST;
            case TIME_SERIES -> TbMsgType.POST_TELEMETRY_REQUEST;
        };
        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue(CF_NAME_METADATA_KEY, cfName);
        if (OutputType.ATTRIBUTES == type) {
            metaData.putValue(SCOPE, scope.name());
        }
        return TbMsg.newMsg()
                .type(msgType)
                .originator(entityId)
                .previousCalculatedFieldIds(cfIds)
                .data(stringValue())
                .metaData(metaData)
                .build();
    }
    /**
     * String value.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String stringValue() {
        return result == null ? null : result.toString();
    }
    /**
     * Is empty.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isEmpty() {
        return result == null || result.isMissingNode() || result.isNull() ||
                (result.isObject() && result.isEmpty()) ||
                (result.isArray() && result.isEmpty()) ||
                (result.isTextual() && result.asText().isEmpty());
    }

}
