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

import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.discovery.event.ClusterTopologyChangeEvent;
import org.thingsboard.server.service.ws.WebSocketSessionRef;
import org.thingsboard.server.service.ws.notification.sub.NotificationRequestUpdate;
import org.thingsboard.server.service.ws.notification.sub.NotificationsSubscriptionUpdate;

import java.util.List;

/**
 * Local (in-process) subscription registry for WebSocket clients. Registers entity/time-series/attribute/alarm/notification subscriptions and forwards updates from the cluster.
 */

public interface TbLocalSubscriptionService {

    /**
     * Registers subscription.
     * @param subscription subscription to register or remove
     * @param sessionRef reference to the WebSocket session
     */

    void addSubscription(TbSubscription<?> subscription, WebSocketSessionRef sessionRef);

    /**
     * Invoked when sub event callback occurs.
     * @param subEventCallback sub event callback
     * @param callback queue callback to ack or retry the message
     */

    void onSubEventCallback(TransportProtos.TbEntitySubEventCallbackProto subEventCallback, TbCallback callback);

    /**
     * Invoked when sub event callback occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param entityId target entity id
     * @param seqNumber seq number
     * @param entityUpdatesInfo entity updates info
     * @param empty empty
     */

    void onSubEventCallback(TenantId tenantId, EntityId entityId, int seqNumber, TbEntityUpdatesInfo entityUpdatesInfo, TbCallback empty);

    /**
     * Cancels subscription.
     * @param tenantId tenant that owns the subscription or entity
     * @param sessionId WebSocket session identifier
     * @param subscriptionId client command/subscription id
     */

    void cancelSubscription(TenantId tenantId, String sessionId, int subscriptionId);

    /**
     * Cancels all session subscriptions.
     * @param tenantId tenant that owns the subscription or entity
     * @param sessionId WebSocket session identifier
     */

    void cancelAllSessionSubscriptions(TenantId tenantId, String sessionId);

    /**
     * Invoked when time series update occurs.
     * @param tsUpdate ts update
     * @param callback queue callback to ack or retry the message
     */

    void onTimeSeriesUpdate(TransportProtos.TbSubUpdateProto tsUpdate, TbCallback callback);

    /**
     * Invoked when time series update occurs.
     * @param entityId target entity id
     * @param update subscription update payload
     * @param callback queue callback to ack or retry the message
     */

    void onTimeSeriesUpdate(EntityId entityId, List<TsKvEntry> update, TbCallback callback);

    /**
     * Invoked when attributes update occurs.
     * @param attrUpdate attr update
     * @param callback queue callback to ack or retry the message
     */

    void onAttributesUpdate(TransportProtos.TbSubUpdateProto attrUpdate, TbCallback callback);

    /**
     * Invoked when attributes update occurs.
     * @param entityId target entity id
     * @param scope scope
     * @param update subscription update payload
     * @param callback queue callback to ack or retry the message
     */

    void onAttributesUpdate(EntityId entityId, String scope, List<TsKvEntry> update, TbCallback callback);

    /**
     * Invoked when alarm update occurs.
     * @param entityId target entity id
     * @param alarm alarm
     * @param deleted deleted
     * @param callback queue callback to ack or retry the message
     */

    void onAlarmUpdate(EntityId entityId, AlarmInfo alarm, boolean deleted, TbCallback callback);

    /**
     * Invoked when alarm update occurs.
     * @param update subscription update payload
     * @param callback queue callback to ack or retry the message
     */

    void onAlarmUpdate(TransportProtos.TbAlarmSubUpdateProto update, TbCallback callback);

    /**
     * Invoked when notification update occurs.
     * @param entityId target entity id
     * @param subscriptionUpdate subscription update
     * @param callback queue callback to ack or retry the message
     */

    void onNotificationUpdate(EntityId entityId, NotificationsSubscriptionUpdate subscriptionUpdate, TbCallback callback);

    /**
     * Invoked when application event occurs.
     * @param event application or cluster event
     */

    void onApplicationEvent(ClusterTopologyChangeEvent event);

    /**
     * Invoked when core startup msg occurs.
     * @param coreStartupMsg core startup msg
     */

    void onCoreStartupMsg(TransportProtos.CoreStartupMsg coreStartupMsg);

    /**
     * Invoked when notification request update occurs.
     * @param tenantId tenant that owns the subscription or entity
     * @param update subscription update payload
     * @param callback queue callback to ack or retry the message
     */

    void onNotificationRequestUpdate(TenantId tenantId, NotificationRequestUpdate update, TbCallback callback);

    /**
     * Invoked when notification update occurs.
     * @param notificationsUpdate notifications update
     * @param callback queue callback to ack or retry the message
     */

    void onNotificationUpdate(TransportProtos.NotificationsSubUpdateProto notificationsUpdate, TbCallback callback);

}
