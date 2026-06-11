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
package org.thingsboard.server.transport.coap.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.transport.SessionMsgListener;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.coap.client.TbCoapClientState;
import org.thingsboard.server.transport.coap.client.TbCoapContentFormatUtil;
import org.thingsboard.server.transport.coap.client.TbCoapObservationState;

import java.util.UUID;

/**
 * Abstract sync session callback.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractSyncSessionCallback implements SessionMsgListener {

    protected final TbCoapClientState state;
    protected final CoapExchange exchange;
    protected final Request request;
    /**
     * Handles get attributes response.
     *
     * @param getAttributesResponse get attributes response
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onGetAttributesResponse(TransportProtos.GetAttributeResponseMsg getAttributesResponse) {
        logUnsupportedCommandMessage(getAttributesResponse);
    }
    /**
     * Handles attribute update.
     *
     * @param sessionId session id ({@link UUID})
     * @param attributeUpdateNotification attribute update notification
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onAttributeUpdate(UUID sessionId, TransportProtos.AttributeUpdateNotificationMsg attributeUpdateNotification) {
        logUnsupportedCommandMessage(attributeUpdateNotification);
    }
    /**
     * Handles remote session close command.
     *
     * @param sessionId session id ({@link UUID})
     * @param sessionCloseNotification session close notification
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onRemoteSessionCloseCommand(UUID sessionId, TransportProtos.SessionCloseNotificationProto sessionCloseNotification) {

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

    }
    /**
     * Handles to device rpc request.
     *
     * @param sessionId session id ({@link UUID})
     * @param toDeviceRequest to device request
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onToDeviceRpcRequest(UUID sessionId, TransportProtos.ToDeviceRpcRequestMsg toDeviceRequest) {
        logUnsupportedCommandMessage(toDeviceRequest);
    }
    /**
     * Handles to server rpc response.
     *
     * @param toServerResponse to server response
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onToServerRpcResponse(TransportProtos.ToServerRpcResponseMsg toServerResponse) {
        logUnsupportedCommandMessage(toServerResponse);
    }

    private void logUnsupportedCommandMessage(Object update) {
        log.trace("[{}] Ignore unsupported update: {}", state.getDeviceId(), update);
    }
    /**
     * Is con request.
     *
     * @param state state ({@link TbCoapObservationState})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public static boolean isConRequest(TbCoapObservationState state) {
        if (state != null) {
            return state.getExchange().advanced().getRequest().isConfirmable();
        } else {
            return false;
        }
    }
    /**
     * Is multicast request.
     *
     * @param state state ({@link TbCoapObservationState})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public static boolean isMulticastRequest(TbCoapObservationState state) {
        if (state != null) {
            return state.getExchange().advanced().getRequest().isMulticast();
        }
        return false;
    }
    /**
     * Respond.
     *
     * @param response response ({@link Response})
     * @return nothing
     * @throws Exception on processing failure
     */
    protected void respond(Response response) {
        response.getOptions().setContentFormat(TbCoapContentFormatUtil.getContentFormat(exchange.getRequestOptions().getContentFormat(), state.getContentFormat()));
        response.setConfirmable(exchange.advanced().getRequest().isConfirmable());
        exchange.respond(response);
    }

}
