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
package org.thingsboard.server.dao.sql.asset;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.edqs.fields.AssetFields;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.AssetEntity;
import org.thingsboard.server.dao.model.sql.AssetInfoEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for asset entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface AssetRepository extends JpaRepository<AssetEntity, UUID>, ExportableEntityRepository<AssetEntity> {

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.id = :assetId")
    /**
     * Finds asset info by id.
     *
     * @param assetId asset id ({@link UUID})
     * @return {@link AssetInfoEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    AssetInfoEntity findAssetInfoById(@Param("assetId") UUID assetId);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.type, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                     @Param("textSearch") String textSearch,
                                     Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(p.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds asset infos by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetInfoEntity> findAssetInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.type, CONCAT('%', :textSearch, '%')) = true)")
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
    Page<AssetEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                  @Param("customerId") UUID customerId,
                                                  @Param("textSearch") String textSearch,
                                                  Pageable pageable);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.assetProfileId = :profileId " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id and profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param profileId profile id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetEntity> findByTenantIdAndProfileId(@Param("tenantId") UUID tenantId,
                                                 @Param("profileId") UUID profileId,
                                                 @Param("searchText") String searchText,
                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(p.name, CONCAT('%', :searchText, '%')) = true) ")
    /**
     * Finds asset infos by tenant id and customer id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                @Param("customerId") UUID customerId,
                                                                @Param("searchText") String searchText,
                                                                Pageable pageable);
    /**
     * Finds by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetIds asset ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<AssetEntity> findByTenantIdAndIdIn(UUID tenantId, List<UUID> assetIds);
    /**
     * Finds by tenant id and customer id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param assetIds asset ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<AssetEntity> findByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> assetIds);
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link AssetEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(a.id, 'ASSET', a.name) " +
            "FROM AssetEntity a WHERE a.tenantId = :tenantId AND a.name LIKE CONCAT(:prefix, '%')")
    /**
     * Finds entity infos by name prefix.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param prefix prefix ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findEntityInfosByNamePrefix(UUID tenantId, String prefix);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true)")
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
    Page<AssetEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                            @Param("type") String type,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true) ")
    /**
     * Finds asset infos by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                          @Param("type") String type,
                                                          @Param("textSearch") String textSearch,
                                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.assetProfileId = :assetProfileId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.type, CONCAT('%', :textSearch, '%')) = true) ")
    /**
     * Finds asset infos by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndAssetProfileId(@Param("tenantId") UUID tenantId,
                                                                    @Param("assetProfileId") UUID assetProfileId,
                                                                    @Param("textSearch") String textSearch,
                                                                    Pageable pageable);

    @Query("SELECT a.id FROM AssetEntity a " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.assetProfileId = :assetProfileId " +
            "AND (:textSearch IS NULL OR ilike(a.type, CONCAT('%', :textSearch, '%')) = true) ")
    /**
     * Finds asset ids by tenant id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileId asset profile id ({@link UUID})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<UUID> findAssetIdsByTenantIdAndAssetProfileId(@Param("tenantId") UUID tenantId,
                                                       @Param("assetProfileId") UUID assetProfileId,
                                                       @Param("textSearch") String textSearch,
                                                       Pageable pageable);


    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true) ")
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
    Page<AssetEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                         @Param("customerId") UUID customerId,
                                                         @Param("type") String type,
                                                         @Param("textSearch") String textSearch,
                                                         Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true) ")
    /**
     * Finds asset infos by tenant id and customer id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param type type ({@link String})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                       @Param("customerId") UUID customerId,
                                                                       @Param("type") String type,
                                                                       @Param("textSearch") String textSearch,
                                                                       Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND a.assetProfileId = :assetProfileId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(a.type, CONCAT('%', :textSearch, '%')) = true) ")
    /**
     * Finds asset infos by tenant id and customer id and asset profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param assetProfileId asset profile id ({@link UUID})
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerIdAndAssetProfileId(@Param("tenantId") UUID tenantId,
                                                                                 @Param("customerId") UUID customerId,
                                                                                 @Param("assetProfileId") UUID assetProfileId,
                                                                                 @Param("textSearch") String textSearch,
                                                                                 Pageable pageable);
    /**
     * Counts by asset profile id.
     *
     * @param assetProfileId asset profile id ({@link UUID})
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long countByAssetProfileId(UUID assetProfileId);

    @Query("SELECT a FROM AssetEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'ASSET' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.type, CONCAT('%', :searchText, '%')) = true) ")
    /**
     * Finds by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                              @Param("edgeId") UUID edgeId,
                                              @Param("searchText") String searchText,
                                              Pageable pageable);

    @Query("SELECT a FROM AssetEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'ASSET' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND a.type = :type " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true " +
            "  OR ilike(a.label, CONCAT('%', :searchText, '%')) = true) ")
    /**
     * Finds by tenant id and edge id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param type type ({@link String})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                     @Param("edgeId") UUID edgeId,
                                                     @Param("type") String type,
                                                     @Param("searchText") String searchText,
                                                     Pageable pageable);
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    Long countByTenantId(UUID tenantId);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM AssetEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);
    /**
     * Returns all asset types.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "SELECT DISTINCT new org.thingsboard.server.common.data.util.TbPair(a.tenantId , a.type) FROM  AssetEntity a")
    Page<TbPair<UUID, String>> getAllAssetTypes(Pageable pageable);


    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.AssetFields(a.id, a.createdTime, a.tenantId, a.customerId," +
            "a.name, a.version, a.type, a.label, a.assetProfileId, a.additionalInfo) FROM AssetEntity a WHERE a.id > :id ORDER BY a.id")
    /**
     * Finds all fields.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AssetFields> findAllFields(@Param("id") UUID id, Limit limit);

}
