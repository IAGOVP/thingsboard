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
package org.thingsboard.monitoring.util;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 * NanoTime stopwatch for latency measurement in health checks.
 */


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TbStopWatch {

    private final StopWatch internal = new StopWatch();
    /**
     * Start.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void start() {
        internal.reset();
        internal.start();
    }
    /**
     * Returns time.
     *
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    public long getTime() {
        internal.stop();
        long nanoTime = internal.getNanoTime();
        internal.reset();
        return nanoTime;
    }

}
