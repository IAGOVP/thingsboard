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
package org.thingsboard.server.dao.sql.cf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.dao.model.sql.CalculatedFieldEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;


/**

 * Spring Data JPA repository for calculated field entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface CalculatedFieldRepository extends JpaRepository<CalculatedFieldEntity, UUID> {
    /**
     * Exists by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean existsByTenantIdAndEntityId(UUID tenantId, UUID entityId);
    /**
     * Finds by entity id and type and name.
     *
     * @param entityId target entity identifier
     * @param type type ({@link String})
     * @param name entity or attribute name
     * @return {@link CalculatedFieldEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    CalculatedFieldEntity findByEntityIdAndTypeAndName(UUID entityId, String type, String name);
    /**
     * Finds calculated field ids by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedFieldId> findCalculatedFieldIdsByTenantIdAndEntityId(UUID tenantId, UUID entityId);
    /**
     * Finds all by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedFieldEntity> findAllByTenantIdAndEntityId(UUID tenantId, UUID entityId);
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<CalculatedFieldEntity> findAllByTenantId(UUID tenantId, Pageable pageable);

    @Query("SELECT cf FROM CalculatedFieldEntity cf WHERE cf.tenantId = :tenantId " +
           "AND cf.entityId = :entityId AND cf.type IN :types " +
           "AND (:textSearch IS NULL OR ilike(cf.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and entity id and types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param types types ({@link List})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<CalculatedFieldEntity> findByTenantIdAndEntityIdAndTypes(UUID tenantId, UUID entityId, List<String> types, String textSearch, Pageable pageable);

    @Query("SELECT cf FROM CalculatedFieldEntity cf WHERE cf.tenantId = :tenantId " +
           "AND cf.type IN :types " +
           "AND cf.entityType IN :entityTypes " +
           "AND (:entityIds IS NULL OR cf.entityId IN :entityIds) " +
           "AND (:names IS NULL OR cf.name IN :names) " +
           "AND (:textSearch IS NULL OR ilike(cf.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param types types ({@link List})
     * @param entityTypes entity types ({@link List})
     * @param entityIds entity ids ({@link Set})
     * @param names names ({@link Set})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<CalculatedFieldEntity> findByTenantIdAndFilter(UUID tenantId, List<String> types, List<String> entityTypes,
                                                        Set<UUID> entityIds, Set<String> names, String textSearch, Pageable pageable);

    @Query("SELECT DISTINCT cf.name FROM CalculatedFieldEntity cf " +
           "WHERE cf.tenantId = :tenantId AND cf.type = :type AND " +
           "(:textSearch IS NULL OR ilike(cf.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds names by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<String> findNamesByTenantIdAndType(UUID tenantId, String type, String textSearch, Pageable pageable);
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedFieldEntity> findAllByTenantId(UUID tenantId);
    /**
     * Removes all by tenant id and entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedFieldEntity> removeAllByTenantIdAndEntityId(UUID tenantId, UUID entityId);
    /**
     * Counts by tenant id and entity id and type not.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param type type ({@link String})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    long countByTenantIdAndEntityIdAndTypeNot(UUID tenantId, UUID entityId, String type);

}
