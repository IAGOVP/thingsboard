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
package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.BaseDataWithAdditionalInfo;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasLabel;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.HasVersion;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.Optional;

@Schema
@EqualsAndHashCode(callSuper = true)
/**
 * Logical or physical asset entity (building, vehicle, production line) within a tenant.
 *
 * <p>Assets group devices and other entities for visualization and access control. Each asset
 * belongs to a {@link org.thingsboard.server.common.data.id.TenantId}, may be assigned to a
 * {@link org.thingsboard.server.common.data.id.CustomerId}, and references an
 * {@link org.thingsboard.server.common.data.id.AssetProfileId} for default configuration.
 * Supports entity relations to devices and other assets.
 *
 * <p>Exposed via REST ({@code /api/asset}) and managed by {@code AssetService} in the dao module.
 */
public class Asset extends BaseDataWithAdditionalInfo<AssetId> implements HasLabel, HasTenantId, HasCustomerId, HasVersion, ExportableEntity<AssetId> {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "type")
    private String type;
    @NoXss
    @Length(fieldName = "label")
    private String label;

    private AssetProfileId assetProfileId;

    @Getter @Setter
    private AssetId externalId;
    @Getter @Setter
    private Long version;

    public Asset() {
        super();
    }

    public Asset(AssetId id) {
        super(id);
    }

    public Asset(Asset asset) {
        super(asset);
        this.tenantId = asset.getTenantId();
        this.customerId = asset.getCustomerId();
        this.name = asset.getName();
        this.type = asset.getType();
        this.label = asset.getLabel();
        this.assetProfileId = asset.getAssetProfileId();
        this.externalId = asset.getExternalId();
        this.version = asset.getVersion();
    }
    /**
     * Updates the requested data.
     *
     * @param asset asset ({@link Asset})
     */

    public void update(Asset asset) {
        this.tenantId = asset.getTenantId();
        this.customerId = asset.getCustomerId();
        this.name = asset.getName();
        this.type = asset.getType();
        this.label = asset.getLabel();
        this.assetProfileId = asset.getAssetProfileId();
        Optional.ofNullable(asset.getAdditionalInfo()).ifPresent(this::setAdditionalInfo);
        this.externalId = asset.getExternalId();
        this.version = asset.getVersion();
    }
    /**
     * Returns id.
     *
     * @return {@link AssetId}
     */

    @Schema(description = "JSON object with the asset Id. " +
            "Specify this field to update the asset. " +
            "Referencing non-existing asset Id will cause error. " +
            "Omit this field to create new asset.")
    @Override
    public AssetId getId() {
        return super.getId();
    }
    /**
     * Returns created time.
     *
     * @return the long result
     */

    @Schema(description = "Timestamp of the asset creation, in milliseconds", example = "1609459200000", accessMode = Schema.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }
    /**
     * Returns tenant id.
     *
     * @return {@link TenantId}
     */

    @Schema(description = "JSON object with Tenant Id.", accessMode = Schema.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }
    /**
     * Set tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }
    /**
     * Returns customer id.
     *
     * @return {@link CustomerId}
     */

    @Schema(description = "JSON object with Customer Id. Use 'assignAssetToCustomer' to change the Customer Id.", accessMode = Schema.AccessMode.READ_ONLY)
    public CustomerId getCustomerId() {
        return customerId;
    }
    /**
     * Set customer id.
     *
     * @param customerId customer id ({@link CustomerId})
     */

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }
    /**
     * Returns owner id.
     *
     * @return {@link EntityId}
     */

    @JsonIgnore
    public EntityId getOwnerId() {
        return customerId != null && !customerId.isNullUid() ? customerId : tenantId;
    }
    /**
     * Returns name.
     *
     * @return {@link String}
     */

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Unique Asset Name in scope of Tenant", example = "Empire State Building")
    @Override
    public String getName() {
        return name;
    }
    /**
     * Set name.
     *
     * @param name name ({@link String})
     */

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Returns type.
     *
     * @return {@link String}
     */

    @Schema(description = "Asset type", example = "Building")
    public String getType() {
        return type;
    }
    /**
     * Set type.
     *
     * @param type type ({@link String})
     */

    public void setType(String type) {
        this.type = type;
    }
    /**
     * Returns label.
     *
     * @return {@link String}
     */

    @Schema(description = "Label that may be used in widgets", example = "NY Building")
    public String getLabel() {
        return label;
    }
    /**
     * Set label.
     *
     * @param label label ({@link String})
     */

    public void setLabel(String label) {
        this.label = label;
    }
    /**
     * Returns asset profile id.
     *
     * @return {@link AssetProfileId}
     */

    @Schema(description = "JSON object with Asset Profile Id.")
    public AssetProfileId getAssetProfileId() {
        return assetProfileId;
    }
    /**
     * Set asset profile id.
     *
     * @param assetProfileId asset profile id ({@link AssetProfileId})
     */

    public void setAssetProfileId(AssetProfileId assetProfileId) {
        this.assetProfileId = assetProfileId;
    }

    @Schema(description = "Additional parameters of the asset. " +
            "May include: 'description' (string).",
            implementation = com.fasterxml.jackson.databind.JsonNode.class,
            example = "{\"description\":\"Building A asset\"}")
    /**
     * Returns additional info.
     *
     * @return {@link JsonNode}
     */
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Asset [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", label=");
        builder.append(label);
        builder.append(", assetProfileId=");
        builder.append(assetProfileId);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
