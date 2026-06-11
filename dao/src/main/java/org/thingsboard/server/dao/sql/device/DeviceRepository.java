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
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.edqs.fields.DeviceFields;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for device entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface DeviceRepository extends JpaRepository<DeviceEntity, UUID>, ExportableEntityRepository<DeviceEntity> {
    /**
     * Finds device info by id.
     *
     * @param deviceId target device identifier
     * @return {@link DeviceInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT d FROM DeviceInfoEntity d WHERE d.id = :deviceId")
    DeviceInfoEntity findDeviceInfoById(@Param("deviceId") UUID deviceId);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                   @Param("customerId") UUID customerId,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :profileId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileId profile id ({@link UUID})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndProfileId(@Param("tenantId") UUID tenantId,
                                                  @Param("profileId") UUID profileId,
                                                  @Param("textSearch") String textSearch,
                                                  Pageable pageable);

    @Query("SELECT d FROM DeviceInfoEntity d " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds device infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                  @Param("customerId") UUID customerId,
                                                                  @Param("textSearch") String textSearch,
                                                                  Pageable pageable);
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    Page<DeviceEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                      Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                      @Param("textSearch") String textSearch,
                                      Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                             @Param("type") String type,
                                             @Param("textSearch") String textSearch,
                                             Pageable pageable);

    @Query("SELECT d.id FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND (:textSearch IS NULL OR ilike(d.type, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds ids by tenant id and device profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<UUID> findIdsByTenantIdAndDeviceProfileId(@Param("tenantId") UUID tenantId,
                                                   @Param("deviceProfileId") UUID deviceProfileId,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.firmwareId IS NULL")
    /**
     * Finds by tenant id and type and firmware id is null.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndTypeAndFirmwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                                @Param("deviceProfileId") UUID deviceProfileId,
                                                                Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.softwareId IS NULL")
    /**
     * Finds by tenant id and type and software id is null.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndTypeAndSoftwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                                @Param("deviceProfileId") UUID deviceProfileId,
                                                                Pageable pageable);

    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.firmwareId IS NULL")
    /**
     * Counts by tenant id and device profile id and firmware id is null.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */
    Long countByTenantIdAndDeviceProfileIdAndFirmwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                              @Param("deviceProfileId") UUID deviceProfileId);

    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.softwareId IS NULL")
    /**
     * Counts by tenant id and device profile id and software id is null.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */
    Long countByTenantIdAndDeviceProfileIdAndSoftwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                              @Param("deviceProfileId") UUID deviceProfileId);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                          @Param("customerId") UUID customerId,
                                                          @Param("type") String type,
                                                          @Param("textSearch") String textSearch,
                                                          Pageable pageable);

    @Query("SELECT d FROM DeviceInfoEntity d " +
            "WHERE d.tenantId = :tenantId " +
            "AND (:customerId IS NULL OR d.customerId = :customerId) " +
            "AND (:edgeId IS NULL OR d.id IN (SELECT re.toId FROM RelationEntity re WHERE re.toType = 'DEVICE' AND re.relationTypeGroup = 'EDGE' AND re.relationType = 'Contains' AND re.fromType = 'EDGE' AND re.fromId = :edgeId)) " +
            "AND ((:deviceType) IS NULL OR d.type = :deviceType) " +
            "AND (:deviceProfileId IS NULL OR d.deviceProfileId = :deviceProfileId) " +
            "AND ((:filterByActive) = FALSE OR d.active = :deviceActive) " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.type, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.customerTitle, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds device infos by filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param deviceProfileId device profile id ({@link UUID})
     * @param filterByActive filter by active
     * @param active active
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceInfoEntity> findDeviceInfosByFilter(@Param("tenantId") UUID tenantId,
                                                   @Param("customerId") UUID customerId,
                                                   @Param("edgeId") UUID edgeId,
                                                   @Param("deviceType") String type,
                                                   @Param("deviceProfileId") UUID deviceProfileId,
                                                   @Param("filterByActive") boolean filterByActive,
                                                   @Param("deviceActive") boolean active,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link DeviceEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(a.id, 'DEVICE', a.name) " +
            "FROM DeviceEntity a WHERE a.tenantId = :tenantId AND a.name LIKE CONCAT(:prefix, '%')")
    /**
     * Finds entity infos by name prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param prefix prefix ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findEntityInfosByNamePrefix(UUID tenantId, String prefix);
    /**
     * Finds devices by tenant id and customer id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param deviceIds device ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<DeviceEntity> findDevicesByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> deviceIds);
    /**
     * Finds devices by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceIds device ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<DeviceEntity> findDevicesByTenantIdAndIdIn(UUID tenantId, List<UUID> deviceIds);
    /**
     * Finds devices by id in.
     *
     * @param deviceIds device ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<DeviceEntity> findDevicesByIdIn(List<UUID> deviceIds);
    /**
     * Finds by tenant id and id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @return {@link DeviceEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceEntity findByTenantIdAndId(UUID tenantId, UUID id);
    /**
     * Counts by device profile id.
     *
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long countByDeviceProfileId(UUID deviceProfileId);

    @Query("SELECT d FROM DeviceEntity d, RelationEntity re WHERE d.tenantId = :tenantId " +
            "AND d.id = re.toId AND re.toType = 'DEVICE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                               @Param("edgeId") UUID edgeId,
                                               @Param("textSearch") String textSearch,
                                               Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d, RelationEntity re WHERE d.tenantId = :tenantId " +
            "AND d.id = re.toId AND re.toType = 'DEVICE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true " +
            "OR ilike(d.label, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<DeviceEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                      @Param("edgeId") UUID edgeId,
                                                      @Param("type") String type,
                                                      @Param("textSearch") String textSearch,
                                                      Pageable pageable);

    
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT d.id FROM DeviceEntity d " +
            "INNER JOIN DeviceProfileEntity p ON d.deviceProfileId = p.id " +
            "WHERE p.transportType = :transportType")
    /**
     * Finds ids by device profile transport type.
     *
     * @param transportType transport type ({@link DeviceTransportType})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<UUID> findIdsByDeviceProfileTransportType(@Param("transportType") DeviceTransportType transportType, Pageable pageable);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM DeviceEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);


    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.DeviceFields(d.id, d.createdTime, d.tenantId, d.customerId," +
            "d.name, d.version, d.type, d.label, d.deviceProfileId, d.additionalInfo) FROM DeviceEntity d WHERE d.id > :id ORDER BY d.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<DeviceFields> findNextBatch(@Param("id") UUID id, Limit limit);

}
