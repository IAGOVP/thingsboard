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
package org.thingsboard.server.service.entitiy.edge;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rule.RuleChain;

/**

 * Application-layer service API for edge entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbEdgeService {
/**
 * Saves or persists the requested data.
 *
 * @param edge edge ({@link Edge})
 * @param edgeTemplateRootRuleChain edge template root rule chain ({@link RuleChain})
 * @param user authenticated user performing the action
 * @return {@link Edge}
 * @throws Exception if an unexpected error occurs during processing
 */



    Edge save(Edge edge, RuleChain edgeTemplateRootRuleChain, User user) throws Exception;
/**
 * Deletes the requested data.
 *
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void delete(Edge edge, User user);
/**
 * Assigns edge to customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param edgeId edge id ({@link EdgeId})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Edge}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Edge assignEdgeToCustomer(TenantId tenantId, EdgeId edgeId, Customer customer, User user) throws ThingsboardException;
/**
 * Unassigns edge from customer.
 *
 * @param edge edge ({@link Edge})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Edge}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Edge unassignEdgeFromCustomer(Edge edge, Customer customer, User user) throws ThingsboardException;
/**
 * Assigns edge to public customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param edgeId edge id ({@link EdgeId})
 * @param user authenticated user performing the action
 * @return {@link Edge}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Edge assignEdgeToPublicCustomer(TenantId tenantId, EdgeId edgeId, User user) throws ThingsboardException;
/**
 * Set edge root rule chain.
 *
 * @param edge edge ({@link Edge})
 * @param ruleChainId rule chain id ({@link RuleChainId})
 * @param user authenticated user performing the action
 * @return {@link Edge}
 * @throws Exception if an unexpected error occurs during processing
 */

    Edge setEdgeRootRuleChain(Edge edge, RuleChainId ruleChainId, User user) throws Exception;

}
