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
import org.thingsboard.server.common.data.housekeeper.HousekeeperTaskType;
import org.thingsboard.server.common.data.housekeeper.TsHistoryDeletionHousekeeperTask;
import org.thingsboard.server.common.data.kv.BaseDeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.dao.timeseries.TimeseriesService;

import java.util.List;

    /**
     * Spring service component for ts history deletion task processor (background housekeeping tasks (alarm unassign, job cleanup, etc.)).
     */

@Component
@RequiredArgsConstructor
@Slf4j
public class TsHistoryDeletionTaskProcessor extends HousekeeperTaskProcessor<TsHistoryDeletionHousekeeperTask> {

    private final TimeseriesService timeseriesService;
    /**
     * Processes the requested data.
     *
     * @param task task ({@link TsHistoryDeletionHousekeeperTask})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void process(TsHistoryDeletionHousekeeperTask task) throws Exception {
        DeleteTsKvQuery deleteQuery = new BaseDeleteTsKvQuery(task.getKey(), 0, System.currentTimeMillis(), false, false);
        wait(timeseriesService.remove(task.getTenantId(), task.getEntityId(), List.of(deleteQuery)));
        log.debug("[{}][{}][{}] Deleted timeseries history for key '{}'", task.getTenantId(), task.getEntityId().getEntityType(), task.getEntityId(), task.getKey());
    }
    /**
     * Returns task type.
     *
     * @return {@link HousekeeperTaskType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public HousekeeperTaskType getTaskType() {
        return HousekeeperTaskType.DELETE_TS_HISTORY;
    }

}
