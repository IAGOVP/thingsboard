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

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.DeviceProfileInfo;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.edqs.fields.DeviceProfileFields;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.DeviceProfileEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for device profile entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface DeviceProfileRepository extends JpaRepository<DeviceProfileEntity, UUID>, ExportableEntityRepository<DeviceProfileEntity> {

    @Query("SELECT new org.thingsboard.server.common.data.DeviceProfileInfo(d.id, d.tenantId, d.name, d.image, d.defaultDashboardId, d.type, d.transportType) " +
            "FROM DeviceProfileEntity d " +
            "WHERE d.id = :deviceProfileId")
    /**
     * Finds device profile info by id.
     *
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link DeviceProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */
    DeviceProfileInfo findDeviceProfileInfoById(@Param("deviceProfileId") UUID deviceProfileId);

    @Query("SELECT d FROM DeviceProfileEntity d WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds device profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceProfileEntity> findDeviceProfiles(@Param("tenantId") UUID tenantId,
                                                 @Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.DeviceProfileInfo(d.id, d.tenantId, d.name, d.image, d.defaultDashboardId, d.type, d.transportType) " +
            "FROM DeviceProfileEntity d WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds device profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceProfileInfo> findDeviceProfileInfos(@Param("tenantId") UUID tenantId,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.DeviceProfileInfo(d.id, d.tenantId, d.name, d.image, d.defaultDashboardId, d.type, d.transportType) " +
            "FROM DeviceProfileEntity d WHERE d.tenantId = :tenantId AND d.transportType = :transportType " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds device profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param transportType transport type ({@link DeviceTransportType})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceProfileInfo> findDeviceProfileInfos(@Param("tenantId") UUID tenantId,
                                                   @Param("textSearch") String textSearch,
                                                   @Param("transportType") DeviceTransportType transportType,
                                                   Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.DeviceProfileInfo(d.id, d.tenantId, d.name, d.image, d.defaultDashboardId, d.type, d.transportType) " +
            "FROM DeviceProfileEntity d WHERE " +
            "d.tenantId = :tenantId AND d.id IN :deviceProfileIds")
    /**
     * Finds device profile infos by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileIds device profile ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<DeviceProfileInfo> findDeviceProfileInfosByTenantIdAndIdIn(@Param("tenantId") UUID tenantId, @Param("deviceProfileIds") List<UUID> deviceProfileIds);

    @Query("SELECT d FROM DeviceProfileEntity d " +
            "WHERE d.tenantId = :tenantId AND d.isDefault = true")
    /**
     * Finds by default true and tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfileEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    DeviceProfileEntity findByDefaultTrueAndTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT new org.thingsboard.server.common.data.DeviceProfileInfo(d.id, d.tenantId, d.name, d.image, d.defaultDashboardId, d.type, d.transportType) " +
            "FROM DeviceProfileEntity d " +
            "WHERE d.tenantId = :tenantId AND d.isDefault = true")
    /**
     * Finds default device profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link DeviceProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */
    DeviceProfileInfo findDefaultDeviceProfileInfo(@Param("tenantId") UUID tenantId);
    /**
     * Finds by tenant id and name.
     *
     * @param id entity UUID primary key
     * @param profileName profile name ({@link String})
     * @return {@link DeviceProfileEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfileEntity findByTenantIdAndName(UUID id, String profileName);
    /**
     * Finds by provision device key.
     *
     * @param provisionDeviceKey provision device key ({@link String})
     * @return {@link DeviceProfileEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfileEntity findByProvisionDeviceKey(@Param("provisionDeviceKey") String provisionDeviceKey);

    @Query("SELECT new org.thingsboard.server.common.data.DeviceProfileInfo(d.id, d.tenantId, d.name, d.image, d.defaultDashboardId, d.type, d.transportType) " +
            "FROM DeviceProfileEntity d WHERE d.tenantId = :tenantId AND d.image = :imageLink")
    /**
     * Finds by tenant and image link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageLink image link ({@link String})
     * @param page page ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<DeviceProfileInfo> findByTenantAndImageLink(@Param("tenantId") UUID tenantId, @Param("imageLink") String imageLink, Pageable page);

    @Query("SELECT new org.thingsboard.server.common.data.DeviceProfileInfo(d.id, d.tenantId, d.name, d.image, d.defaultDashboardId, d.type, d.transportType) " +
            "FROM DeviceProfileEntity d WHERE d.image = :imageLink")
    /**
     * Finds by image link.
     *
     * @param imageLink image link ({@link String})
     * @param page page ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<DeviceProfileInfo> findByImageLink(@Param("imageLink") String imageLink, Pageable page);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM DeviceProfileEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);
    /**
     * Finds all by image not null.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<DeviceProfileEntity> findAllByImageNotNull(Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(dp.id, 'DEVICE_PROFILE', dp.name) " +
            "FROM DeviceProfileEntity dp WHERE dp.tenantId = :tenantId AND EXISTS " +
            "(SELECT 1 FROM DeviceEntity dv WHERE dv.tenantId = :tenantId AND dv.deviceProfileId = dp.id)")
    /**
     * Finds active tenant device profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findActiveTenantDeviceProfileNames(@Param("tenantId") UUID tenantId);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(d.id, 'DEVICE_PROFILE', d.name) " +
            "FROM DeviceProfileEntity d WHERE d.tenantId = :tenantId")
    /**
     * Finds all tenant device profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findAllTenantDeviceProfileNames(@Param("tenantId") UUID tenantId);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.DeviceProfileFields(d.id, d.createdTime, d.tenantId," +
            "d.name, d.version, d.type, d.isDefault) FROM DeviceProfileEntity d WHERE d.id > :id ORDER BY d.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<DeviceProfileFields> findNextBatch(@Param("id") UUID id, Limit limit);
}
