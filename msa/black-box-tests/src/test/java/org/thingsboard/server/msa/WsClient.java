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
package org.thingsboard.server.msa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.thingsboard.server.msa.mapper.WsTelemetryResponse;

import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/**
 * WebSocket test client for telemetry and notification subscription assertions.
 */


@Slf4j
public class WsClient extends WebSocketClient {
    private static final ObjectMapper mapper = new ObjectMapper();
    private WsTelemetryResponse message;

    private volatile boolean firstReplyReceived;
    private final CountDownLatch firstReply = new CountDownLatch(1);
    private final CountDownLatch latch = new CountDownLatch(1);

    private final long timeoutMultiplier;

    WsClient(URI serverUri, long timeoutMultiplier) {
        super(serverUri);
        this.timeoutMultiplier = timeoutMultiplier;
    }
    /**
     * Handles open.
     *
     * @param serverHandshake server handshake ({@link ServerHandshake})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }
    /**
     * Handles message.
     *
     * @param message message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public synchronized void onMessage(String message) {
        log.error("WS onMessage: {}", message);
        if (!firstReplyReceived) {
            firstReplyReceived = true;
            firstReply.countDown();
        } else {
            try {
                WsTelemetryResponse response = mapper.readValue(message, WsTelemetryResponse.class);
                if (!response.getData().isEmpty()) {
                    this.message = response;
                    latch.countDown();
                }
            } catch (IOException e) {
                log.error("ws message can't be read", e);
            }
        }
    }
    /**
     * Handles close.
     *
     * @param code code
     * @param reason reason ({@link String})
     * @param remote remote
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public synchronized void onClose(int code, String reason, boolean remote) {
        log.error("WS onClose: [{}]", reason);
    }
    /**
     * Handles error.
     *
     * @param ex ex ({@link Exception})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public synchronized void onError(Exception ex) {
        log.error("WS onError: ", ex);
        ex.printStackTrace();
    }
    /**
     * Returns last message.
     *
     * @return {@link WsTelemetryResponse}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WsTelemetryResponse getLastMessage() {
        try {
            boolean result = latch.await(10 * timeoutMultiplier, TimeUnit.SECONDS);
            if (result) {
                return this.message;
            } else {
                log.error("Timeout, ws message wasn't received");
                throw new RuntimeException("Timeout, ws message wasn't received");
            }
        } catch (InterruptedException e) {
            log.error("Timeout, ws message wasn't received");
        }
        return null;
    }

    void waitForFirstReply() {
        try {
            boolean result = firstReply.await(10 * timeoutMultiplier, TimeUnit.SECONDS);
            if (!result) {
                log.error("Timeout, ws message wasn't received");
                throw new RuntimeException("Timeout, ws message wasn't received");
            }
        } catch (InterruptedException e) {
            log.error("Timeout, ws message wasn't received");
            throw new RuntimeException(e);
        }
    }
    /**
     * Handles set sslparameters.
     *
     * @param sslParameters ssl parameters ({@link SSLParameters})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {
        sslParameters.setEndpointIdentificationAlgorithm(null);
    }
}
