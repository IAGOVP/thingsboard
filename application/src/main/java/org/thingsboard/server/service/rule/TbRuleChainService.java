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
package org.thingsboard.server.service.rule;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rule.DefaultRuleChainCreateRequest;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainMetaData;
import org.thingsboard.server.common.data.rule.RuleChainOutputLabelsUsage;
import org.thingsboard.server.common.data.rule.RuleChainUpdateResult;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.service.entitiy.SimpleTbEntityService;

import java.util.List;
import java.util.Set;

/**

 * Service contract for tb rule chain operations (rule chain metadata and helpers).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface TbRuleChainService extends SimpleTbEntityService<RuleChain> {

    Set<String> getRuleChainOutputLabels(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Returns output label usage.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleChainOutputLabelsUsage> getOutputLabelUsage(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Updates related rule chains.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @param result result ({@link RuleChainUpdateResult})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleChain> updateRelatedRuleChains(TenantId tenantId, RuleChainId ruleChainId, RuleChainUpdateResult result);

    /**
     * Saves or persists default by name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param request request payload with operation parameters
     * @param user authenticated user performing the action
     * @return {@link RuleChain}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleChain saveDefaultByName(TenantId tenantId, DefaultRuleChainCreateRequest request, User user) throws Exception;

    /**
     * Set root rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChain rule chain ({@link RuleChain})
     * @param user authenticated user performing the action
     * @return {@link RuleChain}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    RuleChain setRootRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;

    /**
     * Saves or persists rule chain meta data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChain rule chain ({@link RuleChain})
     * @param ruleChainMetaData rule chain meta data ({@link RuleChainMetaData})
     * @param updateRelated update related
     * @param user authenticated user performing the action
     * @return {@link RuleChainMetaData}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleChainMetaData saveRuleChainMetaData(TenantId tenantId, RuleChain ruleChain, RuleChainMetaData ruleChainMetaData,
                                            boolean updateRelated, User user) throws Exception;

    /**
     * Assigns rule chain to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChain rule chain ({@link RuleChain})
     * @param edge edge ({@link Edge})
     * @param user authenticated user performing the action
     * @return {@link RuleChain}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    RuleChain assignRuleChainToEdge(TenantId tenantId, RuleChain ruleChain, Edge edge, User user) throws ThingsboardException;

    /**
     * Unassigns rule chain from edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChain rule chain ({@link RuleChain})
     * @param edge edge ({@link Edge})
     * @param user authenticated user performing the action
     * @return {@link RuleChain}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    RuleChain unassignRuleChainFromEdge(TenantId tenantId, RuleChain ruleChain, Edge edge, User user) throws ThingsboardException;

    /**
     * Set edge template root rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChain rule chain ({@link RuleChain})
     * @param user authenticated user performing the action
     * @return {@link RuleChain}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    RuleChain setEdgeTemplateRootRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;

    /**
     * Set auto assign to edge rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChain rule chain ({@link RuleChain})
     * @param user authenticated user performing the action
     * @return {@link RuleChain}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    RuleChain setAutoAssignToEdgeRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;

    /**
     * Unset auto assign to edge rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChain rule chain ({@link RuleChain})
     * @param user authenticated user performing the action
     * @return {@link RuleChain}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    RuleChain unsetAutoAssignToEdgeRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;

    /**
     * Updates rule node configuration.
     *
     * @param ruleNode rule node ({@link RuleNode})
     * @return {@link RuleNode}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleNode updateRuleNodeConfiguration(RuleNode ruleNode);
}
