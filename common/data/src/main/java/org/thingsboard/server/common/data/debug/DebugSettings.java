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
package org.thingsboard.server.common.data.debug;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * Debug settings.
 */
public class DebugSettings {

    private static DebugSettings DEBUG_OFF = new DebugSettings(false, 0);
    private static DebugSettings DEBUG_FAILURES = new DebugSettings(true, 0);

    public DebugSettings(boolean failuresEnabled, long allEnabledUntil) {
        this.failuresEnabled = failuresEnabled;
        this.allEnabled = false;
        this.allEnabledUntil = allEnabledUntil;
    }

    @Schema(description = "Debug failures. ", example = "false")
    private boolean failuresEnabled;
    @Schema(description = "Debug All. Used as a trigger for updating debugAllUntil.", example = "false")
    private boolean allEnabled;
    @Schema(description = "Timestamp of the end time for the processing debug events.")
    private long allEnabledUntil;
    /**
     * Off.
     *
     * @return {@link DebugSettings}
     */

    public static DebugSettings off() {return DebugSettings.DEBUG_OFF;}
    /**
     * Failures.
     *
     * @return {@link DebugSettings}
     */

    public static DebugSettings failures() {return DebugSettings.DEBUG_FAILURES;}
    /**
     * Until.
     *
     * @param ts ts
     * @return {@link DebugSettings}
     */

    public static DebugSettings until(long ts) {return new DebugSettings(false, ts);}
    /**
     * Failures or until.
     *
     * @param ts ts
     * @return {@link DebugSettings}
     */

    public static DebugSettings failuresOrUntil(long ts) {return new DebugSettings(true, ts);}
    /**
     * All.
     *
     * @return {@link DebugSettings}
     */

    public static DebugSettings all() {
        var ds = new DebugSettings();
        ds.setAllEnabled(true);
        return ds;
    }
    /**
     * Copy.
     *
     * @param maxDebugAllUntil max debug all until
     * @return {@link DebugSettings}
     */

    public DebugSettings copy(long maxDebugAllUntil) {
        return new DebugSettings(failuresEnabled, allEnabled ? maxDebugAllUntil : Math.min(allEnabledUntil, maxDebugAllUntil));
    }
}
