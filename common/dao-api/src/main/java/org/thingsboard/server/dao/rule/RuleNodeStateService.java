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

import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleNodeState;

/**
 * Service API for rule node state persistence and domain operations.
 */
public interface RuleNodeStateService {

    /**
     * Finds by rule node id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<RuleNodeState> findByRuleNodeId(TenantId tenantId, RuleNodeId ruleNodeId, PageLink pageLink);

    /**
     * Finds by rule node id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param entityId entity id ({@link EntityId})
     * @return {@link RuleNodeState}
     */
    RuleNodeState findByRuleNodeIdAndEntityId(TenantId tenantId, RuleNodeId ruleNodeId, EntityId entityId);

    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeState rule node state ({@link RuleNodeState})
     * @return {@link RuleNodeState}
     */
    RuleNodeState save(TenantId tenantId, RuleNodeState ruleNodeState);

    /**
     * Removes by rule node id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param selfId self id ({@link RuleNodeId})
     */
    void removeByRuleNodeId(TenantId tenantId, RuleNodeId selfId);

    /**
     * Removes by rule node id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param selfId self id ({@link RuleNodeId})
     * @param entityId entity id ({@link EntityId})
     */
    void removeByRuleNodeIdAndEntityId(TenantId tenantId, RuleNodeId selfId, EntityId entityId);
}
