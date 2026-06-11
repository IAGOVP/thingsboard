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
package org.thingsboard.server.dao.sql.oauth2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.dao.model.sql.OAuth2ClientEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for oauth2client entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface OAuth2ClientRepository extends JpaRepository<OAuth2ClientEntity, UUID> {

    @Query("SELECT с FROM OAuth2ClientEntity с WHERE с.tenantId = :tenantId AND " +
            "(:searchText is NULL OR ilike(с.title, concat('%', :searchText, '%')) = true)")
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param searchText search text ({@link String})
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */
    Page<OAuth2ClientEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                            @Param("searchText") String searchText,
                                            Pageable pageable);

    @Query("SELECT c " +
            "FROM OAuth2ClientEntity c " +
            "LEFT JOIN DomainOauth2ClientEntity dc ON c.id = dc.oauth2ClientId " +
            "LEFT JOIN DomainEntity domain ON dc.domainId = domain.id " +
            "WHERE domain.name = :domainName AND domain.oauth2Enabled = true " +
            "AND (:platformFilter IS NULL OR c.platforms IS NULL OR c.platforms = '' OR ilike(c.platforms, CONCAT('%', :platformFilter, '%')) = true)")
    /**
     * Finds enabled by domain name and platform type.
     *
     * @param domainName domain name ({@link String})
     * @param platformFilter platform filter ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<OAuth2ClientEntity> findEnabledByDomainNameAndPlatformType(@Param("domainName") String domainName,
                                                                    @Param("platformFilter") String platformFilter);

    @Query("SELECT c " +
            "FROM OAuth2ClientEntity c " +
            "LEFT JOIN MobileAppBundleOauth2ClientEntity ac ON c.id = ac.oauth2ClientId " +
            "LEFT JOIN MobileAppBundleEntity b ON ac.mobileAppBundleId = b.id " +
            "LEFT JOIN MobileAppEntity andApp ON b.androidAppId = andApp.id " +
            "WHERE andApp.pkgName = :pkgName AND b.oauth2Enabled = true " +
            "AND (:platformFilter IS NULL OR c.platforms IS NULL OR c.platforms = '' OR ilike(c.platforms, CONCAT('%', :platformFilter, '%')) = true)")
    /**
     * Finds enabled by android pkg name and platform type.
     *
     * @param pkgName pkg name ({@link String})
     * @param platformFilter platform filter ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<OAuth2ClientEntity> findEnabledByAndroidPkgNameAndPlatformType(@Param("pkgName") String pkgName,
                                                                        @Param("platformFilter") String platformFilter);

    @Query("SELECT c " +
            "FROM OAuth2ClientEntity c " +
            "LEFT JOIN MobileAppBundleOauth2ClientEntity ac ON c.id = ac.oauth2ClientId " +
            "LEFT JOIN MobileAppBundleEntity b ON ac.mobileAppBundleId = b.id " +
            "LEFT JOIN MobileAppEntity iosApp ON b.iosAppID = iosApp.id " +
            "WHERE iosApp.pkgName = :pkgName AND b.oauth2Enabled = true " +
            "AND (:platformFilter IS NULL OR c.platforms IS NULL OR c.platforms = '' OR ilike(c.platforms, CONCAT('%', :platformFilter, '%')) = true)")
    /**
     * Finds enabled by ios pkg name and platform type.
     *
     * @param pkgName pkg name ({@link String})
     * @param platformFilter platform filter ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<OAuth2ClientEntity> findEnabledByIosPkgNameAndPlatformType(@Param("pkgName") String pkgName,
                                                                    @Param("platformFilter") String platformFilter);

    @Query("SELECT c " +
            "FROM OAuth2ClientEntity c " +
            "LEFT JOIN DomainOauth2ClientEntity dc ON dc.oauth2ClientId = c.id " +
            "WHERE dc.domainId = :domainId ")
    /**
     * Finds by domain id.
     *
     * @param domainId domain id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<OAuth2ClientEntity> findByDomainId(@Param("domainId") UUID domainId);

    @Query("SELECT c " +
            "FROM OAuth2ClientEntity c " +
            "LEFT JOIN MobileAppBundleOauth2ClientEntity bc ON bc.oauth2ClientId = c.id " +
            "WHERE bc.mobileAppBundleId = :mobileAppBundleId ")
    /**
     * Finds by mobile app bundle id.
     *
     * @param mobileAppBundleId mobile app bundle id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<OAuth2ClientEntity> findByMobileAppBundleId(@Param("mobileAppBundleId") UUID mobileAppBundleId);

    @Query("SELECT a.appSecret " +
            "FROM MobileAppEntity a " +
            "LEFT JOIN MobileAppBundleEntity b ON (b.androidAppId = a.id OR b.iosAppID = a.id) " +
            "LEFT JOIN MobileAppBundleOauth2ClientEntity bc ON bc.mobileAppBundleId = b.id " +
            "LEFT JOIN OAuth2ClientEntity c ON bc.oauth2ClientId = c.id " +
            "WHERE c.id = :clientId " +
            "AND a.pkgName = :pkgName and a.platformType = :platformType")
    /**
     * Finds app secret.
     *
     * @param id entity UUID primary key
     * @param pkgName pkg name ({@link String})
     * @param platformType platform type ({@link PlatformType})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */
    String findAppSecret(@Param("clientId") UUID id,
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */
                         @Param("pkgName") String pkgName,
                         @Param("platformType") PlatformType platformType);

    @Transactional
    @Modifying
    @Query("DELETE FROM OAuth2ClientEntity t WHERE t.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);
    /**
     * Finds by tenant id and id in.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param uuids uuids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<OAuth2ClientEntity> findByTenantIdAndIdIn(UUID tenantId, List<UUID> uuids);

    @Query("SELECT COUNT(d) > 0 FROM DomainEntity d " +
            "JOIN DomainOauth2ClientEntity doc ON d.id = doc.domainId " +
            "WHERE d.tenantId = :tenantId AND doc.oauth2ClientId = :oAuth2ClientId AND d.propagateToEdge = true")
    /**
     * Is propagate to edge.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param oAuth2ClientId o auth2client id ({@link UUID})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */
    boolean isPropagateToEdge(@Param("tenantId") UUID tenantId, @Param("oAuth2ClientId") UUID oAuth2ClientId);

}
