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
package org.thingsboard.server.edqs.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.queue.discovery.HashPartitionService;
import org.thingsboard.server.queue.edqs.EdqsConfig;
import org.thingsboard.server.queue.edqs.EdqsConfig.EdqsPartitioningStrategy;

/**
 * Resolves Kafka partition ownership for tenants (TENANT vs NONE partitioning strategy).
 */

@Service
@RequiredArgsConstructor
public class EdqsPartitionService {

    private final HashPartitionService hashPartitionService;
    private final EdqsConfig edqsConfig;
    /**
     * Resolve partition.
     *
     * @param tenantId tenant that owns the indexed entities
     * @param key key ({@link Object})
     * @return {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    public Integer resolvePartition(TenantId tenantId, Object key) {
        if (edqsConfig.getPartitioningStrategy() == EdqsPartitioningStrategy.TENANT) {
            return hashPartitionService.resolvePartitionIndex(tenantId.getId(), edqsConfig.getPartitions());
        } else {
            if (key == null) {
                throw new IllegalArgumentException("Partitioning key is missing but partitioning strategy is not TENANT");
            }
            return hashPartitionService.resolvePartitionIndex(key.toString(), edqsConfig.getPartitions());
        }
    }

}
