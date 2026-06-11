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
package org.thingsboard.server.transport.lwm2m.server.client;

import org.eclipse.leshan.server.registration.Registration;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.device.profile.Lwm2mDeviceProfileTransportConfiguration;
import org.thingsboard.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import org.thingsboard.server.gen.transport.TransportProtos;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * lw m2m client context contract (LwM2M transport and object model (ThingsBoard common module)).
 */
public interface LwM2mClientContext {

    LwM2mClient getClientByEndpoint(String endpoint);

    /**
     * Returns client by session info.
     *
     * @param sessionInfo session info
     * @return {@link LwM2mClient}
     * @throws Exception on processing failure
     */
    LwM2mClient getClientBySessionInfo(TransportProtos.SessionInfoProto sessionInfo);

    /**
     * Register.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @param registration registration ({@link Registration})
     * @return optional the TransportProtos.SessionInfoProto value, empty if not found
     * @throws LwM2MClientStateException if lw m2mclient state exception is thrown during processing
     */
    Optional<TransportProtos.SessionInfoProto> register(LwM2mClient lwM2MClient, Registration registration) throws LwM2MClientStateException;

    /**
     * Updates registration.
     *
     * @param client client ({@link LwM2mClient})
     * @param registration registration ({@link Registration})
     * @return nothing
     * @throws LwM2MClientStateException if lw m2mclient state exception is thrown during processing
     */
    void updateRegistration(LwM2mClient client, Registration registration) throws LwM2MClientStateException;

    /**
     * Unregister.
     *
     * @param client client ({@link LwM2mClient})
     * @param registration registration ({@link Registration})
     * @return nothing
     * @throws LwM2MClientStateException if lw m2mclient state exception is thrown during processing
     */
    void unregister(LwM2mClient client, Registration registration) throws LwM2MClientStateException;

    /**
     * Returns lw m2m clients.
     *
     * @return {@link Collection}
     * @throws Exception on processing failure
     */
    Collection<LwM2mClient> getLwM2mClients();

    /**
     * Returns profile.
     *
     * @param registration registration ({@link Registration})
     * @return {@link Lwm2mDeviceProfileTransportConfiguration}
     * @throws Exception on processing failure
     */
    Lwm2mDeviceProfileTransportConfiguration getProfile(Registration registration);

    /**
     * Profile update.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return {@link Lwm2mDeviceProfileTransportConfiguration}
     * @throws Exception on processing failure
     */
    Lwm2mDeviceProfileTransportConfiguration profileUpdate(DeviceProfile deviceProfile);

    /**
     * Returns supported id ver in client.
     *
     * @param registration registration ({@link LwM2mClient})
     * @return {@link Set}
     * @throws Exception on processing failure
     */
    Set<String> getSupportedIdVerInClient(LwM2mClient registration);

    /**
     * Returns client by device id.
     *
     * @param deviceId target device identifier
     * @return {@link LwM2mClient}
     * @throws Exception on processing failure
     */
    LwM2mClient getClientByDeviceId(UUID deviceId);

    /**
     * Returns object id by key name from profile.
     *
     * @param lwM2mClient lw m2m client ({@link LwM2mClient})
     * @param keyName key name ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getObjectIdByKeyNameFromProfile(LwM2mClient lwM2mClient, String keyName);

    /**
     * Register client.
     *
     * @param registration registration ({@link Registration})
     * @param credentials credentials ({@link ValidateDeviceCredentialsResponse})
     * @return nothing
     * @throws Exception on processing failure
     */
    void registerClient(Registration registration, ValidateDeviceCredentialsResponse credentials);

    /**
     * Updates the requested data.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @return nothing
     * @throws Exception on processing failure
     */
    void update(LwM2mClient lwM2MClient);

    /**
     * Send msgs after sleeping.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @return nothing
     * @throws Exception on processing failure
     */
    void sendMsgsAfterSleeping(LwM2mClient lwM2MClient);

    /**
     * Handles uplink.
     *
     * @param client client ({@link LwM2mClient})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onUplink(LwM2mClient client);

    /**
     * Returns request timeout.
     *
     * @param client client ({@link LwM2mClient})
     * @return {@link Long}
     * @throws Exception on processing failure
     */
    Long getRequestTimeout(LwM2mClient client);

    /**
     * Asleep.
     *
     * @param client client ({@link LwM2mClient})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean asleep(LwM2mClient client);

    /**
     * Awake.
     *
     * @param client client ({@link LwM2mClient})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean awake(LwM2mClient client);

    /**
     * Is downlink allowed.
     *
     * @param client client ({@link LwM2mClient})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean isDownlinkAllowed(LwM2mClient client);

}
