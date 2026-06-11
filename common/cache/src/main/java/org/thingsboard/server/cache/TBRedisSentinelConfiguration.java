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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Redis Sentinel connection configuration for high-availability Redis deployments.
 *
 * <p>Activated when {@code cache.type=redis} and {@code redis.connection.type=sentinel}.
 * Sentinel monitors a master Redis instance and provides automatic failover.
 *
 * <p>Configuration properties:
 * <ul>
 *   <li>{@code redis.sentinel.master} — monitored master name</li>
 *   <li>{@code redis.sentinel.sentinels} — comma-separated sentinel {@code host:port} list</li>
 *   <li>{@code redis.sentinel.password} — sentinel authentication password</li>
 *   <li>{@code redis.sentinel.useDefaultPoolConfig} — custom pool from {@code redis.pool_config.*}</li>
 *   <li>{@code redis.db} — logical database index on the master</li>
 *   <li>{@code redis.username} / {@code redis.password} — Redis master credentials</li>
 *   <li>{@code redis.ssl.enabled} — TLS via {@link TBRedisCacheConfiguration#createSslSocketFactory()}</li>
 * </ul>
 *
 * @see TBRedisCacheConfiguration
 * @see TBRedisClusterConfiguration
 * @see TBRedisStandaloneConfiguration
 */
@Configuration
@ConditionalOnMissingBean(TbCaffeineCacheConfiguration.class)
@ConditionalOnProperty(prefix = "redis.connection", value = "type", havingValue = "sentinel")
public class TBRedisSentinelConfiguration extends TBRedisCacheConfiguration {

    /** Monitored Redis master name. Property: {@code redis.sentinel.master}. */
    @Value("${redis.sentinel.master:}")
    private String master;

    /** Comma-separated sentinel endpoints. Property: {@code redis.sentinel.sentinels}. */
    @Value("${redis.sentinel.sentinels:}")
    private String sentinels;

    /** Password for authenticating to Sentinel instances. Property: {@code redis.sentinel.password}. */
    @Value("${redis.sentinel.password:}")
    private String sentinelPassword;

    /** When {@code false}, applies custom {@link #buildPoolConfig()}. Default: true. */
    @Value("${redis.sentinel.useDefaultPoolConfig:true}")
    private boolean useDefaultPoolConfig;

    /** Redis logical database index. Property: {@code redis.db}. */
    @Value("${redis.db:}")
    private Integer database;

    /** TLS toggle. Property: {@code redis.ssl.enabled}. Default: false. */
    @Value("${redis.ssl.enabled:false}")
    private boolean useSsl;

    /** Redis ACL username. Property: {@code redis.username}. */
    @Value("${redis.username:}")
    private String username;

    /** Redis master password. Property: {@code redis.password}. */
    @Value("${redis.password:}")
    private String password;

    /**
     * Builds a {@link JedisConnectionFactory} backed by Redis Sentinel.
     *
     * @return sentinel-managed Jedis connection factory
     */
    public JedisConnectionFactory loadFactory() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.setMaster(master);
        redisSentinelConfiguration.setSentinels(getNodes(sentinels));
        redisSentinelConfiguration.setSentinelPassword(sentinelPassword);
        redisSentinelConfiguration.setUsername(username);
        redisSentinelConfiguration.setPassword(password);
        redisSentinelConfiguration.setDatabase(database);
        return new JedisConnectionFactory(redisSentinelConfiguration, buildClientConfig());
    }

    /**
     * Assembles Jedis client options with optional pooling and SSL.
     *
     * @return built Jedis client configuration
     */
    private JedisClientConfiguration buildClientConfig() {
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder = JedisClientConfiguration.builder();
        if (!useDefaultPoolConfig) {
            jedisClientConfigurationBuilder
                    .usePooling()
                    .poolConfig(buildPoolConfig());
        }
        if (useSsl) {
            jedisClientConfigurationBuilder
                    .useSsl()
                    .sslSocketFactory(createSslSocketFactory());
        }
        return jedisClientConfigurationBuilder.build();
    }

}
