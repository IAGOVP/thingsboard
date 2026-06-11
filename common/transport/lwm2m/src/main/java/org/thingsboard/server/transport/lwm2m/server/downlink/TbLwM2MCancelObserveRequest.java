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
package org.thingsboard.server.transport.lwm2m.server.downlink;

import lombok.Builder;
import org.thingsboard.server.transport.lwm2m.server.LwM2MOperationType;

/**
 * Tb lw m2mcancel observe request.
 */
public class TbLwM2MCancelObserveRequest extends AbstractTbLwM2MTargetedDownlinkRequest<Integer> {

    @Builder
    private TbLwM2MCancelObserveRequest(String versionedId, long timeout) {
        super(versionedId, timeout);
    }
    /**
     * Returns type.
     *
     * @return {@link LwM2MOperationType}
     * @throws Exception on processing failure
     */

    @Override
    public LwM2MOperationType getType() {
        return LwM2MOperationType.OBSERVE_CANCEL;
    }



}
