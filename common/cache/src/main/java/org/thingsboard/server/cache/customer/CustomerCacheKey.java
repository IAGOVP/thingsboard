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

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.id.TenantId;

import java.io.Serial;
import java.io.Serializable;

/**
 * Composite cache key for {@link org.thingsboard.server.common.data.Customer} by tenant and title.
 *
 * <p>String form: {@code tenantUuid_title}. Used by {@link CustomerCaffeineCache}
 * and {@link CustomerRedisCache}.
 *
 * @see CustomerCacheEvictEvent
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class CustomerCacheKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 5706958428811356925L;

    @NonNull
    /** Owning tenant; required. */
    private final TenantId tenantId;
    /** Customer title used as lookup dimension. */
    private final String title;

/**
         * @return {@code tenantId + "_" + title} key suffix
         */
    @Override
    public String toString() {
        return tenantId.getId() + "_" + title;
    }

}
