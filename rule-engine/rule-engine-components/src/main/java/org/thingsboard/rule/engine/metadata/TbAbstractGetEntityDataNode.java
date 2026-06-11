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
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.util.TbMsgSource;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.msg.TbMsg;

import static org.thingsboard.common.util.DonAsynchron.withCallback;
/**
 * Abstract base class for get entity data node rule nodes (entity metadata and related-data fetch nodes).
 */


@Slf4j
public abstract class TbAbstractGetEntityDataNode<T extends EntityId> extends TbAbstractGetMappedDataNode<T, TbGetEntityDataNodeConfiguration> {

    private final static String DATA_TO_FETCH_PROPERTY_NAME = "dataToFetch";
    private static final String OLD_DATA_TO_FETCH_PROPERTY_NAME = "telemetry";
    private final static String DATA_MAPPING_PROPERTY_NAME = "dataMapping";
    private static final String OLD_DATA_MAPPING_PROPERTY_NAME = "attrMapping";

    private static final String DATA_TO_FETCH_VALIDATION_MSG = "DataToFetch property has invalid value: %s." +
            " Only ATTRIBUTES and LATEST_TELEMETRY values supported!";
    /**
     * Processes one incoming {@link org.thingsboard.server.common.msg.TbMsg} and routes the result via {@link TbContext}.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param msg incoming or outgoing rule engine message
     * @throws TbNodeException if configuration or processing fails
     */

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        var msgDataAsObjectNode = TbMsgSource.DATA.equals(fetchTo) ? getMsgDataAsObjectNode(msg) : null;
        withCallback(findEntityAsync(ctx, msg.getOriginator()),
                entityId -> processDataAndTell(ctx, msg, entityId, msgDataAsObjectNode),
                t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
    }
    /**
     * Finds entity async.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param originator message originator entity id
     * @return future completing with {@link T}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    protected abstract ListenableFuture<T> findEntityAsync(TbContext ctx, EntityId originator);
    /**
     * Checks data to fetch supported or else throw.
     *
     * @param dataToFetch data to fetch ({@link DataToFetch})
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    protected void checkDataToFetchSupportedOrElseThrow(DataToFetch dataToFetch) throws TbNodeException {
        if (dataToFetch == null || dataToFetch.equals(DataToFetch.FIELDS)) {
            throw new TbNodeException(String.format(DATA_TO_FETCH_VALIDATION_MSG, dataToFetch));
        }
    }
    /**
     * Processes data and tell.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param msg incoming or outgoing rule engine message
     * @param entityId target entity identifier
     * @param msgDataAsJsonNode msg data as json node ({@link ObjectNode})
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void processDataAndTell(TbContext ctx, TbMsg msg, T entityId, ObjectNode msgDataAsJsonNode) {
        DataToFetch dataToFetch = config.getDataToFetch();
        switch (dataToFetch) {
            case ATTRIBUTES -> processAttributesKvEntryData(ctx, msg, entityId, msgDataAsJsonNode);
            case LATEST_TELEMETRY -> processTsKvEntryData(ctx, msg, entityId, msgDataAsJsonNode);
            case FIELDS -> processFieldsData(ctx, msg, entityId, msgDataAsJsonNode, true);
        }
    }
    /**
     * Upgrade to use fetch to and data to fetch.
     *
     * @param oldConfiguration previous JSON configuration to upgrade
     * @return {@link TbPair}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    protected TbPair<Boolean, JsonNode> upgradeToUseFetchToAndDataToFetch(JsonNode oldConfiguration) throws TbNodeException {
        var newConfigObjectNode = (ObjectNode) oldConfiguration;
        if (!newConfigObjectNode.has(OLD_DATA_TO_FETCH_PROPERTY_NAME)) {
            throw new TbNodeException("property to update: '" + OLD_DATA_TO_FETCH_PROPERTY_NAME + "' doesn't exists in configuration!");
        }
        if (!newConfigObjectNode.has(OLD_DATA_MAPPING_PROPERTY_NAME)) {
            throw new TbNodeException("property to update: '" + OLD_DATA_MAPPING_PROPERTY_NAME + "' doesn't exists in configuration!");
        }
        newConfigObjectNode.set(DATA_MAPPING_PROPERTY_NAME, newConfigObjectNode.get(OLD_DATA_MAPPING_PROPERTY_NAME));
        newConfigObjectNode.remove(OLD_DATA_MAPPING_PROPERTY_NAME);
        var value = newConfigObjectNode.get(OLD_DATA_TO_FETCH_PROPERTY_NAME).asText();
        if ("true".equals(value)) {
            newConfigObjectNode.remove(OLD_DATA_TO_FETCH_PROPERTY_NAME);
            newConfigObjectNode.put(DATA_TO_FETCH_PROPERTY_NAME, DataToFetch.LATEST_TELEMETRY.name());
        } else if ("false".equals(value)) {
            newConfigObjectNode.remove(OLD_DATA_TO_FETCH_PROPERTY_NAME);
            newConfigObjectNode.put(DATA_TO_FETCH_PROPERTY_NAME, DataToFetch.ATTRIBUTES.name());
        } else {
            throw new TbNodeException("property to update: '" + OLD_DATA_TO_FETCH_PROPERTY_NAME + "' has unexpected value: " + value + ". Allowed values: true or false!");
        }
        newConfigObjectNode.put(FETCH_TO_PROPERTY_NAME, TbMsgSource.METADATA.name());
        return new TbPair<>(true, newConfigObjectNode);
    }

}
