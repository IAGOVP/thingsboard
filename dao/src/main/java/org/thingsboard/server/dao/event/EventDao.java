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
package org.thingsboard.server.dao.event;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.event.Event;
import org.thingsboard.server.common.data.event.EventFilter;
import org.thingsboard.server.common.data.event.EventType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;

import java.util.List;
import java.util.UUID;


/**

 * Persistence contract for event.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (lifecycle and debug event persistence).

 */


public interface EventDao {
    /**
     * Saves or persists async.
     *
     * @param event event ({@link Event})
     * @return future completing with {@link Void}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Void> saveAsync(Event event);
    /**
     * Finds events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param eventType event type ({@link EventType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<? extends Event> findEvents(UUID tenantId, UUID entityId, EventType eventType, TimePageLink pageLink);
    /**
     * Finds event by filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param eventFilter event filter ({@link EventFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<? extends Event> findEventByFilter(UUID tenantId, UUID entityId, EventFilter eventFilter, TimePageLink pageLink);
    /**
     * Finds latest events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param eventType event type ({@link EventType})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<? extends Event> findLatestEvents(UUID tenantId, UUID entityId, EventType eventType, int limit);
    /**
     * Finds latest debug rule node in event.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link Event}
     * @throws Exception if an unexpected error occurs during processing
     */

    Event findLatestDebugRuleNodeInEvent(UUID tenantId, UUID entityId);
    /**
     * Cleanup events.
     *
     * @param regularEventExpTs regular event exp ts
     * @param debugEventExpTs debug event exp ts
     * @param cleanupDb cleanup db
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void cleanupEvents(long regularEventExpTs, long debugEventExpTs, boolean cleanupDb);
    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeEvents(UUID tenantId, UUID entityId, Long startTime, Long endTime);
    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param types types
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeEvents(UUID tenantId, UUID entityId, Long startTime, Long endTime, EventType... types);
    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param eventFilter event filter ({@link EventFilter})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeEvents(UUID tenantId, UUID entityId, EventFilter eventFilter, Long startTime, Long endTime);

}
