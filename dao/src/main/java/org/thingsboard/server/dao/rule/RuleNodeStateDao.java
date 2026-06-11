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

import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleNodeState;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

/**
 * Persistence contract for rule node state.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (rule chains, nodes, and node state).
 */

public interface RuleNodeStateDao extends Dao<RuleNodeState> {
    /**
     * Finds by rule node id.
     *
     * @param ruleNodeId rule node id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleNodeState> findByRuleNodeId(UUID ruleNodeId, PageLink pageLink);
    /**
     * Finds by rule node id and entity id.
     *
     * @param ruleNodeId rule node id ({@link UUID})
     * @param entityId target entity identifier
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleNodeState findByRuleNodeIdAndEntityId(UUID ruleNodeId, UUID entityId);
    /**
     * Removes by rule node id.
     *
     * @param ruleNodeId rule node id ({@link UUID})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByRuleNodeId(UUID ruleNodeId);
    /**
     * Removes by rule node id and entity id.
     *
     * @param ruleNodeId rule node id ({@link UUID})
     * @param entityId target entity identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByRuleNodeIdAndEntityId(UUID ruleNodeId, UUID entityId);
}
