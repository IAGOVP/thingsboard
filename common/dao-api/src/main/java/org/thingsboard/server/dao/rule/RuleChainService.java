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
package org.thingsboard.server.dao.rule;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainData;
import org.thingsboard.server.common.data.rule.RuleChainImportResult;
import org.thingsboard.server.common.data.rule.RuleChainMetaData;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.common.data.rule.RuleChainUpdateResult;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Persistence API for {@link RuleChain} entities.
 *
 * <p>Implemented in the {@code dao} module; consumed by application services and rule engine.
 */

public interface RuleChainService extends EntityDaoService {

    /**
     * Saves or persists rule chain.
     *
     * @param ruleChain rule chain ({@link RuleChain})
     * @return {@link RuleChain}
     */
    RuleChain saveRuleChain(RuleChain ruleChain);

    /**
     * Saves or persists rule chain.
     *
     * @param ruleChain rule chain ({@link RuleChain})
     * @param publishSaveEvent publish save event
     * @return {@link RuleChain}
     */
    RuleChain saveRuleChain(RuleChain ruleChain, boolean publishSaveEvent);

    /**
     * Saves or persists rule chain.
     *
     * @param ruleChain rule chain ({@link RuleChain})
     * @param publishSaveEvent publish save event
     * @param doValidate whether to run validation before persist
     * @return {@link RuleChain}
     */
    RuleChain saveRuleChain(RuleChain ruleChain, boolean publishSaveEvent, boolean doValidate);

