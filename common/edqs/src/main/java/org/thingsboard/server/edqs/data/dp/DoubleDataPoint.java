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
import org.thingsboard.server.common.data.edqs.DataPoint;
import org.thingsboard.server.common.data.kv.DataType;

/**
 * Typed attribute or latest-TS value stored in the EDQS index (double data point).
 */

public class DoubleDataPoint extends AbstractDataPoint {

    @Getter
    private final double value;

    public DoubleDataPoint(long ts, double value) {
        super(ts);
        this.value = value;
    }
    /**
     * Returns type.
     *
     * @return {@link DataType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DataType getType() {
        return DataType.DOUBLE;
    }
    /**
     * Returns double.
     *
     * @return the double result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public double getDouble() {
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
        return Double.toString(value);
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
        if (dataPoint.getType() == DataType.DOUBLE || dataPoint.getType() == DataType.LONG) {
            return Double.compare(value, dataPoint.getDouble());
        } else {
            return super.compareTo(dataPoint);
        }
    }
}
