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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.Assert;
import org.thingsboard.common.util.SslUtil;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.EntityId;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Abstract Spring configuration that wires Redis as the ThingsBoard cache backend.
 *
 * <p>Activated when {@code cache.type=redis}. Provides shared infrastructure used by all
 * {@link RedisTbTransactionalCache} implementations and Spring's declarative caching:
 * a {@link RedisConnectionFactory}, a transaction-aware {@link CacheManager}, and a
 * generic {@link RedisTemplate}.
 *
 * <p>Concrete subclasses select the Redis topology and are activated by
 * {@code redis.connection.type}:
 * <ul>
 *   <li>{@link TBRedisStandaloneConfiguration} — single Redis instance (default dev setup)</li>
 *   <li>{@link TBRedisClusterConfiguration} — Redis Cluster with slot-aware routing</li>
 *   <li>{@link TBRedisSentinelConfiguration} — Sentinel-managed high availability</li>
 * </ul>
 *
 * <p>Key configuration properties (see field JavaDoc for defaults):
 * <ul>
 *   <li>{@code redis.evictTtlInMs} — tombstone TTL for {@link RedisTbTransactionalCache#evictOrPut}</li>
 *   <li>{@code redis.pool_config.*} — Jedis connection pool sizing and eviction policy</li>
 *   <li>{@code redis.ssl.enabled} — enable TLS; credentials from {@link RedisSslCredentials}</li>
 * </ul>
 *
 * <p>Per-cache TTL and size limits are defined in {@link CacheSpecsMap} under the
 * {@code cache.specs} prefix and consumed by {@link RedisTbTransactionalCache} subclasses.
 *
 * @see TbCaffeineCacheConfiguration
 * @see RedisSslCredentials
 */
@Configuration
@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@EnableCaching
@Data
@Slf4j
public abstract class TBRedisCacheConfiguration {

    private static final String COMMA = ",";
    private static final String COLON = ":";

    /** Tombstone TTL (ms) for short-lived eviction markers written by {@link RedisTbTransactionalCache#evictOrPut}. Default: 60000. */
    @Value("${redis.evictTtlInMs:60000}")
    private int evictTtlInMs;

    /** Maximum total connections in the Jedis pool. Property: {@code redis.pool_config.maxTotal}. Default: 128. */
    @Value("${redis.pool_config.maxTotal:128}")
    private int maxTotal;

    /** Maximum idle connections kept in the pool. Property: {@code redis.pool_config.maxIdle}. Default: 128. */
    @Value("${redis.pool_config.maxIdle:128}")
    private int maxIdle;

    /** Minimum idle connections maintained in the pool. Property: {@code redis.pool_config.minIdle}. Default: 16. */
    @Value("${redis.pool_config.minIdle:16}")
    private int minIdle;

    /** Whether to validate connections on borrow. Property: {@code redis.pool_config.testOnBorrow}. Default: false. */
    @Value("${redis.pool_config.testOnBorrow:false}")
    private boolean testOnBorrow;

    /** Whether to validate connections on return. Property: {@code redis.pool_config.testOnReturn}. Default: false. */
    @Value("${redis.pool_config.testOnReturn:false}")
    private boolean testOnReturn;

    /** Whether to validate idle connections during eviction runs. Property: {@code redis.pool_config.testWhileIdle}. Default: true. */
    @Value("${redis.pool_config.testWhileIdle:true}")
    private boolean testWhileIdle;

    /** Minimum idle time (ms) before a connection is evictable. Property: {@code redis.pool_config.minEvictableMs}. Default: 60000. */
    @Value("${redis.pool_config.minEvictableMs:60000}")
    private long minEvictableMs;

    /** Interval (ms) between pool eviction runs. Property: {@code redis.pool_config.evictionRunsMs}. Default: 30000. */
    @Value("${redis.pool_config.evictionRunsMs:30000}")
    private long evictionRunsMs;

    /** Maximum wait (ms) when borrowing a connection from an exhausted pool. Property: {@code redis.pool_config.maxWaitMills}. Default: 60000. */
    @Value("${redis.pool_config.maxWaitMills:60000}")
    private long maxWaitMills;

    /** Number of connections tested per eviction run. Property: {@code redis.pool_config.numberTestsPerEvictionRun}. Default: 3. */
    @Value("${redis.pool_config.numberTestsPerEvictionRun:3}")
    private int numberTestsPerEvictionRun;

    /** Whether to block when the pool is exhausted. Property: {@code redis.pool_config.blockWhenExhausted}. Default: true. */
    @Value("${redis.pool_config.blockWhenExhausted:true}")
    private boolean blockWhenExhausted;

    /** Global TLS toggle for Redis connections. Property: {@code redis.ssl.enabled}. Default: false. */
    @Value("${redis.ssl.enabled:false}")
    private boolean sslEnabled;

    /** Injected SSL certificate paths used by {@link #createSslSocketFactory()}. */
    @Autowired
    private RedisSslCredentials redisSslCredentials;

    /**
     * Creates the primary {@link RedisConnectionFactory} bean used by all Redis cache services.
     *
     * <p>Delegates to the topology-specific {@link #loadFactory()} implementation provided
     * by the active subclass ({@link TBRedisStandaloneConfiguration},
     * {@link TBRedisClusterConfiguration}, or {@link TBRedisSentinelConfiguration}).
     *
     * @return configured Jedis connection factory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return loadFactory();
    }

    /**
     * Builds the topology-specific {@link JedisConnectionFactory}.
     *
     * <p>Implemented by each Redis connection configuration subclass to apply host/cluster/sentinel
     * settings, optional pooling ({@link #buildPoolConfig()}), and optional SSL
     * ({@link #createSslSocketFactory()}).
     *
     * @return Jedis connection factory for the selected deployment mode
     */
    protected abstract JedisConnectionFactory loadFactory();

    /**
     * Registers a transaction-aware {@link RedisCacheManager} for Spring {@code @Cacheable} support.
     *
     * <p>Configures a default {@link RedisCacheConfiguration} with a conversion service that
     * serializes {@link EntityId} keys to strings. Transaction awareness ensures cache writes
     * participate in surrounding Spring transactions when present.
     *
     * @param cf the Redis connection factory supplied by Spring
     * @return Redis-backed cache manager with default cache settings
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        DefaultFormattingConversionService redisConversionService = new DefaultFormattingConversionService();
        RedisCacheConfiguration.registerDefaultConverters(redisConversionService);
        registerDefaultConverters(redisConversionService);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig().withConversionService(redisConversionService);
        return RedisCacheManager.builder(cf).cacheDefaults(configuration)
                .transactionAware()
                .build();
    }

    /**
     * Exposes a generic {@link RedisTemplate} for ad-hoc Redis access outside transactional caches.
     *
     * <p>Uses the same {@link #redisConnectionFactory()} as cache beans. Specialized caches
     * (e.g. {@link RedisTbTransactionalCache}) manage their own serialization and key prefixes.
     *
     * @return Redis template bound to the configured connection factory
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

    /**
     * Registers ThingsBoard-specific type converters for Redis cache key serialization.
     *
     * @param registry Spring converter registry; must not be {@code null}
     */
    private static void registerDefaultConverters(ConverterRegistry registry) {
        Assert.notNull(registry, "ConverterRegistry must not be null!");
        registry.addConverter(EntityId.class, String.class, EntityId::toString);
    }

    /**
     * Builds a {@link JedisPoolConfig} from {@code redis.pool_config.*} properties.
     *
     * <p>Applied by subclasses when {@code useDefaultPoolConfig} / {@code usePoolConfig}
     * flags request custom pooling instead of Jedis defaults.
     *
     * @return populated Jedis pool configuration
     */
    protected JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        poolConfig.setTestWhileIdle(testWhileIdle);
        poolConfig.setSoftMinEvictableIdleTime(Duration.ofMillis(minEvictableMs));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(evictionRunsMs));
        poolConfig.setMaxWaitMillis(maxWaitMills);
        poolConfig.setNumTestsPerEvictionRun(numberTestsPerEvictionRun);
        poolConfig.setBlockWhenExhausted(blockWhenExhausted);
        return poolConfig;
    }

    /**
     * Parses a comma-separated {@code host:port} node list into {@link RedisNode} instances.
     *
     * <p>Used by cluster and sentinel configurations to populate node lists from
     * {@code redis.cluster.nodes} and {@code redis.sentinel.sentinels}.
     *
     * @param nodes comma-separated list in {@code host:port} format; blank yields empty list
     * @return parsed Redis nodes, never {@code null}
     */
    protected List<RedisNode> getNodes(String nodes) {
        List<RedisNode> result;
        if (StringUtils.isBlank(nodes)) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<>();
            for (String hostPort : nodes.split(COMMA)) {
                String host = hostPort.split(COLON)[0];
                int port = Integer.parseInt(hostPort.split(COLON)[1]);
                result.add(new RedisNode(host, port));
            }
        }
        return result;
    }

    /**
     * Creates an {@link SSLSocketFactory} for encrypted Redis connections.
     *
     * <p>Builds a TLS {@link SSLContext} from {@link RedisSslCredentials}:
     * <ul>
     *   <li>Trust store — CA certificates from {@code redis.ssl.credentials.certFile}</li>
     *   <li>Key store (optional) — client cert/key from {@code userCertFile} / {@code userKeyFile}
     *       for mutual TLS</li>
     * </ul>
     *
     * <p>Invoked by subclasses when {@code redis.ssl.enabled=true}.
     *
     * @return SSL socket factory for Jedis client configuration
     * @throws RuntimeException if TLS context initialization fails
     */
    protected SSLSocketFactory createSslSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = createAndInitKeyManagerFactory();
            TrustManagerFactory trustManagerFactory = createAndInitTrustManagerFactory();
            sslContext.init(keyManagerFactory == null ? null : keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("Creating TLS factory failed!", e);
        }
    }

    /**
     * Initializes the trust manager with CA certificates from {@link RedisSslCredentials#getCertFile()}.
     *
     * @return trust manager factory configured with Redis CA certs
     * @throws Exception if certificate loading or keystore initialization fails
     */
    private TrustManagerFactory createAndInitTrustManagerFactory() throws Exception {
            List<X509Certificate> caCerts = SslUtil.readCertFileByPath(redisSslCredentials.getCertFile());
            KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            caKeyStore.load(null, null);
            for (X509Certificate caCert : caCerts) {
                caKeyStore.setCertificateEntry("redis-caCert-cert-" + caCert.getSubjectX500Principal().getName(), caCert);
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(caKeyStore);
            return trustManagerFactory;
    }

    /**
     * Initializes the key manager with optional client certificate and private key for mTLS.
     *
     * @return key manager factory, or factory initialized with empty keystore when client cert is not configured
     * @throws Exception if keystore loading fails
     */
    private KeyManagerFactory createAndInitKeyManagerFactory() throws Exception {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(loadKeyStore(), null);
        return kmf;
    }

    /**
     * Loads the client key store from {@link RedisSslCredentials} for mutual TLS authentication.
     *
     * <p>Returns {@code null} when {@code userCertFile} or {@code userKeyFile} is blank,
     * indicating server-only TLS (no client certificate).
     *
     * @return populated key store, or {@code null} when client credentials are absent
     * @throws KeyStoreException if the default keystore type is unavailable
     * @throws IOException if certificate or key files cannot be read
     * @throws NoSuchAlgorithmException if the certificate factory algorithm is missing
     * @throws CertificateException if certificate parsing fails
     */
    private KeyStore loadKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        if (redisSslCredentials.getUserCertFile().isBlank() || redisSslCredentials.getUserKeyFile().isBlank()) {
            return null;
        }
        List<X509Certificate> certificates = SslUtil.readCertFileByPath(redisSslCredentials.getCertFile());
        PrivateKey privateKey = SslUtil.readPrivateKeyByFilePath(redisSslCredentials.getUserKeyFile(), null);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        List<X509Certificate> unique = certificates.stream().distinct().toList();
        for (X509Certificate cert : unique) {
            keyStore.setCertificateEntry("redis-cert" + cert.getSubjectX500Principal().getName(), cert);
        }

        if (privateKey != null) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            CertPath certPath = factory.generateCertPath(certificates);
            List<? extends Certificate> path = certPath.getCertificates();
            Certificate[] x509Certificates = path.toArray(new Certificate[0]);
            keyStore.setKeyEntry("redis-private-key", privateKey, null, x509Certificates);
        }
        return keyStore;
    }
}
