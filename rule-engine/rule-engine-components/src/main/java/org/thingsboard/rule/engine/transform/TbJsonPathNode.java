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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.concurrent.ExecutionException;

/**
 * Transformation rule node — <b>json path</b>.
 *
 * <p>Transforms incoming message body using JSONPath expression.
 * <br>JSONPath expression specifies a path to an element or a set of elements in a JSON structure.  
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbJsonPathNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/json-path/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/json-path/</a>
 */
@RuleNode(
        type = ComponentType.TRANSFORMATION,
        name = "json path",
        configClazz = TbJsonPathNodeConfiguration.class,
        nodeDescription = "Transforms incoming message body using JSONPath expression.",
        nodeDetails = "JSONPath expression specifies a path to an element or a set of elements in a JSON structure.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        icon = "functions",
        configDirective = "tbTransformationNodeJsonPathConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/json-path/"
)
public class TbJsonPathNode implements TbNode {

    private Configuration configurationJsonPath;
    private JsonPath jsonPath;
    private String jsonPathValue;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        var config = TbNodeUtils.convert(configuration, TbJsonPathNodeConfiguration.class);
        jsonPathValue = config.getJsonPath();
        if (!TbJsonPathNodeConfiguration.DEFAULT_JSON_PATH.equals(jsonPathValue)) {
            configurationJsonPath = Configuration.builder()
                    .jsonProvider(new JacksonJsonNodeJsonProvider())
                    .build();
            jsonPath = JsonPath.compile(config.getJsonPath());
        }
    }
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
        if (!TbJsonPathNodeConfiguration.DEFAULT_JSON_PATH.equals(jsonPathValue)) {
            try {
                Object jsonPathData = jsonPath.read(msg.getData(), configurationJsonPath);
                ctx.tellSuccess(msg.transform()
                        .data(JacksonUtil.toString(jsonPathData))
                        .build());
            } catch (PathNotFoundException e) {
                ctx.tellFailure(msg, e);
            }
        } else {
            ctx.tellSuccess(msg);
        }
    }

}
