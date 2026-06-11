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
import org.thingsboard.server.common.data.job.JobStats;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

/**
 * Service API for job persistence and domain operations.
 */
public interface JobService extends EntityDaoService {

    /**
     * Saves or persists job.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param job job ({@link Job})
     * @return {@link Job}
     */
    Job saveJob(TenantId tenantId, Job job);

    /**
     * Finds job by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param jobId job id ({@link JobId})
     * @return {@link Job}
     */
    Job findJobById(TenantId tenantId, JobId jobId);

    /**
     * Cancel job.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param jobId job id ({@link JobId})
     */
    void cancelJob(TenantId tenantId, JobId jobId);

    /**
     * Mark as failed.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param jobId job id ({@link JobId})
     * @param error error ({@link String})
     */
    void markAsFailed(TenantId tenantId, JobId jobId, String error);

    /**
     * Processes stats.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param jobId job id ({@link JobId})
     * @param jobStats job stats ({@link JobStats})
     */
    void processStats(TenantId tenantId, JobId jobId, JobStats jobStats);

    /**
     * Finds jobs by filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filter filter ({@link JobFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Job> findJobsByFilter(TenantId tenantId, JobFilter filter, PageLink pageLink);

    /**
     * Finds latest job by key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key key ({@link String})
     * @return {@link Job}
     */
    Job findLatestJobByKey(TenantId tenantId, String key);

    /**
     * Deletes job.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param jobId job id ({@link JobId})
     */
    void deleteJob(TenantId tenantId, JobId jobId);

    /**
     * Deletes jobs by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return the int result
     */
    int deleteJobsByEntityId(TenantId tenantId, EntityId entityId);

}
