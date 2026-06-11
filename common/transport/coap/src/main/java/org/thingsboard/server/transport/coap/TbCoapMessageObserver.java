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

import lombok.RequiredArgsConstructor;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.elements.EndpointContext;

import java.util.function.Consumer;

/**
 * Tb coap message observer.
 */
@RequiredArgsConstructor
public class TbCoapMessageObserver implements MessageObserver {

    private final int msgId;
    private final Consumer<Integer> onAcknowledge;
    private final Consumer<Integer> onTimeout;
    /**
     * Is internal.
     *
     * @return the boolean result
     * @throws Exception on processing failure
     */

    @Override
    public boolean isInternal() {
        return false;
    }
    /**
     * Handles retransmission.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onRetransmission() {

    }
    /**
     * Handles response.
     *
     * @param response response ({@link Response})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onResponse(Response response) {

    }
    /**
     * Handles acknowledgement.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onAcknowledgement() {
        onAcknowledge.accept(msgId);
    }
    /**
     * Handles reject.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onReject() {

    }
    /**
     * Handles timeout.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onTimeout() {
        if (onTimeout != null) {
            onTimeout.accept(msgId);
        }
    }
    /**
     * Handles cancel.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onCancel() {

    }
    /**
     * Handles ready to send.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onReadyToSend() {

    }
    /**
     * Handles connecting.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onConnecting() {

    }
    /**
     * Handles dtls retransmission.
     *
     * @param flight flight
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onDtlsRetransmission(int flight) {

    }
    /**
     * Handles sent.
     *
     * @param retransmission retransmission
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onSent(boolean retransmission) {

    }
    /**
     * Handles send error.
     *
     * @param error error ({@link Throwable})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onSendError(Throwable error) {

    }
    /**
     * Handles response handling error.
     *
     * @param cause cause ({@link Throwable})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onResponseHandlingError(Throwable cause) {

    }
    /**
     * Handles context established.
     *
     * @param endpointContext endpoint context ({@link EndpointContext})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onContextEstablished(EndpointContext endpointContext) {

    }
    /**
     * Handles transfer complete.
     *
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void onTransferComplete() {

    }
}
