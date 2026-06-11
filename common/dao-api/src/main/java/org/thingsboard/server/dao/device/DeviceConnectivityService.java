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
package org.thingsboard.server.dao.device;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.io.Resource;
import org.thingsboard.server.common.data.Device;

import java.net.URISyntaxException;

/**
 * Service API for device connectivity persistence and domain operations.
 */
public interface DeviceConnectivityService {

    /**
     * Finds device publish telemetry commands.
     *
     * @param baseUrl base url ({@link String})
     * @param device device ({@link Device})
     * @return {@link JsonNode}
     * @throws URISyntaxException if urisyntax exception is thrown
     */
    JsonNode findDevicePublishTelemetryCommands(String baseUrl, Device device) throws URISyntaxException;

    /**
     * Returns pem cert file.
     *
     * @param protocol protocol ({@link String})
     * @return {@link Resource}
     */
    Resource getPemCertFile(String protocol);

    /**
     * Creates gateway docker compose file.
     *
     * @param baseUrl base url ({@link String})
     * @param device device ({@link Device})
     * @return {@link Resource}
     * @throws URISyntaxException if urisyntax exception is thrown
     */
    Resource createGatewayDockerComposeFile(String baseUrl, Device device) throws URISyntaxException;
}
