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
package org.thingsboard.rule.engine.flow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.thingsboard.rule.engine.api.EmptyNodeConfiguration;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.msg.TbMsg;

import static org.thingsboard.server.common.data.DataConstants.QUEUE_NAME;

/**
 * Flow rule node — <b>checkpoint</b>.
 *
 * <p>transfers the message to another queue
 * <br>After successful transfer incoming message is automatically acknowledged. Queue name is configurable.
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link EmptyNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/flow/checkpoint/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/flow/checkpoint/</a>
 */
@RuleNode(
        type = ComponentType.FLOW,
        name = "checkpoint",
        configClazz = EmptyNodeConfiguration.class,
        version = 1,
        hasQueueName = true,
        nodeDescription = "transfers the message to another queue",
        nodeDetails = "After successful transfer incoming message is automatically acknowledged. Queue name is configurable.",
        configDirective = "tbNodeEmptyConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/flow/checkpoint/"
)
public class TbCheckpointNode implements TbNode {

    private String queueName;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        queueName = ctx.getQueueName();
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
        ctx.enqueueForTellNext(msg, queueName, TbNodeConnectionType.SUCCESS, () -> ctx.ack(msg), error -> ctx.tellFailure(msg, error));
    }
    /**
     * Upgrades persisted node configuration from an older {@link RuleNode#version()} to the current schema.
     *
     * @param fromVersion configuration schema version stored in the database
     * @param oldConfiguration previous JSON configuration to upgrade
     * @return {@link TbPair}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public TbPair<Boolean, JsonNode> upgrade(int fromVersion, JsonNode oldConfiguration) throws TbNodeException {
        boolean hasChanges = false;
        switch (fromVersion) {
            case 0:
                if (oldConfiguration.has(QUEUE_NAME)) {
                    hasChanges = true;
                    ((ObjectNode) oldConfiguration).remove(QUEUE_NAME);
                }
                break;
            default:
                break;
        }
        return new TbPair<>(hasChanges, oldConfiguration);
    }

}
