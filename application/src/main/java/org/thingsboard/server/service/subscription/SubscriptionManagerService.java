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

import org.springframework.context.ApplicationListener;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.queue.discovery.event.OtherServiceShutdownEvent;
import org.thingsboard.server.queue.discovery.event.PartitionChangeEvent;
import org.thingsboard.server.service.ws.notification.sub.NotificationUpdate;

import java.util.List;

/**
 * Cluster-wide subscription coordinator. Tracks which tb-core node owns each entity subscription and routes {@code SubscriptionMgrMsgProto} between nodes.
 */

public interface SubscriptionManagerService extends ApplicationListener<PartitionChangeEvent> {

    /**
     * Invoked when sub event occurs.
     * @param serviceId service id
     * @param event application or cluster event
     * @param empty empty
     */

    void onSubEvent(String serviceId, TbEntitySubEvent event, TbCallback empty);

    /**
     * Invoked when application event occurs.
     * @param event application or cluster event
     */

    void onApplicationEvent(OtherServiceShutdownEvent event);

    /**
     * Invoked when time series update occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param ts ts
     * @param callback queue callback to ack or retry the message
     */

    void onTimeSeriesUpdate(TenantId tenantId, EntityId entityId, List<TsKvEntry> ts, TbCallback callback);

    /**
     * Invoked when attributes update occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param scope scope
     * @param attributes attributes
     * @param callback queue callback to ack or retry the message
     */

    void onAttributesUpdate(TenantId tenantId, EntityId entityId, String scope, List<AttributeKvEntry> attributes, TbCallback callback);

    /**
     * Invoked when attributes delete occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param scope scope
     * @param keys keys
     * @param empty empty
     */

    void onAttributesDelete(TenantId tenantId, EntityId entityId, String scope, List<String> keys, TbCallback empty);

    /**
     * Invoked when time series delete occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param keys keys
     * @param callback queue callback to ack or retry the message
     */

    void onTimeSeriesDelete(TenantId tenantId, EntityId entityId, List<String> keys, TbCallback callback);

    /**
     * Invoked when alarm update occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param alarm alarm
     * @param callback queue callback to ack or retry the message
     */

    void onAlarmUpdate(TenantId tenantId, EntityId entityId, AlarmInfo alarm, TbCallback callback);

    /**
     * Invoked when alarm deleted occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param alarm alarm
     * @param callback queue callback to ack or retry the message
     */

    void onAlarmDeleted(TenantId tenantId, EntityId entityId, AlarmInfo alarm, TbCallback callback);

    /**
     * Invoked when notification update occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param recipientId user identifier
     * @param notificationUpdate notification update
     * @param callback queue callback to ack or retry the message
     */

    void onNotificationUpdate(TenantId tenantId, UserId recipientId, NotificationUpdate notificationUpdate, TbCallback callback);

}
