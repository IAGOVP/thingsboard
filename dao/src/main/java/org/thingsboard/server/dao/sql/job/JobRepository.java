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
package org.thingsboard.server.dao.sql.job;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.job.JobStatus;
import org.thingsboard.server.common.data.job.JobType;
import org.thingsboard.server.dao.model.sql.JobEntity;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for job entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */


@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {

    @Query("SELECT j FROM JobEntity j WHERE j.tenantId = :tenantId " +
           "AND (:types IS NULL OR j.type IN (:types)) " +
           "AND (:statuses IS NULL OR j.status IN (:statuses)) " +
           "AND (:entities IS NULL OR j.entityId IN :entities) " +
           "AND (:startTime <= 0 OR j.createdTime >= :startTime) " +
           "AND (:endTime <= 0 OR j.createdTime <= :endTime) " +
           "AND (:searchText IS NULL OR ilike(j.key, concat('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and types and statuses and entities and time and search text.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param types types ({@link List})
     * @param statuses statuses ({@link List})
     * @param entities entities ({@link List})
     * @param startTime start time
     * @param endTime end time
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<JobEntity> findByTenantIdAndTypesAndStatusesAndEntitiesAndTimeAndSearchText(@Param("tenantId") UUID tenantId,
                                                                                     @Param("types") List<JobType> types,
                                                                                     @Param("statuses") List<JobStatus> statuses,
                                                                                     @Param("entities") List<UUID> entities,
                                                                                     @Param("startTime") long startTime,
                                                                                     @Param("endTime") long endTime,
                                                                                     @Param("searchText") String searchText,
                                                                                     Pageable pageable);
    /**
     * Finds by id for update.
     *
     * @param id entity UUID primary key
     * @return {@link JobEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT * FROM job j WHERE j.id = :id FOR UPDATE", nativeQuery = true)
    JobEntity findByIdForUpdate(UUID id);

    @Query("SELECT j FROM JobEntity j WHERE j.tenantId = :tenantId AND j.key = :key " +
           "ORDER BY j.createdTime DESC")
    /**
     * Finds latest by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @param limit maximum number of records to return
     * @return {@link JobEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    JobEntity findLatestByTenantIdAndKey(@Param("tenantId") UUID tenantId, @Param("key") String key, Limit limit);
    /**
     * Exists by tenant id and key and status in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @param statuses statuses ({@link List})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndKeyAndStatusIn(UUID tenantId, String key, List<JobStatus> statuses);
    /**
     * Exists by tenant id and type and status in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link JobType})
     * @param statuses statuses ({@link List})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndTypeAndStatusIn(UUID tenantId, JobType type, List<JobStatus> statuses);
    /**
     * Exists by tenant id and entity id and status in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param statuses statuses ({@link List})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndEntityIdAndStatusIn(UUID tenantId, UUID entityId, List<JobStatus> statuses);

    @Query(value = "SELECT * FROM job j WHERE j.tenant_id = :tenantId AND j.type = :type " +
                   "AND j.status = :status ORDER BY j.created_time ASC, j.id ASC LIMIT 1 FOR UPDATE", nativeQuery = true)
    /**
     * Finds oldest by tenant id and type and status for update.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param status status ({@link String})
     * @return {@link JobEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    JobEntity findOldestByTenantIdAndTypeAndStatusForUpdate(UUID tenantId, String type, String status);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM JobEntity j WHERE j.tenantId = :tenantId")
    void deleteByTenantId(UUID tenantId);
    /**
     * Deletes by entity id.
     *
     * @param entityId target entity identifier
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM JobEntity j WHERE j.entityId = :entityId")
    int deleteByEntityId(UUID entityId);

}