    /**
     * Set root rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return the boolean result
     */
    boolean setRootRuleChain(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Saves or persists rule chain meta data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainMetaData rule chain meta data ({@link RuleChainMetaData})
     * @param ruleNodeUpdater rule node updater ({@link Function})
     * @return {@link RuleChainUpdateResult}
     */
    RuleChainUpdateResult saveRuleChainMetaData(TenantId tenantId, RuleChainMetaData ruleChainMetaData, Function<RuleNode, RuleNode> ruleNodeUpdater);

    /**
     * Saves or persists rule chain meta data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainMetaData rule chain meta data ({@link RuleChainMetaData})
     * @param ruleNodeUpdater rule node updater ({@link Function})
     * @param publishSaveEvent publish save event
     * @return {@link RuleChainUpdateResult}
     */
    RuleChainUpdateResult saveRuleChainMetaData(TenantId tenantId, RuleChainMetaData ruleChainMetaData, Function<RuleNode, RuleNode> ruleNodeUpdater, boolean publishSaveEvent);

    /**
     * Loads rule chain meta data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return {@link RuleChainMetaData}
     */
    RuleChainMetaData loadRuleChainMetaData(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Finds rule chain by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return {@link RuleChain}
     */
    RuleChain findRuleChainById(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Finds rule node by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @return {@link RuleNode}
     */
    RuleNode findRuleNodeById(TenantId tenantId, RuleNodeId ruleNodeId);

    /**
     * Finds rule chain by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return future completing with {@link RuleChain}
     */
    ListenableFuture<RuleChain> findRuleChainByIdAsync(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Finds rule node by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @return future completing with {@link RuleNode}
     */
    ListenableFuture<RuleNode> findRuleNodeByIdAsync(TenantId tenantId, RuleNodeId ruleNodeId);

    /**
     * Returns root tenant rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link RuleChain}
     */
    RuleChain getRootTenantRuleChain(TenantId tenantId);

    /**
     * Returns rule chain nodes.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return {@link List}
     */
    List<RuleNode> getRuleChainNodes(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Returns referencing rule chain nodes.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return {@link List}
     */
    List<RuleNode> getReferencingRuleChainNodes(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Returns rule node relations.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @return {@link List}
     */
    List<EntityRelation> getRuleNodeRelations(TenantId tenantId, RuleNodeId ruleNodeId);

    /**
     * Finds tenant rule chains by type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<RuleChain> findTenantRuleChainsByType(TenantId tenantId, RuleChainType type, PageLink pageLink);

    /**
     * Finds tenant rule chains by type and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link Collection}
     */
    Collection<RuleChain> findTenantRuleChainsByTypeAndName(TenantId tenantId, RuleChainType type, String name);

    /**
     * Deletes rule chain by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     */
    void deleteRuleChainById(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Deletes rule chains by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteRuleChainsByTenantId(TenantId tenantId);

    /**
     * Exports tenant rule chains.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link RuleChainData}
     * @throws ThingsboardException if thingsboard exception is thrown
     */
    RuleChainData exportTenantRuleChains(TenantId tenantId, PageLink pageLink) throws ThingsboardException;

    /**
     * Imports tenant rule chains.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainData rule chain data ({@link RuleChainData})
     * @param overwrite overwrite
     * @param ruleNodeUpdater rule node updater ({@link Function})
     * @return {@link List}
     */
    List<RuleChainImportResult> importTenantRuleChains(TenantId tenantId, RuleChainData ruleChainData, boolean overwrite, Function<RuleNode, RuleNode> ruleNodeUpdater);

    /**
     * Assigns rule chain to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @param edgeId edge id ({@link EdgeId})
     * @return {@link RuleChain}
     */
    RuleChain assignRuleChainToEdge(TenantId tenantId, RuleChainId ruleChainId, EdgeId edgeId);

    /**
     * Unassigns rule chain from edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @param edgeId edge id ({@link EdgeId})
     * @param remove remove
     * @return {@link RuleChain}
     */
    RuleChain unassignRuleChainFromEdge(TenantId tenantId, RuleChainId ruleChainId, EdgeId edgeId, boolean remove);

    /**
     * Finds rule chains by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link EdgeId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<RuleChain> findRuleChainsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    /**
     * Returns edge template root rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link RuleChain}
     */
    RuleChain getEdgeTemplateRootRuleChain(TenantId tenantId);

    /**
     * Set edge template root rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return the boolean result
     */
    boolean setEdgeTemplateRootRuleChain(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Set auto assign to edge rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return the boolean result
     */
    boolean setAutoAssignToEdgeRuleChain(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Unset auto assign to edge rule chain.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @return the boolean result
     */
    boolean unsetAutoAssignToEdgeRuleChain(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Finds auto assign to edge rule chains by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<RuleChain> findAutoAssignToEdgeRuleChainsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds rule nodes by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity name (unique within tenant scope where applicable)
     * @param toString to string ({@link String})
     * @return {@link List}
     */
    List<RuleNode> findRuleNodesByTenantIdAndType(TenantId tenantId, String name, String toString);

    /**
     * Finds rule nodes by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @return {@link List}
     */
    List<RuleNode> findRuleNodesByTenantIdAndType(TenantId tenantId, String type);

    /**
     * Finds all rule nodes by type.
     *
     * @param type type ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<RuleNode> findAllRuleNodesByType(String type, PageLink pageLink);

    @Deprecated(forRemoval = true, since = "3.6.3")
    /**
     * Finds all rule nodes by type and version less than.
     *
     * @param type type ({@link String})
     * @param version version
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<RuleNode> findAllRuleNodesByTypeAndVersionLessThan(String type, int version, PageLink pageLink);

    /**
     * Finds all rule node ids by type and version less than.
     *
     * @param type type ({@link String})
     * @param version version
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<RuleNodeId> findAllRuleNodeIdsByTypeAndVersionLessThan(String type, int version, PageLink pageLink);

    /**
     * Finds all rule nodes by ids.
     *
     * @param ruleNodeIds rule node ids ({@link List})
     * @return {@link List}
     */
    List<RuleNode> findAllRuleNodesByIds(List<RuleNodeId> ruleNodeIds);

    /**
     * Saves or persists rule node.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNode rule node ({@link RuleNode})
     * @return {@link RuleNode}
     */
    RuleNode saveRuleNode(TenantId tenantId, RuleNode ruleNode);

    /**
     * Deletes rule nodes.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainId rule chain id ({@link RuleChainId})
     */
    void deleteRuleNodes(TenantId tenantId, RuleChainId ruleChainId);

    /**
     * Finds rule chains by ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainIds rule chain ids ({@link List})
     * @return {@link List}
     */
    List<RuleChain> findRuleChainsByIds(TenantId tenantId, List<RuleChainId> ruleChainIds);

}
