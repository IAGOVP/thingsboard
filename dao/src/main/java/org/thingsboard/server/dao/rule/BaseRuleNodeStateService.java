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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleNodeState;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.exception.DataValidationException;
/**
 * Default DAO-layer service implementation for rule node state.
 *
 * <p>Coordinates validation, caching, cluster events, and {@code *Dao} persistence (rule chains, nodes, and node state).
 */


@Service
@Slf4j
public class BaseRuleNodeStateService extends AbstractEntityService implements RuleNodeStateService {

    @Autowired
    private RuleNodeStateDao ruleNodeStateDao;

    
    /**
     * Finds by rule node id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<RuleNodeState> findByRuleNodeId(TenantId tenantId, RuleNodeId ruleNodeId, PageLink pageLink) {
        if (tenantId == null) {
            throw new DataValidationException("Tenant id should be specified!.");
        }
        if (ruleNodeId == null) {
            throw new DataValidationException("RuleNode id should be specified!.");
        }
        return ruleNodeStateDao.findByRuleNodeId(ruleNodeId.getId(), pageLink);
    }

    
    /**
     * Finds by rule node id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param entityId target entity identifier
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public RuleNodeState findByRuleNodeIdAndEntityId(TenantId tenantId, RuleNodeId ruleNodeId, EntityId entityId) {
        if (tenantId == null) {
            throw new DataValidationException("Tenant id should be specified!.");
        }
        if (ruleNodeId == null) {
            throw new DataValidationException("RuleNode id should be specified!.");
        }
        if (entityId == null) {
            throw new DataValidationException("Entity id should be specified!.");
        }
        return ruleNodeStateDao.findByRuleNodeIdAndEntityId(ruleNodeId.getId(), entityId.getId());
    }

    
    /**
     * Saves or persists the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeState rule node state ({@link RuleNodeState})
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public RuleNodeState save(TenantId tenantId, RuleNodeState ruleNodeState) {
        if (tenantId == null) {
            throw new DataValidationException("Tenant id should be specified!.");
        }
        return saveOrUpdate(tenantId, ruleNodeState, false);
    }

    
    /**
     * Removes by rule node id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void removeByRuleNodeId(TenantId tenantId, RuleNodeId ruleNodeId) {
        if (tenantId == null) {
            throw new DataValidationException("Tenant id should be specified!.");
        }
        if (ruleNodeId == null) {
            throw new DataValidationException("Rule node id should be specified!.");
        }
        ruleNodeStateDao.removeByRuleNodeId(ruleNodeId.getId());
    }

    
    /**
     * Removes by rule node id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param entityId target entity identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void removeByRuleNodeIdAndEntityId(TenantId tenantId, RuleNodeId ruleNodeId, EntityId entityId) {
        if (tenantId == null) {
            throw new DataValidationException("Tenant id should be specified!.");
        }
        if (ruleNodeId == null) {
            throw new DataValidationException("Rule node id should be specified!.");
        }
        if (entityId == null) {
            throw new DataValidationException("Entity id should be specified!.");
        }
        ruleNodeStateDao.removeByRuleNodeIdAndEntityId(ruleNodeId.getId(), entityId.getId());
    }

    
    /**
     * Saves or updates the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleNodeState rule node state ({@link RuleNodeState})
     * @param update update
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */


    public RuleNodeState saveOrUpdate(TenantId tenantId, RuleNodeState ruleNodeState, boolean update) {
        try {
            if (update) {
                RuleNodeState old = ruleNodeStateDao.findByRuleNodeIdAndEntityId(ruleNodeState.getRuleNodeId().getId(), ruleNodeState.getEntityId().getId());
                if (old != null && !old.getId().equals(ruleNodeState.getId())) {
                    ruleNodeState.setId(old.getId());
                    ruleNodeState.setCreatedTime(old.getCreatedTime());
                }
            }
            return ruleNodeStateDao.save(tenantId, ruleNodeState);
        } catch (Exception t) {
            ConstraintViolationException e = extractConstraintViolationException(t).orElse(null);
            if (e != null && e.getConstraintName() != null && e.getConstraintName().equalsIgnoreCase("rule_node_state_unq_key")) {
                if (!update) {
                    return saveOrUpdate(tenantId, ruleNodeState, true);
                } else {
                    throw new DataValidationException("Rule node state for such rule node id and entity id already exists!");
                }
            } else {
                throw t;
            }
        }
    }
}
