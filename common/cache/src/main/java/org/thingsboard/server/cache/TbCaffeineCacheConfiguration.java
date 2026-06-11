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
package org.thingsboard.server.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Ticker;
import com.github.benmanes.caffeine.cache.Weigher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Spring configuration registering Caffeine-backed caches from {@link CacheSpecsMap}.
 *
 * <p>Activated when {@code cache.type=caffeine} (default when property is absent).
 * Builds a {@link org.springframework.cache.support.SimpleCacheManager} with one
 * {@link org.springframework.cache.caffeine.CaffeineCache} per configured spec.
 *
 * <p>Each cache uses:
 * <ul>
 *   <li>{@link CacheSpecs#getMaxSize()} as Caffeine maximum weight</li>
 *   <li>{@link CacheSpecs#getTimeToLiveInMinutes()} for {@code expireAfterWrite} (skipped when 0)</li>
 *   <li>A collection-aware weigher so list-valued entries count by element count</li>
 * </ul>
 *
 * @see CacheSpecsMap
 * @see CaffeineTbTransactionalCache
 * @see TBRedisCacheConfiguration
 */
@Configuration
@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@EnableCaching
@Slf4j
public class TbCaffeineCacheConfiguration {

    private final CacheSpecsMap configuration;

    public TbCaffeineCacheConfiguration(CacheSpecsMap configuration) {
        this.configuration = configuration;
    }

/**
         * Creates the Spring {@link CacheManager} with all configured Caffeine caches.
         *
         * @return initialized simple cache manager
         */
    /**
     * Transaction aware CaffeineCache implementation with TransactionAwareCacheManagerProxy
     * to synchronize cache put/evict operations with ongoing Spring-managed transactions.
     */
    @Bean
    public CacheManager cacheManager() {
        log.trace("Initializing cache: {} specs {}", Arrays.toString(RemovalCause.values()), configuration.getSpecs());
        SimpleCacheManager manager = new SimpleCacheManager();
        if (configuration.getSpecs() != null) {
            List<CaffeineCache> caches =
                    configuration.getSpecs().entrySet().stream()
                            .map(entry -> buildCache(entry.getKey(),
                                    entry.getValue()))
                            .collect(Collectors.toList());
            manager.setCaches(caches);
        }

        //SimpleCacheManager is not a bean (will be wrapped), so call initializeCaches manually
        manager.initializeCaches();

        return manager;
    }

    private CaffeineCache buildCache(String name, CacheSpecs cacheSpec) {
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .weigher(collectionSafeWeigher())
                .maximumWeight(cacheSpec.getMaxSize())
                .recordStats()
                .ticker(ticker());
        if (!cacheSpec.getTimeToLiveInMinutes().equals(0)) {
            caffeineBuilder.expireAfterWrite(cacheSpec.getTimeToLiveInMinutes(), TimeUnit.MINUTES);
        }
        return new CaffeineCache(name, caffeineBuilder.build());
    }

/**
         * Supplies the Caffeine {@link com.github.benmanes.caffeine.cache.Ticker} for TTL measurement.
         *
         * @return system nano-time ticker
         */
    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }

    private Weigher<? super Object, ? super Object> collectionSafeWeigher() {
        return (Weigher<Object, Object>) (key, value) -> {
            if (value instanceof Collection) {
                return ((Collection) value).size();
            }
            return 1;
        };
    }

}
