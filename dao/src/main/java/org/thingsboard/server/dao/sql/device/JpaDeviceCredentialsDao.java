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
package org.thingsboard.server.dao.sql.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.device.DeviceCredentialsDao;
import org.thingsboard.server.dao.model.sql.DeviceCredentialsEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;

/**
 * JPA/PostgreSQL implementation of device credentials dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */

@Slf4j
@Component
@SqlDao
public class JpaDeviceCredentialsDao extends JpaAbstractDao<DeviceCredentialsEntity, DeviceCredentials> implements DeviceCredentialsDao, TenantEntityDao<DeviceCredentials> {

    @Autowired
    private DeviceCredentialsRepository deviceCredentialsRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<DeviceCredentialsEntity> getEntityClass() {
        return DeviceCredentialsEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<DeviceCredentialsEntity, UUID> getRepository() {
        return deviceCredentialsRepository;
    }

    
    /**
     * Finds by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceCredentials findByDeviceId(TenantId tenantId, UUID deviceId) {
        return DaoUtil.getData(deviceCredentialsRepository.findByDeviceId(deviceId));
    }

    
    /**
     * Finds by credentials id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param credentialsId credentials id ({@link String})
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceCredentials findByCredentialsId(TenantId tenantId, String credentialsId) {
        log.trace("[{}] findByCredentialsId [{}]", tenantId, credentialsId);
        return DaoUtil.getData(deviceCredentialsRepository.findByCredentialsId(credentialsId));
    }

    
    /**
     * Removes by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @return {@link DeviceCredentials}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public DeviceCredentials removeByDeviceId(TenantId tenantId, DeviceId deviceId) {
        return DaoUtil.getData(deviceCredentialsRepository.deleteByDeviceId(deviceId.getId()));
    }

    
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<DeviceCredentials> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(deviceCredentialsRepository.findByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }

}
