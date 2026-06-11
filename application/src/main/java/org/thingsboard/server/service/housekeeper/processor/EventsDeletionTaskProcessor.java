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
package org.thingsboard.server.service.housekeeper.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.event.EventType;
import org.thingsboard.server.common.data.housekeeper.HousekeeperTask;
import org.thingsboard.server.common.data.housekeeper.HousekeeperTaskType;
import org.thingsboard.server.dao.event.EventService;

import java.util.Arrays;

    /**
     * Spring service component for events deletion task processor (background housekeeping tasks (alarm unassign, job cleanup, etc.)).
     */

@Component
@RequiredArgsConstructor
public class EventsDeletionTaskProcessor extends HousekeeperTaskProcessor<HousekeeperTask> {

    private final EventService eventService;
    /**
     * Processes the requested data.
     *
     * @param task task ({@link HousekeeperTask})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void process(HousekeeperTask task) throws Exception {
        // Only delete non-debug events for deleted entities.
        EventType[] nonDebugEventTypes = Arrays.stream(EventType.values()).filter(eventType -> !eventType.isDebug()).toArray(EventType[]::new);
        eventService.removeEvents(task.getTenantId(), task.getEntityId(), 0L, System.currentTimeMillis(), nonDebugEventTypes);
    }
    /**
     * Returns task type.
     *
     * @return {@link HousekeeperTaskType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public HousekeeperTaskType getTaskType() {
        return HousekeeperTaskType.DELETE_EVENTS;
    }

}
