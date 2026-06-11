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
package org.thingsboard.server.dao.component;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.id.ComponentDescriptorId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.plugin.ComponentDescriptor;
import org.thingsboard.server.common.data.plugin.ComponentScope;
import org.thingsboard.server.common.data.plugin.ComponentType;

/**
 * DAO service API for component descriptor persistence and queries.
 *
 * <p>Implemented in the {@code dao} module.
 */

public interface ComponentDescriptorService {

    /**
     * Saves or persists component.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param component component ({@link ComponentDescriptor})
     * @return {@link ComponentDescriptor}
     */
    ComponentDescriptor saveComponent(TenantId tenantId, ComponentDescriptor component);

    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param componentId component id ({@link ComponentDescriptorId})
     * @return {@link ComponentDescriptor}
     */
    ComponentDescriptor findById(TenantId tenantId, ComponentDescriptorId componentId);

    /**
     * Finds by clazz.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param clazz clazz ({@link String})
     * @return {@link ComponentDescriptor}
     */
    ComponentDescriptor findByClazz(TenantId tenantId, String clazz);

    /**
     * Finds by type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link ComponentType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<ComponentDescriptor> findByTypeAndPageLink(TenantId tenantId, ComponentType type, PageLink pageLink);

    /**
     * Finds by scope and type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param scope scope ({@link ComponentScope})
     * @param type type ({@link ComponentType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<ComponentDescriptor> findByScopeAndTypeAndPageLink(TenantId tenantId, ComponentScope scope, ComponentType type, PageLink pageLink);

    /**
     * Validates the requested data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param component component ({@link ComponentDescriptor})
     * @param configuration configuration ({@link JsonNode})
     * @return the boolean result
     */
    boolean validate(TenantId tenantId, ComponentDescriptor component, JsonNode configuration);

    /**
     * Deletes by clazz.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param clazz clazz ({@link String})
     */
    void deleteByClazz(TenantId tenantId, String clazz);

}
