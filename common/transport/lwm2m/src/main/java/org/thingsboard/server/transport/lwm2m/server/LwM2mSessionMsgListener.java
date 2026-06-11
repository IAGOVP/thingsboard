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
package org.thingsboard.server.transport.lwm2m.server;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.transport.SessionMsgListener;
import org.thingsboard.server.common.transport.TransportService;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.AttributeUpdateNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetAttributeResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.SessionCloseNotificationProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToDeviceRpcRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToServerRpcResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportUpdateCredentialsProto;
import org.thingsboard.server.transport.lwm2m.server.attributes.LwM2MAttributesService;
import org.thingsboard.server.transport.lwm2m.server.rpc.LwM2MRpcRequestHandler;
import org.thingsboard.server.transport.lwm2m.server.uplink.LwM2mUplinkMsgHandler;

import java.util.Optional;
import java.util.UUID;

/**
 * Lw m2m session msg listener.
 */
@Slf4j
@RequiredArgsConstructor
public class LwM2mSessionMsgListener implements GenericFutureListener<Future<? super Void>>, SessionMsgListener {
    private final LwM2mUplinkMsgHandler handler;
    private final LwM2MAttributesService attributesService;
    private final LwM2MRpcRequestHandler rpcHandler;
    private final TransportProtos.SessionInfoProto sessionInfo;
    private final TransportService transportService;
    /**
     * Handles get attributes response.
     *
     * @param getAttributesResponse get attributes response ({@link GetAttributeResponseMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onGetAttributesResponse(GetAttributeResponseMsg getAttributesResponse) {
        this.attributesService.onGetAttributesResponse(getAttributesResponse, this.sessionInfo);
    }
    /**
     * Handles attribute update.
     *
     * @param sessionId session id ({@link UUID})
     * @param attributeUpdateNotification attribute update notification ({@link AttributeUpdateNotificationMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onAttributeUpdate(UUID sessionId, AttributeUpdateNotificationMsg attributeUpdateNotification) {
        log.trace("[{}] Received attributes update notification to device", sessionId);
        this.attributesService.onAttributesUpdate(attributeUpdateNotification, this.sessionInfo);
    }
    /**
     * Handles remote session close command.
     *
     * @param sessionId session id ({@link UUID})
     * @param sessionCloseNotification session close notification ({@link SessionCloseNotificationProto})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onRemoteSessionCloseCommand(UUID sessionId, SessionCloseNotificationProto sessionCloseNotification) {
        log.trace("[{}] Received the remote command to close the session: {}", sessionId, sessionCloseNotification.getMessage());
    }
    /**
     * Handles to transport update credentials.
     *
     * @param updateCredentials update credentials ({@link ToTransportUpdateCredentialsProto})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onToTransportUpdateCredentials(ToTransportUpdateCredentialsProto updateCredentials) {
        this.handler.onToTransportUpdateCredentials(sessionInfo, updateCredentials);
    }
    /**
     * Handles device profile update.
     *
     * @param sessionInfo session info
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onDeviceProfileUpdate(TransportProtos.SessionInfoProto sessionInfo, DeviceProfile deviceProfile) {
        this.handler.onDeviceProfileUpdate(sessionInfo, deviceProfile);
    }
    /**
     * Handles device update.
     *
     * @param sessionInfo session info
     * @param device device ({@link Device})
     * @param deviceProfileOpt device profile opt ({@link Optional})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onDeviceUpdate(TransportProtos.SessionInfoProto sessionInfo, Device device, Optional<DeviceProfile> deviceProfileOpt) {
        this.handler.onDeviceUpdate(sessionInfo, device, deviceProfileOpt);
    }
    /**
     * Handles to device rpc request.
     *
     * @param sessionId session id ({@link UUID})
     * @param toDeviceRequest to device request ({@link ToDeviceRpcRequestMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onToDeviceRpcRequest(UUID sessionId, ToDeviceRpcRequestMsg toDeviceRequest) {
        log.trace("[{}] Received RPC command to device", sessionId);
        this.rpcHandler.onToDeviceRpcRequest(toDeviceRequest, this.sessionInfo);
    }
    /**
     * Handles to server rpc response.
     *
     * @param toServerResponse to server response ({@link ToServerRpcResponseMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onToServerRpcResponse(ToServerRpcResponseMsg toServerResponse) {
        this.rpcHandler.onToServerRpcResponse(toServerResponse);
    }
    /**
     * Operation complete.
     *
     * @param future future ({@link Future})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {
        log.info("[{}]  operationComplete", future);
    }
    /**
     * Handles resource update.
     *
     * @param resourceUpdateMsgOpt resource update msg opt
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onResourceUpdate(TransportProtos.ResourceUpdateMsg resourceUpdateMsgOpt) {
        if (ResourceType.LWM2M_MODEL.name().equals(resourceUpdateMsgOpt.getResourceType())) {
            this.handler.onResourceUpdate(resourceUpdateMsgOpt);
        }
    }
    /**
     * Handles resource delete.
     *
     * @param resourceDeleteMsgOpt resource delete msg opt
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onResourceDelete(TransportProtos.ResourceDeleteMsg resourceDeleteMsgOpt) {
        if (ResourceType.LWM2M_MODEL.name().equals(resourceDeleteMsgOpt.getResourceType())) {
            this.handler.onResourceDelete(resourceDeleteMsgOpt);
        }
    }
    /**
     * Handles device deleted.
     *
     * @param deviceId target device identifier
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onDeviceDeleted(DeviceId deviceId) {
        log.trace("[{}] Device on delete", deviceId);
        this.handler.onDeviceDelete(deviceId);
    }
}
