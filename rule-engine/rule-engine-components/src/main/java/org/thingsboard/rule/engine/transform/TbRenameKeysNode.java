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
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.rule.engine.util.TbMsgSource;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
/**
 * Transformation rule node — <b>rename keys</b>.
 *
 * <p>Renames message or message metadata keys.
 * <br>Renames keys in the message or message metadata according to the provided mapping. 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbRenameKeysNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/rename-keys/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/rename-keys/</a>
 */
@RuleNode(
        type = ComponentType.TRANSFORMATION,
        name = "rename keys",
        version = 2,
        configClazz = TbRenameKeysNodeConfiguration.class,
        nodeDescription = "Renames message or message metadata keys.",
        nodeDetails = "Renames keys in the message or message metadata according to the provided mapping. " +
                "If key to rename doesn't exist in the specified source (message or message metadata) it will be ignored.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        configDirective = "tbTransformationNodeRenameKeysConfig",
        icon = "find_replace",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/rename-keys/"
)
public class TbRenameKeysNode extends TbAbstractTransformNodeWithTbMsgSource {

    private Map<String, String> renameKeysMapping;
    private TbMsgSource renameIn;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        var config = TbNodeUtils.convert(configuration, TbRenameKeysNodeConfiguration.class);
        renameIn = config.getRenameIn();
        renameKeysMapping = config.getRenameKeysMapping();
        if (renameIn == null) {
            throw new TbNodeException("RenameIn can't be null! Allowed values: " + Arrays.toString(TbMsgSource.values()));
        }
        if (renameKeysMapping == null || renameKeysMapping.isEmpty()) {
            throw new TbNodeException("At least one mapping entry should be specified!");
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
        TbMsgMetaData metaDataCopy = msg.getMetaData().copy();
        String data = msg.getData();
        boolean msgChanged = false;
        switch (renameIn) {
            case METADATA:
                Map<String, String> metaDataMap = metaDataCopy.getData();
                for (Map.Entry<String, String> entry : renameKeysMapping.entrySet()) {
                    String currentKeyName = entry.getKey();
                    String newKeyName = entry.getValue();
                    if (metaDataMap.containsKey(currentKeyName)) {
                        msgChanged = true;
                        String value = metaDataMap.get(currentKeyName);
                        metaDataMap.put(newKeyName, value);
                        metaDataMap.remove(currentKeyName);
                    }
                }
                metaDataCopy = new TbMsgMetaData(metaDataMap);
                break;
            case DATA:
                JsonNode dataNode = JacksonUtil.toJsonNode(data);
                if (dataNode.isObject()) {
                    ObjectNode msgData = (ObjectNode) dataNode;
                    for (Map.Entry<String, String> entry : renameKeysMapping.entrySet()) {
                        String currentKeyName = entry.getKey();
                        String newKeyName = entry.getValue();
                        if (msgData.has(currentKeyName)) {
                            msgChanged = true;
                            JsonNode value = msgData.get(currentKeyName);
                            msgData.set(newKeyName, value);
                            msgData.remove(currentKeyName);
                        }
                    }
                    data = JacksonUtil.toString(msgData);
                }
                break;
            default:
                log.debug("Unexpected RenameIn value: {}. Allowed values: {}", renameIn, TbMsgSource.values());
        }
        ctx.tellSuccess(msgChanged ? msg.transform()
                .metaData(metaDataCopy)
                .data(data)
                .build() : msg);
    }
    /**
     * Returns new key for upgrade from version zero.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getNewKeyForUpgradeFromVersionZero() {
        return "renameIn";
    }
    /**
     * Returns key to upgrade from version one.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getKeyToUpgradeFromVersionOne() {
        return FROM_METADATA_PROPERTY;
    }

}
