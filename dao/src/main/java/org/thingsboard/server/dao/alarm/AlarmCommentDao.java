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
package org.thingsboard.server.dao.alarm;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.alarm.AlarmCommentInfo;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

/**
 * Persistence contract for alarm comment.
 *
 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (alarm persistence, comments, and alarm-type caching).
 */

public interface AlarmCommentDao extends Dao<AlarmComment> {
    /**
     * Finds alarm comment by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link AlarmComment}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmComment findAlarmCommentById(TenantId tenantId, UUID key);
    /**
     * Finds alarm comments.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<AlarmCommentInfo> findAlarmComments(TenantId tenantId, AlarmId id, PageLink pageLink);
    /**
     * Finds alarm comment by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return future completing with {@link AlarmComment}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<AlarmComment> findAlarmCommentByIdAsync(TenantId tenantId, UUID key);

}
