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
package org.thingsboard.server.dao.cf;

import org.thingsboard.server.common.data.cf.CalculatedField;
import org.thingsboard.server.common.data.cf.CalculatedFieldFilter;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.Set;


/**

 * Persistence contract for calculated field.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (calculated-field definitions and evaluation state).

 */


public interface CalculatedFieldDao extends Dao<CalculatedField> {
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedField> findAllByTenantId(TenantId tenantId);
    /**
     * Finds calculated field ids by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedFieldId> findCalculatedFieldIdsByEntityId(TenantId tenantId, EntityId entityId);
    /**
     * Finds calculated fields by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedField> findCalculatedFieldsByEntityId(TenantId tenantId, EntityId entityId);
    /**
     * Finds all.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedField> findAll();
    /**
     * Finds by entity id and type and name.
     *
     * @param entityId target entity identifier
     * @param type type ({@link CalculatedFieldType})
     * @param name entity or attribute name
     * @return {@link CalculatedField}
     * @throws Exception if an unexpected error occurs during processing
     */

    CalculatedField findByEntityIdAndTypeAndName(EntityId entityId, CalculatedFieldType type, String name);
    /**
     * Finds all.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<CalculatedField> findAll(PageLink pageLink);
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<CalculatedField> findAllByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Finds by entity id and types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param types types ({@link Set})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<CalculatedField> findByEntityIdAndTypes(TenantId tenantId, EntityId entityId, Set<CalculatedFieldType> types, PageLink pageLink);
    /**
     * Removes all by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedField> removeAllByEntityId(TenantId tenantId, EntityId entityId);
    /**
     * Counts by entity id and type not.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param type type ({@link CalculatedFieldType})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    long countByEntityIdAndTypeNot(TenantId tenantId, EntityId entityId, CalculatedFieldType type);
    /**
     * Finds by tenant id and filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filter filter ({@link CalculatedFieldFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<CalculatedField> findByTenantIdAndFilter(TenantId tenantId, CalculatedFieldFilter filter, PageLink pageLink);
    /**
     * Finds names by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link CalculatedFieldType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<String> findNamesByTenantIdAndType(TenantId tenantId, CalculatedFieldType type, PageLink pageLink);

}
