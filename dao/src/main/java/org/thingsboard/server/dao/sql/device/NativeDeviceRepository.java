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

import org.springframework.data.domain.Pageable;
import org.thingsboard.server.common.data.DeviceIdInfo;
import org.thingsboard.server.common.data.ProfileEntityIdInfo;
import org.thingsboard.server.common.data.page.PageData;


/**

 * Spring Data JPA repository for native device entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface NativeDeviceRepository extends NativeProfileEntityRepository {
    /**
     * Finds device id infos.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<DeviceIdInfo> findDeviceIdInfos(Pageable pageable);

}
