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
package org.thingsboard.server.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

/**
 * Spring Boot entry for the HTTP transport microservice.
 *
 * <p>Defaults config to {@code tb-http-transport} and component-scans
 * {@code org.thingsboard.server.transport.http} (device API {@code /api/v1}).
 *
 * @see org.thingsboard.server.transport.http.DeviceApiController
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@ComponentScan({"org.thingsboard.server.http", "org.thingsboard.server.common", "org.thingsboard.server.transport.http", "org.thingsboard.server.queue", "org.thingsboard.server.cache"})
public class ThingsboardHttpTransportApplication {

    private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "tb-http-transport";

    /**
     * @param args CLI args; appends {@code --spring.config.name=tb-http-transport} when missing
     */
    public static void main(String[] args) {
        SpringApplication.run(ThingsboardHttpTransportApplication.class, updateArguments(args));
    }

    /** Ensures Spring loads {@code tb-http-transport.yml} from the classpath. */
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
