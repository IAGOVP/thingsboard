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
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.rule.engine.util.EntitiesRelatedDeviceIdAsyncLoader;
import org.thingsboard.rule.engine.util.TbMsgSource;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.msg.TbMsg;

/**
 * Enrichment rule node — <b>related device attributes</b>.
 *
 * <p>Add originators related device attributes and/or latest telemetry values into message or message metadata
 * <br>Related device lookup based on the configured relation query. 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbGetDeviceAttrNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/related-device-attributes/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/related-device-attributes/</a>
 */
@RuleNode(
        type = ComponentType.ENRICHMENT,
        name = "related device attributes",
        configClazz = TbGetDeviceAttrNodeConfiguration.class,
        version = 1,
        nodeDescription = "Add originators related device attributes and/or latest telemetry values into message or message metadata",
        nodeDetails = "Related device lookup based on the configured relation query. " +
                "If multiple related devices are found, only first device is used for message enrichment, other entities are discarded. " +
                "Useful when you need to retrieve attributes and/or latest telemetry values from device that has a relation " +
                "to the message originator and use them for further message processing.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        configDirective = "tbEnrichmentNodeDeviceAttributesConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/related-device-attributes/"
)
public class TbGetDeviceAttrNode extends TbAbstractGetAttributesNode<TbGetDeviceAttrNodeConfiguration, DeviceId> {

    private static final String RELATED_DEVICE_NOT_FOUND_MESSAGE = "Failed to find related device to message originator using relation query specified in the configuration!";
    /**
     * Loads node configuration.
     *
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @return {@link TbGetDeviceAttrNodeConfiguration}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    protected TbGetDeviceAttrNodeConfiguration loadNodeConfiguration(TbNodeConfiguration configuration) throws TbNodeException {
        return TbNodeUtils.convert(configuration, TbGetDeviceAttrNodeConfiguration.class);
    }
    /**
     * Finds entity id async.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param msg incoming or outgoing rule engine message
     * @return future completing with {@link DeviceId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected ListenableFuture<DeviceId> findEntityIdAsync(TbContext ctx, TbMsg msg) {
        return Futures.transform(
                EntitiesRelatedDeviceIdAsyncLoader.findDeviceAsync(ctx, msg.getOriginator(), config.getDeviceRelationsQuery()),
                checkIfEntityIsPresentOrThrow(RELATED_DEVICE_NOT_FOUND_MESSAGE),
                ctx.getDbCallbackExecutor());
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
        return fromVersion == 0 ?
                upgradeRuleNodesWithOldPropertyToUseFetchTo(
                        oldConfiguration,
                        "fetchToData",
                        TbMsgSource.DATA.name(),
                        TbMsgSource.METADATA.name()) :
                new TbPair<>(false, oldConfiguration);
    }

}
