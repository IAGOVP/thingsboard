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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Spring configuration for cryptographic primitives used across the ThingsBoard server.
 *
 * <p>Registers a {@link BCryptPasswordEncoder} bean for hashing and verifying user passwords
 * during authentication. BCrypt is used because it is adaptive (work factor) and resistant
 * to rainbow-table attacks. The encoder is injected wherever password comparison or hashing
 * is required (e.g. REST login, user registration, password reset flows).
 */
@Configuration
public class CryptoConfig {

    /**
     * Creates the application-wide password encoder bean.
     *
     * <p>Uses the default BCrypt strength (10 rounds). All stored user passwords are
     * expected to be BCrypt hashes; plaintext passwords are never persisted.
     *
     * @return a {@link BCryptPasswordEncoder} shared by authentication providers
     */
    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
