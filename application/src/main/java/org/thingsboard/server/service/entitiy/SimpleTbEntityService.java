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
package org.thingsboard.server.service.entitiy;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.service.security.model.SecurityUser;


/**

 * Service contract for simple tb entity operations (REST-layer entity operations (application-layer entity CRUD with audit logging and version-control hooks)).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */


public interface SimpleTbEntityService<T> {
/**
 * Saves or persists the requested data.
 *
 * @param entity entity ({@link T})
 * @return {@link T}
 * @throws Exception if an unexpected error occurs during processing
 */



    default T save(T entity) throws Exception {
        
        /**
         * Saves or persists the requested data.
         *
         * @return the return value
         * @throws Exception if an unexpected error occurs during processing
         */

        return save(entity, null);
    }
/**
 * Saves or persists the requested data.
 *
 * @param entity entity ({@link T})
 * @param user authenticated user performing the action
 * @return {@link T}
 * @throws Exception if an unexpected error occurs during processing
 */

    T save(T entity, SecurityUser user) throws Exception;
/**
 * Deletes the requested data.
 *
 * @param entity entity ({@link T})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void delete(T entity, User user);

}
