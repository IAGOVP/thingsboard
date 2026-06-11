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
package org.thingsboard.server.cluster;

import org.thingsboard.server.common.data.ApiUsageState;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.cf.CalculatedField;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.plugin.ComponentLifecycleEvent;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.ToDeviceActorNotificationMsg;
import org.thingsboard.server.common.msg.edge.EdgeEventUpdateMsg;
import org.thingsboard.server.common.msg.edge.EdgeHighPriorityMsg;
import org.thingsboard.server.common.msg.edge.FromEdgeSyncResponse;
import org.thingsboard.server.common.msg.edge.ToEdgeSyncRequest;
import org.thingsboard.server.common.msg.plugin.ComponentLifecycleMsg;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.common.msg.rpc.FromDeviceRpcResponse;
import org.thingsboard.server.gen.transport.TransportProtos.RestApiCallResponseMsgProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCalculatedFieldNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToEdgeMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToVersionControlServiceMsg;
import org.thingsboard.server.queue.TbQueueCallback;
import org.thingsboard.server.queue.TbQueueClusterService;

import java.util.UUID;

/**
 * Cross-service messaging in clustered ThingsBoard: core, rule engine, transport, EDQS, version control, edge.
 *
 * <p>Publishes protobuf envelopes ({@link ToCoreMsg}, {@link ToRuleEngineMsg}, {@link ToTransportMsg}, …)
 * to Kafka topics with tenant/entity partitioning.
 */
public interface TbClusterService extends TbQueueClusterService {

    /** Push msg to core. */
    void pushMsgToCore(TopicPartitionInfo tpi, UUID msgKey, ToCoreMsg msg, TbQueueCallback callback);

    /** Push msg to core. */
    void pushMsgToCore(TenantId tenantId, EntityId entityId, ToCoreMsg msg, TbQueueCallback callback);

    /** Push msg to core. */
    void pushMsgToCore(ToDeviceActorNotificationMsg msg, TbQueueCallback callback);

    /** Broadcast to core. */
    void broadcastToCore(ToCoreNotificationMsg msg);

    /** Broadcast to calculated fields. */
    void broadcastToCalculatedFields(ToCalculatedFieldNotificationMsg build, TbQueueCallback callback);

    /** Push msg to version control. */
    void pushMsgToVersionControl(TenantId tenantId, ToVersionControlServiceMsg msg, TbQueueCallback callback);

    /** Push notification to core. */
    void pushNotificationToCore(String targetServiceId, FromDeviceRpcResponse response, TbQueueCallback callback);

    /** Push notification to core. */
    void pushNotificationToCore(String targetServiceId, RestApiCallResponseMsgProto msg, TbQueueCallback callback);

    /** Push msg to rule engine. */
    void pushMsgToRuleEngine(TopicPartitionInfo tpi, UUID msgId, ToRuleEngineMsg msg, TbQueueCallback callback);

    /** Push msg to rule engine. */
    void pushMsgToRuleEngine(TenantId tenantId, EntityId entityId, TbMsg msg, TbQueueCallback callback);

    /** Push msg to rule engine. */
    void pushMsgToRuleEngine(TenantId tenantId, EntityId entityId, TbMsg msg, boolean useQueueFromTbMsg, TbQueueCallback callback);

    /** Push notification to rule engine. */
    void pushNotificationToRuleEngine(String targetServiceId, FromDeviceRpcResponse response, TbQueueCallback callback);

    /** Push notification to transport. */
    void pushNotificationToTransport(String targetServiceId, ToTransportMsg response, TbQueueCallback callback);

    /** Push msg to calculated fields. */
    void pushMsgToCalculatedFields(TenantId tenantId, EntityId entityId, ToCalculatedFieldMsg msg, TbQueueCallback callback);

    /** Push msg to calculated fields. */
    void pushMsgToCalculatedFields(TopicPartitionInfo tpi, UUID msgId, ToCalculatedFieldMsg msg, TbQueueCallback callback);

    /** Broadcast entity state change event. */
    void broadcastEntityStateChangeEvent(TenantId tenantId, EntityId entityId, ComponentLifecycleEvent state);

    /** Broadcast. */
    void broadcast(ComponentLifecycleMsg componentLifecycleMsg);

    /** On device profile change. */
    void onDeviceProfileChange(DeviceProfile deviceProfile, DeviceProfile oldDeviceProfile, TbQueueCallback callback);

    /** On device profile delete. */
    void onDeviceProfileDelete(DeviceProfile deviceProfile, TbQueueCallback callback);

    /** On tenant profile change. */
    void onTenantProfileChange(TenantProfile tenantProfile, TbQueueCallback callback);

    /** On tenant profile delete. */
    void onTenantProfileDelete(TenantProfile tenantProfile, TbQueueCallback callback);

    /** On tenant change. */
    void onTenantChange(Tenant tenant, TbQueueCallback callback);

    /** On tenant delete. */
    void onTenantDelete(Tenant tenant, TbQueueCallback callback);

    /** On api state change. */
    void onApiStateChange(ApiUsageState apiUsageState, TbQueueCallback callback);

    /** On device updated. */
    void onDeviceUpdated(Device device, Device old);

    /** On device deleted. */
    void onDeviceDeleted(TenantId tenantId, Device device, TbQueueCallback callback);

    /** On device assigned to tenant. */
    void onDeviceAssignedToTenant(TenantId oldTenantId, Device device);

    /** On asset updated. */
    void onAssetUpdated(Asset asset, Asset old);

    /** On asset deleted. */
    void onAssetDeleted(TenantId tenantId, Asset asset, TbQueueCallback callback);

    /** On resource change. */
    void onResourceChange(TbResourceInfo resource, TbQueueCallback callback);

    /** On resource deleted. */
    void onResourceDeleted(TbResourceInfo resource, TbQueueCallback callback);

    /** On edge high priority msg. */
    void onEdgeHighPriorityMsg(EdgeHighPriorityMsg msg);

    /** On edge event update. */
    void onEdgeEventUpdate(EdgeEventUpdateMsg msg);

    /** On edge state change event. */
    void onEdgeStateChangeEvent(ComponentLifecycleMsg msg);

    /** Push edge sync request to edge. */
    void pushEdgeSyncRequestToEdge(ToEdgeSyncRequest request);

    /** Push edge sync response to core. */
    void pushEdgeSyncResponseToCore(FromEdgeSyncResponse response, String requestServiceId);

    /** Push msg to edge. */
    void pushMsgToEdge(TenantId tenantId, EntityId entityId, ToEdgeMsg msg, TbQueueCallback callback);

    /** Send notification msg to edge. */
    void sendNotificationMsgToEdge(TenantId tenantId, EdgeId edgeId, EntityId entityId, String body, EdgeEventType type, EdgeEventActionType action, EdgeId sourceEdgeId);

    /** On customer updated. */
    void onCustomerUpdated(Customer customer, Customer oldCustomer);

    /** On calculated field updated. */
    void onCalculatedFieldUpdated(CalculatedField calculatedField, CalculatedField oldCalculatedField, TbQueueCallback callback);

    /** On calculated field deleted. */
    void onCalculatedFieldDeleted(CalculatedField calculatedField, TbQueueCallback callback);

    /** On relation updated. */
    void onRelationUpdated(TenantId tenantId, EntityRelation entityRelation, TbQueueCallback callback);

    /** On relation deleted. */
    void onRelationDeleted(TenantId tenantId, EntityRelation entityRelation, TbQueueCallback callback);

}
