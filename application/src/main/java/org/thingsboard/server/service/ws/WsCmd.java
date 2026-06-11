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
package org.thingsboard.server.service.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Marker for inbound WebSocket command DTOs deserialized from JSON; exposes command id and {@link WsCmdType}.
 */


public interface WsCmd {

    /**
     * Returns cmd id.
     * @return numeric result
     */

    int getCmdId();

    /**
     * Returns type.
     * @return {@link WsCmdType}
     */

    @JsonIgnore
    WsCmdType getType();

}
