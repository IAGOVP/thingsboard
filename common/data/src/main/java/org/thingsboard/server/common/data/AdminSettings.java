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
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.thingsboard.server.common.data.id.AdminSettingsId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@Schema
/**
 * Admin settings.
 */
public class AdminSettings extends BaseData<AdminSettingsId> implements HasTenantId {

    private static final long serialVersionUID = -7670322981725511892L;

    private TenantId tenantId;

    @NoXss
    @Length(fieldName = "key")
    private String key;
    private JsonNode jsonValue;

    public AdminSettings() {
        super();
    }

    public AdminSettings(AdminSettingsId id) {
        super(id);
    }

    public AdminSettings(AdminSettings adminSettings) {
        super(adminSettings);
        this.tenantId = adminSettings.getTenantId();
        this.key = adminSettings.getKey();
        this.jsonValue = adminSettings.getJsonValue();
    }
    /**
     * Returns id.
     *
     * @return {@link AdminSettingsId}
     */

    @Schema(description = "The Id of the Administration Settings, auto-generated, UUID")
    @Override
    public AdminSettingsId getId() {
        return super.getId();
    }
    /**
     * Returns created time.
     *
     * @return the long result
     */

    @Schema(description = "Timestamp of the settings creation, in milliseconds", example = "1609459200000", accessMode = Schema.AccessMode.READ_ONLY)
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
     * Returns key.
     *
     * @return {@link String}
     */

    @Schema(description = "The Administration Settings key, (e.g. 'general' or 'mail')", example = "mail")
    public String getKey() {
        return key;
    }
    /**
     * Set key.
     *
     * @param key key ({@link String})
     */

    public void setKey(String key) {
        this.key = key;
    }
    /**
     * Returns json value.
     *
     * @return {@link JsonNode}
     */

    @Schema(description = "JSON representation of the Administration Settings value")
    public JsonNode getJsonValue() {
        return jsonValue;
    }
    /**
     * Set json value.
     *
     * @param jsonValue json value ({@link JsonNode})
     */

    public void setJsonValue(JsonNode jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((jsonValue == null) ? 0 : jsonValue.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdminSettings other = (AdminSettings) obj;
        if (jsonValue == null) {
            if (other.jsonValue != null)
                return false;
        } else if (!jsonValue.equals(other.jsonValue))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AdminSettings [key=");
        builder.append(key);
        builder.append(", jsonValue=");
        builder.append(jsonValue);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
