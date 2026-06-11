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
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

/**
 * Filter rule node — <b>entity type filter</b>.
 *
 * <p>Filter incoming messages by the type of message originator entity
 * <br>Checks that the entity type of the incoming message originator matches one of the values specified in the filter.  
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbOriginatorTypeFilterNodeConfiguration}.
 * <br>Output relations: {@code TbNodeConnectionType.TRUE, TbNodeConnectionType.FALSE}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/entity-type-filter/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/entity-type-filter/</a>
 */
@RuleNode(
        type = ComponentType.FILTER,
        name = "entity type filter",
        configClazz = TbOriginatorTypeFilterNodeConfiguration.class,
        relationTypes = {TbNodeConnectionType.TRUE, TbNodeConnectionType.FALSE},
        nodeDescription = "Filter incoming messages by the type of message originator entity",
        nodeDetails = "Checks that the entity type of the incoming message originator matches one of the values specified in the filter.<br><br>" +
                "Output connections: <code>True</code>, <code>False</code>, <code>Failure</code>",
        configDirective = "tbFilterNodeOriginatorTypeConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/filter/entity-type-filter/"
)
public class TbOriginatorTypeFilterNode implements TbNode {

    private TbOriginatorTypeFilterNodeConfiguration config;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        config = TbNodeUtils.convert(configuration, TbOriginatorTypeFilterNodeConfiguration.class);
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
        EntityType originatorType = msg.getOriginator().getEntityType();
        ctx.tellNext(msg, config.getOriginatorTypes().contains(originatorType) ? TbNodeConnectionType.TRUE : TbNodeConnectionType.FALSE);
    }

}
