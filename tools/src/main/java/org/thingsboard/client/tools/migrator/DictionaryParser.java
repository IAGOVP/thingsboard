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
package org.thingsboard.client.tools.migrator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.thingsboard.server.common.data.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses {@code COPY public.key_dictionary (...)} from a PostgreSQL dump into an in-memory
 * map from numeric key id (column 2 in dump row) to key name (column 1).
 */
public class DictionaryParser {
    /** key id (string) → telemetry key name */
    private Map<String, String> dictionaryParsed = new HashMap<>();

    /**
     * Scans {@code sourceFile} once for the key_dictionary COPY block.
     *
     * @param sourceFile SQL dump (may be the same file as telemetry COPY blocks)
     */
    public DictionaryParser(File sourceFile) throws IOException {
        parseDictionaryDump(FileUtils.lineIterator(sourceFile));
    }

    /**
     * @param keyId dictionary id from {@code ts_kv} row (second tab field)
     * @return telemetry key name, or null if unknown
     */
    public String getKeyByKeyId(String keyId) {
        return dictionaryParsed.get(keyId);
    }

    private boolean isBlockFinished(String line) {
        return StringUtils.isBlank(line) || line.equals("\\.");
    }

    private boolean isBlockStarted(String line) {
        return line.startsWith("COPY public.key_dictionary (");
    }

    private void parseDictionaryDump(LineIterator iterator) throws IOException {
        try {
            String tempLine;
            while (iterator.hasNext()) {
                tempLine = iterator.nextLine();

                if (isBlockStarted(tempLine)) {
                    processBlock(iterator);
                }
            }
        } finally {
            iterator.close();
        }
    }

    private void processBlock(LineIterator lineIterator) {
        String tempLine;
        String[] lineSplited;
        while(lineIterator.hasNext()) {
            tempLine = lineIterator.nextLine();
            if(isBlockFinished(tempLine)) {
                return;
            }

            lineSplited = tempLine.split("\t");
            dictionaryParsed.put(lineSplited[1], lineSplited[0]);
        }
    }
}
