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
import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.util.TbMsgSource;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.KvEntry;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.Arrays;
import java.util.NoSuchElementException;
/**
 * Abstract base class for node with fetch to rule nodes (entity metadata and related-data fetch nodes).
 */


@Slf4j
public abstract class TbAbstractNodeWithFetchTo<C extends TbAbstractFetchToNodeConfiguration> implements TbNode {

    protected final static String FETCH_TO_PROPERTY_NAME = "fetchTo";

    protected C config;
    protected TbMsgSource fetchTo;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        config = loadNodeConfiguration(configuration);
        if (config.getFetchTo() == null) {
            throw new TbNodeException("FetchTo option can't be null! Allowed values: " + Arrays.toString(TbMsgSource.values()));
        }
        fetchTo = config.getFetchTo();
    }
    /**
     * Loads node configuration.
     *
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @return {@link C}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    protected abstract C loadNodeConfiguration(TbNodeConfiguration configuration) throws TbNodeException;
    /**
     * Checks if entity is present or throw.
     *
     * @param message message ({@link String})
     * @return {@link Function}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected <I extends EntityId> Function<I, I> checkIfEntityIsPresentOrThrow(String message) {
        return id -> {
            if (id == null || id.isNullUid()) {
                throw new NoSuchElementException(message);
            }
            return id;
        };
    }
    /**
     * Returns msg data as object node.
     *
     * @param msg incoming or outgoing rule engine message
     * @return {@link ObjectNode}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected ObjectNode getMsgDataAsObjectNode(TbMsg msg) {
        var msgDataNode = JacksonUtil.toJsonNode(msg.getData());
        if (msgDataNode == null || !msgDataNode.isObject()) {
            throw new IllegalArgumentException("Message body is not an object!");
        }
        return (ObjectNode) msgDataNode;
    }
    /**
     * Enrich message.
     *
     * @param msgData msg data ({@link ObjectNode})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param kvEntry kv entry ({@link KvEntry})
     * @param targetKey target key ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void enrichMessage(ObjectNode msgData, TbMsgMetaData metaData, KvEntry kvEntry, String targetKey) {
        if (TbMsgSource.DATA.equals(fetchTo)) {
            JacksonUtil.addKvEntry(msgData, kvEntry, targetKey);
        } else if (TbMsgSource.METADATA.equals(fetchTo)) {
            metaData.putValue(targetKey, kvEntry.getValueAsString());
        }
    }
    /**
     * Transform message.
     *
     * @param msg incoming or outgoing rule engine message
     * @param msgDataNode msg data node ({@link ObjectNode})
     * @param msgMetaData msg meta data ({@link TbMsgMetaData})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected TbMsg transformMessage(TbMsg msg, ObjectNode msgDataNode, TbMsgMetaData msgMetaData) {
        switch (fetchTo) {
            case DATA:
                return msg.transform()
                        .data(JacksonUtil.toString(msgDataNode))
                        .build();
            case METADATA:
                return msg.transform()
                        .metaData(msgMetaData)
                        .build();
            default:
                log.debug("Unexpected FetchTo value: {}. Allowed values: {}", fetchTo, TbMsgSource.values());
                return msg;
        }
    }
    /**
     * Upgrade rule nodes with old property to use fetch to.
     *
     * @param oldConfiguration previous JSON configuration to upgrade
     * @param oldProperty old property ({@link String})
     * @param ifTrue if true ({@link String})
     * @param ifFalse if false ({@link String})
     * @return {@link TbPair}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    protected TbPair<Boolean, JsonNode> upgradeRuleNodesWithOldPropertyToUseFetchTo(
            JsonNode oldConfiguration,
            String oldProperty,
            String ifTrue,
            String ifFalse
    ) throws TbNodeException {
        var newConfig = (ObjectNode) oldConfiguration;
        if (!newConfig.has(oldProperty)) {
            throw new TbNodeException("property to update: '" + oldProperty + "' doesn't exists in configuration!");
        }
        return upgradeConfigurationToUseFetchTo(oldProperty, ifTrue, ifFalse, newConfig);
    }
    /**
     * Upgrade configuration to use fetch to.
     *
     * @param oldProperty old property ({@link String})
     * @param ifTrue if true ({@link String})
     * @param ifFalse if false ({@link String})
     * @param newConfig new config ({@link ObjectNode})
     * @return {@link TbPair}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    protected TbPair<Boolean, JsonNode> upgradeConfigurationToUseFetchTo(
            String oldProperty, String ifTrue,
            String ifFalse, ObjectNode newConfig
    ) throws TbNodeException {
        var value = newConfig.get(oldProperty).asText();
        if ("true".equals(value)) {
            newConfig.remove(oldProperty);
            newConfig.put(FETCH_TO_PROPERTY_NAME, ifTrue);
            return new TbPair<>(true, newConfig);
        } else if ("false".equals(value)) {
            newConfig.remove(oldProperty);
            newConfig.put(FETCH_TO_PROPERTY_NAME, ifFalse);
            return new TbPair<>(true, newConfig);
        } else {
            throw new TbNodeException("property to update: '" + oldProperty + "' has unexpected value: "
                    + value + ". Allowed values: true or false!");
        }
    }

}
