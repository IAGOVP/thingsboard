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
package org.thingsboard.rule.engine.api;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.JobId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.job.Job;
import org.thingsboard.server.common.msg.queue.TbCallback;


/**

 * Schedules and manages background jobs initiated from rule nodes.

 */


public interface JobManager {
    /**
     * Submit job.
     *
     * @param job job ({@link Job})
     * @return future completing with {@link Job}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Job> submitJob(Job job); // TODO: rate limits
    /**
     * Submit job.
     *
     * @param job job ({@link Job})
     * @param finishCallback finish callback ({@link TbCallback})
     * @return future completing with {@link Job}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Job> submitJob(Job job, TbCallback finishCallback);
    /**
     * Cancel job.
     *
     * @param tenantId tenant UUID
     * @param jobId job id ({@link JobId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void cancelJob(TenantId tenantId, JobId jobId);
    /**
     * Reprocess job.
     *
     * @param tenantId tenant UUID
     * @param jobId job id ({@link JobId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void reprocessJob(TenantId tenantId, JobId jobId);
    /**
     * Handles job update.
     *
     * @param job job ({@link Job})
     * @throws Exception if an unexpected error occurs during processing
     */

    void onJobUpdate(Job job);

}
