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
package org.thingsboard.server.service.entitiy.alarm;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.exception.ThingsboardException;

/**

 * Application-layer service API for alarm comment entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbAlarmCommentService {
/**
 * Saves or persists alarm comment.
 *
 * @param alarm alarm ({@link Alarm})
 * @param alarmComment alarm comment ({@link AlarmComment})
 * @param user authenticated user performing the action
 * @return {@link AlarmComment}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    AlarmComment saveAlarmComment(Alarm alarm, AlarmComment alarmComment, User user) throws ThingsboardException;
/**
 * Deletes alarm comment.
 *
 * @param alarm alarm ({@link Alarm})
 * @param alarmComment alarm comment ({@link AlarmComment})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void deleteAlarmComment(Alarm alarm, AlarmComment alarmComment, User user) throws ThingsboardException;
}
