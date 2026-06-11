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
package org.thingsboard.server.service.resource;

import org.thingsboard.server.common.data.ResourceExportData;
import org.thingsboard.server.common.data.TbImageDeleteResult;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.dao.resource.ImageCacheKey;
import org.thingsboard.server.service.security.model.SecurityUser;

/**

 * Service contract for tb image operations (tenant/system resource file management).

 *

 * <p>Implemented by the corresponding {@code Default*} class in this package.

 */

public interface TbImageService {

    TbResourceInfo save(TbResource image, User user) throws Exception;

    /**
     * Saves or persists the requested data.
     *
     * @param imageInfo image info ({@link TbResourceInfo})
     * @param oldImageInfo old image info ({@link TbResourceInfo})
     * @param user authenticated user performing the action
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceInfo save(TbResourceInfo imageInfo, TbResourceInfo oldImageInfo, User user);

    /**
     * Deletes the requested data.
     *
     * @param imageInfo image info ({@link TbResourceInfo})
     * @param user authenticated user performing the action
     * @param force force
     * @return {@link TbImageDeleteResult}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbImageDeleteResult delete(TbResourceInfo imageInfo, User user, boolean force);

    /**
     * Returns etag.
     *
     * @param imageCacheKey image cache key ({@link ImageCacheKey})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String getETag(ImageCacheKey imageCacheKey);

    /**
     * Put etag.
     *
     * @param imageCacheKey image cache key ({@link ImageCacheKey})
     * @param etag etag ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void putETag(ImageCacheKey imageCacheKey, String etag);

    /**
     * Evict etags.
     *
     * @param imageCacheKey image cache key ({@link ImageCacheKey})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void evictETags(ImageCacheKey imageCacheKey);

    /**
     * Imports image.
     *
     * @param imageData image data ({@link ResourceExportData})
     * @param checkExisting check existing
     * @param user authenticated user performing the action
     * @return {@link TbResourceInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceInfo importImage(ResourceExportData imageData, boolean checkExisting, SecurityUser user) throws Exception;

}
