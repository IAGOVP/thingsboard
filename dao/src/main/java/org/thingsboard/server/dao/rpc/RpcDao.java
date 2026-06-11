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

import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.data.rpc.RpcStatus;
import org.thingsboard.server.dao.Dao;


/**

 * Persistence contract for rpc.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (device RPC request persistence).

 */


public interface RpcDao extends Dao<Rpc> {
    /**
     * Finds all by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Rpc> findAllByDeviceId(TenantId tenantId, DeviceId deviceId, PageLink pageLink);
    /**
     * Finds all by device id and status.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param rpcStatus rpc status ({@link RpcStatus})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Rpc> findAllByDeviceIdAndStatus(TenantId tenantId, DeviceId deviceId, RpcStatus rpcStatus, PageLink pageLink);
    /**
     * Finds all rpc by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<Rpc> findAllRpcByTenantId(TenantId tenantId, PageLink pageLink);
    /**
     * Deletes outdated rpc by tenant id batch.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param expirationTime expiration time ({@link Long})
     * @param batchSize batch size
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    int deleteOutdatedRpcByTenantIdBatch(TenantId tenantId, Long expirationTime, int batchSize);

}
