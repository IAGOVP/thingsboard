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
package org.thingsboard.server.transport.lwm2m.server.model;

import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClient;

/**
 * Service contract for lw m2mmodel config (LwM2M transport and object model (ThingsBoard common module)).
 *
 * <p>Implemented by the corresponding class in this or the dao module.
 */
public interface LwM2MModelConfigService {

    void sendUpdates(LwM2mClient lwM2mClient);

    /**
     * Send updates.
     *
     * @param lwM2mClient lw m2m client ({@link LwM2mClient})
     * @param modelConfig model config ({@link LwM2MModelConfig})
     * @return nothing
     * @throws Exception on processing failure
     */
    void sendUpdates(LwM2mClient lwM2mClient, LwM2MModelConfig modelConfig);

    /**
     * Persist updates.
     *
     * @param endpoint endpoint ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void persistUpdates(String endpoint);

    /**
     * Removes updates.
     *
     * @param endpoint endpoint ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void removeUpdates(String endpoint);
}
