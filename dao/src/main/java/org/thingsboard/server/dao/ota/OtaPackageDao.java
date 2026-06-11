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
package org.thingsboard.server.dao.ota;

import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityWithDataDao;

import java.util.UUID;


/**

 * Persistence contract for ota package.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (OTA firmware/software package metadata and data cache).

 */


public interface OtaPackageDao extends Dao<OtaPackage>, TenantEntityWithDataDao, ExportableEntityDao<OtaPackageId, OtaPackage> {
    /**
     * Sum data size by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long sumDataSizeByTenantId(TenantId tenantId);
    /**
     * Finds ota package by tenant id and title and version.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param title title ({@link String})
     * @param version version ({@link String})
     * @return {@link OtaPackage}
     * @throws Exception if an unexpected error occurs during processing
     */

    OtaPackage findOtaPackageByTenantIdAndTitleAndVersion(TenantId tenantId, String title, String version);
    /**
     * Returns data oid by id.
     *
     * @param id entity UUID primary key
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long getDataOidById(UUID id);
    /**
     * Unlink large object.
     *
     * @param dataOid data oid ({@link Long})
     * @return {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    Integer unlinkLargeObject(Long dataOid);

}
