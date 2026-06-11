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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
/**
 * Transformation rule node — <b>delete key-value pairs</b>.
 *
 * <p>Deletes key-value pairs from message or message metadata.
 * <br>Deletes key-value pairs from the message or message metadata according to the configured 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbDeleteKeysNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/delete-key-value-pairs/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/delete-key-value-pairs/</a>
 */
@RuleNode(
        type = ComponentType.TRANSFORMATION,
        name = "delete key-value pairs",
        version = 2,
        configClazz = TbDeleteKeysNodeConfiguration.class,
        nodeDescription = "Deletes key-value pairs from message or message metadata.",
        nodeDetails = "Deletes key-value pairs from the message or message metadata according to the configured " +
                "keys and/or regular expressions.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        configDirective = "tbTransformationNodeDeleteKeysConfig",
        icon = "remove_circle",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/transformation/delete-key-value-pairs/"
)
public class TbDeleteKeysNode extends TbAbstractTransformNodeWithTbMsgSource {

    private TbMsgSource deleteFrom;
    private List<Pattern> compiledKeyPatterns;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        var config = TbNodeUtils.convert(configuration, TbDeleteKeysNodeConfiguration.class);
        deleteFrom = config.getDeleteFrom();
        if (deleteFrom == null) {
            throw new TbNodeException("DeleteFrom can't be null! Allowed values: " + Arrays.toString(TbMsgSource.values()));
        }
        compiledKeyPatterns = config.getKeys().stream().map(Pattern::compile).collect(Collectors.toList());
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
        var metaDataCopy = msg.getMetaData().copy();
        var msgDataStr = msg.getData();
        boolean hasNoChanges = false;
        switch (deleteFrom) {
            case METADATA:
                var metaDataMap = metaDataCopy.getData();
                var mdKeysToDelete = metaDataMap.keySet()
                        .stream()
                        .filter(this::matches)
                        .toList();
                mdKeysToDelete.forEach(metaDataMap::remove);
                metaDataCopy = new TbMsgMetaData(metaDataMap);
                hasNoChanges = mdKeysToDelete.isEmpty();
                break;
            case DATA:
                JsonNode dataNode = JacksonUtil.toJsonNode(msgDataStr);
                if (dataNode.isObject()) {
                    var msgDataObject = (ObjectNode) dataNode;
                    var msgKeysToDelete = new ArrayList<String>();
                    dataNode.fieldNames().forEachRemaining(key -> {
                        if (matches(key)) {
                            msgKeysToDelete.add(key);
                        }
                    });
                    msgDataObject.remove(msgKeysToDelete);
                    msgDataStr = JacksonUtil.toString(msgDataObject);
                    hasNoChanges = msgKeysToDelete.isEmpty();
                }
                break;
            default:
                log.debug("Unexpected DeleteFrom value: {}. Allowed values: {}", deleteFrom, TbMsgSource.values());
        }
        ctx.tellSuccess(hasNoChanges ? msg : msg.transform()
                .metaData(metaDataCopy)
                .data(msgDataStr)
                .build());
    }
    /**
     * Returns new key for upgrade from version zero.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getNewKeyForUpgradeFromVersionZero() {
        return "deleteFrom";
    }
    /**
     * Returns key to upgrade from version one.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getKeyToUpgradeFromVersionOne() {
        return "dataToFetch";
    }

    boolean matches(String key) {
        return compiledKeyPatterns.stream().anyMatch(pattern -> pattern.matcher(key).matches());
    }

}
