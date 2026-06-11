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
package org.thingsboard.server.cache.user;

import java.io.Serial;
import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.id.TenantId;

/**
 * Composite cache key for {@link org.thingsboard.server.common.data.User} lookups by tenant and email.
 *
 * <p>Used as the key type for {@link UserCaffeineCache} and {@link UserRedisCache} under the
 * {@code users} cache name ({@link org.thingsboard.server.common.data.CacheConstants#USER_CACHE}).
 * Enables fast authentication and user resolution without hitting the database on every request.
 *
 * <p>Key fields:
 * <ul>
 *   <li>{@link #tenantId} — tenant scope; required, never {@code null}</li>
 *   <li>{@link #email} — user email address used as the secondary lookup dimension</li>
 * </ul>
 *
 * <p>String representation ({@link #toString()}) is {@code tenantUuid_email} and is prefixed
 * with the cache name by {@link org.thingsboard.server.cache.RedisTbTransactionalCache#getRawKey}.
 *
 * <p>When a user's email changes, {@link UserCacheEvictEvent} is published to evict both old and
 * new key variants across cluster nodes.
 *
 * @see UserCaffeineCache
 * @see UserRedisCache
 * @see UserCacheEvictEvent
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class UserCacheKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 7357353074893750678L;

    /** Tenant that owns the user; part of the composite key and access-control boundary. */
    @NonNull
    private final TenantId tenantId;

    /** User email address; unique within a tenant for cache lookup purposes. */
    private final String email;

    /**
     * Returns the Redis/Caffeine key suffix appended after the cache name prefix.
     *
     * @return string of the form {@code <tenantId>_<email>}
     */
    @Override
    public String toString() {
        return tenantId.getId() + "_" + email;
    }

}
