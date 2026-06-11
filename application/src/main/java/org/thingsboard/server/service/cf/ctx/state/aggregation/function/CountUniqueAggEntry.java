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

import org.thingsboard.server.common.data.cf.configuration.aggregation.AggFunction;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**

 * Argument or aggregation entry for calculated-field state (count unique agg entry).

 */

public class CountUniqueAggEntry implements AggEntry {

    private final Set<String> items = new HashSet<>();
    /**
     * Updates the requested data.
     *
     * @param value value ({@link Object})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void update(Object value) {
        if (value != null) {
            items.add(String.valueOf(value));
        }
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
        return Optional.of(items.size());
    }
    /**
     * Returns type.
     *
     * @return {@link AggFunction}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AggFunction getType() {
        return AggFunction.COUNT_UNIQUE;
    }
}
