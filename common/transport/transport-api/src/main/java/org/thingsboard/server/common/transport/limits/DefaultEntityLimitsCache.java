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
package org.thingsboard.server.common.transport.limits;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thingsboard.server.queue.util.TbTransportComponent;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Default entity limits cache.
 */
@Service
@TbTransportComponent
@Slf4j
public class DefaultEntityLimitsCache implements EntityLimitsCache {

    private static final int DEVIATION = 10;
    private final Cache<EntityLimitKey, Boolean> cache;

    public DefaultEntityLimitsCache(@Value("${cache.entityLimits.timeToLiveInMinutes:5}") int ttl,
                                    @Value("${cache.entityLimits.maxSize:100000}") int maxSize) {
        // We use the 'random' expiration time to avoid peak loads.
        long mainPart = (TimeUnit.MINUTES.toNanos(ttl) / 100) * (100 - DEVIATION);
        long randomPart = (TimeUnit.MINUTES.toNanos(ttl) / 100) * DEVIATION;
        cache = Caffeine.newBuilder()
                .expireAfter(new Expiry<EntityLimitKey, Boolean>() {
                    /**
                     * Expire after create.
                     *
                     * @param key key ({@link EntityLimitKey})
                     * @param value value ({@link Boolean})
                     * @param currentTime current time
                     * @return the long result
                     * @throws Exception on processing failure
                     */
                    @Override
                    public long expireAfterCreate(@NotNull EntityLimitKey key, @NotNull Boolean value, long currentTime) {
                        return mainPart + (long) (randomPart * ThreadLocalRandom.current().nextDouble());
                    }
                    /**
                     * Expire after update.
                     *
                     * @param key key ({@link EntityLimitKey})
                     * @param value value ({@link Boolean})
                     * @param currentTime current time
                     * @param currentDuration current duration
                     * @return the long result
                     * @throws Exception on processing failure
                     */

                    @Override
                    public long expireAfterUpdate(@NotNull EntityLimitKey key, @NotNull Boolean value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                    /**
                     * Expire after read.
                     *
                     * @param key key ({@link EntityLimitKey})
                     * @param value value ({@link Boolean})
                     * @param currentTime current time
                     * @param currentDuration current duration
                     * @return the long result
                     * @throws Exception on processing failure
                     */

                    @Override
                    public long expireAfterRead(@NotNull EntityLimitKey key, @NotNull Boolean value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .maximumSize(maxSize)
                .build();
    }
    /**
     * Returns the requested data.
     *
     * @param key key ({@link EntityLimitKey})
     * @return the boolean result
     * @throws Exception on processing failure
     */

    @Override
    public boolean get(EntityLimitKey key) {
        var result = cache.getIfPresent(key);
        return result != null ? result : false;
    }
    /**
     * Put.
     *
     * @param key key ({@link EntityLimitKey})
     * @param value value
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void put(EntityLimitKey key, boolean value) {
        cache.put(key, value);
    }
}
