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
 * Basic ts kv entry.
 */
public class BasicTsKvEntry implements TsKvEntry {
    private static final int MAX_CHARS_PER_DATA_POINT = 512;
    protected final long ts;
    @Valid
    private final KvEntry kv;

    private final Long version;

    public BasicTsKvEntry(long ts, KvEntry kv) {
        this.ts = ts;
        this.kv = kv;
        this.version = null;
    }

    public BasicTsKvEntry(long ts, KvEntry kv, Long version) {
        this.ts = ts;
        this.kv = kv;
        this.version = version;
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
     * Returns value.
     *
     * @return {@link Object}
     */

    @Override
    public Object getValue() {
        return kv.getValue();
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
     * Returns data points.
     *
     * @return the int result
     */

    @Override
    public int getDataPoints() {
        int length;
        switch (getDataType()) {
            case STRING:
                length = getStrValue().get().length();
                break;
            case JSON:
                length = getJsonValue().get().length();
                break;
            default:
                return 1;
        }
        return Math.max(1, (length + MAX_CHARS_PER_DATA_POINT - 1) / MAX_CHARS_PER_DATA_POINT);
    }

}
