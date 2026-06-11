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
package org.thingsboard.server.transport.coap;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.thingsboard.server.common.transport.TransportService;
import org.thingsboard.server.common.transport.TransportServiceCallback;
import org.thingsboard.server.gen.transport.TransportProtos;

/**
 * Base Californium resource for the device API: token auth from URI path, delegates to {@link TransportService}.
 */
@Slf4j
public abstract class AbstractCoapTransportResource extends CoapResource {

    protected final CoapTransportContext transportContext;
    protected final TransportService transportService;

    public AbstractCoapTransportResource(CoapTransportContext context, String name) {
        super(name);
        this.transportContext = context;
        this.transportService = context.getTransportService();
    }
    /**
     * Handles get.
     *
     * @param exchange exchange ({@link CoapExchange})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void handleGET(CoapExchange exchange) {
        processHandleGet(exchange);
    }
    /**
     * Handles post.
     *
     * @param exchange exchange ({@link CoapExchange})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void handlePOST(CoapExchange exchange) {
        processHandlePost(exchange);
    }
    /**
     * Processes handle get.
     *
     * @param exchange exchange ({@link CoapExchange})
     * @return nothing
     * @throws Exception on processing failure
     */
    protected abstract void processHandleGet(CoapExchange exchange);
    /**
     * Processes handle post.
     *
     * @param exchange exchange ({@link CoapExchange})
     * @return nothing
     * @throws Exception on processing failure
     */
    protected abstract void processHandlePost(CoapExchange exchange);
    /**
     * Report subscription info.
     *
     * @param sessionInfo session info
     * @param hasAttributeSubscription has attribute subscription
     * @param hasRpcSubscription has rpc subscription
     * @return nothing
     * @throws Exception on processing failure
     */
    protected void reportSubscriptionInfo(TransportProtos.SessionInfoProto sessionInfo, boolean hasAttributeSubscription, boolean hasRpcSubscription) {
        transportContext.getTransportService().process(sessionInfo, TransportProtos.SubscriptionInfoProto.newBuilder()
                .setAttributeSubscription(hasAttributeSubscription)
                .setRpcSubscription(hasRpcSubscription)
                .setLastActivityTime(System.currentTimeMillis())
                .build(), TransportServiceCallback.EMPTY);
    }

}
