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
package org.thingsboard.server.common.transport;

import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.transport.profile.TenantProfileUpdateResult;
import org.thingsboard.server.gen.transport.TransportProtos;

import java.util.Set;

/**
 * transport tenant profile cache contract.
 */
public interface TransportTenantProfileCache {

    TenantProfile get(TenantId tenantId);

    /**
     * Put.
     *
     * @param proto proto
     * @return {@link TenantProfileUpdateResult}
     * @throws Exception on processing failure
     */
    TenantProfileUpdateResult put(TransportProtos.TenantProfileProto proto);

    /**
     * Put.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileId profile id ({@link TenantProfileId})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean put(TenantId tenantId, TenantProfileId profileId);

    /**
     * Removes the requested data.
     *
     * @param profileId profile id ({@link TenantProfileId})
     * @return {@link Set}
     * @throws Exception on processing failure
     */
    Set<TenantId> remove(TenantProfileId profileId);

}
