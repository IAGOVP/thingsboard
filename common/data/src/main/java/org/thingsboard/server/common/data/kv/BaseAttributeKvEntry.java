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
package org.thingsboard.server.common.data.kv;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.Optional;

@Data
/**
 * Base attribute kv entry.
 */
public class BaseAttributeKvEntry implements AttributeKvEntry {

    private static final long serialVersionUID = -6460767583563159407L;

    private final long lastUpdateTs;
    @Valid
    private final KvEntry kv;

    private final Long version;

    public BaseAttributeKvEntry(KvEntry kv, long lastUpdateTs) {
        this.kv = kv;
        this.lastUpdateTs = lastUpdateTs;
        this.version = null;
    }

    public BaseAttributeKvEntry(KvEntry kv, long lastUpdateTs, Long version) {
        this.kv = kv;
        this.lastUpdateTs = lastUpdateTs;
        this.version = version;
    }

    public BaseAttributeKvEntry(long lastUpdateTs, KvEntry kv) {
        this(kv, lastUpdateTs);
    }
    /**
     * Returns key.
     *
     * @return {@link String}
     */

    @Override
    public String getKey() {
        return kv.getKey();
    }
    /**
     * Returns data type.
     *
     * @return {@link DataType}
     */

    @Override
    public DataType getDataType() {
        return kv.getDataType();
    }
    /**
     * Returns str value.
     *
     * @return optional {@link String}, empty if not found
     */

    @Override
    public Optional<String> getStrValue() {
        return kv.getStrValue();
    }
    /**
     * Returns long value.
     *
     * @return optional {@link Long}, empty if not found
     */

    @Override
    public Optional<Long> getLongValue() {
        return kv.getLongValue();
    }
    /**
     * Returns boolean value.
     *
     * @return optional {@link Boolean}, empty if not found
     */

    @Override
    public Optional<Boolean> getBooleanValue() {
        return kv.getBooleanValue();
    }
    /**
     * Returns double value.
     *
     * @return optional {@link Double}, empty if not found
     */

    @Override
    public Optional<Double> getDoubleValue() {
        return kv.getDoubleValue();
    }
    /**
     * Returns json value.
     *
     * @return optional {@link String}, empty if not found
     */

    @Override
    public Optional<String> getJsonValue() {
        return kv.getJsonValue();
    }
    /**
     * Returns value as string.
     *
     * @return {@link String}
     */

    @Override
    public String getValueAsString() {
        return kv.getValueAsString();
    }
    /**
     * Returns value.
     *
     * @return {@link Object}
     */

    @Override
    public Object getValue() {
        return kv.getValue();
    }

}
