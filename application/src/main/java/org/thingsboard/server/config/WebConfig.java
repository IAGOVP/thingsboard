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
package org.thingsboard.server.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thingsboard.server.utils.MiscUtils;

import java.io.IOException;

/**
 * MVC controller for SPA routing and Swagger UI URL compatibility.
 *
 * <p>The ThingsBoard web UI is a single-page application (Angular). Deep links such as
 * {@code /dashboards} or {@code /entities/devices} must be forwarded to {@code index.html}
 * so the client-side router can handle them. API, static asset, and Swagger paths are excluded.
 */
@Controller
public class WebConfig {

    /**
     * Forwards all non-API, non-static UI routes to the SPA entry point.
     *
     * <p>Matches {@code /assets}, {@code /assets/}, and any path segment that does not
     * start with {@code api}, {@code assets}, {@code static}, {@code webjars}, or
     * {@code swagger-ui}, and contains no file extension (dot).
     *
     * @return Spring MVC forward directive to {@code /index.html}
     */
    @RequestMapping(value = {"/assets", "/assets/", "/{path:^(?!api$)(?!assets$)(?!static$)(?!webjars$)(?!swagger-ui$)[^\\.]*}/**"})
    public String redirect() {
        return "forward:/index.html";
    }

    /**
     * Redirects legacy {@code /swagger-ui.html} URLs to the springdoc Swagger UI path.
     *
     * @param request  incoming HTTP request (used to build the base URL)
     * @param response response used to send a 302 redirect
     */
    @RequestMapping("/swagger-ui.html")
    public void redirectSwagger(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseUrl = MiscUtils.constructBaseUrl(request);
        response.sendRedirect(baseUrl + "/swagger-ui/");
    }

    /**
     * Forwards {@code /swagger-ui/} to the actual springdoc index page.
     *
     * @return forward to {@code /swagger-ui/index.html}
     */
    @RequestMapping("/swagger-ui/")
    public String redirectSwaggerIndex() throws IOException {
        return "forward:/swagger-ui/index.html";
    }

}
