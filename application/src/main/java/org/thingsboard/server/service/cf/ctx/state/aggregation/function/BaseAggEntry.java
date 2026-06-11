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
package org.thingsboard.server.service.cf.ctx.state.aggregation.function;

import java.util.Optional;

/**

 * Argument or aggregation entry for calculated-field state (base agg entry).

 */

public abstract class BaseAggEntry implements AggEntry {

    private boolean hasResult = false;
    /**
     * Updates the requested data.
     *
     * @param value value ({@link Object})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void update(Object value) {
        doUpdate(extractDoubleValue(value));
        hasResult = true;
    }
    /**
     * Result.
     *
     * @param precision precision ({@link Integer})
     * @return optional {@link Object}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Optional<Object> result(Integer precision) {
        if (hasResult) {
            hasResult = false;
            return Optional.of(prepareResult(precision));
        } else {
            return Optional.empty();
        }
    }
    /**
     * Do update.
     *
     * @param value value
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract void doUpdate(double value);
    /**
     * Prepare result.
     *
     * @param precision precision ({@link Integer})
     * @return {@link Object}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract Object prepareResult(Integer precision);
    /**
     * Extract double value.
     *
     * @param value value ({@link Object})
     * @return the double result
     * @throws Exception if an unexpected error occurs during processing
     */

    protected double extractDoubleValue(Object value) {
        try {
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            throw new NumberFormatException("Cannot parse value " + value.toString());
        }
    }

}
