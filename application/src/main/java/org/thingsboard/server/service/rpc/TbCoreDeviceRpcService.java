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
package org.thingsboard.server.service.rpc;

import org.thingsboard.server.common.msg.rpc.FromDeviceRpcResponse;
import org.thingsboard.server.common.msg.rpc.RemoveRpcActorMsg;
import org.thingsboard.server.common.msg.rpc.ToDeviceRpcRequest;
import org.thingsboard.server.common.msg.rpc.ToDeviceRpcRequestActorMsg;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.function.Consumer;

/**
 * Handles REST API calls that contain RPC requests to Device.
 */
public interface TbCoreDeviceRpcService {

    
    /**
     * Processes rest api rpc request.
     *
     * @param request request payload with operation parameters
     * @param responseConsumer response consumer ({@link Consumer})
     * @param currentUser current user ({@link SecurityUser})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void processRestApiRpcRequest(ToDeviceRpcRequest request, Consumer<FromDeviceRpcResponse> responseConsumer, SecurityUser currentUser);

    
    /**
     * Processes rpc response from rule engine.
     *
     * @param response response ({@link FromDeviceRpcResponse})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void processRpcResponseFromRuleEngine(FromDeviceRpcResponse response);

    
    /**
     * Forward rpc request to device actor.
     *
     * @param request request payload with operation parameters
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void forwardRpcRequestToDeviceActor(ToDeviceRpcRequestActorMsg request);

    
    /**
     * Processes rpc response from device actor.
     *
     * @param response response ({@link FromDeviceRpcResponse})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void processRpcResponseFromDeviceActor(FromDeviceRpcResponse response);

    /**
     * Processes remove rpc.
     *
     * @param removeRpcMsg remove rpc msg ({@link RemoveRpcActorMsg})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void processRemoveRpc(RemoveRpcActorMsg removeRpcMsg);

}
