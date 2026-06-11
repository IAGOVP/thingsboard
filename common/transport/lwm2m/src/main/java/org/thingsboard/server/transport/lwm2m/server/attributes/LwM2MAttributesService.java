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
package org.thingsboard.server.transport.lwm2m.server.attributes;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClient;

import java.util.Collection;
import java.util.List;

/**
 * Service contract for lw m2mattributes (LwM2M transport and object model (ThingsBoard common module)).
 *
 * <p>Implemented by the corresponding class in this or the dao module.
 */
public interface LwM2MAttributesService {

    ListenableFuture<List<TransportProtos.TsKvProto>> getSharedAttributes(LwM2mClient client, Collection<String> keys);

    /**
     * Handles get attributes response.
     *
     * @param getAttributesResponse get attributes response
     * @param sessionInfo session info
     * @return nothing
     * @throws Exception on processing failure
     */
    void onGetAttributesResponse(TransportProtos.GetAttributeResponseMsg getAttributesResponse, TransportProtos.SessionInfoProto sessionInfo);

    /**
     * Handles attributes update.
     *
     * @param attributeUpdateNotification attribute update notification
     * @param sessionInfo session info
     * @return nothing
     * @throws Exception on processing failure
     */
    void onAttributesUpdate(TransportProtos.AttributeUpdateNotificationMsg attributeUpdateNotification, TransportProtos.SessionInfoProto sessionInfo);

    /**
     * Handles attributes update.
     *
     * @param lwM2MClient lw m2mclient ({@link LwM2mClient})
     * @param tsKvProtos ts kv protos ({@link List})
     * @param logFailedUpdateOfNonChangedValue log failed update of non changed value
     * @return nothing
     * @throws Exception on processing failure
     */
    void onAttributesUpdate(LwM2mClient lwM2MClient, List<TransportProtos.TsKvProto> tsKvProtos, boolean logFailedUpdateOfNonChangedValue);
}
