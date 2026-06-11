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
package org.thingsboard.server.transport.lwm2m.server.ota.firmware;

/**
 * lw m2mfirmware update strategy values.
 */
public enum LwM2MFirmwareUpdateStrategy {
    OBJ_5_BINARY(1, "ObjectId 5, Binary"),
    OBJ_5_TEMP_URL(2, "ObjectId 5, URI"),
    OBJ_19_BINARY(3, "ObjectId 19, Binary");

    public int code;
    public String type;

    LwM2MFirmwareUpdateStrategy(int code, String type) {
        this.code = code;
        this.type = type;
    }
    /**
     * From strategy fw by type.
     *
     * @param type type ({@link String})
     * @return {@link LwM2MFirmwareUpdateStrategy}
     * @throws Exception on processing failure
     */
    public static LwM2MFirmwareUpdateStrategy fromStrategyFwByType(String type) {
        for (LwM2MFirmwareUpdateStrategy to : LwM2MFirmwareUpdateStrategy.values()) {
            if (to.type.equals(type)) {
                return to;
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported FW State type  : %s", type));
    }
    /**
     * From strategy fw by code.
     *
     * @param code code
     * @return {@link LwM2MFirmwareUpdateStrategy}
     * @throws Exception on processing failure
     */
    public static LwM2MFirmwareUpdateStrategy fromStrategyFwByCode(int code) {
        for (LwM2MFirmwareUpdateStrategy to : LwM2MFirmwareUpdateStrategy.values()) {
            if (to.code == code) {
                return to;
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported FW Strategy code : %s", code));
    }
}
