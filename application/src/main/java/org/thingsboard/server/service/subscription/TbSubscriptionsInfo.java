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
package org.thingsboard.server.service.subscription;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Information about the local websocket subscriptions.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"seqNumber"})
@ToString
public class TbSubscriptionsInfo {

    protected boolean notifications;
    protected boolean alarms;
    protected boolean tsAllKeys;
    protected Set<String> tsKeys;
    protected boolean attrAllKeys;
    protected Set<String> attrKeys;
    protected int seqNumber;

    /**
     * Is empty.
     * @return {@code true} when the condition holds
     */

    public boolean isEmpty() {
        return !notifications && !alarms && !tsAllKeys && !attrAllKeys && tsKeys == null && attrKeys == null;
    }

    /**
     * Copy.
     * @return {@link TbSubscriptionsInfo}
     */

    protected TbSubscriptionsInfo copy() {
        return copy(0);
    }

    /**
     * Copy.
     * @param seqNumber seq number
     * @return {@link TbSubscriptionsInfo}
     */

    protected TbSubscriptionsInfo copy(int seqNumber) {
        return new TbSubscriptionsInfo(notifications, alarms, tsAllKeys, tsKeys != null ? new HashSet<>(tsKeys) : null, attrAllKeys, attrKeys != null ? new HashSet<>(attrKeys) : null, seqNumber);
    }

}
