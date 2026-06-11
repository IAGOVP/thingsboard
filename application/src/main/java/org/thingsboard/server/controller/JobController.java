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
package org.thingsboard.server.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.rule.engine.api.JobManager;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.JobId;
import org.thingsboard.server.common.data.job.Job;
import org.thingsboard.server.common.data.job.JobFilter;
import org.thingsboard.server.common.data.job.JobStatus;
import org.thingsboard.server.common.data.job.JobType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;

import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;

/**
 * REST API for monitoring and managing background jobs (cancel, reprocess, delete).
 *
 * <p>Base path: {@code /api}.
 *
 * <p>Authorization: {@code TENANT_ADMIN} only.
 *
 * <p>Uses {@link org.thingsboard.rule.engine.api.JobManager} for job lifecycle actions
 * and inherited {@code jobService} from {@link BaseController} for queries and deletion.
 */
@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class JobController extends BaseController {

    private final JobManager jobManager;

    /**
     * GET {@code /api/job/{id}} — Fetch a job by id.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param id job UUID
     * @return the {@link Job} entity
     * @throws ThingsboardException if the job does not exist or access is denied
     */
    @GetMapping("/job/{id}")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    public Job getJobById(@PathVariable UUID id) throws ThingsboardException {
        JobId jobId = new JobId(id);
        return checkJobId(jobId, Operation.READ);
    }

    /**
     * GET {@code /api/jobs} — List jobs for the current tenant with optional filters.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param pageSize   number of jobs per page
     * @param page       zero-based page index
     * @param textSearch optional case-insensitive filter on job description
     * @param sortProperty optional sort field
     * @param sortOrder  optional sort direction
     * @param types      optional list of {@link JobType} values to include
     * @param statuses   optional list of {@link JobStatus} values to include
     * @param entities   optional list of related entity UUIDs
     * @param startTime  optional creation time lower bound
     * @param endTime    optional creation time upper bound
     * @return a page of matching {@link Job} records
     * @throws ThingsboardException if query parameters are invalid
     */
    @GetMapping("/jobs")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    public PageData<Job> getJobs(@Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
                                 @RequestParam int pageSize,
                                 @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
                                 @RequestParam int page,
                                 @Parameter(description = "Case-insensitive 'substring' filter based on job's description")
                                 @RequestParam(required = false) String textSearch,
                                 @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                                 @RequestParam(required = false) String sortProperty,
                                 @Parameter(description = SORT_ORDER_DESCRIPTION)
                                 @RequestParam(required = false) String sortOrder,
                                 @RequestParam(required = false) List<JobType> types,
                                 @RequestParam(required = false) List<JobStatus> statuses,
                                 @RequestParam(required = false) List<UUID> entities,
                                 @RequestParam(required = false) Long startTime,
                                 @RequestParam(required = false) Long endTime) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        JobFilter filter = JobFilter.builder()
                .types(types)
                .statuses(statuses)
                .entities(entities)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        return jobService.findJobsByFilter(getTenantId(), filter, pageLink);
    }

    /**
     * POST {@code /api/job/{id}/cancel} — Cancel a running or scheduled job.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param id job UUID to cancel
     * @throws ThingsboardException if the job does not exist or cannot be cancelled
     */
    @PostMapping("/job/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    public void cancelJob(@PathVariable UUID id) throws ThingsboardException {
        JobId jobId = new JobId(id);
        checkJobId(jobId, Operation.WRITE);
        jobManager.cancelJob(getTenantId(), jobId);
    }

    /**
     * POST {@code /api/job/{id}/reprocess} — Re-queue a job for reprocessing.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param id job UUID to reprocess
     * @throws ThingsboardException if the job does not exist or reprocessing is not allowed
     */
    @PostMapping("/job/{id}/reprocess")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    public void reprocessJob(@PathVariable UUID id) throws ThingsboardException {
        JobId jobId = new JobId(id);
        checkJobId(jobId, Operation.WRITE);
        jobManager.reprocessJob(getTenantId(), jobId);
    }

    /**
     * DELETE {@code /api/job/{id}} — Delete a job record.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param id job UUID to delete
     * @throws ThingsboardException if the job does not exist or deletion is denied
     */
    @DeleteMapping("/job/{id}")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    public void deleteJob(@PathVariable UUID id) throws ThingsboardException {
        JobId jobId = new JobId(id);
        checkJobId(jobId, Operation.DELETE);
        jobService.deleteJob(getTenantId(), jobId);
    }

}
