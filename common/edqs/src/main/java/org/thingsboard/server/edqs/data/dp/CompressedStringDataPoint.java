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
import lombok.SneakyThrows;
import org.thingsboard.common.util.TbBytePool;
import org.thingsboard.server.common.data.kv.DataType;

import java.util.function.Function;

/**
 * Typed attribute or latest-TS value stored in the EDQS index (compressed string data point).
 */

public class CompressedStringDataPoint extends AbstractDataPoint {

    @Getter
    private final byte[] compressedValue;

    protected final Function<byte[], String> uncompressor;

    @SneakyThrows
    public CompressedStringDataPoint(long ts, byte[] compressedValue, Function<byte[], String> uncompressor) {
        super(ts);
        this.compressedValue = TbBytePool.intern(compressedValue);
        this.uncompressor = uncompressor;
    }
    /**
     * Returns type.
     *
     * @return {@link DataType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DataType getType() {
        return DataType.STRING;
    }
    /**
     * Returns str.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @SneakyThrows
    @Override
    public String getStr() {
        return uncompressor.apply(compressedValue);
    }
    /**
     * Value to string.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String valueToString() {
        /** Returns the str. */
        return getStr();
    }

}
