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
package org.thingsboard.server.dao.sqlts.timescale;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvEntity;
import org.thingsboard.server.dao.util.TimescaleDBTsOrTsLatestDao;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for ts kv timescale entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */


@TimescaleDBTsOrTsLatestDao
public interface TsKvTimescaleRepository extends JpaRepository<TimescaleTsKvEntity, TimescaleTsKvCompositeKey> {

    @Query(value = "SELECT * FROM ts_kv WHERE entity_id = :entityId " +
            "AND key = :entityKey AND ts >= :startTs AND ts < :endTs", nativeQuery = true)
    /**
     * Finds all with limit.
     *
     * @param entityId target entity identifier
     * @param key attribute or cache key
     * @param startTs interval start timestamp (epoch ms)
     * @param endTs interval end timestamp (epoch ms)
     * @param pageable pageable ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<TimescaleTsKvEntity> findAllWithLimit(@Param("entityId") UUID entityId,
                                               @Param("entityKey") int key,
                                               @Param("startTs") long startTs,
                                               @Param("endTs") long endTs,
                                               Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM TimescaleTsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :entityKey " +
            "AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    /**
     * Deletes the requested data.
     *
     * @param entityId target entity identifier
     * @param key attribute or cache key
     * @param startTs interval start timestamp (epoch ms)
     * @param endTs interval end timestamp (epoch ms)
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */
    void delete(@Param("entityId") UUID entityId,
                @Param("entityKey") int key,
                @Param("startTs") long startTs,
                @Param("endTs") long endTs);

}
