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
package org.thingsboard.server.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SSL/TLS credential paths for encrypted Redis connections.
 *
 * <p>Bound from {@code redis.ssl.credentials.*} and consumed by
 * {@link TBRedisCacheConfiguration#createSslSocketFactory()} when
 * {@code redis.ssl.enabled=true}.
 *
 * <p>File layout:
 * <ul>
 *   <li>{@link #certFile} — CA / server trust chain used to build the trust store</li>
 *   <li>{@link #userCertFile} — optional client certificate for mutual TLS</li>
 *   <li>{@link #userKeyFile} — private key matching {@link #userCertFile}</li>
 * </ul>
 *
 * <p>When {@code userCertFile} or {@code userKeyFile} is blank, only server verification
 * (one-way TLS) is configured.
 *
 * @see TBRedisCacheConfiguration
 */
@Configuration
@ConfigurationProperties(prefix = "redis.ssl.credentials")
@Data
public class RedisSslCredentials {

    /** Path to CA certificate file(s) for trusting the Redis server. Property: {@code redis.ssl.credentials.certFile}. */
    private String certFile;

    /** Path to client certificate PEM for mutual TLS. Property: {@code redis.ssl.credentials.userCertFile}. */
    private String userCertFile;

    /** Path to client private key PEM. Property: {@code redis.ssl.credentials.userKeyFile}. */
    private String userKeyFile;
}
