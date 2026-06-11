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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds HTTP security response header settings from {@code security.headers.*} configuration.
 *
 * <p>Each nested group maps to a standard browser security header. Values are applied
 * by {@link HttpSecurityHeadersCustomizer} on every {@link org.springframework.security.web.SecurityFilterChain}.
 *
 * <p>Defaults favor XSS protection ({@code X-Content-Type-Options}) and referrer privacy
 * while leaving frame embedding and CSP disabled unless explicitly enabled by the operator.
 */
@Component
@ConfigurationProperties(prefix = "security.headers")
@Data
public class HttpSecurityHeadersProperties {

    /** Controls {@code X-Content-Type-Options: nosniff} (prevents MIME-type sniffing). */
    private XContentTypeOptions xContentTypeOptions = new XContentTypeOptions();

    /** Controls {@code Referrer-Policy} response header. */
    private ReferrerPolicy referrerPolicy = new ReferrerPolicy();

    /** Controls {@code X-Frame-Options} (clickjacking protection). */
    private XFrameOptions xFrameOptions = new XFrameOptions();

    /** Controls {@code Content-Security-Policy} or {@code Content-Security-Policy-Report-Only}. */
    private ContentSecurityPolicy contentSecurityPolicy = new ContentSecurityPolicy();

    /** Settings for the {@code X-Content-Type-Options} header. */
    @Data
    public static class XContentTypeOptions {
        /** When true, sends {@code X-Content-Type-Options: nosniff}. */
        private boolean enabled = true;
    }

    /** Settings for the {@code Referrer-Policy} header. */
    @Data
    public static class ReferrerPolicy {
        /** When true, adds the Referrer-Policy header. */
        private boolean enabled = true;
        /** Policy value, e.g. {@code strict-origin-when-cross-origin}. */
        private String value = "strict-origin-when-cross-origin";
    }

    /** Settings for the {@code X-Frame-Options} header. */
    @Data
    public static class XFrameOptions {
        /** When true, adds X-Frame-Options. Disabled by default to allow dashboard embedding. */
        private boolean enabled = false;
        /** Valid values: {@code DENY} or {@code SAMEORIGIN}. */
        private String value = "SAMEORIGIN";
    }

    /** Settings for Content Security Policy headers. */
    @Data
    public static class ContentSecurityPolicy {
        /** When true and {@link #value} is non-empty, sends a CSP header. */
        private boolean enabled = false;
        /** CSP directive string, e.g. {@code default-src 'self'}. */
        private String value = "";
        /** When true, uses {@code Content-Security-Policy-Report-Only} instead of enforcing. */
        private boolean reportOnly = false;
    }

}
