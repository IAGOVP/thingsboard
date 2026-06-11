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
package org.thingsboard.server.dao.sql.component;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.plugin.ComponentScope;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.dao.model.sql.ComponentDescriptorEntity;

import java.util.UUID;

/**
 * Spring Data JPA repository for component descriptor entities.
 *
 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.
 */

public interface ComponentDescriptorRepository extends JpaRepository<ComponentDescriptorEntity, UUID> {
    /**
     * Finds by clazz.
     *
     * @param clazz clazz ({@link String})
     * @return {@link ComponentDescriptorEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    ComponentDescriptorEntity findByClazz(String clazz);

    @Query("SELECT cd FROM ComponentDescriptorEntity cd WHERE cd.type = :type " +
            "AND (:textSearch IS NULL OR ilike(cd.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by type.
     *
     * @param type type ({@link ComponentType})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<ComponentDescriptorEntity> findByType(@Param("type") ComponentType type,
                                               @Param("textSearch") String textSearch,
                                               Pageable pageable);

    @Query("SELECT cd FROM ComponentDescriptorEntity cd WHERE cd.type = :type AND cd.scope = :scope " +
            "AND (:textSearch IS NULL OR ilike(cd.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by scope and type.
     *
     * @param type type ({@link ComponentType})
     * @param scope attribute scope (SERVER_SCOPE, SHARED_SCOPE, etc.)
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<ComponentDescriptorEntity> findByScopeAndType(@Param("type") ComponentType type,
                                                       @Param("scope") ComponentScope scope,
                                                       @Param("textSearch") String textSearch,
                                                       Pageable pageable);
    /**
     * Deletes by clazz.
     *
     * @param clazz clazz ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM ComponentDescriptorEntity cd where cd.clazz = :clazz")
    void deleteByClazz(@Param("clazz") String clazz);
}
