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
package org.thingsboard.rule.engine.api;

import org.thingsboard.server.common.data.id.RpcId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by ashvayka on 02.04.18.
 */
/**
 * Facade for sending two-way device RPC from rule nodes.
 */

public interface RuleEngineRpcService {
    /**
     * Send rpc reply to device.
     *
     * @param serviceId service id ({@link String})
     * @param sessionId session id ({@link UUID})
     * @param requestId request id
     * @param body body ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    void sendRpcReplyToDevice(String serviceId, UUID sessionId, int requestId, String body);
    /**
     * Send rpc request to device.
     *
     * @param request async service request DTO
     * @param consumer consumer ({@link Consumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void sendRpcRequestToDevice(RuleEngineDeviceRpcRequest request, Consumer<RuleEngineDeviceRpcResponse> consumer);
    /**
     * Send rest api call reply.
     *
     * @param serviceId service id ({@link String})
     * @param requestId request id ({@link UUID})
     * @param msg incoming or outgoing rule engine message
     * @throws Exception if an unexpected error occurs during processing
     */

    void sendRestApiCallReply(String serviceId, UUID requestId, TbMsg msg);
    /**
     * Finds rpc by id.
     *
     * @param tenantId tenant UUID
     * @param id id ({@link RpcId})
     * @return {@link Rpc}
     * @throws Exception if an unexpected error occurs during processing
     */

    Rpc findRpcById(TenantId tenantId, RpcId id);
}
