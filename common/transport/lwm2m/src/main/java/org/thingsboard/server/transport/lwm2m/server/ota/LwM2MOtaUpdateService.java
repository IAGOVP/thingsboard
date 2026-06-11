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
package org.thingsboard.server.transport.lwm2m.server.ota;

import org.thingsboard.server.common.data.device.profile.lwm2m.OtherConfiguration;
import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClient;

import java.util.Optional;

/**
 * Service contract for lw m2mota update (LwM2M transport and object model (ThingsBoard common module)).
 *
 * <p>Implemented by the corresponding class in this or the dao module.
 */
public interface LwM2MOtaUpdateService {

    void init(LwM2mClient client);

    /**
     * Force firmware update.
     *
     * @param client client ({@link LwM2mClient})
     * @return nothing
     * @throws Exception on processing failure
     */
    void forceFirmwareUpdate(LwM2mClient client);

    /**
     * Handles target firmware update.
     *
     * @param client client ({@link LwM2mClient})
     * @param newFwTitle new fw title ({@link String})
     * @param newFwVersion new fw version ({@link String})
     * @param newFwUrl new fw url ({@link Optional})
     * @param newFwTag new fw tag ({@link Optional})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onTargetFirmwareUpdate(LwM2mClient client, String newFwTitle, String newFwVersion, Optional<String> newFwUrl, Optional<String> newFwTag);

    /**
     * Handles target software update.
     *
     * @param client client ({@link LwM2mClient})
     * @param newSwTitle new sw title ({@link String})
     * @param newSwVersion new sw version ({@link String})
     * @param newSwUrl new sw url ({@link Optional})
     * @param newSwTag new sw tag ({@link Optional})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onTargetSoftwareUpdate(LwM2mClient client, String newSwTitle, String newSwVersion, Optional<String> newSwUrl, Optional<String> newSwTag);

    /**
     * Handles current firmware name update.
     *
     * @param client client ({@link LwM2mClient})
     * @param name name ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentFirmwareNameUpdate(LwM2mClient client, String name);

    /**
     * Handles firmware strategy update.
     *
     * @param client client ({@link LwM2mClient})
     * @param configuration configuration ({@link OtherConfiguration})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onFirmwareStrategyUpdate(LwM2mClient client, OtherConfiguration configuration);

    /**
     * Handles current software strategy update.
     *
     * @param client client ({@link LwM2mClient})
     * @param configuration configuration ({@link OtherConfiguration})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentSoftwareStrategyUpdate(LwM2mClient client, OtherConfiguration configuration);

    /**
     * Handles current firmware version3update.
     *
     * @param client client ({@link LwM2mClient})
     * @param version version ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentFirmwareVersion3Update(LwM2mClient client, String version);

    /**
     * Handles current firmware version update.
     *
     * @param client client ({@link LwM2mClient})
     * @param version version ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentFirmwareVersionUpdate(LwM2mClient client, String version);

    /**
     * Handles current firmware state update.
     *
     * @param client client ({@link LwM2mClient})
     * @param state state ({@link Long})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentFirmwareStateUpdate(LwM2mClient client, Long state);

    /**
     * Handles current firmware result update.
     *
     * @param client client ({@link LwM2mClient})
     * @param result result ({@link Long})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentFirmwareResultUpdate(LwM2mClient client, Long result);

    /**
     * Handles current firmware delivery method update.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @param value value ({@link Long})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentFirmwareDeliveryMethodUpdate(LwM2mClient lwM2MClient, Long value);

    /**
     * Handles current software name update.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @param name name ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentSoftwareNameUpdate(LwM2mClient lwM2MClient, String name);

    /**
     * Handles current software version3update.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @param version version ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentSoftwareVersion3Update(LwM2mClient lwM2MClient, String version);

    /**
     * Handles current software version update.
     *
     * @param client client ({@link LwM2mClient})
     * @param version version ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentSoftwareVersionUpdate(LwM2mClient client, String version);

    /**
     * Handles current software state update.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @param value value ({@link Long})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentSoftwareStateUpdate(LwM2mClient lwM2MClient, Long value);

    /**
     * Handles current software result update.
     *
     * @param client client ({@link LwM2mClient})
     * @param result result ({@link Long})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onCurrentSoftwareResultUpdate(LwM2mClient client, Long result);

    /**
     * Is ota downloading.
     *
     * @param client client ({@link LwM2mClient})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean isOtaDownloading(LwM2mClient client);
}
