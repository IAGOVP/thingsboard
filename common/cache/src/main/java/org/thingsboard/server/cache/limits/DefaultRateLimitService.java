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
package org.thingsboard.server.cache.limits;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.exception.TenantProfileNotFoundException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.limit.LimitedApi;
import org.thingsboard.server.common.data.notification.rule.trigger.RateLimitsTrigger;
import org.thingsboard.server.common.msg.notification.NotificationRuleProcessor;
import org.thingsboard.server.common.msg.tools.TbRateLimits;

import java.util.concurrent.TimeUnit;

/**
 * Default {@link RateLimitService} using Caffeine-cached {@link org.thingsboard.server.common.msg.tools.TbRateLimits} buckets.
 *
 * <p>Configuration:
 * <ul>
 *   <li>{@code cache.rateLimits.timeToLiveInMinutes} — bucket cache idle TTL (default 120)</li>
 *   <li>{@code cache.rateLimits.maxSize} — max cached buckets (default 200000)</li>
 * </ul>
 *
 * <p>On limit breach, publishes a {@link org.thingsboard.server.common.data.notification.rule.trigger.RateLimitsTrigger}
 * via {@link org.thingsboard.server.common.msg.notification.NotificationRuleProcessor}.
 *
 * @see TenantProfileProvider
 */
@Lazy
@Service
@Slf4j
public class DefaultRateLimitService implements RateLimitService {

    /** Resolves tenant-specific rate limit config strings. */

    private final TenantProfileProvider tenantProfileProvider;
    /** Fires notifications on limit breaches. */
    private final NotificationRuleProcessor notificationRuleProcessor;

    /**
     * @param tenantProfileProvider      source of per-tenant limit configuration
     * @param notificationRuleProcessor  publishes rate-limit breach notifications
     * @param rateLimitsTtl              bucket cache TTL in minutes
     * @param rateLimitsCacheMaxSize     maximum cached buckets
     */
    public DefaultRateLimitService(TenantProfileProvider tenantProfileProvider,
                                   @Lazy NotificationRuleProcessor notificationRuleProcessor,
                                   @Value("${cache.rateLimits.timeToLiveInMinutes:120}") int rateLimitsTtl,
                                   @Value("${cache.rateLimits.maxSize:200000}") int rateLimitsCacheMaxSize) {
        this.tenantProfileProvider = tenantProfileProvider;
        this.notificationRuleProcessor = notificationRuleProcessor;
        this.rateLimits = Caffeine.newBuilder()
                .expireAfterAccess(rateLimitsTtl, TimeUnit.MINUTES)
                .maximumSize(rateLimitsCacheMaxSize)
                .build();
    }

    /** Cached token-bucket limiters per API/level pair. */

    private final Cache<RateLimitKey, TbRateLimits> rateLimits;

/** {@inheritDoc} */
    @Override
    public boolean checkRateLimit(LimitedApi api, TenantId tenantId) {
        return checkRateLimit(api, tenantId, tenantId);
    }

/** {@inheritDoc} */
    @Override
    public boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level) {
        return checkRateLimit(api, tenantId, level, false);
    }

/** {@inheritDoc} */
    @Override
    public boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level, boolean ignoreTenantNotFound) {
        if (tenantId.isSysTenantId()) {
            return true;
        }
        TenantProfile tenantProfile = tenantProfileProvider.get(tenantId);
        if (tenantProfile == null) {
            if (ignoreTenantNotFound) {
                return true;
            } else {
                throw new TenantProfileNotFoundException(tenantId);
            }
        }

        String rateLimitConfig = tenantProfile.getProfileConfiguration()
                .map(api::getLimitConfig).orElse(null);
        boolean success = checkRateLimit(api, level, rateLimitConfig);
        if (!success) {
            notificationRuleProcessor.process(RateLimitsTrigger.builder()
                    .tenantId(tenantId)
                    .api(api)
                    .limitLevel(level instanceof EntityId ? (EntityId) level : tenantId)
                    .limitLevelEntityName(null)
                    .build());
        }
        return success;
    }

/** {@inheritDoc} */
    @Override
    public boolean checkRateLimit(LimitedApi api, Object level, String rateLimitConfig) {
        RateLimitKey key = new RateLimitKey(api, level);
        if (StringUtils.isEmpty(rateLimitConfig)) {
            rateLimits.invalidate(key);
            return true;
        }
        log.trace("[{}] Checking rate limit for {} ({})", level, api, rateLimitConfig);

        TbRateLimits rateLimit = rateLimits.asMap().compute(key, (k, limit) -> {
            if (limit == null || !limit.getConfiguration().equals(rateLimitConfig)) {
                limit = new TbRateLimits(rateLimitConfig, api.isRefillRateLimitIntervally());
                log.trace("[{}] Created new rate limit bucket for {} ({})", level, api, rateLimitConfig);
            }
            return limit;
        });
        boolean success = rateLimit.tryConsume();
        if (!success) {
            log.debug("[{}] Rate limit exceeded for {} ({})", level, api, rateLimitConfig);
        }
        return success;
    }

/** {@inheritDoc} */
    @Override
    public void cleanUp(LimitedApi api, Object level) {
        RateLimitKey key = new RateLimitKey(api, level);
        rateLimits.invalidate(key);
    }

    @Data(staticConstructor = "of")
    private static class RateLimitKey {
        private final LimitedApi api;
        private final Object level;
    }

}
