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

import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClient;

import java.util.Collections;
import java.util.Set;

/**
 * Tb dummy lw m2mclient store.
 */
public class TbDummyLwM2MClientStore implements TbLwM2MClientStore {
    /**
     * Returns the requested data.
     *
     * @param endpoint endpoint ({@link String})
     * @return {@link LwM2mClient}
     * @throws Exception on processing failure
     */
    @Override
    public LwM2mClient get(String endpoint) {
        return null;
    }
    /**
     * Returns all.
     *
     * @return {@link Set}
     * @throws Exception on processing failure
     */

    @Override
    public Set<LwM2mClient> getAll() {
        return Collections.emptySet();
    }
    /**
     * Put.
     *
     * @param client client ({@link LwM2mClient})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void put(LwM2mClient client) {

    }
    /**
     * Removes the requested data.
     *
     * @param endpoint endpoint ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void remove(String endpoint) {

    }
}
