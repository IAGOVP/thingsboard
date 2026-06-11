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
package org.thingsboard.server.dao.resource;

import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.HasImage;
import org.thingsboard.server.common.data.ResourceExportData;
import org.thingsboard.server.common.data.ResourceSubType;
import org.thingsboard.server.common.data.TbImageDeleteResult;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;

import java.util.Collection;
import java.util.Set;

/**
 * Service API for image persistence and domain operations.
 */
public interface ImageService {

    /**
     * Saves or persists image.
     *
     * @param image image ({@link TbResource})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo saveImage(TbResource image);

    /**
     * Saves or persists image info.
     *
     * @param imageInfo image info ({@link TbResourceInfo})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo saveImageInfo(TbResourceInfo imageInfo);

    /**
     * Returns image info by tenant id and key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key key ({@link String})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo getImageInfoByTenantIdAndKey(TenantId tenantId, String key);

    /**
     * Returns all image keys by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Set}
     */
    Set<String> getAllImageKeysByTenantId(TenantId tenantId);

    /**
     * Returns public image info by key.
     *
     * @param publicResourceKey public resource key ({@link String})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo getPublicImageInfoByKey(String publicResourceKey);

    /**
     * Returns images by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageSubType image sub type ({@link ResourceSubType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TbResourceInfo> getImagesByTenantId(TenantId tenantId, ResourceSubType imageSubType, PageLink pageLink);

    /**
     * Returns all images by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageSubType image sub type ({@link ResourceSubType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<TbResourceInfo> getAllImagesByTenantId(TenantId tenantId, ResourceSubType imageSubType, PageLink pageLink);

    /**
     * Returns image data.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageId image id ({@link TbResourceId})
     * @return the byte[] value
     */
    byte[] getImageData(TenantId tenantId, TbResourceId imageId);

    /**
     * Returns image preview.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageId image id ({@link TbResourceId})
     * @return the byte[] value
     */
    byte[] getImagePreview(TenantId tenantId, TbResourceId imageId);

    /**
     * Exports image.
     *
     * @param imageInfo image info ({@link TbResourceInfo})
     * @return {@link ResourceExportData}
     */
    ResourceExportData exportImage(TbResourceInfo imageInfo);

    /**
     * To image.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param imageData image data ({@link ResourceExportData})
     * @param checkExisting check existing
     * @return {@link TbResource}
     */
    TbResource toImage(TenantId tenantId, ResourceExportData imageData, boolean checkExisting);

    /**
     * Deletes image.
     *
     * @param imageInfo image info ({@link TbResourceInfo})
     * @param force force
     * @return {@link TbImageDeleteResult}
     */
    TbImageDeleteResult deleteImage(TbResourceInfo imageInfo, boolean force);

    /**
     * Calculate image etag.
     *
     * @param imageData image data
     * @return {@link String}
     */
    String calculateImageEtag(byte[] imageData);

    /**
     * Finds system or tenant image by etag.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param etag etag ({@link String})
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo findSystemOrTenantImageByEtag(TenantId tenantId, String etag);

    /**
     * Replace base64with image url.
     *
     * @param entity entity ({@link HasImage})
     * @param type type ({@link String})
     * @return the boolean result
     */
    boolean replaceBase64WithImageUrl(HasImage entity, String type);

    /**
     * Updates images usage.
     *
     * @param dashboard dashboard ({@link Dashboard})
     * @return the boolean result
     */
    boolean updateImagesUsage(Dashboard dashboard);

    /**
     * Updates images usage.
     *
     * @param widgetType widget type ({@link WidgetTypeDetails})
     * @return the boolean result
     */
    boolean updateImagesUsage(WidgetTypeDetails widgetType);

    /**
     * Inline image.
     *
     * @param entity entity ({@link T})
     * @return the operation result
     */
    <T extends HasImage> T inlineImage(T entity);

    /**
     * Returns used images.
     *
     * @param dashboard dashboard ({@link Dashboard})
     * @return {@link Collection}
     */
    Collection<TbResourceInfo> getUsedImages(Dashboard dashboard);

    /**
     * Returns used images.
     *
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     * @return {@link Collection}
     */
    Collection<TbResourceInfo> getUsedImages(WidgetTypeDetails widgetTypeDetails);

    /**
     * Inline image for edge.
     *
     * @param entity entity ({@link HasImage})
     */
    void inlineImageForEdge(HasImage entity);

    /**
     * Inline images for edge.
     *
     * @param dashboard dashboard ({@link Dashboard})
     */
    void inlineImagesForEdge(Dashboard dashboard);

    /**
     * Inline images for edge.
     *
     * @param widgetTypeDetails widget type details ({@link WidgetTypeDetails})
     */
    void inlineImagesForEdge(WidgetTypeDetails widgetTypeDetails);

    /**
     * Creates or update system image.
     *
     * @param resourceKey resource key ({@link String})
     * @param data data
     * @return {@link TbResourceInfo}
     */
    TbResourceInfo createOrUpdateSystemImage(String resourceKey, byte[] data);

}
