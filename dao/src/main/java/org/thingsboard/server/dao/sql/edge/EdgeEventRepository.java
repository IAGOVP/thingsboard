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
package org.thingsboard.server.dao.sql.edge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.EdgeEventEntity;

import java.util.UUID;


/**

 * Spring Data JPA repository for edge event entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface EdgeEventRepository extends JpaRepository<EdgeEventEntity, UUID>, JpaSpecificationExecutor<EdgeEventEntity> {

    @Query("SELECT e FROM EdgeEventEntity e WHERE " +
            "e.tenantId = :tenantId " +
            "AND e.edgeId = :edgeId " +
            "AND (:startTime IS NULL OR e.createdTime >= :startTime) " +
            "AND (:endTime IS NULL OR e.createdTime <= :endTime) " +
            "AND (:seqIdStart IS NULL OR e.seqId > :seqIdStart) " +
            "AND (:seqIdEnd IS NULL OR e.seqId < :seqIdEnd) " +
            "AND (:textSearch IS NULL OR ilike(e.edgeEventType, CONCAT('%', :textSearch, '%')) = true)"
    )
    /**
     * Finds edge events by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param textSearch text search ({@link String})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param seqIdStart seq id start ({@link Long})
     * @param seqIdEnd seq id end ({@link Long})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<EdgeEventEntity> findEdgeEventsByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                                            @Param("edgeId") UUID edgeId,
                                                            @Param("textSearch") String textSearch,
                                                            @Param("startTime") Long startTime,
                                                            @Param("endTime") Long endTime,
                                                            @Param("seqIdStart") Long seqIdStart,
                                                            @Param("seqIdEnd") Long seqIdEnd,
                                                            Pageable pageable);
}
