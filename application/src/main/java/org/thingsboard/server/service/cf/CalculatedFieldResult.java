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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.List;
import java.util.Objects;

/**

 * Result of calculated-field evaluation (calculated field result).

 */

public interface CalculatedFieldResult {
/**
 * To tb msg.
 *
 * @param entityId target entity identifier
 * @param cfName cf name ({@link String})
 * @param cfIds cf ids ({@link List})
 * @return {@link TbMsg}
 * @throws Exception if an unexpected error occurs during processing
 */



    TbMsg toTbMsg(EntityId entityId, String cfName, List<CalculatedFieldId> cfIds);
/**
 * String value.
 *
 * @return {@link String}
 * @throws Exception if an unexpected error occurs during processing
 */

    String stringValue();
/**
 * Is empty.
 *
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean isEmpty();
/**
 * To json element.
 *
 * @return {@link JsonElement}
 * @throws Exception if an unexpected error occurs during processing
 */

    default JsonElement toJsonElement() {
        return JsonParser.parseString(Objects.requireNonNull(stringValue()));
    }

}
