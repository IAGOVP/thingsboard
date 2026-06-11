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
package org.thingsboard.server.service.edge.instructions;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.service.install.InstallScripts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.thingsboard.edge.rpc.EdgeGrpcClient.getNewestEdgeVersion;
/**
 * Service implementation for base edge install upgrade instructions in edge upgrade instructions.
 */

@Slf4j
@RequiredArgsConstructor
public abstract class BaseEdgeInstallUpgradeInstructionsService {

    private static final String EDGE_DIR = "edge";
    private static final String INSTRUCTIONS_DIR = "instructions";

    private final InstallScripts installScripts;

    @Setter
    protected String platformEdgeVersion = convertEdgeVersionToDocsFormat(getNewestEdgeVersion().name());

    /**
     * Converts edge version to docs format.
     *
     * @param edgeVersion edge version (String)
     * @return {@link String} result
     */

    protected String convertEdgeVersionToDocsFormat(String edgeVersion) {
        return edgeVersion.replace("_", ".").substring(2);
    }

    /**
     * Read file.
     *
     * @param file file (Path)
     * @return {@link String} result
     */

    protected String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            log.warn("Failed to read file: {}", file, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns tag version.
     *
     * @param version version (String)
     * @return {@link String} result
     */

    protected String getTagVersion(String version) {
        return version.endsWith(".0") ? version.substring(0, version.length() - 2) : version;
    }

    /**
     * Resolve file.
     *
     * @param subDir sub dir (String)
     * @param subDirs sub dirs
     * @return {@link Path} result
     */

    protected Path resolveFile(String subDir, String... subDirs) {
        return getEdgeInstructionsDir().resolve(Paths.get(subDir, subDirs));
    }

    /**
     * Returns edge instructions dir.
     *
     */

    protected Path getEdgeInstructionsDir() {
        return Paths.get(installScripts.getDataDir(), InstallScripts.JSON_DIR, EDGE_DIR, INSTRUCTIONS_DIR, getBaseDirName());
    }

    /**
     * Returns base dir name.
     *
     */

    protected abstract String getBaseDirName();

}
