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

import java.util.UUID;

/**
 * Serialized payload on the cluster queue (typically Kafka).
 *
 * <p>Producers set {@link #getKey()} for partitioning; consumers decode {@link #getData()} using
 * protobuf or JSON per topic. Implementations include {@code TbProtoQueueMsg} in {@code common/queue}.
 */
public interface TbQueueMsg {

    /** Partition key (often tenant or entity UUID). */
    /** Partition key for the queue message (often entity id). */
    UUID getKey();

    /** Headers: tenant id, component type, trace ids, etc. */
    /** Optional metadata headers attached to the message. */
    TbQueueMsgHeaders getHeaders();

    /** Serialized message body (protobuf bytes in production). */
    /** Serialized message body (protobuf or JSON bytes). */
    byte[] getData();
}
