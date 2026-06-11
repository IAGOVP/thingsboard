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
package org.thingsboard.server.vc.service;

import org.springframework.stereotype.Service;
import org.thingsboard.server.queue.discovery.QueueRoutingInfo;
import org.thingsboard.server.queue.discovery.QueueRoutingInfoService;

import java.util.Collections;
import java.util.List;

/**
 * Stub {@link org.thingsboard.server.queue.discovery.QueueRoutingInfoService} for VC executor.
 *
 * <p>Returns an empty queue list — VC does not host rule-engine Kafka topics.
 */

@Service
public class VersionControlQueueRoutingInfoService implements QueueRoutingInfoService {

    
  /**
   * Returns empty list — VC executor has no rule-engine queues.
   *
   * @return {@link List}
   * @throws Exception if an unexpected error occurs during processing
   */

    @Override
    public List<QueueRoutingInfo> getAllQueuesRoutingInfo() {
        return Collections.emptyList();
    }
}
