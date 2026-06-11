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
package org.thingsboard.server.service.job;

import org.thingsboard.server.common.data.job.Job;
import org.thingsboard.server.common.data.job.JobType;
import org.thingsboard.server.common.data.job.task.Task;
import org.thingsboard.server.common.data.job.task.TaskResult;

import java.util.List;
import java.util.function.Consumer;

/**

 * job processor contract for background job scheduling and execution.

 */

public interface JobProcessor {

    int process(Job job, Consumer<Task<?>> taskConsumer) throws Exception;

    /**
     * Reprocess.
     *
     * @param job job ({@link Job})
     * @param taskFailures task failures ({@link List})
     * @param taskConsumer task consumer ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void reprocess(Job job, List<TaskResult> taskFailures, Consumer<Task<?>> taskConsumer) throws Exception;

    /**
     * Handles job finished.
     *
     * @param job job ({@link Job})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    default void onJobFinished(Job job) {}
/**
 * Returns type.
 *
 * @return {@link JobType}
 * @throws Exception if an unexpected error occurs during processing
 */

    JobType getType();

}
