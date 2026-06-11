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
package org.thingsboard.server.queue.discovery;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.discovery.event.PartitionChangeEvent;


/**
 * Partition service used by the ThingsBoard cache and queue subsystem.
 */
public interface PartitionService {

    TopicPartitionInfo resolve(ServiceType serviceType, String queueName, TenantId tenantId, EntityId entityId);

    TopicPartitionInfo resolve(ServiceType serviceType, String queueName, TenantId tenantId, EntityId entityId, Integer partition);

    TopicPartitionInfo resolve(ServiceType serviceType, TenantId tenantId, EntityId entityId);

    List<TopicPartitionInfo> resolveAll(ServiceType serviceType, String queueName, TenantId tenantId, EntityId entityId);

    boolean isMyPartition(ServiceType serviceType, TenantId tenantId, EntityId entityId);

    boolean isSystemPartitionMine(ServiceType serviceType);

        
    List<Integer> getMyPartitions(QueueKey queueKey);
    
    String getTopic(QueueKey queueKey);
        
    void recalculatePartitions(TransportProtos.ServiceInfo currentService, List<TransportProtos.ServiceInfo> otherServices);
    Set<String> getAllServiceIds(ServiceType serviceType);

    Set<TransportProtos.ServiceInfo> getAllServices(ServiceType serviceType);

    Set<TransportProtos.ServiceInfo> getOtherServices(ServiceType serviceType);

    void evictTenantInfo(TenantId tenantId);

    int countTransportsByType(String type);

    void updateQueues(List<TransportProtos.QueueUpdateMsg> queueUpdateMsgs);

    void removeQueues(List<TransportProtos.QueueDeleteMsg> queueDeleteMsgs);

    void removeTenant(TenantId tenantId);

    boolean isManagedByCurrentService(TenantId tenantId);

    int resolvePartitionIndex(UUID entityId, int partitions);

    int resolvePartitionIndex(String key, int partitions);

}
