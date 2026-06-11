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
package org.thingsboard.server.transport.coap.client;

import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.coap.CoapSessionMsgType;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * coap client context contract (CoAP transport adaptor (ThingsBoard common module)).
 */
public interface CoapClientContext {

    boolean registerAttributeObservation(TbCoapClientState clientState, String token, CoapExchange exchange);

    /**
     * Register rpc observation.
     *
     * @param clientState client state ({@link TbCoapClientState})
     * @param token token ({@link String})
     * @param exchange exchange ({@link CoapExchange})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean registerRpcObservation(TbCoapClientState clientState, String token, CoapExchange exchange);

    /**
     * Returns notification counter by token.
     *
     * @param token token ({@link String})
     * @return {@link AtomicInteger}
     * @throws Exception on processing failure
     */
    AtomicInteger getNotificationCounterByToken(String token);

    /**
     * Returns or create client.
     *
     * @param type type ({@link CoapSessionMsgType})
     * @param deviceCredentials device credentials ({@link ValidateDeviceCredentialsResponse})
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @return {@link TbCoapClientState}
     * @throws AdaptorException on invalid payload or topic format
     */
    TbCoapClientState getOrCreateClient(CoapSessionMsgType type, ValidateDeviceCredentialsResponse deviceCredentials, DeviceProfile deviceProfile) throws AdaptorException;

    /**
     * Returns new sync session.
     *
     * @param clientState client state ({@link TbCoapClientState})
     * @return the TransportProtos.SessionInfoProto value
     * @throws Exception on processing failure
     */
    TransportProtos.SessionInfoProto getNewSyncSession(TbCoapClientState clientState);

    /**
     * Deregister attribute observation.
     *
     * @param clientState client state ({@link TbCoapClientState})
     * @param token token ({@link String})
     * @param exchange exchange ({@link CoapExchange})
     * @return nothing
     * @throws Exception on processing failure
     */
    void deregisterAttributeObservation(TbCoapClientState clientState, String token, CoapExchange exchange);

    /**
     * Deregister rpc observation.
     *
     * @param clientState client state ({@link TbCoapClientState})
     * @param token token ({@link String})
     * @param exchange exchange ({@link CoapExchange})
     * @return nothing
     * @throws Exception on processing failure
     */
    void deregisterRpcObservation(TbCoapClientState clientState, String token, CoapExchange exchange);

    /**
     * Report activity.
     *
     * @return nothing
     * @throws Exception on processing failure
     */
    void reportActivity();

    /**
     * Register observe relation.
     *
     * @param token token ({@link String})
     * @param relation relation ({@link ObserveRelation})
     * @return nothing
     * @throws Exception on processing failure
     */
    void registerObserveRelation(String token, ObserveRelation relation);

    /**
     * Deregister observe relation.
     *
     * @param token token ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    void deregisterObserveRelation(String token);

    /**
     * Awake.
     *
     * @param client client ({@link TbCoapClientState})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    boolean awake(TbCoapClientState client);
}
