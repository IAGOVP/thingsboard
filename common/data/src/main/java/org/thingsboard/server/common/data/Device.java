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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Core IoT device entity owned by a tenant and optionally assigned to a customer.
 *
 * <p>Represents a physical or logical device that connects via transport protocols (MQTT, HTTP,
 * CoAP, LwM2M, SNMP). Linked to a {@link org.thingsboard.server.common.data.id.DeviceProfileId}
 * that defines transport configuration, alarm rules, and provision strategy. Device credentials
 * are stored separately; this entity holds metadata, label, type, OTA package references, and
 * optional {@link org.thingsboard.server.common.data.device.data.DeviceData} (transport-specific
 * configuration serialized as JSON).
 *
 * <p>Serialized to/from JSON for the REST API ({@code /api/device}) and persisted via
 * {@code DeviceService} in the dao module.
 */
@Schema
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
public class Device extends BaseDataWithAdditionalInfo<DeviceId> implements HasLabel, HasTenantId, HasCustomerId, HasOtaPackage, HasVersion, ExportableEntity<DeviceId> {

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
    private DeviceProfileId deviceProfileId;
    private transient DeviceData deviceData;
    @JsonIgnore
    @Getter @Setter
    private byte[] deviceDataBytes;

    private OtaPackageId firmwareId;
    private OtaPackageId softwareId;

    @Getter @Setter
    private DeviceId externalId;
    @Getter @Setter
    private Long version;

    public Device() {
        super();
    }

    public Device(DeviceId id) {
        super(id);
    }

    public Device(Device device) {
        super(device);
        this.tenantId = device.getTenantId();
        this.customerId = device.getCustomerId();
        this.name = device.getName();
        this.type = device.getType();
        this.label = device.getLabel();
        this.deviceProfileId = device.getDeviceProfileId();
        this.setDeviceData(device.getDeviceData());
        this.firmwareId = device.getFirmwareId();
        this.softwareId = device.getSoftwareId();
        this.externalId = device.getExternalId();
        this.version = device.getVersion();
    }
    /**
     * Updates device.
     *
     * @param device device ({@link Device})
     * @return {@link Device}
     */

    public Device updateDevice(Device device) {
        this.tenantId = device.getTenantId();
        this.customerId = device.getCustomerId();
        this.name = device.getName();
        this.type = device.getType();
        this.label = device.getLabel();
        this.deviceProfileId = device.getDeviceProfileId();
        this.setDeviceData(device.getDeviceData());
        this.setFirmwareId(device.getFirmwareId());
        this.setSoftwareId(device.getSoftwareId());
        Optional.ofNullable(device.getAdditionalInfo()).ifPresent(this::setAdditionalInfo);
        this.setExternalId(device.getExternalId());
        this.setVersion(device.getVersion());
        return this;
    }
    /**
     * Returns id.
     *
     * @return {@link DeviceId}
     */

    @Schema(description = "JSON object with the Device Id. " +
            "Specify this field to update the Device. " +
            "Referencing non-existing Device Id will cause error. " +
            "Omit this field to create new Device." )
    @Override
    public DeviceId getId() {
        return super.getId();
    }
    /**
     * Returns created time.
     *
     * @return the long result
     */

    @Schema(description = "Timestamp of the device creation, in milliseconds", example = "1609459200000", accessMode = Schema.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }
    /**
     * Returns tenant id.
     *
     * @return {@link TenantId}
     */

    @Schema(description = "JSON object with Tenant Id. Use 'assignDeviceToTenant' to change the Tenant Id.", accessMode = Schema.AccessMode.READ_ONLY)
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

    @Schema(description = "JSON object with Customer Id. Use 'assignDeviceToCustomer' to change the Customer Id.", accessMode = Schema.AccessMode.READ_ONLY)
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

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Unique Device Name in scope of Tenant", example = "A4B72CCDFF33")
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

    @Schema(description = "Device Profile Name", example = "Temperature Sensor")
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

    @Schema(description = "Label that may be used in widgets", example = "Room 234 Sensor")
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
     * Returns device profile id.
     *
     * @return {@link DeviceProfileId}
     */

    @Schema(description = "JSON object with Device Profile Id. If not provided, the type will be used to determine the profile. If neither deviceProfileId nor type is specified, the default device profile will be used.")
    public DeviceProfileId getDeviceProfileId() {
        return deviceProfileId;
    }
    /**
     * Set device profile id.
     *
     * @param deviceProfileId device profile id ({@link DeviceProfileId})
     */

    public void setDeviceProfileId(DeviceProfileId deviceProfileId) {
        this.deviceProfileId = deviceProfileId;
    }
    /**
     * Returns device data.
     *
     * @return {@link DeviceData}
     */

    @Schema(description = "JSON object with content specific to type of transport in the device profile.")
    public DeviceData getDeviceData() {
        if (deviceData != null) {
            return deviceData;
        } else {
            if (deviceDataBytes != null) {
                try {
                    deviceData = mapper.readValue(new ByteArrayInputStream(deviceDataBytes), DeviceData.class);
                } catch (IOException e) {
                    log.warn("Can't deserialize device data: ", e);
                    return null;
                }
                return deviceData;
            } else {
                return null;
            }
        }
    }
    /**
     * Set device data.
     *
     * @param data data ({@link DeviceData})
     */

    public void setDeviceData(DeviceData data) {
        this.deviceData = data;
        try {
            this.deviceDataBytes = data != null ? mapper.writeValueAsBytes(data) : null;
        } catch (JsonProcessingException e) {
            log.warn("Can't serialize device data: ", e);
        }
    }
    /**
     * Returns firmware id.
     *
     * @return {@link OtaPackageId}
     */

    @Schema(description = "JSON object with Ota Package Id.")
    public OtaPackageId getFirmwareId() {
        return firmwareId;
    }
    /**
     * Set firmware id.
     *
     * @param firmwareId firmware id ({@link OtaPackageId})
     */

    public void setFirmwareId(OtaPackageId firmwareId) {
        this.firmwareId = firmwareId;
    }
    /**
     * Returns software id.
     *
     * @return {@link OtaPackageId}
     */

    @Schema(description = "JSON object with Ota Package Id.")
    public OtaPackageId getSoftwareId() {
        return softwareId;
    }
    /**
     * Set software id.
     *
     * @param softwareId software id ({@link OtaPackageId})
     */

    public void setSoftwareId(OtaPackageId softwareId) {
        this.softwareId = softwareId;
    }

    @Schema(description = "Additional parameters of the device. " +
            "May include: 'gateway' (boolean, whether the device is a gateway), " +
            "'description' (string), " +
            "'lastConnectedGateway' (string, UUID of the last gateway that connected this device).",
            implementation = com.fasterxml.jackson.databind.JsonNode.class,
            example = "{\"gateway\":false,\"description\":\"Temperature sensor\",\"lastConnectedGateway\":\"784f394c-42b6-435a-983c-b7beff2784f9\"}")
    /**
     * Returns additional info.
     *
     * @return {@link JsonNode}
     */
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

}
