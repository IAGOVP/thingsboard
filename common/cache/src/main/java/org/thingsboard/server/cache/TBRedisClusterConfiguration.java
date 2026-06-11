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
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Redis Cluster connection configuration for distributed ThingsBoard deployments.
 *
 * <p>Activated when {@code cache.type=redis} and {@code redis.connection.type=cluster}.
 * Not loaded when {@link TbCaffeineCacheConfiguration} is present (Caffeine takes precedence).
 *
 * <p>Configuration properties:
 * <ul>
 *   <li>{@code redis.cluster.nodes} — comma-separated {@code host:port} seed nodes</li>
 *   <li>{@code redis.cluster.max-redirects} — MOVED/ASK redirection limit (default 12)</li>
 *   <li>{@code redis.cluster.useDefaultPoolConfig} — when {@code false}, applies
 *       {@link TBRedisCacheConfiguration#buildPoolConfig()} pooling settings</li>
 *   <li>{@code redis.username} / {@code redis.password} — ACL or legacy authentication</li>
 *   <li>{@code redis.ssl.enabled} — enables TLS via {@link TBRedisCacheConfiguration#createSslSocketFactory()}</li>
 * </ul>
 *
 * <p>Cluster-aware slot routing for transactions is handled by
 * {@link RedisTbTransactionalCache#getConnection(byte[])}.
 *
 * @see TBRedisCacheConfiguration
 * @see TBRedisStandaloneConfiguration
 * @see TBRedisSentinelConfiguration
 */
@Configuration
@ConditionalOnMissingBean(TbCaffeineCacheConfiguration.class)
@ConditionalOnProperty(prefix = "redis.connection", value = "type", havingValue = "cluster")
public class TBRedisClusterConfiguration extends TBRedisCacheConfiguration {

    /** Comma-separated cluster seed nodes ({@code host:port}). Property: {@code redis.cluster.nodes}. */
    @Value("${redis.cluster.nodes:}")
    private String clusterNodes;

    /** Maximum cluster redirections before giving up. Property: {@code redis.cluster.max-redirects}. Default: 12. */
    @Value("${redis.cluster.max-redirects:12}")
    private Integer maxRedirects;

    /** When {@code true}, uses Jedis default pool; when {@code false}, applies {@code redis.pool_config.*}. Default: true. */
    @Value("${redis.cluster.useDefaultPoolConfig:true}")
    private boolean useDefaultPoolConfig;

    /** Redis ACL username. Property: {@code redis.username}. */
    @Value("${redis.username:}")
    private String username;

    /** Redis password or ACL password. Property: {@code redis.password}. */
    @Value("${redis.password:}")
    private String password;

    /** TLS toggle for cluster connections. Property: {@code redis.ssl.enabled}. Default: false. */
    @Value("${redis.ssl.enabled:false}")
    private boolean useSsl;

    /**
     * Builds a {@link JedisConnectionFactory} for Redis Cluster.
     *
     * @return cluster-aware Jedis connection factory with optional pooling and SSL
     */
    public JedisConnectionFactory loadFactory() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        clusterConfiguration.setClusterNodes(getNodes(clusterNodes));
        clusterConfiguration.setMaxRedirects(maxRedirects);
        clusterConfiguration.setUsername(username);
        clusterConfiguration.setPassword(password);
        return new JedisConnectionFactory(clusterConfiguration, buildClientConfig());
    }

    /**
     * Assembles Jedis client options: optional custom pool and optional SSL socket factory.
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
