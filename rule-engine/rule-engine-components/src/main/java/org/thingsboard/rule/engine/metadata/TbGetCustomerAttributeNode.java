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
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.util.TbPair;

import java.util.NoSuchElementException;

import static com.google.common.util.concurrent.Futures.immediateFuture;

/**
 * Enrichment rule node — <b>customer attributes</b>.
 *
 * <p>Adds message originator customer attributes or latest telemetry into message or message metadata
 * <br>Useful in multi-customer solutions where each customer has a different configuration or threshold set 
 *
 * <p>Implements {@link org.thingsboard.rule.engine.api.TbNode}. Configuration: {@link TbGetEntityDataNodeConfiguration}.
 * <br>Documentation: <a href="https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/customer-attributes/">https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/customer-attributes/</a>
 */
@RuleNode(
        type = ComponentType.ENRICHMENT,
        name = "customer attributes",
        configClazz = TbGetEntityDataNodeConfiguration.class,
        version = 1,
        nodeDescription = "Adds message originator customer attributes or latest telemetry into message or message metadata",
        nodeDetails = "Useful in multi-customer solutions where each customer has a different configuration or threshold set " +
                "that is stored as customer attributes or telemetry data and used for dynamic message filtering, transformation, " +
                "or actions such as alarm creation if the threshold is exceeded.<br><br>" +
                "Output connections: <code>Success</code>, <code>Failure</code>.",
        configDirective = "tbEnrichmentNodeCustomerAttributesConfig",
        docUrl = "https://thingsboard.io/docs/user-guide/rule-engine-2-0/nodes/enrichment/customer-attributes/"
)
public class TbGetCustomerAttributeNode extends TbAbstractGetEntityDataNode<CustomerId> {
    /**
     * Loads node configuration.
     *
     * @param configuration node configuration wrapper ({@link TbNodeConfiguration})
     * @return {@link TbGetEntityDataNodeConfiguration}
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    protected TbGetEntityDataNodeConfiguration loadNodeConfiguration(TbNodeConfiguration configuration) throws TbNodeException {
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
     * @return future completing with {@link CustomerId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected ListenableFuture<CustomerId> findEntityAsync(TbContext ctx, EntityId originator) {
        if (originator.getEntityType() == EntityType.CUSTOMER) {
            return immediateFuture((CustomerId) originator);
        }
        return ctx.getEntityService().fetchEntityCustomerIdAsync(ctx.getTenantId(), originator)
                .transform(customerIdOpt -> {
                    if (customerIdOpt.isEmpty()) {
                        throw new NoSuchElementException("Originator not found");
                    }
                    if (customerIdOpt.get().isNullUid()) {
                        throw new IllegalStateException("Originator is not assigned to any customer");
                    }
                    return customerIdOpt.get();
                }, ctx.getDbCallbackExecutor());
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
