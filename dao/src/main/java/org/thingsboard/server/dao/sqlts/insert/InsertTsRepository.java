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
package org.thingsboard.server.dao.sqlts.insert;

import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;

import java.util.List;


/**

 * Spring Data JPA repository for insert ts entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface InsertTsRepository<T extends AbstractTsKvEntity> {
    /**
     * Saves or updates the requested data.
     *
     * @param entities entities ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void saveOrUpdate(List<T> entities);

}
