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
package org.thingsboard.server.transport.coap.client;

import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 * Tb coap content format util.
 */
public class TbCoapContentFormatUtil {
    /**
     * Returns content format.
     *
     * @param requestFormat request format
     * @param adaptorFormat adaptor format
     * @return monotonically increasing MQTT packet identifier
     * @throws Exception on processing failure
     */
    public static int getContentFormat(int requestFormat, int adaptorFormat) {
        if (isStrict(adaptorFormat)) {
            return adaptorFormat;
        } else {
            return requestFormat != MediaTypeRegistry.UNDEFINED ? requestFormat : adaptorFormat;
        }
    }
    /**
     * Is strict.
     *
     * @param contentFormat content format
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public static boolean isStrict(int contentFormat) {
        return contentFormat == MediaTypeRegistry.APPLICATION_OCTET_STREAM;
    }
}
