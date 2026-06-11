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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.common.data.edqs.DataPoint;

/**
 * Typed attribute or latest-TS value stored in the EDQS index (abstract data point).
 */

@RequiredArgsConstructor
public abstract class AbstractDataPoint implements DataPoint {

    @Getter
    private final long ts;
    /**
     * Returns str.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getStr() {
        throw new RuntimeException(NOT_SUPPORTED);
    }
    /**
     * Returns long.
     *
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public long getLong() {
        throw new RuntimeException(NOT_SUPPORTED);
    }
    /**
     * Returns double.
     *
     * @return the double result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public double getDouble() {
        throw new RuntimeException(NOT_SUPPORTED);
    }
    /**
     * Returns bool.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean getBool() {
        throw new RuntimeException(NOT_SUPPORTED);
    }
    /**
     * Returns json.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getJson() {
        throw new RuntimeException(NOT_SUPPORTED);
    }

    public String toString() {
        /** Value to string. */
        return valueToString();
    }
    /**
     * Compares to.
     *
     * @param dataPoint data point ({@link DataPoint})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int compareTo(DataPoint dataPoint) {
        return StringUtils.compareIgnoreCase(valueToString(), dataPoint.valueToString());
    }

}
