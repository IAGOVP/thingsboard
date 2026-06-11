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
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.edqs.fields.AssetProfileFields;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.AssetProfileEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for asset profile entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface AssetProfileRepository extends JpaRepository<AssetProfileEntity, UUID>, ExportableEntityRepository<AssetProfileEntity> {

    @Query("SELECT new org.thingsboard.server.common.data.asset.AssetProfileInfo(a.id, a.tenantId, a.name, a.image, a.defaultDashboardId) " +
            "FROM AssetProfileEntity a " +
            "WHERE a.id = :assetProfileId")
    /**
     * Finds asset profile info by id.
     *
     * @param assetProfileId asset profile id ({@link UUID})
     * @return {@link AssetProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */
    AssetProfileInfo findAssetProfileInfoById(@Param("assetProfileId") UUID assetProfileId);

    @Query("SELECT a FROM AssetProfileEntity a WHERE " +
            "a.tenantId = :tenantId AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds asset profiles.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetProfileEntity> findAssetProfiles(@Param("tenantId") UUID tenantId,
                                               @Param("textSearch") String textSearch,
                                               Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.asset.AssetProfileInfo(a.id, a.tenantId, a.name, a.image, a.defaultDashboardId) " +
            "FROM AssetProfileEntity a WHERE " +
            "a.tenantId = :tenantId AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    /**
     * Finds asset profile infos.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param textSearch text search ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<AssetProfileInfo> findAssetProfileInfos(@Param("tenantId") UUID tenantId,
                                                 @Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT a FROM AssetProfileEntity a " +
            "WHERE a.tenantId = :tenantId AND a.isDefault = true")
    /**
     * Finds by default true and tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfileEntity}
     * @throws Exception if an unexpected error occurs during processing
     */
    AssetProfileEntity findByDefaultTrueAndTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT new org.thingsboard.server.common.data.asset.AssetProfileInfo(a.id, a.tenantId, a.name, a.image, a.defaultDashboardId) " +
            "FROM AssetProfileEntity a " +
            "WHERE a.tenantId = :tenantId AND a.isDefault = true")
    /**
     * Finds default asset profile info.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link AssetProfileInfo}
     * @throws Exception if an unexpected error occurs during processing
     */
    AssetProfileInfo findDefaultAssetProfileInfo(@Param("tenantId") UUID tenantId);
    /**
     * Finds by tenant id and name.
     *
     * @param id entity UUID primary key
     * @param profileName profile name ({@link String})
     * @return {@link AssetProfileEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfileEntity findByTenantIdAndName(UUID id, String profileName);
    /**
     * Returns external id by id.
     *
     * @param id entity UUID primary key
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT externalId FROM AssetProfileEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query("SELECT new org.thingsboard.server.common.data.asset.AssetProfileInfo(a.id, a.tenantId, a.name, a.image, a.defaultDashboardId) " +
            "FROM AssetProfileEntity a WHERE a.tenantId = :tenantId AND a.image = :imageLink")
    /**
     * Finds by tenant and image link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageLink image link ({@link String})
     * @param page page ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AssetProfileInfo> findByTenantAndImageLink(@Param("tenantId") UUID tenantId, @Param("imageLink") String imageLink, Pageable page);

    @Query("SELECT new org.thingsboard.server.common.data.asset.AssetProfileInfo(a.id, a.tenantId, a.name, a.image, a.defaultDashboardId) " +
            "FROM AssetProfileEntity a WHERE a.image = :imageLink")
    /**
     * Finds by image link.
     *
     * @param imageLink image link ({@link String})
     * @param page page ({@link Pageable})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AssetProfileInfo> findByImageLink(@Param("imageLink") String imageLink, Pageable page);
    /**
     * Finds all by image not null.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    Page<AssetProfileEntity> findAllByImageNotNull(Pageable pageable);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(ap.id, 'ASSET_PROFILE', ap.name) " +
            "FROM AssetProfileEntity ap WHERE ap.tenantId = :tenantId AND EXISTS " +
            "(SELECT 1 FROM AssetEntity a WHERE a.tenantId = :tenantId AND a.assetProfileId = ap.id)")
    /**
     * Finds active tenant asset profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findActiveTenantAssetProfileNames(@Param("tenantId") UUID tenantId);

    @Query("SELECT new org.thingsboard.server.common.data.EntityInfo(a.id, 'ASSET_PROFILE', a.name) " +
            "FROM AssetProfileEntity a WHERE a.tenantId = :tenantId")
    /**
     * Finds all tenant asset profile names.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<EntityInfo> findAllTenantAssetProfileNames(@Param("tenantId") UUID tenantId);

    @Query("SELECT new org.thingsboard.server.common.data.edqs.fields.AssetProfileFields(a.id, a.createdTime, a.tenantId," +
            "a.name, a.version, a.isDefault) FROM AssetProfileEntity a WHERE a.id > :id ORDER BY a.id")
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AssetProfileFields> findNextBatch(@Param("id") UUID id, Limit limit);

    @Query("SELECT new org.thingsboard.server.common.data.asset.AssetProfileInfo(a.id, a.tenantId, a.name, a.image, a.defaultDashboardId) " +
            "FROM AssetProfileEntity a WHERE " +
            "a.tenantId = :tenantId AND a.id IN :assetProfileIds")
    /**
     * Finds asset profile infos by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param assetProfileIds asset profile ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AssetProfileInfo> findAssetProfileInfosByTenantIdAndIdIn(@Param("tenantId") UUID tenantId,
                                                                  @Param("assetProfileIds") List<UUID> assetProfileIds);

}
