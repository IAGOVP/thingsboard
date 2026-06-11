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

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.AttributeUpdateNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetAttributeResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.SessionCloseNotificationProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToDeviceRpcRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToServerRpcResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportUpdateCredentialsProto;
import org.thingsboard.server.gen.transport.TransportProtos.UplinkNotificationMsg;

import java.util.Optional;
import java.util.UUID;

/**
 * Callback interface for messages pushed from core to an active transport session (attributes, RPC, OTA chunks).
 */
public interface SessionMsgListener {

    void onGetAttributesResponse(GetAttributeResponseMsg getAttributesResponse);

    /**
     * Handles attribute update.
     *
     * @param sessionId session id ({@link UUID})
     * @param attributeUpdateNotification attribute update notification ({@link AttributeUpdateNotificationMsg})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onAttributeUpdate(UUID sessionId, AttributeUpdateNotificationMsg attributeUpdateNotification);

    /**
     * Handles remote session close command.
     *
     * @param sessionId session id ({@link UUID})
     * @param sessionCloseNotification session close notification ({@link SessionCloseNotificationProto})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onRemoteSessionCloseCommand(UUID sessionId, SessionCloseNotificationProto sessionCloseNotification);

    /**
     * Handles to device rpc request.
     *
     * @param sessionId session id ({@link UUID})
     * @param toDeviceRequest to device request ({@link ToDeviceRpcRequestMsg})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onToDeviceRpcRequest(UUID sessionId, ToDeviceRpcRequestMsg toDeviceRequest);

    /**
     * Handles to server rpc response.
     *
     * @param toServerResponse to server response ({@link ToServerRpcResponseMsg})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onToServerRpcResponse(ToServerRpcResponseMsg toServerResponse);

    /**
     * Handles device deleted.
     *
     * @param deviceId target device identifier
     * @return nothing
     * @throws Exception on processing failure
     */
    void onDeviceDeleted(DeviceId deviceId);

    /**
     * Handles uplink notification.
     *
     * @param notificationMsg notification msg ({@link UplinkNotificationMsg})
     * @return nothing
     * @throws Exception on processing failure
     */
    default void onUplinkNotification(UplinkNotificationMsg notificationMsg){};

    /**
     * Handles to transport update credentials.
     *
     * @param toTransportUpdateCredentials to transport update credentials ({@link ToTransportUpdateCredentialsProto})
     * @return nothing
     * @throws Exception on processing failure
     */
    default void onToTransportUpdateCredentials(ToTransportUpdateCredentialsProto toTransportUpdateCredentials){}
/**
 * Handles device profile update.
 *
 * @param newSessionInfo new session info
 * @param deviceProfile device profile ({@link DeviceProfile})
 * @return nothing
 * @throws Exception on processing failure
 */
    default void onDeviceProfileUpdate(TransportProtos.SessionInfoProto newSessionInfo, DeviceProfile deviceProfile) {}
/**
 * Handles device update.
 *
 * @param sessionInfo session info
 * @param device device ({@link Device})
 * @param deviceProfileOpt device profile opt ({@link Optional})
 * @return nothing
 * @throws Exception on processing failure
 */
    default void onDeviceUpdate(TransportProtos.SessionInfoProto sessionInfo, Device device,
                                Optional<DeviceProfile> deviceProfileOpt) {}
/**
 * Handles resource update.
 *
 * @param resourceUpdateMsgOpt resource update msg opt
 * @return nothing
 * @throws Exception on processing failure
 */
    default void onResourceUpdate(TransportProtos.ResourceUpdateMsg resourceUpdateMsgOpt) {}
/**
 * Handles resource delete.
 *
 * @param resourceUpdateMsgOpt resource update msg opt
 * @return nothing
 * @throws Exception on processing failure
 */
    default void onResourceDelete(TransportProtos.ResourceDeleteMsg resourceUpdateMsgOpt) {}
}
