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
package org.thingsboard.server.service.telemetry;

import io.swagger.v3.oas.annotations.media.Schema;

    /**
     * Attribute data (telemetry subscription and WebSocket push to clients).
     */

@Schema
public class AttributeData implements Comparable<AttributeData>{

    private final long lastUpdateTs;
    private final String key;
    private final Object value;

    public AttributeData(long lastUpdateTs, String key, Object value) {
        super();
        this.lastUpdateTs = lastUpdateTs;
        this.key = key;
        this.value = value;
    }
    /**
     * Returns last update ts.
     *
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Schema(description = "Timestamp last updated attribute, in milliseconds", example = "1609459200000", accessMode = Schema.AccessMode.READ_ONLY)
    public long getLastUpdateTs() {
        return lastUpdateTs;
    }
    /**
     * Returns key.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Schema(description = "String representing attribute key", example = "active", accessMode = Schema.AccessMode.READ_ONLY)
    public String getKey() {
        return key;
    }
    /**
     * Returns value.
     *
     * @return {@link Object}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Schema(description = "Object representing value of attribute key", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    public Object getValue() {
        return value;
    }
    /**
     * Compares to.
     *
     * @param o o ({@link AttributeData})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int compareTo(AttributeData o) {
        return Long.compare(lastUpdateTs, o.lastUpdateTs);
    }

}
