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
import org.thingsboard.server.service.ws.telemetry.sub.AlarmSubscriptionUpdate;

import java.util.function.BiConsumer;

/**
 * Subscription state holder for tb alarms.
 * <p>Links a WebSocket session to entity keys and update processors.
 */

public class TbAlarmsSubscription extends TbSubscription<AlarmSubscriptionUpdate> {

    @Getter
    private final long ts;

    /**
     * Constructs {@link TbAlarmsSubscription} with the supplied dependencies and configuration.
     * @param serviceId service id
     * @param sessionId WebSocket session identifier
     * @param subscriptionId client command/subscription id
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param updateProcessor update processor
     * @param ts ts
     * @return @Builder
    public
     */

    @Builder
    public TbAlarmsSubscription(String serviceId, String sessionId, int subscriptionId, TenantId tenantId, EntityId entityId,
                                BiConsumer<TbSubscription<AlarmSubscriptionUpdate>, AlarmSubscriptionUpdate> updateProcessor, long ts) {
        super(serviceId, sessionId, subscriptionId, tenantId, entityId, TbSubscriptionType.ALARMS, updateProcessor);
        this.ts = ts;
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
