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
package org.thingsboard.server.common.transport.limits;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.transport.profile.TenantProfileUpdateResult;

import java.net.InetSocketAddress;

/**
 * Service API for transport rate limit persistence and domain operations.
 */
public interface TransportRateLimitService {

    TbPair<EntityType, Boolean> checkLimits(TenantId tenantId, DeviceId gatewayId, DeviceId deviceId, int dataPoints, boolean isGateway);

    /**
     * Updates the requested data.
     *
     * @param update update ({@link TenantProfileUpdateResult})
     * @return nothing
     * @throws Exception on processing failure
     */
    void update(TenantProfileUpdateResult update);

    /**
     * Updates the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception on processing failure
     */
    void update(TenantId tenantId);

    /**
     * Removes the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception on processing failure
     */
    void remove(TenantId tenantId);

    /**
     * Removes the requested data.
     *
     * @param deviceId target device identifier
     * @return nothing
     * @throws Exception on processing failure
     */
    void remove(DeviceId deviceId);

    /**
     * Updates the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param transportEnabled transport enabled
     * @return nothing
     * @throws Exception on processing failure
     */
    void update(TenantId tenantId, boolean transportEnabled);

    /**
     * Checks address.
     *
     * @param address address ({@link InetSocketAddress})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean checkAddress(InetSocketAddress address);

    /**
     * Handles auth success.
     *
     * @param address address ({@link InetSocketAddress})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onAuthSuccess(InetSocketAddress address);

    /**
     * Handles auth failure.
     *
     * @param address address ({@link InetSocketAddress})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onAuthFailure(InetSocketAddress address);

    /**
     * Invalidate rate limits ip table.
     *
     * @param sessionInactivityTimeout session inactivity timeout
     * @return nothing
     * @throws Exception on processing failure
     */
    void invalidateRateLimitsIpTable(long sessionInactivityTimeout);

}
