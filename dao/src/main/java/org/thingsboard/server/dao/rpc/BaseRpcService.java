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

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.RpcId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.data.rpc.RpcStatus;
import org.thingsboard.server.dao.service.PaginatedRemover;

import java.util.Optional;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;
/**
 * Default DAO-layer service implementation for rpc.
 *
 * <p>Coordinates validation, caching, cluster events, and {@code *Dao} persistence (device RPC request persistence).
 */


@Service("RpcDaoService")
@Slf4j
@RequiredArgsConstructor
public class BaseRpcService implements RpcService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_RPC_ID = "Incorrect rpcId ";

    private final RpcDao rpcDao;

    
    /**
     * Saves or persists the requested data.
     *
     * @param rpc rpc ({@link Rpc})
     * @return {@link Rpc}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Rpc save(Rpc rpc) {
        log.trace("Executing save, [{}]", rpc);
        return rpcDao.save(rpc.getTenantId(), rpc);
    }

    
    /**
     * Deletes rpc.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param rpcId rpc id ({@link RpcId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteRpc(TenantId tenantId, RpcId rpcId) {
        log.trace("Executing deleteRpc, tenantId [{}], rpcId [{}]", tenantId, rpcId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(rpcId, id -> INCORRECT_RPC_ID + id);
        rpcDao.removeById(tenantId, rpcId.getId());
    }

    
    /**
     * Deletes entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param force force
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        deleteRpc(tenantId, (RpcId) id);
    }

    
    /**
     * Deletes all rpc by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteAllRpcByTenantId(TenantId tenantId) {
        log.trace("Executing deleteAllRpcByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        tenantRpcRemover.removeEntities(tenantId, tenantId);
    }

    
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteByTenantId(TenantId tenantId) {
        deleteAllRpcByTenantId(tenantId);
    }

    
    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param rpcId rpc id ({@link RpcId})
     * @return {@link Rpc}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Rpc findById(TenantId tenantId, RpcId rpcId) {
        log.trace("Executing findById, tenantId [{}], rpcId [{}]", tenantId, rpcId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(rpcId, id -> INCORRECT_RPC_ID + id);
        return rpcDao.findById(tenantId, rpcId.getId());
    }

    
    /**
     * Finds rpc by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param rpcId rpc id ({@link RpcId})
     * @return future completing with {@link Rpc}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<Rpc> findRpcByIdAsync(TenantId tenantId, RpcId rpcId) {
        log.trace("Executing findRpcByIdAsync, tenantId [{}], rpcId: [{}]", tenantId, rpcId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validateId(rpcId, id -> INCORRECT_RPC_ID + id);
        return rpcDao.findByIdAsync(tenantId, rpcId.getId());
    }

    
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


    @Override
    public PageData<Rpc> findAllByDeviceIdAndStatus(TenantId tenantId, DeviceId deviceId, RpcStatus rpcStatus, PageLink pageLink) {
        log.trace("Executing findAllByDeviceIdAndStatus, tenantId [{}], deviceId [{}], rpcStatus [{}], pageLink [{}]", tenantId, deviceId, rpcStatus, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validatePageLink(pageLink);
        return rpcDao.findAllByDeviceIdAndStatus(tenantId, deviceId, rpcStatus, pageLink);
    }

    
    /**
     * Finds all by device id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceId target device identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<Rpc> findAllByDeviceId(TenantId tenantId, DeviceId deviceId, PageLink pageLink) {
        log.trace("Executing findAllByDeviceIdAndStatus, tenantId [{}], deviceId [{}], pageLink [{}]", tenantId, deviceId, pageLink);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        validatePageLink(pageLink);
        return rpcDao.findAllByDeviceId(tenantId, deviceId, pageLink);
    }

    
    /**
     * Finds entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return optional {@link HasId}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findById(tenantId, new RpcId(entityId.getId())));
    }

    
    /**
     * Finds entity async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(findRpcByIdAsync(tenantId, new RpcId(entityId.getId())))
                .transform(Optional::ofNullable, directExecutor());
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.RPC;
    }

    private final PaginatedRemover<TenantId, Rpc> tenantRpcRemover = new PaginatedRemover<>() {

        /**

         * Loads entities.

         */

        @Override
        protected PageData<Rpc> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return rpcDao.findAllRpcByTenantId(id, pageLink);
        }

        /**

         * Removes entity.

         */

        @Override
        protected void removeEntity(TenantId tenantId, Rpc entity) {
            deleteRpc(tenantId, entity.getId());
        }

    };

}
