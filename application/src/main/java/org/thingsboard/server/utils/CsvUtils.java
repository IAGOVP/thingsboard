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
package org.thingsboard.server.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.CharSequenceReader;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CSV parsing and generation helpers built on Apache Commons CSV.
 *
 * <p>Supports configurable delimiters for import parsing and UTF-8 encoded output for export.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvUtils {

    /**
     * Parses CSV text content into a list of rows, each row being a list of cell values.
     *
     * @param content   raw CSV string to parse
     * @param delimiter field separator character (comma uses {@link CSVFormat#DEFAULT})
     * @return list of rows, where each row is an ordered list of string cell values
     * @throws Exception if CSV parsing fails
     */
    public static List<List<String>> parseCsv(String content, Character delimiter) throws Exception {
        CSVFormat csvFormat = delimiter.equals(',') ? CSVFormat.DEFAULT : CSVFormat.DEFAULT.withDelimiter(delimiter);

        List<CSVRecord> records;
        try (CharSequenceReader reader = new CharSequenceReader(content)) {
            records = csvFormat.parse(reader).getRecords();
        }

        return records.stream()
                .map(record -> Stream.iterate(0, i -> i < record.size(), i -> i + 1)
                        .map(record::get)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Generates UTF-8 encoded CSV bytes from a list of string rows.
     *
     * @param rows table data where each inner list represents one CSV record
     * @return UTF-8 byte array containing the serialized CSV content
     */
    @SneakyThrows
    public static byte[] generateCsv(List<List<String>> rows) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (List<String> row : rows) {
                csvPrinter.printRecord(row);
            }
            csvPrinter.flush();
        }
        return out.toByteArray();
    }

}
