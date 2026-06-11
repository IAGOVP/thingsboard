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
package org.thingsboard.server.cache.customer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CacheSpecsMap;
import org.thingsboard.server.cache.RedisTbTransactionalCache;
import org.thingsboard.server.cache.TBRedisCacheConfiguration;
import org.thingsboard.server.cache.TbJsonRedisSerializer;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.Customer;

/**
 * Redis {@link RedisTbTransactionalCache} for {@link org.thingsboard.server.common.data.Customer} entities.
 *
 * <p>Spring bean {@code "CustomerCache"} activated when
 * {@code cache.type=redis}. Shares cluster-wide state via {@link TBRedisCacheConfiguration}.
 * Cache name: {@link org.thingsboard.server.common.data.CacheConstants#CUSTOMER_CACHE}.
 *
 * @see CustomerCacheKey
 * @see RedisTbTransactionalCache
 */
@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("CustomerCache")
public class CustomerRedisCache extends RedisTbTransactionalCache<CustomerCacheKey, Customer> {

    /**
     * Constructs the Redis cache with JSON serialization and TTL from {@link CacheSpecsMap}.
     *
     * @param configuration     Redis connection and evict TTL settings
     * @param cacheSpecsMap     per-cache size and TTL configuration
     * @param connectionFactory Redis connection from {@link TBRedisCacheConfiguration}
     */
    public CustomerRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.CUSTOMER_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbJsonRedisSerializer<>(Customer.class));
    }
}
