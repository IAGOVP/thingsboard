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
package org.thingsboard.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.thingsboard.server.controller.plugin.TbWebSocketHandler;
import org.thingsboard.server.queue.util.TbCoreComponent;

/**
 * Registers the ThingsBoard WebSocket API used for real-time telemetry, alarms, and notifications.
 *
 * <p>Clients connect to {@link #WS_API_ENDPOINT} ({@code /api/ws}) and exchange JSON commands
 * defined in {@code org.thingsboard.server.service.ws}. Message buffer sizes are configurable
 * via {@code server.ws.max_text_message_buffer_size} and {@code server.ws.max_binary_message_buffer_size}.
 *
 * <p>Only active on tb-core / monolith nodes ({@link TbCoreComponent}).
 */
@Configuration
@TbCoreComponent
@EnableWebSocket
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    /** Primary WebSocket endpoint path prefix. */
    public static final String WS_API_ENDPOINT = "/api/ws";

    /** Legacy plugins WebSocket path (referenced by security skip lists). */
    public static final String WS_PLUGINS_ENDPOINT = "/api/ws/plugins/";

    private static final String WS_API_MAPPING = "/api/ws/**";

    /** Must be a {@link org.thingsboard.server.controller.plugin.TbWebSocketHandler} instance. */
    private final WebSocketHandler wsHandler;

    /** Max size in bytes for inbound text WebSocket frames (default 32 KB). */
    @Value("${server.ws.max_text_message_buffer_size:32768}")
    private int maxTextMessageBufferSize;

    /** Max size in bytes for inbound binary WebSocket frames (default 32 KB). */
    @Value("${server.ws.max_binary_message_buffer_size:32768}")
    private int maxBinaryMessageBufferSize;

    /**
     * Configures the servlet container's WebSocket buffer limits.
     *
     * @return factory bean applied to the embedded servlet container
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(maxTextMessageBufferSize);
        container.setMaxBinaryMessageBufferSize(maxBinaryMessageBufferSize);
        return container;
    }

    /**
     * Registers {@link TbWebSocketHandler} on {@code /api/ws/**} with permissive CORS
     * (all origin patterns). Authentication is handled inside the handler via JWT.
     *
     * @param registry Spring WebSocket handler registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        if (!(wsHandler instanceof TbWebSocketHandler)) {
            log.error("TbWebSocketHandler expected but [{}] provided", wsHandler);
            throw new RuntimeException("TbWebSocketHandler expected but " + wsHandler + " provided");
        }
        registry.addHandler(wsHandler, WS_API_MAPPING)
                .setAllowedOriginPatterns("*");
    }

}
