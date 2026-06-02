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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thingsboard.server.queue.util.AfterStartUp;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Spring Boot entry point for the ThingsBoard core server (CE monolith or {@code tb-node} microservice).
 *
 * <p>Scans {@code org.thingsboard.server} and {@code org.thingsboard.script}. Default config file name is
 * {@code thingsboard} ({@code thingsboard.yml} on the classpath, overridable via {@code /etc/thingsboard/conf}).
 *
 * @see org.thingsboard.server.controller package for REST API
 * @see org.thingsboard.server.actors package for actor runtime
 */
@SpringBootConfiguration
@EnableAsync
@EnableScheduling
@ComponentScan({"org.thingsboard.server", "org.thingsboard.script"})
@Slf4j
public class ThingsboardServerApplication {

    private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "thingsboard";

    private static long startTs;

    /**
     * Starts the JVM process and Spring context. Appends {@code --spring.config.name=thingsboard} when not supplied.
     */
    public static void main(String[] args) {
        startTs = System.currentTimeMillis();
        SpringApplication.run(ThingsboardServerApplication.class, updateArguments(args));
    }

    /** Ensures Spring loads {@code thingsboard.yml} unless the caller already set {@code --spring.config.name}. */
    private static String[] updateArguments(String[] args) {
        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))) {
            String[] modifiedArgs = new String[args.length + 1];
            System.arraycopy(args, 0, modifiedArgs, 0, args.length);
            modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
            return modifiedArgs;
        }
        return args;
    }

    /** Logs total wall-clock startup time after all {@link AfterStartUp} hooks complete. */
    @AfterStartUp(order = Ordered.LOWEST_PRECEDENCE)
    public void afterStartUp() {
        long startupTimeMs = System.currentTimeMillis() - startTs;
        log.info("Started ThingsBoard in {} seconds", TimeUnit.MILLISECONDS.toSeconds(startupTimeMs));
    }

}
