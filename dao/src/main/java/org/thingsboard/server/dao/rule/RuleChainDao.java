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
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.ResourceContainerDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Persistence contract for rule chain.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (rule chains, nodes, and node state).
 */

public interface RuleChainDao extends Dao<RuleChain>, TenantEntityDao<RuleChain>, ExportableEntityDao<RuleChainId, RuleChain>, ResourceContainerDao<RuleChain> {

    
    /**
     * Finds rule chains by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleChain> findRuleChainsByTenantId(UUID tenantId, PageLink pageLink);

    
    /**
     * Finds rule chains by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleChain> findRuleChainsByTenantIdAndType(UUID tenantId, RuleChainType type, PageLink pageLink);

    
    /**
     * Finds root rule chain by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @return {@link RuleChain}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleChain findRootRuleChainByTenantIdAndType(UUID tenantId, RuleChainType type);

    
    /**
     * Finds rule chains by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleChain> findRuleChainsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink);

    
    /**
     * Finds auto assign to edge rule chains by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleChain> findAutoAssignToEdgeRuleChainsByTenantId(UUID tenantId, PageLink pageLink);
    /**
     * Finds by tenant id and type and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param name entity or attribute name
     * @return {@link Collection}
     * @throws Exception if an unexpected error occurs during processing
     */

    Collection<RuleChain> findByTenantIdAndTypeAndName(TenantId tenantId, RuleChainType type, String name);
    /**
     * Finds rule chains by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainIds rule chain ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleChain> findRuleChainsByTenantIdAndIds(UUID tenantId, List<UUID> ruleChainIds);

}
