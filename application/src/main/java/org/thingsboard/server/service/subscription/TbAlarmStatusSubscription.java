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
import lombok.Setter;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.service.ws.telemetry.sub.AlarmSubscriptionUpdate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;


/**
 * Subscription state holder for tb alarm status.
 * <p>Links a WebSocket session to entity keys and update processors.
 */


public class TbAlarmStatusSubscription extends TbSubscription<AlarmSubscriptionUpdate> {

    @Getter
    private final Set<UUID> alarmIds = new HashSet<>();
    @Getter
    @Setter
    private boolean hasMoreAlarmsInDB;
    @Getter
    private final List<String> typeList;
    @Getter
    private final List<AlarmSeverity> severityList;

    /**
     * Constructs {@link TbAlarmStatusSubscription} with the supplied dependencies and configuration.
     * @param serviceId service id
     * @param sessionId WebSocket session identifier
     * @param subscriptionId client command/subscription id
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param updateProcessor update processor
     * @param typeList type list
     * @param severityList severity list
     * @return @Builder
    public
     */

    @Builder
    public TbAlarmStatusSubscription(String serviceId, String sessionId, int subscriptionId, TenantId tenantId, EntityId entityId,
                                     BiConsumer<TbSubscription<AlarmSubscriptionUpdate>, AlarmSubscriptionUpdate> updateProcessor,
                                     List<String> typeList, List<AlarmSeverity> severityList) {
        super(serviceId, sessionId, subscriptionId, tenantId, entityId, TbSubscriptionType.ALARMS, updateProcessor);
        this.typeList = typeList;
        this.severityList = severityList;
    }

    /**
     * Matches.
     * @param alarm alarm
     * @return boolean result
     */

    public boolean matches(AlarmInfo alarm) {
        return !alarm.isCleared() && (this.typeList == null || this.typeList.contains(alarm.getType())) &&
                (this.severityList == null || this.severityList.contains(alarm.getSeverity()));
    }

    /**
     * Has alarms.
     * @return {@code true} when the condition holds
     */

    public boolean hasAlarms() {
        return !alarmIds.isEmpty();
    }
}
