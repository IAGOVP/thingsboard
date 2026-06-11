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
package org.thingsboard.server.service.entitiy.device;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.NameConflictStrategy;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.dao.device.claim.ClaimResult;
import org.thingsboard.server.dao.device.claim.ReclaimResult;

/**

 * Application-layer service API for device entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbDeviceService {
/**
 * Saves or persists the requested data.
 *
 * @param device device ({@link Device})
 * @param accessToken access token ({@link String})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws Exception if an unexpected error occurs during processing
 */



    Device save(Device device, String accessToken, User user) throws Exception;
/**
 * Saves or persists the requested data.
 *
 * @param device device ({@link Device})
 * @param accessToken access token ({@link String})
 * @param nameConflictStrategy name conflict strategy ({@link NameConflictStrategy})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws Exception if an unexpected error occurs during processing
 */

    Device save(Device device, String accessToken, NameConflictStrategy nameConflictStrategy, User user) throws Exception;
/**
 * Saves a device with credentials the requested data.
 *
 * @param device device ({@link Device})
 * @param deviceCredentials device credentials ({@link DeviceCredentials})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials, User user) throws ThingsboardException;
/**
 * Saves a device with credentials the requested data.
 *
 * @param device device ({@link Device})
 * @param deviceCredentials device credentials ({@link DeviceCredentials})
 * @param nameConflictStrategy name conflict strategy ({@link NameConflictStrategy})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials, NameConflictStrategy nameConflictStrategy, User user) throws ThingsboardException;
/**
 * Deletes the requested data.
 *
 * @param device device ({@link Device})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void delete(Device device, User user);
/**
 * Assigns device to customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param deviceId target device identifier
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device assignDeviceToCustomer(TenantId tenantId, DeviceId deviceId, Customer customer, User user) throws ThingsboardException;
/**
 * Unassigns device from customer.
 *
 * @param device device ({@link Device})
 * @param customer customer ({@link Customer})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device unassignDeviceFromCustomer(Device device, Customer customer, User user) throws ThingsboardException;
/**
 * Assigns device to public customer.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param deviceId target device identifier
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device assignDeviceToPublicCustomer(TenantId tenantId, DeviceId deviceId, User user) throws ThingsboardException;
/**
 * Returns device credentials by device id.
 *
 * @param device device ({@link Device})
 * @param user authenticated user performing the action
 * @return {@link DeviceCredentials}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    DeviceCredentials getDeviceCredentialsByDeviceId(Device device, User user) throws ThingsboardException;
/**
 * Updates device credentials.
 *
 * @param device device ({@link Device})
 * @param deviceCredentials device credentials ({@link DeviceCredentials})
 * @param user authenticated user performing the action
 * @return {@link DeviceCredentials}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    DeviceCredentials updateDeviceCredentials(Device device, DeviceCredentials deviceCredentials, User user) throws ThingsboardException;
/**
 * Claim device.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param device device ({@link Device})
 * @param customerId customer id ({@link CustomerId})
 * @param secretKey secret key ({@link String})
 * @param user authenticated user performing the action
 * @return future completing with {@link ClaimResult}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<ClaimResult> claimDevice(TenantId tenantId, Device device, CustomerId customerId, String secretKey, User user);
/**
 * Reclaim device.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param device device ({@link Device})
 * @param user authenticated user performing the action
 * @return future completing with {@link ReclaimResult}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    ListenableFuture<ReclaimResult> reclaimDevice(TenantId tenantId, Device device, User user);
/**
 * Assigns device to tenant.
 *
 * @param device device ({@link Device})
 * @param newTenant new tenant ({@link Tenant})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device assignDeviceToTenant(Device device, Tenant newTenant, User user);
/**
 * Assigns device to edge.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param deviceId target device identifier
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device assignDeviceToEdge(TenantId tenantId, DeviceId deviceId, Edge edge, User user) throws ThingsboardException;
/**
 * Unassigns device from edge.
 *
 * @param device device ({@link Device})
 * @param edge edge ({@link Edge})
 * @param user authenticated user performing the action
 * @return {@link Device}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    Device unassignDeviceFromEdge(Device device, Edge edge, User user) throws ThingsboardException;
}
