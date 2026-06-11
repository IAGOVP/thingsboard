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
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;

import java.util.List;
import java.util.UUID;

/**

 * Application-layer service API for alarm entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbAlarmService {
/**
 * Saves or persists the requested data.
 *
 * @param entity entity ({@link Alarm})
 * @param user authenticated user performing the action
 * @return {@link Alarm}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    Alarm save(Alarm entity, User user) throws ThingsboardException;
/**
 * Ack.
 *
 * @param alarm alarm ({@link Alarm})
 * @param user authenticated user performing the action
 * @return {@link AlarmInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    AlarmInfo ack(Alarm alarm, User user) throws ThingsboardException;
/**
 * Ack.
 *
 * @param alarm alarm ({@link Alarm})
 * @param ackTs ack ts
 * @param user authenticated user performing the action
 * @return {@link AlarmInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    AlarmInfo ack(Alarm alarm, long ackTs, User user) throws ThingsboardException;
/**
 * Clear.
 *
 * @param alarm alarm ({@link Alarm})
 * @param user authenticated user performing the action
 * @return {@link AlarmInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    AlarmInfo clear(Alarm alarm, User user) throws ThingsboardException;
/**
 * Clear.
 *
 * @param alarm alarm ({@link Alarm})
 * @param clearTs clear ts
 * @param user authenticated user performing the action
 * @return {@link AlarmInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    AlarmInfo clear(Alarm alarm, long clearTs, User user) throws ThingsboardException;
/**
 * Assigns the requested data.
 *
 * @param alarm alarm ({@link Alarm})
 * @param assigneeId assignee id ({@link UserId})
 * @param assignTs assign ts
 * @param user authenticated user performing the action
 * @return {@link AlarmInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    AlarmInfo assign(Alarm alarm, UserId assigneeId, long assignTs, User user) throws ThingsboardException;
/**
 * Unassigns the requested data.
 *
 * @param alarm alarm ({@link Alarm})
 * @param unassignTs unassign ts
 * @param user authenticated user performing the action
 * @return {@link AlarmInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    AlarmInfo unassign(Alarm alarm, long unassignTs, User user) throws ThingsboardException;
/**
 * Unassigns deleted user alarms.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param userId user id ({@link UserId})
 * @param userTitle user title ({@link String})
 * @param alarms alarms ({@link List})
 * @param unassignTs unassign ts
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void unassignDeletedUserAlarms(TenantId tenantId, UserId userId, String userTitle, List<UUID> alarms, long unassignTs);
/**
 * Deletes the requested data.
 *
 * @param alarm alarm ({@link Alarm})
 * @param user authenticated user performing the action
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean delete(Alarm alarm, User user);

}
