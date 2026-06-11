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
package org.thingsboard.server.cache.ota;

/**
 * Cache for OTA firmware/package binary blobs to avoid repeated database reads during device updates.
 *
 * <p>Implementations: {@link CaffeineOtaPackageCache} (local), {@link RedisOtaPackageDataCache} (cluster).
 * Keys are OTA package IDs; values are raw {@code byte[]} firmware data.
 */
public interface OtaPackageDataCache {

/**
         * Returns full package data for the given key.
         *
         * @param key OTA package identifier
         * @return firmware bytes, or {@code null} on miss
         */
    byte[] get(String key);

/**
         * Returns a byte range slice for chunked firmware download.
         *
         * @param key       OTA package identifier
         * @param chunkSize bytes per chunk; values {@code < 1} return full data
         * @param chunk     zero-based chunk index
         * @return slice bytes, or empty array when out of range
         */
    byte[] get(String key, int chunkSize, int chunk);

/**
         * Stores firmware data (typically put-if-absent semantics in implementations).
         *
         * @param key   OTA package identifier
         * @param value raw firmware bytes
         */
    void put(String key, byte[] value);

/**
         * Removes cached firmware data.
         *
         * @param key OTA package identifier
         */
    void evict(String key);

/**
         * Checks whether any data exists for the package.
         *
         * @param otaPackageId package identifier
         * @return {@code true} when the first byte chunk is non-empty
         */
    default boolean has(String otaPackageId) {
        byte[] data = get(otaPackageId, 1, 0);
        return data != null && data.length > 0;
    }
}
