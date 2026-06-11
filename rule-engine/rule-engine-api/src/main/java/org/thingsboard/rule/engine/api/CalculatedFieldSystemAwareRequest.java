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
package org.thingsboard.rule.engine.api;

import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.msg.TbMsgType;

import java.util.List;
import java.util.UUID;

/**

 * Request DTO for rule engine calculated field system aware.

 */


/**

 * Async request DTO for rule engine calculated field system aware (rule engine public API contracts and services).

 */


public interface CalculatedFieldSystemAwareRequest {
    /**
     * Returns previous calculated field ids.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<CalculatedFieldId> getPreviousCalculatedFieldIds();
    /**
     * Returns tb msg id.
     *
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    UUID getTbMsgId();
    /**
     * Returns tb msg type.
     *
     * @return {@link TbMsgType}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbMsgType getTbMsgType();

}
