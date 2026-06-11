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
package org.thingsboard.server.dao.timeseries;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.CompletableFuture;






















/**






 * Cassandra ts partitions cache (Cassandra telemetry and latest-value DAO (Cassandra time-series DAO and latest-value caches)).






 */







public class CassandraTsPartitionsCache {

    private AsyncLoadingCache<CassandraPartitionCacheKey, Boolean> partitionsCache;

    public CassandraTsPartitionsCache(long maxCacheSize) {
        this.partitionsCache = Caffeine.newBuilder()
                .maximumSize(maxCacheSize)
                .buildAsync(key -> {
                    throw new IllegalStateException("'get' methods calls are not supported!");
                });
    }
    /**
     * Has.
     *
     * @param key attribute or cache key
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean has(CassandraPartitionCacheKey key) {
        return partitionsCache.getIfPresent(key) != null;
    }
    /**
     * Put.
     *
     * @param key attribute or cache key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void put(CassandraPartitionCacheKey key) {
        partitionsCache.put(key, CompletableFuture.completedFuture(true));
    }
    /**
     * Invalidate.
     *
     * @param key attribute or cache key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void invalidate(CassandraPartitionCacheKey key) {
        partitionsCache.synchronous().invalidate(key);
    }
}
