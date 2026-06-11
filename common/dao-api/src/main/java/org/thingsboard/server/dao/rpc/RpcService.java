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
package org.thingsboard.server.dao.rpc;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.RpcId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.data.rpc.RpcStatus;
import org.thingsboard.server.dao.entity.EntityDaoService;

/**
 * Service API for rpc persistence and domain operations.
 */
public interface RpcService extends EntityDaoService {

    /**
     * Saves or persists the requested data.
     *
     * @param rpc rpc ({@link Rpc})
     * @return {@link Rpc}
     */
    Rpc save(Rpc rpc);

    /**
     * Deletes rpc.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link RpcId})
     */
    void deleteRpc(TenantId tenantId, RpcId id);

    /**
     * Deletes all rpc by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */
    void deleteAllRpcByTenantId(TenantId tenantId);

    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link RpcId})
     * @return {@link Rpc}
     */
    Rpc findById(TenantId tenantId, RpcId id);

    /**
     * Finds rpc by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id id ({@link RpcId})
     * @return future completing with {@link Rpc}
     */
    ListenableFuture<Rpc> findRpcByIdAsync(TenantId tenantId, RpcId id);

    /**
     * Finds all by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Rpc> findAllByDeviceId(TenantId tenantId, DeviceId deviceId, PageLink pageLink);

    /**
     * Finds all by device id and status.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param rpcStatus rpc status ({@link RpcStatus})
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     */
    PageData<Rpc> findAllByDeviceIdAndStatus(TenantId tenantId, DeviceId deviceId, RpcStatus rpcStatus, PageLink pageLink);
}
