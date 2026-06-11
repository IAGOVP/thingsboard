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
package org.thingsboard.rule.engine.filter;

import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

/**
 * Filter rule node — <b>message type filter</b>.
 *
 * <p>Filter incoming messages by Message Type
 * <br>If incoming message type is expected - send Message via True chain, otherwise False chain is used.  
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbMsgTypeFilterNodeConfiguration}.
 * <br>Output relations: {@code TbNodeConnectionType.TRUE, TbNodeConnectionType.FALSE}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/message-type-filter/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/message-type-filter/</a>
 */
@RuleNode(
        type = ComponentType.FILTER,
        name = "message type filter",
        configClazz = TbMsgTypeFilterNodeConfiguration.class,
        relationTypes = {TbNodeConnectionType.TRUE, TbNodeConnectionType.FALSE},
        nodeDescription = "Filter incoming messages by Message Type",
        nodeDetails = "If incoming message type is expected - send Message via <b>True</b> chain, otherwise <b>False</b> chain is used.<br><br>" +
                "Output connections: <code>True</code>, <code>False</code>, <code>Failure</code>",
        configDirective = "tbFilterNodeMessageTypeConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/message-type-filter/"
)
public class TbMsgTypeFilterNode implements TbNode {

    private TbMsgTypeFilterNodeConfiguration config;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        config = TbNodeUtils.convert(configuration, TbMsgTypeFilterNodeConfiguration.class);
    }
    /**
     * Processes one incoming {@link org.thingsboard.server.common.msg.TbMsg} and routes the result via {@link TbContext}.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param msg incoming or outgoing rule engine message
     * @throws TbNodeException if configuration or processing fails
     */

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        ctx.tellNext(msg, config.getMessageTypes().contains(msg.getType()) ? TbNodeConnectionType.TRUE : TbNodeConnectionType.FALSE);
    }

}
