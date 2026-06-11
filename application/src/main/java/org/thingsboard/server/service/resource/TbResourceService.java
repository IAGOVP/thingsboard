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
package org.thingsboard.server.service.resource;

import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.ResourceExportData;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceDeleteResult;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.lwm2m.LwM2mObject;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.List;

/**

 * Service contract for tb resource operations (tenant/system resource file management).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface TbResourceService {

    default TbResourceInfo save(TbResource entity) throws Exception {
        /**
         * Saves or persists the requested data.
         *
         * @return the return value
         * @throws Exception if an unexpected error occurs during processing
         */
        return save(entity, null);
    }
/**
 * Saves or persists the requested data.
 *
 * @param entity entity ({@link TbResource})
 * @param user authenticated user performing the action
 * @return {@link TbResourceInfo}
 * @throws Exception if an unexpected error occurs during processing
 */

    TbResourceInfo save(TbResource entity, SecurityUser user) throws Exception;

    /**
     * Deletes the requested data.
     *
     * @param entity entity ({@link TbResourceInfo})
     * @param force force
     * @param user authenticated user performing the action
     * @return {@link TbResourceDeleteResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceDeleteResult delete(TbResourceInfo entity, boolean force, User user);

    /**
     * Finds lw m2m object.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param sortOrder sort order ({@link String})
     * @param sortProperty sort property ({@link String})
     * @param objectIds object ids
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<LwM2mObject> findLwM2mObject(TenantId tenantId,
                                      String sortOrder,
                                      String sortProperty,
                                      String[] objectIds);

    /**
     * Finds lw m2m object page.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param sortProperty sort property ({@link String})
     * @param sortOrder sort order ({@link String})
     * @param pageLink pagination and sort parameters
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<LwM2mObject> findLwM2mObjectPage(TenantId tenantId,
                                          String sortProperty,
                                          String sortOrder,
                                          PageLink pageLink);

    /**
     * Exports resources.
     *
     * @param dashboard dashboard ({@link Dashboard})
     * @param user authenticated user performing the action
     * @return {@link List}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    List<ResourceExportData> exportResources(Dashboard dashboard, SecurityUser user) throws ThingsboardException;

    /**
     * Exports resources.
     *
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @param user authenticated user performing the action
     * @return {@link List}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    List<ResourceExportData> exportResources(WidgetTypeDetails widgetTypeDetails, SecurityUser user) throws ThingsboardException;

    /**
     * Imports resources.
     *
     * @param resources resources ({@link List})
     * @param user authenticated user performing the action
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void importResources(List<ResourceExportData> resources, SecurityUser user) throws Exception;

}
