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

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Configures internationalization (i18n) message resolution for the server.
 *
 * <p>Registers a {@link ResourceBundleMessageSource} that loads translated strings from
 * {@code classpath:i18n/messages*.properties}. Controllers and services resolve user-facing
 * error messages and labels via {@link org.springframework.context.MessageSource#getMessage}.
 */
@Configuration
public class ThingsboardMessageConfiguration {

    /**
     * Primary {@link MessageSource} bean for the application.
     *
     * <p>Basename {@code i18n/messages} resolves locale-specific files such as
     * {@code messages_en.properties}, {@code messages_de.properties}, etc.
     *
     * @return UTF-8 encoded resource bundle message source
     */
    @Bean
    @Primary
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
