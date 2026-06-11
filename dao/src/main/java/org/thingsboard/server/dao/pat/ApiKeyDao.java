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
package org.thingsboard.server.dao.pat;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.Set;


/**

 * Persistence contract for api key.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (personal access tokens (API keys)).

 */


public interface ApiKeyDao extends Dao<ApiKey> {
    /**
     * Finds by value.
     *
     * @param value value ({@link String})
     * @return {@link ApiKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    ApiKey findByValue(String value);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<ApiKey> findByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Finds by tenant id and user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<ApiKey> findByTenantIdAndUserId(TenantId tenantId, UserId userId);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    Set<String> deleteByTenantId(TenantId tenantId);
    /**
     * Deletes by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    Set<String> deleteByUserId(TenantId tenantId, UserId userId);
    /**
     * Deletes all by expiration time before.
     *
     * @param ts ts
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int deleteAllByExpirationTimeBefore(long ts);

}
