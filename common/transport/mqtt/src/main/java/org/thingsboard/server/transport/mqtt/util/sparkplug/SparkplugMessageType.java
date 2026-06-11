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
package org.thingsboard.server.transport.mqtt.util.sparkplug;

import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;

/**
 * An enumeration of Sparkplug MQTT message types.  The type provides an indication as to what the MQTT Payload of 
 * message will contain.
 */
public enum SparkplugMessageType {
	
	/**
	 * Birth certificate for MQTT Edge of Network (EoN) Nodes.
	 */
	NBIRTH,
	
	/**
	 * Death certificate for MQTT Edge of Network (EoN) Nodes.
	 */
	NDEATH,
	
	/**
	 * Birth certificate for MQTT Devices.
	 */
	DBIRTH,
	
	/**
	 * Death certificate for MQTT Devices.
	 */
	DDEATH,
	
	/**
	 * Edge of Network (EoN) Node data message.
	 */
	NDATA,
	
	/**
	 * Device data message.
	 */
	DDATA,
	
	/**
	 * Edge of Network (EoN) Node command message.
	 */
	NCMD,
	
	/**
	 * Device command message.
	 */
	DCMD,
	
	/**
	 * Critical application state message.
	 */
	STATE,
	
	/**
	 * Device record message.
	 */
	DRECORD,
	
	/**
	 * Edge of Network (EoN) Node record message.
	 */
	NRECORD;

    /**
     * Parse message type.
     *
     * @param type type ({@link String})
     * @return {@link SparkplugMessageType}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */
	public static SparkplugMessageType parseMessageType(String type) throws ThingsboardException {
		for (SparkplugMessageType messageType : SparkplugMessageType.values()) {
			if (messageType.name().equals(type)) {
				return messageType;
			}
		}
		throw new ThingsboardException("Invalid message type: " + type, ThingsboardErrorCode.INVALID_ARGUMENTS);
	}
	/**
	 * Message name.
	 *
	 * @param type type ({@link SparkplugMessageType})
	 * @return {@link String}
	 * @throws Exception on processing failure
	 */
	public static String messageName(SparkplugMessageType type) {
		return STATE.equals(type) ? "sparkplugConnectionState" : type.name();
	}
	/**
	 * Is state.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isState() {
		return this.equals(STATE);
	}
	/**
	 * Is death.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isDeath() {
		return this.equals(DDEATH) || this.equals(NDEATH);
	}
	/**
	 * Is command.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isCommand() {
		return this.equals(DCMD) || this.equals(NCMD);
	}
	/**
	 * Is data.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isData() {
		return this.equals(DDATA) || this.equals(NDATA);
	}
	/**
	 * Is birth.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isBirth() {
		return this.equals(DBIRTH) || this.equals(NBIRTH);
	}
	/**
	 * Is record.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isRecord() {
		return this.equals(DRECORD) || this.equals(NRECORD);
	}
	/**
	 * Is subscribe.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isSubscribe() {
		return isCommand() || isData() || isRecord();
	}
	/**
	 * Is node.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isNode() {
		return this.equals(NBIRTH)
				|| this.equals(NCMD) || this.equals(NDATA)
				||this.equals(NDEATH) || this.equals(NRECORD);
	}
	/**
	 * Is device.
	 *
	 * @return the boolean result
	 * @throws Exception on processing failure
	 */
	public boolean isDevice() {
		return this.equals(DBIRTH)
				|| this.equals(DCMD) || this.equals(DDATA)
				||this.equals(DDEATH) || this.equals(DRECORD);
	}

}
