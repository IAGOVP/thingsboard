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
package org.thingsboard.server.edqs.data.dp;

import lombok.Getter;
import org.thingsboard.server.common.data.kv.DataType;
import org.thingsboard.common.util.TbStringPool;

/**
 * Typed attribute or latest-TS value stored in the EDQS index (json data point).
 */

public class JsonDataPoint extends AbstractDataPoint {

    @Getter
    private final String value;

    public JsonDataPoint(long ts, String value) {
        super(ts);
        this.value = TbStringPool.intern(value);
    }
    /**
     * Returns type.
     *
     * @return {@link DataType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DataType getType() {
        return DataType.JSON;
    }
    /**
     * Returns json.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getJson() {
        return value;
    }
    /**
     * Value to string.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String valueToString() {
        return value;
    }

}
