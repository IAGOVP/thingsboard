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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.housekeeper.HousekeeperTask;
import org.thingsboard.server.common.data.housekeeper.HousekeeperTaskType;
import org.thingsboard.server.dao.cf.CalculatedFieldService;

    /**
     * Spring service component for calculated fields deletion task processor (background housekeeping tasks (alarm unassign, job cleanup, etc.)).
     */

@Component
@RequiredArgsConstructor
@Slf4j
public class CalculatedFieldsDeletionTaskProcessor extends HousekeeperTaskProcessor<HousekeeperTask> {

    private final CalculatedFieldService calculatedFieldService;
    /**
     * Processes the requested data.
     *
     * @param task task ({@link HousekeeperTask})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void process(HousekeeperTask task) throws Exception {
        int deletedCount = calculatedFieldService.deleteAllCalculatedFieldsByEntityId(task.getTenantId(), task.getEntityId());
        log.debug("[{}][{}][{}] Deleted {} calculated fields", task.getTenantId(), task.getEntityId().getEntityType(), task.getEntityId(), deletedCount);
    }
    /**
     * Returns task type.
     *
     * @return {@link HousekeeperTaskType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public HousekeeperTaskType getTaskType() {
        return HousekeeperTaskType.DELETE_CALCULATED_FIELDS;
    }

}
