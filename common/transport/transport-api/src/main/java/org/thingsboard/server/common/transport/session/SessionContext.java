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
package org.thingsboard.server.common.transport.session;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.gen.transport.TransportProtos;

import java.util.Optional;
import java.util.UUID;

/**
 * Base contract for an active device transport session.
 *
 * <p>Tracks session id, MQTT message ids, and reacts to device/profile updates propagated from core.
 */
public interface SessionContext {

    UUID getSessionId();

    /**
     * Returns the next MQTT packet identifier for outbound publishes on this session.
     *
     * @return monotonically increasing message id (1–65535)
     */
    int nextMsgId();

    /**
     * Applies a device profile change pushed from core (transport configuration, topic filters, payload type).
     *
     * @param sessionInfo   active session protobuf descriptor
     * @param deviceProfile updated device profile
     */
    void onDeviceProfileUpdate(TransportProtos.SessionInfoProto sessionInfo, DeviceProfile deviceProfile);

    /**
     * Applies a device entity change pushed from core (name, label, type, customer assignment).
     *
     * @param sessionInfo       active session protobuf descriptor
     * @param device            updated device entity
     * @param deviceProfileOpt  profile when it changed together with the device, else empty
     */
    void onDeviceUpdate(TransportProtos.SessionInfoProto sessionInfo, Device device, Optional<DeviceProfile> deviceProfileOpt);

}
