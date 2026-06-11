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
package org.thingsboard.server.common.stats;

import org.thingsboard.server.common.data.ObjectType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityDataQuery;

/**
 * Service API for edqs stats persistence and domain operations.
 */
public interface EdqsStatsService {

    /** Report added. */
    void reportAdded(ObjectType objectType);

    /** Report removed. */
    void reportRemoved(ObjectType objectType);

    /** Report entity data query. */
    void reportEntityDataQuery(TenantId tenantId, EntityDataQuery query, long timingNanos);

    /** Report entity count query. */
    void reportEntityCountQuery(TenantId tenantId, EntityCountQuery query, long timingNanos);

    /** Report edqs data query. */
    void reportEdqsDataQuery(TenantId tenantId, EntityDataQuery query, long timingNanos);

    /** Report edqs count query. */
    void reportEdqsCountQuery(TenantId tenantId, EntityCountQuery query, long timingNanos);

    /** Report string compressed. */
    void reportStringCompressed();

    /** Report string uncompressed. */
    void reportStringUncompressed();

}
