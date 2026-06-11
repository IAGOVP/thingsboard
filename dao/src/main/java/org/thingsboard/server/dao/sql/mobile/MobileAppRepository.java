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
package org.thingsboard.server.dao.sql.mobile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.dao.model.sql.MobileAppEntity;

import java.util.UUID;


/**

 * Spring Data JPA repository for mobile app entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface MobileAppRepository extends JpaRepository<MobileAppEntity, UUID> {

    @Query("SELECT a FROM MobileAppEntity a WHERE a.tenantId = :tenantId AND " +
            "(:platformType is NULL OR a.platformType = :platformType) AND" +
            "(:searchText is NULL OR ilike(a.pkgName, concat('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param platformType platform type ({@link PlatformType})
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<MobileAppEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                         @Param("platformType") PlatformType platformType,
                                         @Param("searchText") String searchText,
                                         Pageable pageable);
    /**
     * Finds by pkg name and platform type.
     *
     * @param pkgName pkg name ({@link String})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link MobileAppEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    MobileAppEntity findByPkgNameAndPlatformType(@Param("pkgName") String pkgName, @Param("platformType") PlatformType platformType);
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Transactional
    @Modifying
    @Query("DELETE FROM MobileAppEntity r WHERE r.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Finds android app by bundle id.
     *
     * @param bundleId bundle id ({@link UUID})
     * @return {@link MobileAppEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT a FROM MobileAppEntity a LEFT JOIN MobileAppBundleEntity b ON b.androidAppId = a.id WHERE b.id = :bundleId")
    MobileAppEntity findAndroidAppByBundleId(@Param("bundleId") UUID bundleId);
    /**
     * Finds iosapp by bundle id.
     *
     * @param bundleId bundle id ({@link UUID})
     * @return {@link MobileAppEntity}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT a FROM MobileAppEntity a LEFT JOIN MobileAppBundleEntity b ON b.iosAppID = a.id WHERE b.id = :bundleId")
    MobileAppEntity findIOSAppByBundleId(@Param("bundleId") UUID bundleId);

}
