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
package org.thingsboard.server.edqs.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteOptions;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * Tb rocks db (EDQS microservice — EDQS utilities (RocksDB, mapping, versions)).
 */

@Slf4j
public class TbRocksDb {

    protected final String path;
    private final Options dbOptions;
    private final WriteOptions writeOptions;
    protected RocksDB db;

    static {
        RocksDB.loadLibrary();
    }

    public TbRocksDb(String path, Options dbOptions, WriteOptions writeOptions) {
        this.path = path;
        this.dbOptions = dbOptions;
        this.writeOptions = writeOptions;
    }
    /**
     * Starts Kafka consumers and wires partition/state services.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @SneakyThrows
    public void init() {
        log.debug("RocksDB init in {}", path);
        Files.createDirectories(Path.of(path).getParent());
        db = RocksDB.open(dbOptions, path);
    }
    /**
     * Put.
     *
     * @param key key ({@link String})
     * @param value value
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @SneakyThrows
    public void put(String key, byte[] value) {
        db.put(writeOptions, key.getBytes(StandardCharsets.UTF_8), value);
    }
    /**
     * For each.
     *
     * @param processor processor ({@link BiConsumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void forEach(BiConsumer<String, byte[]> processor) {
        try (RocksIterator iterator = db.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                String key = new String(iterator.key(), StandardCharsets.UTF_8);
                processor.accept(key, iterator.value());
            }
        }
    }
    /**
     * Deletes the requested data.
     *
     * @param key key ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @SneakyThrows
    public void delete(String key) {
        db.delete(writeOptions, key.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * Close.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void close() {
        if (db != null) {
            db.close();
        }
    }

}
