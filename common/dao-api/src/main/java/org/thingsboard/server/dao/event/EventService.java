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
import org.thingsboard.server.common.data.EventInfo;
import org.thingsboard.server.common.data.event.Event;
import org.thingsboard.server.common.data.event.EventFilter;
import org.thingsboard.server.common.data.event.EventType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;

import java.util.List;

/**
 * Service API for event persistence and domain operations.
 */
public interface EventService {

    /**
     * Saves or persists async.
     *
     * @param event event ({@link Event})
     * @return future completing with {@link Void}
     */
    ListenableFuture<Void> saveAsync(Event event);

    /**
     * Finds events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param eventType event type ({@link EventType})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EventInfo> findEvents(TenantId tenantId, EntityId entityId, EventType eventType, TimePageLink pageLink);

    /**
     * Finds latest events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param eventType event type ({@link EventType})
     * @param limit limit
     * @return {@link List}
     */
    List<EventInfo> findLatestEvents(TenantId tenantId, EntityId entityId, EventType eventType, int limit);

    /**
     * Finds latest debug rule node in event.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @return {@link EventInfo}
     */
    EventInfo findLatestDebugRuleNodeInEvent(TenantId tenantId, EntityId entityId);

    /**
     * Finds events by filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param eventFilter event filter ({@link EventFilter})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<EventInfo> findEventsByFilter(TenantId tenantId, EntityId entityId, EventFilter eventFilter, TimePageLink pageLink);

    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     */
    void removeEvents(TenantId tenantId, EntityId entityId);

    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     * @param types types
     */
    void removeEvents(TenantId tenantId, EntityId entityId, Long startTime, Long endTime, EventType... types);

    /**
     * Removes events.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId entity id ({@link EntityId})
     * @param eventFilter event filter ({@link EventFilter})
     * @param startTime start time ({@link Long})
     * @param endTime end time ({@link Long})
     */
    void removeEvents(TenantId tenantId, EntityId entityId, EventFilter eventFilter, Long startTime, Long endTime);

    /**
     * Cleanup events.
     *
     * @param regularEventExpTs regular event exp ts
     * @param debugEventExpTs debug event exp ts
     * @param cleanupDb cleanup db
     */
    void cleanupEvents(long regularEventExpTs, long debugEventExpTs, boolean cleanupDb);

}
