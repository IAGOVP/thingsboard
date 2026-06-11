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
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.util.TbPair;

/**
 * Enrichment rule node — <b>tenant attributes</b>.
 *
 * <p>Adds message originator tenant attributes or latest telemetry into message or message metadata
 * <br>Useful when you need to retrieve some common configuration or threshold set 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbGetEntityDataNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/tenant-attributes/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/tenant-attributes/</a>
 */
@RuleNode(
        type = ComponentType.ENRICHMENT,
        name = "tenant attributes",
        configClazz = TbGetEntityDataNodeConfiguration.class,
        version = 1,
        nodeDescription = "Adds message originator tenant attributes or latest telemetry into message or message metadata",
        nodeDetails = "Useful when you need to retrieve some common configuration or threshold set " +
                "that is stored as tenant attributes or telemetry data and use it for further message processing.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        configDirective = "tbEnrichmentNodeTenantAttributesConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/tenant-attributes/"
)
public class TbGetTenantAttributeNode extends TbAbstractGetEntityDataNode<TenantId> {
    /**
     * Loads node configuration.
     *
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @return {@link TbGetEntityDataNodeConfiguration}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public TbGetEntityDataNodeConfiguration loadNodeConfiguration(TbNodeConfiguration configuration) throws TbNodeException {
        var config = TbNodeUtils.convert(configuration, TbGetEntityDataNodeConfiguration.class);
        checkIfMappingIsNotEmptyOrElseThrow(config.getDataMapping());
        checkDataToFetchSupportedOrElseThrow(config.getDataToFetch());
        return config;
    }
    /**
     * Finds entity async.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param originator message originator entity id
     * @return future completing with {@link TenantId}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public ListenableFuture<TenantId> findEntityAsync(TbContext ctx, EntityId originator) {
        return Futures.immediateFuture(ctx.getTenantId());
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
        return fromVersion == 0 ? upgradeToUseFetchToAndDataToFetch(oldConfiguration) : new TbPair<>(false, oldConfiguration);
    }

}
