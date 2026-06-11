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
package org.thingsboard.server.transport.lwm2m.server.store;

import org.thingsboard.server.transport.lwm2m.server.ota.firmware.LwM2MClientFwOtaInfo;
import org.thingsboard.server.transport.lwm2m.server.ota.software.LwM2MClientSwOtaInfo;

/**
 * tb lw m2mclient ota info store contract (LwM2M transport and object model (ThingsBoard common module)).
 */
public interface TbLwM2MClientOtaInfoStore {

    LwM2MClientFwOtaInfo getFw(String endpoint);

    /**
     * Returns sw.
     *
     * @param endpoint endpoint ({@link String})
     * @return {@link LwM2MClientSwOtaInfo}
     * @throws Exception on processing failure
     */
    LwM2MClientSwOtaInfo getSw(String endpoint);

    /**
     * Put fw.
     *
     * @param info info ({@link LwM2MClientFwOtaInfo})
     * @return nothing
     * @throws Exception on processing failure
     */
    void putFw(LwM2MClientFwOtaInfo info);

    /**
     * Put sw.
     *
     * @param info info ({@link LwM2MClientSwOtaInfo})
     * @return nothing
     * @throws Exception on processing failure
     */
    void putSw(LwM2MClientSwOtaInfo info);
}
