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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.ScriptEngine;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.script.ScriptLanguage;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.Set;

/**
 * Filter rule node — <b>switch</b>.
 *
 * <p>Routes incoming message to one OR multiple output connections.
 * <br>Node executes configured TBEL(recommended) or JavaScript function that returns array of strings (connection names). 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbJsSwitchNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/switch/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/switch/</a>
 */
@RuleNode(
        type = ComponentType.FILTER,
        name = "switch", customRelations = true,
        relationTypes = {},
        configClazz = TbJsSwitchNodeConfiguration.class,
        nodeDescription = "Routes incoming message to one OR multiple output connections.",
        nodeDetails = "Node executes configured TBEL(recommended) or JavaScript function that returns array of strings (connection names). " +
                "If Array is empty - message not routed to next Node. " +
                "Message payload can be accessed via <code>msg</code> property. For example <code>msg.temperature < 10;</code><br/>" +
                "Message metadata can be accessed via <code>metadata</code> property. For example <code>metadata.customerName === 'John';</code><br/>" +
                "Message type can be accessed via <code>msgType</code> property.<br><br>" +
                "Output connections: <i>Custom connection(s) defined by switch node</i> or <code>Failure</code>",
        configDirective = "tbFilterNodeSwitchConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/switch/"
)
public class TbJsSwitchNode implements TbNode {

    private ScriptEngine scriptEngine;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        var config = TbNodeUtils.convert(configuration, TbJsSwitchNodeConfiguration.class);
        scriptEngine = ctx.createScriptEngine(config.getScriptLang(), ScriptLanguage.TBEL.equals(config.getScriptLang()) ? config.getTbelScript() : config.getJsScript());
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
        Futures.addCallback(scriptEngine.executeSwitchAsync(msg), new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Set<String> result) {
                processSwitch(ctx, msg, result);
            }

            @Override
            public void onFailure(Throwable t) {
                ctx.tellFailure(msg, t);
            }
        }, MoreExecutors.directExecutor()); //usually runs in a callbackExecutor
    }

    private void processSwitch(TbContext ctx, TbMsg msg, Set<String> nextRelations) {
        ctx.tellNext(msg, nextRelations);
    }
    /**
     * Releases resources held by the node (script engines, clients, thread pools).
     *
     */

    @Override
    public void destroy() {
        if (scriptEngine != null) {
            scriptEngine.destroy();
        }
    }

}
