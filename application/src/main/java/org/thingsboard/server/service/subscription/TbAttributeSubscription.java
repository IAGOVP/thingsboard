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
package org.thingsboard.server.service.subscription;

import lombok.Builder;
import lombok.Getter;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.service.ws.telemetry.sub.TelemetrySubscriptionUpdate;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Subscription state holder for tb attribute.
 * <p>Links a WebSocket session to entity keys and update processors.
 */

public class TbAttributeSubscription extends TbSubscription<TelemetrySubscriptionUpdate> {


    /**
     * Constructs {@link TbAttributeSubscription} with the supplied dependencies and configuration.
     * @param serviceId service id
     * @param sessionId WebSocket session identifier
     * @param subscriptionId client command/subscription id
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param updateProcessor update processor
     * @param queryTs query ts
     * @param allKeys all keys
     * @param keyStates key states
     * @param scope scope
     * @return @Builder
    public
     */
    @Getter private final long queryTs;
    @Getter private final boolean allKeys;
    @Getter private final Map<String, Long> keyStates;
    @Getter private final TbAttributeSubscriptionScope scope;

    @Builder
    public TbAttributeSubscription(String serviceId, String sessionId, int subscriptionId, TenantId tenantId, EntityId entityId,
                                   BiConsumer<TbSubscription<TelemetrySubscriptionUpdate>, TelemetrySubscriptionUpdate> updateProcessor,
                                   long queryTs, boolean allKeys, Map<String, Long> keyStates, TbAttributeSubscriptionScope scope) {
        super(serviceId, sessionId, subscriptionId, tenantId, entityId, TbSubscriptionType.ATTRIBUTES, updateProcessor);
        this.queryTs = queryTs;
        this.allKeys = allKeys;
        this.keyStates = keyStates;
        this.scope = scope;
    }

    /**
     * Compares this object to another for equality.
     * @param o o
     * @return boolean result
     */

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * <p>Default implementation inherited from the supertype.
     * @return {@code true} when the condition holds
     */

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
