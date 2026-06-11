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
package org.thingsboard.server.dao.util.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.thingsboard.common.util.JacksonUtil;
/**
 * Json converter (DAO utilities (KV conversion, rate executors, JSON mapping)).
 */







@Converter
public class JsonConverter implements AttributeConverter<JsonNode, String> {
    /**
     * Convert to database column.
     *
     * @param jsonNode json node ({@link JsonNode})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */
    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        return JacksonUtil.toString(jsonNode);
    }
    /**
     * Convert to entity attribute.
     *
     * @param s s ({@link String})
     * @return {@link JsonNode}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public JsonNode convertToEntityAttribute(String s) {
        return JacksonUtil.toJsonNode(s);
    }
}
