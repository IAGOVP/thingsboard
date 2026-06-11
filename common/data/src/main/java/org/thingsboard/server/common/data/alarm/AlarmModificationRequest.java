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
package org.thingsboard.server.common.data.alarm;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;

/**
 * Batch alarm modification payload.
 */
public interface AlarmModificationRequest {

    TenantId getTenantId();
/**
 * Returns severity.
 *
 * @return {@link AlarmSeverity}
 */

    AlarmSeverity getSeverity();
/**
 * Returns start ts.
 *
 * @return the long result
 */

    long getStartTs();
/**
 * Returns end ts.
 *
 * @return the long result
 */

    long getEndTs();
/**
 * Set start ts.
 *
 * @param startTs start ts
 */

    void setStartTs(long startTs);
/**
 * Set end ts.
 *
 * @param endTs end ts
 */

    void setEndTs(long endTs);
/**
 * Returns user id.
 *
 * @return {@link UserId}
 */

    UserId getUserId();
}
