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

import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.plugin.ComponentLifecycleEvent;
import org.thingsboard.server.common.data.rpc.RpcStatus;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.common.transport.auth.GetOrCreateDeviceFromGatewayResponse;
import org.thingsboard.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import org.thingsboard.server.common.transport.service.SessionMetaData;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.ClaimDeviceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetAttributeRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetDeviceCredentialsRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetDeviceCredentialsResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetDeviceRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetDeviceResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetEntityProfileRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetEntityProfileResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetOrCreateDeviceFromGatewayRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetOtaPackageRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetOtaPackageResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetResourceRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetResourceResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetSnmpDevicesRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetSnmpDevicesResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.LwM2MRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.LwM2MResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.PostAttributeMsg;
import org.thingsboard.server.gen.transport.TransportProtos.PostTelemetryMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ProvisionDeviceRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ProvisionDeviceResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.SessionEventMsg;
import org.thingsboard.server.gen.transport.TransportProtos.SessionInfoProto;
import org.thingsboard.server.gen.transport.TransportProtos.SubscribeToAttributeUpdatesMsg;
import org.thingsboard.server.gen.transport.TransportProtos.SubscribeToRPCMsg;
import org.thingsboard.server.gen.transport.TransportProtos.SubscriptionInfoProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToDeviceRpcRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToDeviceRpcResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToServerRpcRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.TransportToDeviceActorMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ValidateBasicMqttCredRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ValidateDeviceLwM2MCredentialsRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ValidateDeviceTokenRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ValidateDeviceX509CertRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ValidateOrCreateDeviceX509CertRequestMsg;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bridge API from <strong>transport microservices</strong> (MQTT, HTTP, …) to the ThingsBoard core.
 *
 * <p>Validates device credentials, loads profiles, posts telemetry/attributes to queues, and handles
 * RPC sessions. Request/response types are protobuf ({@code org.thingsboard.server.gen.transport}).
 *
 * <p>Not used by REST controllers — see {@code common/DEVICE_PROTOCOLS.md}.
 */
public interface TransportService {

    GetEntityProfileResponseMsg getEntityProfile(GetEntityProfileRequestMsg msg);

    /**
     * Returns queue routing info.
     *
     * @param msg msg
     * @return {@link List}
     * @throws Exception on processing failure
     */
    List<TransportProtos.GetQueueRoutingInfoResponseMsg> getQueueRoutingInfo(TransportProtos.GetAllQueueRoutingInfoRequestMsg msg);

    /**
     * Returns resource.
     *
     * @param msg msg ({@link GetResourceRequestMsg})
     * @return {@link GetResourceResponseMsg}
     * @throws Exception on processing failure
     */
    GetResourceResponseMsg getResource(GetResourceRequestMsg msg);

    /**
     * Returns snmp devices ids.
     *
     * @param requestMsg request msg ({@link GetSnmpDevicesRequestMsg})
     * @return {@link GetSnmpDevicesResponseMsg}
     * @throws Exception on processing failure
     */
    GetSnmpDevicesResponseMsg getSnmpDevicesIds(GetSnmpDevicesRequestMsg requestMsg);

    /**
     * Returns device.
     *
     * @param requestMsg request msg ({@link GetDeviceRequestMsg})
     * @return {@link GetDeviceResponseMsg}
     * @throws Exception on processing failure
     */
    GetDeviceResponseMsg getDevice(GetDeviceRequestMsg requestMsg);

    /**
     * Returns device credentials.
     *
     * @param requestMsg request msg ({@link GetDeviceCredentialsRequestMsg})
     * @return {@link GetDeviceCredentialsResponseMsg}
     * @throws Exception on processing failure
     */
    GetDeviceCredentialsResponseMsg getDeviceCredentials(GetDeviceCredentialsRequestMsg requestMsg);

