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
package org.thingsboard.server.dao.job;

import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.JobId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.job.Job;
import org.thingsboard.server.common.data.job.JobFilter;
import org.thingsboard.server.common.data.job.JobStatus;
import org.thingsboard.server.common.data.job.JobType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;


/**

 * Persistence contract for job.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (background job persistence and scheduling metadata).

 */


public interface JobDao extends Dao<Job> {
    /**
     * Finds by tenant id and filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filter filter ({@link JobFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Job> findByTenantIdAndFilter(TenantId tenantId, JobFilter filter, PageLink pageLink);
    /**
     * Finds by id for update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param jobId job id ({@link JobId})
     * @return {@link Job}
     * @throws Exception if an unexpected error occurs during processing
     */

    Job findByIdForUpdate(TenantId tenantId, JobId jobId);
    /**
     * Finds latest by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link Job}
     * @throws Exception if an unexpected error occurs during processing
     */

    Job findLatestByTenantIdAndKey(TenantId tenantId, String key);
    /**
     * Exists by tenant and key and status one of.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @param statuses statuses
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantAndKeyAndStatusOneOf(TenantId tenantId, String key, JobStatus... statuses);
    /**
     * Exists by tenant id and type and status one of.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link JobType})
     * @param statuses statuses
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndTypeAndStatusOneOf(TenantId tenantId, JobType type, JobStatus... statuses);
    /**
     * Exists by tenant id and entity id and status one of.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param statuses statuses
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndEntityIdAndStatusOneOf(TenantId tenantId, EntityId entityId, JobStatus... statuses);
    /**
     * Finds oldest by tenant id and type and status for update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link JobType})
     * @param status status ({@link JobStatus})
     * @return {@link Job}
     * @throws Exception if an unexpected error occurs during processing
     */

    Job findOldestByTenantIdAndTypeAndStatusForUpdate(TenantId tenantId, JobType type, JobStatus status);
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeByTenantId(TenantId tenantId);
    /**
     * Removes by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int removeByEntityId(TenantId tenantId, EntityId entityId);

}
