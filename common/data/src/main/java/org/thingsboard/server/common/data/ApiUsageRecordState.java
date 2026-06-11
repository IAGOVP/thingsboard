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
package org.thingsboard.server.common.data;

import lombok.Data;

import java.io.Serializable;

@Data
/**
 * Snapshot of a single {@link ApiUsageRecordKey} counter versus its configured threshold.
 *
 * <p>Returned by usage reporting APIs to show current consumption and limit proximity
 * for a tenant feature (transport, rule engine, email, etc.).
 */
public class ApiUsageRecordState implements Serializable {

    private final ApiFeature apiFeature;
    private final ApiUsageRecordKey key;
    private final long threshold;
    private final long value;
    /**
     * Returns value as string.
     *
     * @return {@link String}
     */

    public String getValueAsString() {
        return valueAsString(value);
    }
    /**
     * Returns threshold as string.
     *
     * @return {@link String}
     */

    public String getThresholdAsString() {
        return valueAsString(threshold);
    }

    private String valueAsString(long value) {
        if (value > 1_000_000 && value % 1_000_000 < 10_000) {
            return value / 1_000_000 + "M";
        } else if (value > 10_000) {
            return String.format("%.2fM", ((double) value) / 1_000_000);
        } else {
            return value + "";
        }
    }

}
