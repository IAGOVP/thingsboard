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
package org.thingsboard.server.queue;

import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;

import java.util.Set;

/**
 * Contract for tb queue response template.
 */
public interface TbQueueResponseTemplate<Request extends TbQueueMsg, Response extends TbQueueMsg> {

    /** Subscribe. */
    void subscribe();

    /** Subscribe. */
    void subscribe(Set<TopicPartitionInfo> partitions);

    /** Launch. */
    void launch(TbQueueHandler<Request, Response> handler);

    /** Stops the actor and releases its resources. */
    void stop();
}
