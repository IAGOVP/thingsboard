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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Binds Cross-Origin Resource Sharing (CORS) settings from {@code spring.mvc.cors.*}.
 *
 * <p>Each key in {@link #mappings} is a URL pattern (e.g. {@code /api/**}) and the value
 * is a Spring {@link CorsConfiguration} (allowed origins, methods, headers, credentials).
 * When mappings are non-empty, {@link ThingsboardSecurityConfiguration#corsFilter} registers
 * a {@link org.springframework.web.filter.CorsFilter} that applies them globally.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.mvc.cors")
public class MvcCorsProperties {

    /**
     * URL-pattern → CORS configuration map.
     * Populated from YAML under {@code spring.mvc.cors.mappings}.
     */
    private Map<String, CorsConfiguration> mappings = new HashMap<>();

    public MvcCorsProperties() {
        super();
    }

    /** Returns the CORS mapping table keyed by request path pattern. */
    public Map<String, CorsConfiguration> getMappings() {
        return mappings;
    }

    /** Sets the CORS mapping table (used by Spring Boot property binding). */
    public void setMappings(Map<String, CorsConfiguration> mappings) {
        this.mappings = mappings;
    }
}
