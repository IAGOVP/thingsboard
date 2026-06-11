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
package org.thingsboard.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.thingsboard.server.install.ThingsboardInstallService;

import java.util.Arrays;

/**
 * Spring Boot entry point for ThingsBoard database installation and upgrade.
 *
 * <p>Activates the {@code install} profile, loads a minimal component scan covering install
 * services, DAO, and cache layers, then delegates to {@link ThingsboardInstallService#performInstall()}.
 * Exits with status code {@code 1} on failure.
 *
 * <p>Default config file name is {@code thingsboard} ({@code thingsboard.yml} on the classpath).
 *
 * @see ThingsboardInstallService
 * @see ThingsboardServerApplication
 */
@Slf4j
@SpringBootConfiguration
@ComponentScan({"org.thingsboard.server.install",
        "org.thingsboard.server.service.component",
        "org.thingsboard.server.service.install",
        "org.thingsboard.server.service.security.auth.jwt.settings",
        "org.thingsboard.server.dao",
        "org.thingsboard.server.common.stats",
        "org.thingsboard.server.common.transport.config.ssl",
        "org.thingsboard.server.cache"
})
public class ThingsboardInstallApplication {

    private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "thingsboard";

    /**
     * Starts the install application, runs installation or upgrade, and exits the JVM.
     *
     * <p>Appends {@code --spring.config.name=thingsboard} when not supplied on the command line.
     *
     * @param args command-line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        try {
            SpringApplication application = new SpringApplication(ThingsboardInstallApplication.class);
            application.setAdditionalProfiles("install");
            ConfigurableApplicationContext context = application.run(updateArguments(args));
            context.getBean(ThingsboardInstallService.class).performInstall();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Ensures Spring loads {@code thingsboard.yml} unless the caller already set {@code --spring.config.name}.
     *
     * @param args original command-line arguments
     * @return arguments with default config name appended when missing
     */
    private static String[] updateArguments(String[] args) {
        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))) {
            String[] modifiedArgs = new String[args.length + 1];
            System.arraycopy(args, 0, modifiedArgs, 0, args.length);
            modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
            return modifiedArgs;
        }
        return args;
    }
}
