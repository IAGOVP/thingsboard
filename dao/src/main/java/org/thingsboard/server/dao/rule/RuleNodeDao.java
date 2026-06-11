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

import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.dao.Dao;

import java.util.List;

/**
 * Persistence contract for rule node.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (rule chains, nodes, and node state).
 */

public interface RuleNodeDao extends Dao<RuleNode> {
    /**
     * Finds rule nodes by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param configurationSearch configuration search ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleNode> findRuleNodesByTenantIdAndType(TenantId tenantId, String type, String configurationSearch);
    /**
     * Finds all rule nodes by type.
     *
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleNode> findAllRuleNodesByType(String type, PageLink pageLink);
    /**
     * Finds all rule nodes by type and version less than.
     *
     * @param type type ({@link String})
     * @param version version
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleNode> findAllRuleNodesByTypeAndVersionLessThan(String type, int version, PageLink pageLink);
    /**
     * Finds all rule node ids by type and version less than.
     *
     * @param type type ({@link String})
     * @param version version
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleNodeId> findAllRuleNodeIdsByTypeAndVersionLessThan(String type, int version, PageLink pageLink);
    /**
     * Finds all rule node by ids.
     *
     * @param ruleNodeIds rule node ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleNode> findAllRuleNodeByIds(List<RuleNodeId> ruleNodeIds);
    /**
     * Finds by external ids.
     *
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @param externalIds external ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleNode> findByExternalIds(RuleChainId ruleChainId, List<RuleNodeId> externalIds);
    /**
     * Deletes by id in.
     *
     * @param ruleNodeIds rule node ids ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void deleteByIdIn(List<RuleNodeId> ruleNodeIds);

}
