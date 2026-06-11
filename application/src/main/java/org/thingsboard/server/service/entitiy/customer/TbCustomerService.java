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
package org.thingsboard.server.service.entitiy.customer;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.service.entitiy.SimpleTbEntityService;
import org.thingsboard.server.service.security.model.SecurityUser;

/**

 * Application-layer service API for customer entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbCustomerService extends SimpleTbEntityService<Customer> {
/**
 * Saves or persists the requested data.
 *
 * @param customer customer ({@link Customer})
 * @param nameConflictStrategy name conflict strategy ({@link NameConflictStrategy})
 * @param user authenticated user performing the action
 * @return {@link Customer}
 * @throws Exception if an unexpected error occurs during processing
 */



    Customer save(Customer customer, NameConflictStrategy nameConflictStrategy, SecurityUser user) throws Exception;

}
