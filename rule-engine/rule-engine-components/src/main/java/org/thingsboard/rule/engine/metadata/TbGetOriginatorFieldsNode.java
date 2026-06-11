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
package org.thingsboard.rule.engine.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.rule.engine.util.TbMsgSource;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.concurrent.ExecutionException;

/**
 * Enrichment rule node — <b>originator fields</b>.
 *
 * <p>Adds message originator fields values into message or message metadata
 * <br>Fetches fields values specified in the mapping. If specified field is not part of originator fields it will be ignored. 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbGetOriginatorFieldsConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/originator-fields/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/originator-fields/</a>
 */
@RuleNode(
        type = ComponentType.ENRICHMENT,
        name = "originator fields",
        configClazz = TbGetOriginatorFieldsConfiguration.class,
        version = 1,
        nodeDescription = "Adds message originator fields values into message or message metadata",
        nodeDetails = "Fetches fields values specified in the mapping. If specified field is not part of originator fields it will be ignored. " +
                "Useful when you need to retrieve originator fields and use them for further message processing.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        configDirective = "tbEnrichmentNodeOriginatorFieldsConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/originator-fields/"
)
public class TbGetOriginatorFieldsNode extends TbAbstractGetMappedDataNode<EntityId, TbGetOriginatorFieldsConfiguration> {

    protected final static String DATA_MAPPING_PROPERTY_NAME = "dataMapping";
    protected static final String OLD_DATA_MAPPING_PROPERTY_NAME = "fieldsMapping";
    /**
     * Loads node configuration.
     *
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @return {@link TbGetOriginatorFieldsConfiguration}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    protected TbGetOriginatorFieldsConfiguration loadNodeConfiguration(TbNodeConfiguration configuration) throws TbNodeException {
        var config = TbNodeUtils.convert(configuration, TbGetOriginatorFieldsConfiguration.class);
        checkIfMappingIsNotEmptyOrElseThrow(config.getDataMapping());
        return config;
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
        var msgDataAsJsonNode = TbMsgSource.DATA.equals(fetchTo) ? getMsgDataAsObjectNode(msg) : null;
        processFieldsData(ctx, msg, msg.getOriginator(), msgDataAsJsonNode, config.isIgnoreNullStrings());
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
        if (fromVersion == 0) {
            var newConfigObjectNode = (ObjectNode) oldConfiguration;
            if (!newConfigObjectNode.has(OLD_DATA_MAPPING_PROPERTY_NAME)) {
                throw new TbNodeException("property to update: '" + OLD_DATA_MAPPING_PROPERTY_NAME + "' doesn't exists in configuration!");
            }
            newConfigObjectNode.set(DATA_MAPPING_PROPERTY_NAME, newConfigObjectNode.get(OLD_DATA_MAPPING_PROPERTY_NAME));
            newConfigObjectNode.remove(OLD_DATA_MAPPING_PROPERTY_NAME);
            newConfigObjectNode.put(FETCH_TO_PROPERTY_NAME, TbMsgSource.METADATA.name());
            return new TbPair<>(true, newConfigObjectNode);
        }
        return new TbPair<>(false, oldConfiguration);
    }

}
