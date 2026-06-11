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
package org.thingsboard.rule.engine.transform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.EmptyNodeConfiguration;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.queue.RuleEngineException;
import org.thingsboard.server.common.msg.queue.TbMsgCallback;

import java.util.concurrent.ExecutionException;

/**
 * Transformation rule node — <b>split array msg</b>.
 *
 * <p>Split array message into several messages
 * <br>Splits an array message into individual elements, with each element sent as a separate message. 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link EmptyNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/split-array-msg/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/split-array-msg/</a>
 */
@RuleNode(
        type = ComponentType.TRANSFORMATION,
        name = "split array msg",
        configClazz = EmptyNodeConfiguration.class,
        nodeDescription = "Split array message into several messages",
        nodeDetails = "Splits an array message into individual elements, with each element sent as a separate message. " +
                "All outbound messages will have the same type and metadata as the original array message.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        icon = "content_copy",
        configDirective = "tbNodeEmptyConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/split-array-msg/"
)
public class TbSplitArrayMsgNode implements TbNode {
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws ExecutionException if execution exception is thrown during processing
     * @throws InterruptedException if interrupted exception is thrown during processing
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) {}
    /**
     * Processes one incoming {@link org.thingsboard.server.common.msg.TbMsg} and routes the result via {@link TbContext}.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param msg incoming or outgoing rule engine message
     * @throws ExecutionException if execution exception is thrown during processing
     * @throws InterruptedException if interrupted exception is thrown during processing
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) throws ExecutionException, InterruptedException, TbNodeException {
        JsonNode jsonNode = JacksonUtil.toJsonNode(msg.getData());
        if (jsonNode.isArray()) {
            ArrayNode data = (ArrayNode) jsonNode;
            if (data.isEmpty()) {
                ctx.ack(msg);
            } else if (data.size() == 1) {
                ctx.tellSuccess(msg.transform()
                        .data(JacksonUtil.toString(data.get(0)))
                        .build());
            } else {
                TbMsgCallbackWrapper wrapper = new MultipleTbMsgsCallbackWrapper(data.size(), new TbMsgCallback() {
                    @Override
                    public void onSuccess() {
                        ctx.ack(msg);
                    }

                    @Override
                    public void onFailure(RuleEngineException e) {
                        ctx.tellFailure(msg, e);
                    }
                });
                data.forEach(msgNode -> {
                    TbMsg outMsg = msg.transform()
                            .data(JacksonUtil.toString(msgNode))
                            .build();
                    ctx.enqueueForTellNext(outMsg, TbNodeConnectionType.SUCCESS, wrapper::onSuccess, wrapper::onFailure);
                });
            }
        } else {
            ctx.tellFailure(msg, new RuntimeException("Msg data is not a JSON Array!"));
        }
    }

}
