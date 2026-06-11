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
package org.thingsboard.server.transport.lwm2m.server.uplink;

import org.eclipse.leshan.core.node.TimestampedLwM2mNodes;
import org.eclipse.leshan.core.node.codec.LwM2mValueConverter;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.CreateRequest;
import org.eclipse.leshan.core.request.WriteCompositeRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ReadCompositeResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.registration.Registration;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.lwm2m.config.LwM2MTransportServerConfig;
import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClient;
import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClientContext;

import java.util.Collection;
import java.util.Optional;

/**
 * Handles LwM2M uplink operations: registration, observe notifications, writes, and forwards converted data to {@link TransportService}.
 */
public interface LwM2mUplinkMsgHandler {

    void onRegistered(Registration registration, Collection<Observation> previousObsersations);

    /**
     * Updates d reg.
     *
     * @param registration registration ({@link Registration})
     * @return nothing
     * @throws Exception on processing failure
     */
    void updatedReg(Registration registration);

    /**
     * Un reg.
     *
     * @param registration registration ({@link Registration})
     * @param observations observations ({@link Collection})
     * @return nothing
     * @throws Exception on processing failure
     */
    void unReg(Registration registration, Collection<Observation> observations);

    /**
     * Handles sleeping dev.
     *
     * @param registration registration ({@link Registration})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onSleepingDev(Registration registration);

    /**
     * Handles update value after read response.
     *
     * @param registration registration ({@link Registration})
     * @param path path ({@link String})
     * @param response response ({@link ReadResponse})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onUpdateValueAfterReadResponse(Registration registration, String path, ReadResponse response);

    /**
     * Handles update value after read composite response.
     *
     * @param registration registration ({@link Registration})
     * @param response response ({@link ReadCompositeResponse})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onUpdateValueAfterReadCompositeResponse(Registration registration, ReadCompositeResponse response);

    /**
     * Handles error observation.
     *
     * @param registration registration ({@link Registration})
     * @param errorMsg error msg ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onErrorObservation(Registration registration, String errorMsg);

    /**
     * Handles update value with send request.
     *
     * @param registration registration ({@link Registration})
     * @param data data ({@link TimestampedLwM2mNodes})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onUpdateValueWithSendRequest(Registration registration, TimestampedLwM2mNodes data);

    /**
     * Handles device profile update.
     *
     * @param sessionInfo session info
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onDeviceProfileUpdate(TransportProtos.SessionInfoProto sessionInfo, DeviceProfile deviceProfile);

    /**
     * Handles device update.
     *
     * @param sessionInfo session info
     * @param device device ({@link Device})
     * @param deviceProfileOpt device profile opt ({@link Optional})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onDeviceUpdate(TransportProtos.SessionInfoProto sessionInfo, Device device, Optional<DeviceProfile> deviceProfileOpt);

    /**
     * Handles device delete.
     *
     * @param deviceId target device identifier
     * @return nothing
     * @throws Exception on processing failure
     */
    void onDeviceDelete(DeviceId deviceId);

    /**
     * Handles resource update.
     *
     * @param resourceUpdateMsgOpt resource update msg opt
     * @return nothing
     * @throws Exception on processing failure
     */
    void onResourceUpdate(TransportProtos.ResourceUpdateMsg resourceUpdateMsgOpt);

    /**
     * Handles resource delete.
     *
     * @param resourceDeleteMsgOpt resource delete msg opt
     * @return nothing
     * @throws Exception on processing failure
     */
    void onResourceDelete(TransportProtos.ResourceDeleteMsg resourceDeleteMsgOpt);

    /**
     * Handles awake dev.
     *
     * @param registration registration ({@link Registration})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onAwakeDev(Registration registration);

    /**
     * Handles write response ok.
     *
     * @param client client ({@link LwM2mClient})
     * @param path path ({@link String})
     * @param request request payload with operation parameters
     * @param code code
     * @return nothing
     * @throws Exception on processing failure
     */
    void onWriteResponseOk(LwM2mClient client, String path, WriteRequest request, int code);

    /**
     * Handles createbject instances response ok.
     *
     * @param client client ({@link LwM2mClient})
     * @param path path ({@link String})
     * @param request request payload with operation parameters
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCreatebjectInstancesResponseOk(LwM2mClient client, String path, CreateRequest request);

    /**
     * Handles write composite response ok.
     *
     * @param client client ({@link LwM2mClient})
     * @param request request payload with operation parameters
     * @param code code
     * @return nothing
     * @throws Exception on processing failure
     */
    void onWriteCompositeResponseOk(LwM2mClient client, WriteCompositeRequest request, int code);

    /**
     * Handles to transport update credentials.
     *
     * @param sessionInfo session info
     * @param updateCredentials update credentials
     * @return nothing
     * @throws Exception on processing failure
     */
    void onToTransportUpdateCredentials(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.ToTransportUpdateCredentialsProto updateCredentials);

    /**
     * Init attributes.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @param logFailedUpdateOfNonChangedValue log failed update of non changed value
     * @return nothing
     * @throws Exception on processing failure
     */
    void initAttributes(LwM2mClient lwM2MClient, boolean logFailedUpdateOfNonChangedValue);

    /**
     * Returns config.
     *
     * @return {@link LwM2MTransportServerConfig}
     * @throws Exception on processing failure
     */
    LwM2MTransportServerConfig getConfig();

    /**
     * Returns converter.
     *
     * @return {@link LwM2mValueConverter}
     * @throws Exception on processing failure
     */
    LwM2mValueConverter getConverter();

    /**
     * Returns client context.
     *
     * @return {@link LwM2mClientContext}
     * @throws Exception on processing failure
     */
    LwM2mClientContext getClientContext();
}
