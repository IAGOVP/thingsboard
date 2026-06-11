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

import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.gen.transport.TransportProtos.ServiceInfo;

import java.util.Set;
import java.util.UUID;

/**
 * Provides identity and readiness of the local ThingsBoard microservice for queue discovery.
 *
 * <p>Exposes {@link ServiceInfo} to partition services and tracks tenant-profile assignment for this node.
 */
public interface TbServiceInfoProvider {

    /** Unique id of this service instance in the cluster. */
    String getServiceId();

    /** Logical service type (core, rule-engine, transport, edqs, …). */
    String getServiceType();

    /** Protobuf descriptor published to the discovery topic. */
    ServiceInfo getServiceInfo();

    /** Whether this JVM runs all service types (monolith deployment). */
    boolean isMonolith();

    /** Whether this JVM hosts the given {@link ServiceType}. */
    boolean isService(ServiceType serviceType);

    /** Builds an updated {@link ServiceInfo} with current JVM metrics. */
    ServiceInfo generateNewServiceInfoWithCurrentSystemInfo();

    /** Tenant profile ids this node is responsible for (empty in monolith). */
    Set<UUID> getAssignedTenantProfiles();

    /** Marks the service ready to receive partition assignments; returns previous state. */
    boolean setReady(boolean ready);

}
