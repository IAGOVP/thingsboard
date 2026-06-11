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
package org.thingsboard.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.queue.util.TbCoreComponent;

/**
 * REST API exposing UI-related server configuration to authenticated clients.
 *
 * <p>Base path: {@code /api/uiSettings}. Values are read from server properties
 * (for example {@code ui.help.base-url} in {@code thingsboard.yml}).
 */
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class UiSettingsController extends BaseController {

    @Value("${ui.help.base-url}")
    private String helpBaseUrl;

    /**
     * Returns the base URL used by the web UI to load help documentation assets.
     *
     * <p><b>HTTP:</b> {@code GET /api/uiSettings/helpBaseUrl}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @return configured help base URL string
     */
    @ApiOperation(value = "Get UI help base url (getHelpBaseUrl)",
            notes = "Get UI help base url used to fetch help assets. " +
                    "The actual value of the base url is configurable in the system configuration file.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/uiSettings/helpBaseUrl")
    public String getHelpBaseUrl() {
        return helpBaseUrl;
    }

}
