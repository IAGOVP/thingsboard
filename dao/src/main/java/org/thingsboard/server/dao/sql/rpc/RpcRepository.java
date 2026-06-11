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
package org.thingsboard.server.dao.sql.rpc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.rpc.RpcStatus;
import org.thingsboard.server.dao.model.sql.RpcEntity;

import java.util.UUID;


/**

 * Spring Data JPA repository for rpc entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface RpcRepository extends JpaRepository<RpcEntity, UUID> {
    /**
     * Finds all by tenant id and device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<RpcEntity> findAllByTenantIdAndDeviceId(UUID tenantId, UUID deviceId, Pageable pageable);
    /**
     * Finds all by tenant id and device id and status.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param status status ({@link RpcStatus})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<RpcEntity> findAllByTenantIdAndDeviceIdAndStatus(UUID tenantId, UUID deviceId, RpcStatus status, Pageable pageable);
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<RpcEntity> findAllByTenantId(UUID tenantId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM rpc WHERE id IN " +
            "(SELECT id FROM rpc WHERE tenant_id = :tenantId AND created_time < :expirationTime LIMIT :batchSize)",
            nativeQuery = true)
    /**
     * Deletes outdated rpc by tenant id batch.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param expirationTime expiration time ({@link Long})
     * @param batchSize batch size
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */
    int deleteOutdatedRpcByTenantIdBatch(@Param("tenantId") UUID tenantId,
                                         @Param("expirationTime") Long expirationTime,
                                         @Param("batchSize") int batchSize);

}
