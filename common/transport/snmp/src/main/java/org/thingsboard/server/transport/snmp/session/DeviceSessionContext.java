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
package org.thingsboard.server.transport.snmp.session;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.device.data.SnmpDeviceTransportConfiguration;
import org.thingsboard.server.common.data.device.profile.SnmpDeviceProfileTransportConfiguration;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rpc.RpcStatus;
import org.thingsboard.server.common.data.transport.snmp.SnmpCommunicationSpec;
import org.thingsboard.server.common.transport.SessionMsgListener;
import org.thingsboard.server.common.transport.TransportServiceCallback;
import org.thingsboard.server.common.transport.service.DefaultTransportService;
import org.thingsboard.server.common.transport.session.DeviceAwareSessionContext;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.AttributeUpdateNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.GetAttributeResponseMsg;
import org.thingsboard.server.gen.transport.TransportProtos.SessionCloseNotificationProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToDeviceRpcRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToServerRpcResponseMsg;
import org.thingsboard.server.transport.snmp.SnmpTransportContext;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Device session context.
 */
@Slf4j
public class DeviceSessionContext extends DeviceAwareSessionContext implements SessionMsgListener, ResponseListener {
    @Getter
    private Target target;
    private final String token;
    @Getter
    @Setter
    private SnmpDeviceProfileTransportConfiguration profileTransportConfiguration;
    @Getter
    @Setter
    private SnmpDeviceTransportConfiguration deviceTransportConfiguration;
    @Getter
    @Setter
    private Device device;
    @Getter
    private final TenantId tenantId;

    private final SnmpTransportContext snmpTransportContext;

    private final AtomicInteger msgIdSeq = new AtomicInteger(0);
    @Getter
    private boolean isActive = true;
    @Setter
    private Runnable sessionTimeoutHandler;

    @Getter
    private final List<ScheduledTask> queryingTasks = new LinkedList<>();

    @Builder
    public DeviceSessionContext(TenantId tenantId, Device device, DeviceProfile deviceProfile, String token,
                                SnmpDeviceProfileTransportConfiguration profileTransportConfiguration,
                                SnmpDeviceTransportConfiguration deviceTransportConfiguration,
                                SnmpTransportContext snmpTransportContext) throws Exception {
        super(UUID.randomUUID());
        super.setDeviceId(device.getId());
        super.setDeviceProfile(deviceProfile);
        this.device = device;
        this.tenantId = tenantId;

        this.token = token;
        this.snmpTransportContext = snmpTransportContext;

        this.profileTransportConfiguration = profileTransportConfiguration;
        this.deviceTransportConfiguration = deviceTransportConfiguration;

        initializeTarget(profileTransportConfiguration, deviceTransportConfiguration);
    }
    /**
     * Handles device profile update.
     *
     * @param newSessionInfo new session info
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onDeviceProfileUpdate(TransportProtos.SessionInfoProto newSessionInfo, DeviceProfile deviceProfile) {
        super.onDeviceProfileUpdate(newSessionInfo, deviceProfile);
        if (isActive) {
            snmpTransportContext.onDeviceProfileUpdated(deviceProfile, this);
        }
    }
    /**
     * Handles device deleted.
     *
     * @param deviceId target device identifier
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onDeviceDeleted(DeviceId deviceId) {
        snmpTransportContext.onDeviceDeleted(this);
    }
    /**
     * Handles response.
     *
     * @param event event ({@link ResponseEvent})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onResponse(ResponseEvent event) {
        if (isActive) {
            snmpTransportContext.getSnmpTransportService().processResponseEvent(this, event);
        }
    }
    /**
     * Initialize target.
     *
     * @param profileTransportConfig profile transport config ({@link SnmpDeviceProfileTransportConfiguration})
     * @param deviceTransportConfig device transport config ({@link SnmpDeviceTransportConfiguration})
     * @return nothing
     * @throws Exception on processing failure
     */
    public void initializeTarget(SnmpDeviceProfileTransportConfiguration profileTransportConfig, SnmpDeviceTransportConfiguration deviceTransportConfig) throws Exception {
        log.trace("Initializing target for SNMP session of device {}", device);
        this.target = snmpTransportContext.getSnmpAuthService().setUpSnmpTarget(profileTransportConfig, deviceTransportConfig);
        log.debug("SNMP target initialized: {}", target);
    }
    /**
     * Close.
     *
     * @return nothing
     * @throws Exception on processing failure
     */
    public void close() {
        isActive = false;
    }
    /**
     * Returns token.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getToken() {
        return token;
    }
    /**
     * Next msg id.
     *
     * @return monotonically increasing MQTT packet identifier
     * @throws Exception on processing failure
     */

    @Override
    public int nextMsgId() {
        return msgIdSeq.incrementAndGet();
    }
    /**
     * Handles get attributes response.
     *
     * @param getAttributesResponse get attributes response ({@link GetAttributeResponseMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onGetAttributesResponse(GetAttributeResponseMsg getAttributesResponse) {
    }
    /**
     * Handles attribute update.
     *
     * @param sessionId session id ({@link UUID})
     * @param attributeUpdateNotification attribute update notification ({@link AttributeUpdateNotificationMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onAttributeUpdate(UUID sessionId, AttributeUpdateNotificationMsg attributeUpdateNotification) {
        log.trace("[{}] Received attributes update notification to device", sessionId);
        try {
            snmpTransportContext.getSnmpTransportService().onAttributeUpdate(this, attributeUpdateNotification);
        } catch (Exception e) {
            snmpTransportContext.getTransportService().errorEvent(getTenantId(), getDeviceId(), SnmpCommunicationSpec.SHARED_ATTRIBUTES_SETTING.getLabel(), e);
        }
    }
    /**
     * Handles remote session close command.
     *
     * @param sessionId session id ({@link UUID})
     * @param sessionCloseNotification session close notification ({@link SessionCloseNotificationProto})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onRemoteSessionCloseCommand(UUID sessionId, SessionCloseNotificationProto sessionCloseNotification) {
        log.trace("[{}] Received the remote command to close the session: {}", sessionId, sessionCloseNotification.getMessage());
        if (sessionCloseNotification.getMessage().equals(DefaultTransportService.SESSION_EXPIRED_MESSAGE)) {
            if (sessionTimeoutHandler != null) {
                sessionTimeoutHandler.run();
            }
        }
    }
    /**
     * Handles to device rpc request.
     *
     * @param sessionId session id ({@link UUID})
     * @param toDeviceRequest to device request ({@link ToDeviceRpcRequestMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onToDeviceRpcRequest(UUID sessionId, ToDeviceRpcRequestMsg toDeviceRequest) {
        log.trace("[{}] Received RPC command to device", sessionId);
        try {
            snmpTransportContext.getSnmpTransportService().onToDeviceRpcRequest(this, toDeviceRequest);
            snmpTransportContext.getTransportService().process(getSessionInfo(), toDeviceRequest, RpcStatus.DELIVERED, TransportServiceCallback.EMPTY);
        } catch (Exception e) {
            snmpTransportContext.getTransportService().errorEvent(getTenantId(), getDeviceId(), SnmpCommunicationSpec.TO_DEVICE_RPC_REQUEST.getLabel(), e);
        }
    }
    /**
     * Handles to server rpc response.
     *
     * @param toServerResponse to server response ({@link ToServerRpcResponseMsg})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onToServerRpcResponse(ToServerRpcResponseMsg toServerResponse) {
    }
}
