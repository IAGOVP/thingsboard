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
package org.thingsboard.server.cache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Standalone (single-node) Redis connection configuration.
 *
 * <p>Activated when {@code cache.type=redis} and {@code redis.connection.type=standalone}.
 * Typical for development and small single-node production setups.
 *
 * <p>Configuration properties:
 * <ul>
 *   <li>{@code redis.standalone.host} — Redis hostname (default {@code localhost})</li>
 *   <li>{@code redis.standalone.port} — Redis port (default {@code 6379})</li>
 *   <li>{@code redis.standalone.clientName} — Jedis client name for monitoring</li>
 *   <li>{@code redis.standalone.connectTimeout} — connection timeout in ms (default 30000)</li>
 *   <li>{@code redis.standalone.readTimeout} — socket read timeout in ms (default 60000)</li>
 *   <li>{@code redis.standalone.useDefaultClientConfig} — skip custom timeouts/client name</li>
 *   <li>{@code redis.standalone.usePoolConfig} — enable {@link #buildPoolConfig()} pooling</li>
 *   <li>{@code redis.db} — logical database index (default 0)</li>
 *   <li>{@code redis.username} / {@code redis.password} — authentication</li>
 *   <li>{@code redis.ssl.enabled} — TLS via {@link TBRedisCacheConfiguration#createSslSocketFactory()}</li>
 * </ul>
 *
 * @see TBRedisCacheConfiguration
 * @see TBRedisClusterConfiguration
 * @see TBRedisSentinelConfiguration
 */
@Configuration
@ConditionalOnMissingBean(TbCaffeineCacheConfiguration.class)
@ConditionalOnProperty(prefix = "redis.connection", value = "type", havingValue = "standalone")
public class TBRedisStandaloneConfiguration extends TBRedisCacheConfiguration {

    /** Redis server hostname. Property: {@code redis.standalone.host}. Default: localhost. */
    @Value("${redis.standalone.host:localhost}")
    private String host;

    /** Redis server port. Property: {@code redis.standalone.port}. Default: 6379. */
    @Value("${redis.standalone.port:6379}")
    private Integer port;

    /** Jedis client name reported to Redis. Property: {@code redis.standalone.clientName}. Default: standalone. */
    @Value("${redis.standalone.clientName:standalone}")
    private String clientName;

    /** Connection establishment timeout (ms). Property: {@code redis.standalone.connectTimeout}. Default: 30000. */
    @Value("${redis.standalone.connectTimeout:30000}")
    private Long connectTimeout;

    /** Socket read timeout (ms). Property: {@code redis.standalone.readTimeout}. Default: 60000. */
    @Value("${redis.standalone.readTimeout:60000}")
    private Long readTimeout;

    /** When {@code true}, uses Jedis default client timeouts. Default: true. */
    @Value("${redis.standalone.useDefaultClientConfig:true}")
    private boolean useDefaultClientConfig;

    /** When {@code true}, enables custom Jedis pool from {@code redis.pool_config.*}. Default: false. */
    @Value("${redis.standalone.usePoolConfig:false}")
    private boolean usePoolConfig;

    /** Logical Redis database index. Property: {@code redis.db}. Default: 0. */
    @Value("${redis.db:0}")
    private Integer db;

    /** Redis ACL username. Property: {@code redis.username}. */
    @Value("${redis.username:}")
    private String username;

    /** Redis password. Property: {@code redis.password}. */
    @Value("${redis.password:}")
    private String password;

    /** TLS toggle. Property: {@code redis.ssl.enabled}. Default: false. */
    @Value("${redis.ssl.enabled:false}")
    private boolean useSsl;

    /**
     * Builds a {@link JedisConnectionFactory} for a single Redis instance.
     *
     * @return standalone Jedis connection factory
     */
    public JedisConnectionFactory loadFactory() {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(host);
        standaloneConfiguration.setPort(port);
        standaloneConfiguration.setDatabase(db);
        standaloneConfiguration.setUsername(username);
        standaloneConfiguration.setPassword(password);
        return new JedisConnectionFactory(standaloneConfiguration, buildClientConfig());
    }

    /**
     * Assembles Jedis client options: custom timeouts, optional pooling, and optional SSL.
     *
     * @return built Jedis client configuration
     */
    private JedisClientConfiguration buildClientConfig() {
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder = JedisClientConfiguration.builder();
        if (!useDefaultClientConfig) {
            jedisClientConfigurationBuilder
                    .clientName(clientName)
                    .connectTimeout(Duration.ofMillis(connectTimeout))
                    .readTimeout(Duration.ofMillis(readTimeout));
        }
        if (useSsl) {
            jedisClientConfigurationBuilder
                    .useSsl()
                    .sslSocketFactory(createSslSocketFactory());
        }
        if (usePoolConfig) {
            jedisClientConfigurationBuilder
                    .usePooling()
                    .poolConfig(buildPoolConfig());
        }
        return jedisClientConfigurationBuilder.build();
    }

}
