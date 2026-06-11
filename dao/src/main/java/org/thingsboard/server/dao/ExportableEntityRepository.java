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
package org.thingsboard.server.dao;

import java.util.UUID;


/**

 * Spring Data JPA repository for exportable entity entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface ExportableEntityRepository<D> {
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link D}
     * @throws Exception if an unexpected error occurs during processing
     */

    D findByTenantIdAndExternalId(UUID tenantId, UUID externalId);

}
