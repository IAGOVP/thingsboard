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
import org.thingsboard.server.common.data.cf.CalculatedFieldInfo;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

/**
 * Service API for calculated field persistence and domain operations.
 */
public interface CalculatedFieldService extends EntityDaoService {

    /**
     * Saves or persists the requested data.
     *
     * @param calculatedField calculated field ({@link CalculatedField})
     * @return {@link CalculatedField}
     */
    CalculatedField save(CalculatedField calculatedField);

    /**
     * Saves or persists the requested data.
     *
     * @param calculatedField calculated field ({@link CalculatedField})
     * @param doValidate whether to run validation before persist
     * @return {@link CalculatedField}
     */
    CalculatedField save(CalculatedField calculatedField, boolean doValidate);

    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
     * @return {@link CalculatedField}
     */
    CalculatedField findById(TenantId tenantId, CalculatedFieldId calculatedFieldId);

    /**
     * Finds by entity id and type and name.
     *
     * @param entityId entity id ({@link EntityId})
     * @param type type ({@link CalculatedFieldType})
     * @param name entity name (unique within tenant scope where applicable)
     * @return {@link CalculatedField}
     */
    CalculatedField findByEntityIdAndTypeAndName(EntityId entityId, CalculatedFieldType type, String name);

    /**
     * Finds calculated field ids by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return {@link List}
     */
    List<CalculatedFieldId> findCalculatedFieldIdsByEntityId(TenantId tenantId, EntityId entityId);

    /**
     * Finds calculated fields by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return {@link List}
     */
    List<CalculatedField> findCalculatedFieldsByEntityId(TenantId tenantId, EntityId entityId);

    /**
     * Finds all calculated fields.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<CalculatedField> findAllCalculatedFields(PageLink pageLink);

    /**
     * Finds calculated fields by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<CalculatedField> findCalculatedFieldsByTenantId(TenantId tenantId, PageLink pageLink);

    /**
     * Finds calculated fields by tenant id and filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filter filter ({@link CalculatedFieldFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<CalculatedFieldInfo> findCalculatedFieldsByTenantIdAndFilter(TenantId tenantId, CalculatedFieldFilter filter, PageLink pageLink);

    /**
     * Finds calculated field names by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link CalculatedFieldType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<String> findCalculatedFieldNamesByTenantIdAndType(TenantId tenantId, CalculatedFieldType type, PageLink pageLink);

    /**
     * Finds calculated fields by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param type type ({@link CalculatedFieldType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<CalculatedField> findCalculatedFieldsByEntityId(TenantId tenantId, EntityId entityId, CalculatedFieldType type, PageLink pageLink);

    /**
     * Deletes calculated field.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param calculatedFieldId calculated field id ({@link CalculatedFieldId})
     */
    void deleteCalculatedField(TenantId tenantId, CalculatedFieldId calculatedFieldId);

    /**
     * Deletes all calculated fields by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return the int result
     */
    int deleteAllCalculatedFieldsByEntityId(TenantId tenantId, EntityId entityId);

    /**
     * Referenced in any calculated field.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param referencedEntityId referenced entity id ({@link EntityId})
     * @return the boolean result
     */
    boolean referencedInAnyCalculatedField(TenantId tenantId, EntityId referencedEntityId);

}
