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
package org.thingsboard.server.service.telemetry;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.discovery.TbApplicationEventListener;
import org.thingsboard.server.queue.discovery.event.PartitionChangeEvent;
import org.thingsboard.server.service.subscription.SubscriptionManagerService;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by ashvayka on 27.03.18.
 */
@Slf4j
public abstract class AbstractSubscriptionService extends TbApplicationEventListener<PartitionChangeEvent> {

    protected final Set<TopicPartitionInfo> currentPartitions = ConcurrentHashMap.newKeySet();

    @Autowired
    protected TbClusterService clusterService;
    @Autowired
    protected PartitionService partitionService;
    @Autowired
    protected Optional<SubscriptionManagerService> subscriptionManagerService;

    protected ExecutorService wsCallBackExecutor;
    /**
     * Returns executor prefix.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected abstract String getExecutorPrefix();
    /**
     * Init executor.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PostConstruct
    public void initExecutor() {
        wsCallBackExecutor = Executors.newSingleThreadExecutor(ThingsBoardThreadFactory.forName(getExecutorPrefix() + "-service-ws-callback"));
    }
    /**
     * Shutdown executor.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @PreDestroy
    public void shutdownExecutor() {
        if (wsCallBackExecutor != null) {
            wsCallBackExecutor.shutdownNow();
        }
    }
    /**
     * Handles tb application event.
     *
     * @param partitionChangeEvent partition change event ({@link PartitionChangeEvent})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void onTbApplicationEvent(PartitionChangeEvent partitionChangeEvent) {
        if (ServiceType.TB_CORE.equals(partitionChangeEvent.getServiceType())) {
            currentPartitions.clear();
            currentPartitions.addAll(partitionChangeEvent.getCorePartitions());
        }
    }
    /**
     * Forward to subscription manager service.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param toSubscriptionManagerService to subscription manager service ({@link Consumer})
     * @param toCore to core ({@link Supplier})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void forwardToSubscriptionManagerService(TenantId tenantId, EntityId entityId,
                                                       Consumer<SubscriptionManagerService> toSubscriptionManagerService,
                                                       Supplier<TransportProtos.ToCoreMsg> toCore) {
        TopicPartitionInfo tpi = partitionService.resolve(ServiceType.TB_CORE, tenantId, entityId);
        if (currentPartitions.contains(tpi)) {
            if (subscriptionManagerService.isPresent()) {
                toSubscriptionManagerService.accept(subscriptionManagerService.get());
            } else {
                log.warn("Possible misconfiguration because subscriptionManagerService is null!");
            }
        } else {
            TransportProtos.ToCoreMsg toCoreMsg = toCore.get();
            clusterService.pushMsgToCore(tpi, entityId.getId(), toCoreMsg, null);
        }
    }
    /**
     * Add ws callback.
     *
     * @param saveFuture save future ({@link ListenableFuture})
     * @param callback queue callback invoked when processing completes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected <T> void addWsCallback(ListenableFuture<T> saveFuture, Consumer<T> callback) {
        addCallback(saveFuture, callback, wsCallBackExecutor);
    }
    /**
     * Add callback.
     *
     * @param saveFuture save future ({@link ListenableFuture})
     * @param callback queue callback invoked when processing completes
     * @param executor executor ({@link Executor})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected <T> void addCallback(ListenableFuture<T> saveFuture, Consumer<T> callback, Executor executor) {
        Futures.addCallback(saveFuture, new FutureCallback<>() {
            /**
             * Handles success.
             *
             * @param result result ({@link T})
             * @return nothing
             * @throws Exception if an unexpected error occurs during processing
             */
            @Override
            public void onSuccess(@Nullable T result) {
                callback.accept(result);
            }
            /**
             * Handles failure.
             *
             * @param t t ({@link Throwable})
             * @return nothing
             * @throws Exception if an unexpected error occurs during processing
             */

            @Override
            public void onFailure(Throwable t) {}
        }, executor);
    }
    /**
     * Safe callback.
     *
     * @param callback queue callback invoked when processing completes
     * @return {@link Consumer}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected static Consumer<Throwable> safeCallback(FutureCallback<Void> callback) {
        if (callback != null) {
            return callback::onFailure;
        } else {
            return throwable -> {};
        }
    }

}