    /**
     * Processes the requested data.
     *
     * @param transportType transport type ({@link DeviceTransportType})
     * @param msg msg ({@link ValidateDeviceTokenRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(DeviceTransportType transportType, ValidateDeviceTokenRequestMsg msg,
                 TransportServiceCallback<ValidateDeviceCredentialsResponse> callback);

    /**
     * Processes the requested data.
     *
     * @param transportType transport type ({@link DeviceTransportType})
     * @param msg msg ({@link ValidateBasicMqttCredRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(DeviceTransportType transportType, ValidateBasicMqttCredRequestMsg msg,
                 TransportServiceCallback<ValidateDeviceCredentialsResponse> callback);

    /**
     * Processes the requested data.
     *
     * @param transportType transport type ({@link DeviceTransportType})
     * @param msg msg ({@link ValidateDeviceX509CertRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(DeviceTransportType transportType, ValidateDeviceX509CertRequestMsg msg,
                 TransportServiceCallback<ValidateDeviceCredentialsResponse> callback);

    /**
     * Processes the requested data.
     *
     * @param transportType transport type ({@link DeviceTransportType})
     * @param msg msg ({@link ValidateOrCreateDeviceX509CertRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(DeviceTransportType transportType, ValidateOrCreateDeviceX509CertRequestMsg msg,
                 TransportServiceCallback<ValidateDeviceCredentialsResponse> callback);

    /**
     * Processes the requested data.
     *
     * @param msg msg ({@link ValidateDeviceLwM2MCredentialsRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(ValidateDeviceLwM2MCredentialsRequestMsg msg,
                 TransportServiceCallback<ValidateDeviceCredentialsResponse> callback);

    /**
     * Processes the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param msg msg ({@link GetOrCreateDeviceFromGatewayRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(TenantId tenantId, GetOrCreateDeviceFromGatewayRequestMsg msg,
                 TransportServiceCallback<GetOrCreateDeviceFromGatewayResponse> callback);

    /**
     * Processes the requested data.
     *
     * @param msg msg ({@link ProvisionDeviceRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(ProvisionDeviceRequestMsg msg,
                 TransportServiceCallback<ProvisionDeviceResponseMsg> callback);

    /**
     * Handles profile update.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onProfileUpdate(DeviceProfile deviceProfile);

    /**
     * Processes the requested data.
     *
     * @param msg msg ({@link LwM2MRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(LwM2MRequestMsg msg,
                 TransportServiceCallback<LwM2MResponseMsg> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link SessionEventMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, SessionEventMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link PostTelemetryMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, PostTelemetryMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link PostTelemetryMsg})
     * @param md md ({@link TbMsgMetaData})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, PostTelemetryMsg msg, TbMsgMetaData md, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link PostAttributeMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, PostAttributeMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link PostAttributeMsg})
     * @param md md ({@link TbMsgMetaData})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, PostAttributeMsg msg, TbMsgMetaData md, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link GetAttributeRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, GetAttributeRequestMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link SubscribeToAttributeUpdatesMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, SubscribeToAttributeUpdatesMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link SubscribeToRPCMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, SubscribeToRPCMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link ToDeviceRpcResponseMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, ToDeviceRpcResponseMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link ToServerRpcRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, ToServerRpcRequestMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link ToDeviceRpcRequestMsg})
     * @param rpcStatus rpc status ({@link RpcStatus})
     * @param reportActivity report activity
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, ToDeviceRpcRequestMsg msg, RpcStatus rpcStatus, boolean reportActivity, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link ToDeviceRpcRequestMsg})
     * @param rpcStatus rpc status ({@link RpcStatus})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, ToDeviceRpcRequestMsg msg, RpcStatus rpcStatus, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link SubscriptionInfoProto})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, SubscriptionInfoProto msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link ClaimDeviceMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfo, ClaimDeviceMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param msg msg ({@link TransportToDeviceActorMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(TransportToDeviceActorMsg msg, TransportServiceCallback<Void> callback);

    /**
     * Processes the requested data.
     *
     * @param sessionInfoProto session info proto ({@link SessionInfoProto})
     * @param msg msg ({@link GetOtaPackageRequestMsg})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception on processing failure
     */
    void process(SessionInfoProto sessionInfoProto, GetOtaPackageRequestMsg msg, TransportServiceCallback<GetOtaPackageResponseMsg> callback);

    /**
     * Register async session.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param listener listener ({@link SessionMsgListener})
     * @return {@link SessionMetaData}
     * @throws Exception on processing failure
     */
    SessionMetaData registerAsyncSession(SessionInfoProto sessionInfo, SessionMsgListener listener);

    /**
     * Register sync session.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param listener listener ({@link SessionMsgListener})
     * @param timeout timeout
     * @return {@link SessionMetaData}
     * @throws Exception on processing failure
     */
    SessionMetaData registerSyncSession(SessionInfoProto sessionInfo, SessionMsgListener listener, long timeout);

    /**
     * Record activity.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @return nothing
     * @throws Exception on processing failure
     */
    void recordActivity(SessionInfoProto sessionInfo);

    /**
     * Lifecycle event.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param eventType event type ({@link ComponentLifecycleEvent})
     * @param success success
     * @param error error ({@link Throwable})
     * @return nothing
     * @throws Exception on processing failure
     */
    void lifecycleEvent(TenantId tenantId, DeviceId deviceId, ComponentLifecycleEvent eventType, boolean success, Throwable error);

    /**
     * Error event.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param method method ({@link String})
     * @param error error ({@link Throwable})
     * @return nothing
     * @throws Exception on processing failure
     */
    void errorEvent(TenantId tenantId, DeviceId deviceId, String method, Throwable error);

    /**
     * Deregister session.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @return nothing
     * @throws Exception on processing failure
     */
    void deregisterSession(SessionInfoProto sessionInfo);

    /**
     * Log.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param msg msg ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void log(SessionInfoProto sessionInfo, String msg);

    /**
     * Notify about uplink.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @param build build
     * @param empty empty ({@link TransportServiceCallback})
     * @return nothing
     * @throws Exception on processing failure
     */
    void notifyAboutUplink(SessionInfoProto sessionInfo, TransportProtos.UplinkNotificationMsg build, TransportServiceCallback<Void> empty);

    /**
     * Returns callback executor.
     *
     * @return {@link ExecutorService}
     * @throws Exception on processing failure
     */
    ExecutorService getCallbackExecutor();

    /**
     * Has session.
     *
     * @param sessionInfo session info ({@link SessionInfoProto})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean hasSession(SessionInfoProto sessionInfo);

    /**
     * Creates gauge stats.
     *
     * @param statsName stats name ({@link String})
     * @param number number ({@link AtomicInteger})
     * @param tags tags
     * @return nothing
     * @throws Exception on processing failure
     */
    void createGaugeStats(String statsName, AtomicInteger number, String... tags);

}
