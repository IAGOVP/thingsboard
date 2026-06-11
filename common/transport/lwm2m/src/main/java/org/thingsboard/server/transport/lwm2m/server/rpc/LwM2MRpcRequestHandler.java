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
package org.thingsboard.server.transport.lwm2m.server.rpc;

import org.thingsboard.server.gen.transport.TransportProtos;

/**
 * Maps ThingsBoard device RPC requests to LwM2M downlink operations on registered clients.
 */
public interface LwM2MRpcRequestHandler {

    void onToDeviceRpcRequest(TransportProtos.ToDeviceRpcRequestMsg toDeviceRequest, TransportProtos.SessionInfoProto sessionInfo);

    /**
     * Handles to device rpc response.
     *
     * @param toDeviceRpcResponse to device rpc response
     * @param sessionInfo session info
     * @return nothing
     * @throws Exception on processing failure
     */
    void onToDeviceRpcResponse(TransportProtos.ToDeviceRpcResponseMsg toDeviceRpcResponse, TransportProtos.SessionInfoProto sessionInfo);

    /**
     * Handles to server rpc response.
     *
     * @param toServerResponse to server response
     * @return nothing
     * @throws Exception on processing failure
     */
    void onToServerRpcResponse(TransportProtos.ToServerRpcResponseMsg toServerResponse);


}
