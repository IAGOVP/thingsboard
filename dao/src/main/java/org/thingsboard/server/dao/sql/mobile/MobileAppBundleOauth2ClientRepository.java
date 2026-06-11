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

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.dao.model.sql.MobileAppOauth2ClientCompositeKey;
import org.thingsboard.server.dao.model.sql.MobileAppBundleOauth2ClientEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for mobile app bundle oauth2client entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface MobileAppBundleOauth2ClientRepository extends JpaRepository<MobileAppBundleOauth2ClientEntity, MobileAppOauth2ClientCompositeKey> {
    /**
     * Finds all by mobile app bundle id.
     *
     * @param mobileAppId mobile app id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<MobileAppBundleOauth2ClientEntity> findAllByMobileAppBundleId(UUID mobileAppId);

}
