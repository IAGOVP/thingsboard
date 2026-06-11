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
package org.thingsboard.server.install;

import org.springframework.boot.ExitCodeGenerator;

/**
 * Unchecked exception thrown when ThingsBoard installation or upgrade fails fatally.
 *
 * <p>Implements {@link ExitCodeGenerator} so Spring Boot exits the JVM with a non-zero
 * status code when this exception propagates from the install profile.
 *
 * @see ThingsboardInstallService
 * @see ThingsboardInstallApplication
 */
public class ThingsboardInstallException extends RuntimeException implements ExitCodeGenerator {

    /**
     * Creates an install failure exception with a descriptive message and root cause.
     *
     * @param message detail message describing the installation failure
     * @param cause   underlying exception that caused the install to abort
     */
    public ThingsboardInstallException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the process exit code used when the install application terminates on failure.
     *
     * @return non-zero exit code ({@code 1}) indicating install failure
     */
    public int getExitCode() {
        return 1;
    }

}
