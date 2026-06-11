#!/usr/bin/env python3
"""Add detailed JavaDoc to common/cache Java files (comments only, no logic changes)."""
from __future__ import annotations

import re
import sys
from pathlib import Path

CACHE_ROOT = Path("common/cache/src/main/java/org/thingsboard/server/cache")

# Maps relative path -> (class_javadoc, {method_signature_prefix: javadoc})
# Method keys match start of method declaration line (after annotations).

def fix_whitespace(content: str) -> str:
    """Remove whitespace-only lines inside class bodies left by prior scripts."""
    lines = content.split("\n")
    out = []
    in_class = False
    brace_depth = 0
    for i, line in enumerate(lines):
        stripped = line.strip()
        if not in_class:
            out.append(line)
            if re.search(r"\b(class|interface|enum|record)\b", line) and "{" in line:
                in_class = True
                brace_depth = line.count("{") - line.count("}")
            elif re.search(r"\b(class|interface|enum|record)\b", line):
                in_class = True
                brace_depth = 0
            continue
        brace_depth += line.count("{") - line.count("}")
        if brace_depth <= 0 and stripped == "}":
            in_class = False
            out.append(line)
            continue
        # Drop lines that are only whitespace between methods
        if stripped == "" and out and out[-1].strip() == "":
            continue
        if stripped == "" and i + 1 < len(lines):
            nxt = lines[i + 1].strip()
            if nxt.startswith("/**") or nxt.startswith("*") or nxt.startswith("@") or nxt.startswith("public") or nxt.startswith("protected") or nxt.startswith("private") or nxt.startswith("void") or nxt.startswith("default"):
                if out and out[-1].rstrip().endswith(";"):
                    out.append(line)
                    continue
        if stripped == "" and out and (out[-1].strip().endswith(";") or out[-1].strip().endswith("}")):
            # keep single blank before javadoc/method
            if not (out[-1].strip() == ""):
                out.append(line)
            continue
        if stripped == "" and len(line) > 0 and line.strip() == "" and all(c in " \t" for c in line):
            # skip orphan whitespace-only lines mid-class
            prev = out[-1] if out else ""
            if prev.strip().endswith(";") or prev.strip().endswith("}"):
                continue
        out.append(line)
    return "\n".join(out)


def is_license_javadoc(block: str) -> bool:
    return "Copyright" in block or "Licensed under the Apache License" in block


def replace_class_javadoc(content: str, new_javadoc: str) -> str:
    """Replace or insert class/interface/enum javadoc immediately before the type declaration."""
    decl_re = re.compile(
        r"^[ \t]*((?:@[\w.()=\"\s,{}#:-]+\s*\n[ \t]*)*)"
        r"((?:public|protected|private|abstract|sealed|non-sealed|final|\s)+(?:class|interface|enum|record)\s+\w+)",
        re.MULTILINE,
    )
    m = decl_re.search(content)
    if not m:
        return content
    start = m.start()
    prefix = content[:start]
    annotations = m.group(1) or ""
    decl = m.group(2)
    prefix = re.sub(r"/\*\*(?:(?!\*/)[\s\S])*?\*/\s*\n\s*$", "", prefix)
    trailing_blocks = list(re.finditer(r"/\*\*(?:(?!\*/)[\s\S])*?\*/", prefix))
    if trailing_blocks:
        last = trailing_blocks[-1]
        tail = prefix[last.end() :].strip()
        if not tail and not is_license_javadoc(last.group(0)):
            prefix = prefix[: last.start()]
    return prefix.rstrip() + "\n\n" + new_javadoc + "\n" + annotations + decl + content[m.end() :]


def insert_method_javadoc(content: str, method_prefix: str, javadoc: str) -> str:
    """Insert or replace javadoc immediately before a method matching prefix."""
    escaped = re.escape(method_prefix)
    pattern = re.compile(
        rf"^[ \t]*(?:/\*\*(?:(?!\*/)[\s\S])*?\*/\s*\n[ \t]*)*(?:@\w+[^\n]*\n[ \t]*)*{escaped}",
        re.MULTILINE,
    )
    m = pattern.search(content)
    if not m:
        return content
    line_start = m.start()
    # Find beginning of existing method javadoc/annotations block
    block_start = line_start
    before = content[:line_start]
    javadoc_m = re.search(r"/\*\*(?:(?!\*/)[\s\S])*?\*/\s*$", before)
    if javadoc_m and not is_license_javadoc(javadoc_m.group(0)):
        block_start = javadoc_m.start()
    indent_match = re.search(r"^([ \t]*)", content[line_start:], re.MULTILINE)
    indent = indent_match.group(1) if indent_match else "    "
    javadoc_indented = "\n".join(
        (indent + line if i else line) for i, line in enumerate(javadoc.strip().split("\n"))
    )
    return content[:block_start] + javadoc_indented + "\n" + content[line_start:]


def add_field_comment(content: str, field_pattern: str, comment: str) -> str:
    if comment in content:
        return content
    pat = re.compile(rf"^(\s*)({re.escape(field_pattern)})", re.MULTILINE)
    def repl(m):
        indent = m.group(1)
        prev = content[: m.start()].rstrip().split("\n")[-1] if m.start() > 0 else ""
        if prev.strip().startswith("/**") or prev.strip().startswith("*"):
            return m.group(0)
        return f"{indent}{comment}\n{indent}{m.group(2)}"
    return pat.sub(repl, content, count=1)


# --- Per-file documentation definitions ---
DOCS: dict[str, dict] = {}

def doc(path: str, class_doc: str, methods: dict | None = None, fields: dict | None = None):
    DOCS[path] = {"class": class_doc, "methods": methods or {}, "fields": fields or {}}


doc("CacheSpecs.java", """\
/**
 * Per-cache sizing and time-to-live settings bound from {@code cache.specs.<name>.*}.
 *
 * <p>Used by {@link CacheSpecsMap} to configure both Caffeine weight limits
 * ({@link #maxSize}) and Redis/Caffeine entry expiration ({@link #timeToLiveInMinutes}).
 * A TTL of {@code 0} means entries never expire.
 *
 * @see CacheSpecsMap
 * @see TbCaffeineCacheConfiguration
 * @see RedisTbTransactionalCache
 */""", fields={
    "private Integer timeToLiveInMinutes": "/** Entry TTL in minutes; {@code 0} disables expiration. */",
    "private Integer maxSize": "/** Maximum cache weight (Caffeine) or enable flag (Redis: {@code > 0} enables). */",
})

doc("CacheSpecsMap.java", """\
/**
 * Central registry mapping cache names to {@link CacheSpecs} loaded from YAML/properties.
 *
 * <p>Bound under the {@code cache} prefix ({@code cache.specs.<cacheName>.maxSize},
 * {@code cache.specs.<cacheName>.timeToLiveInMinutes}). Injected into
 * {@link TbCaffeineCacheConfiguration}, {@link RedisTbTransactionalCache}, and entity caches.
 *
 * <p>On startup, {@link #replaceTheJWTTokenRefreshExpTime()} aligns the
 * {@link org.thingsboard.server.common.data.CacheConstants#USERS_SESSION_INVALIDATION_CACHE}
 * TTL with JWT refresh token lifetime ({@code security.jwt.refreshTokenExpTime}).
 *
 * @see CacheSpecs
 * @see TbCaffeineCacheConfiguration
 */""", methods={
    "public void replaceTheJWTTokenRefreshExpTime()": """\
    /**
     * Adjusts session-invalidation cache TTL to exceed JWT refresh token lifetime.
     *
     * <p>Sets {@code timeToLiveInMinutes} to {@code (refreshTokenExpTime / 60) + 1} so
     * cached credential-update timestamps outlive refresh tokens.
     */""",
}, fields={
    "private int refreshTokenExpTime": "/** JWT refresh token lifetime in seconds. Property: {@code security.jwt.refreshTokenExpTime}. Default: 604800. */",
    "private Map<String, CacheSpecs> specs": "/** Map of cache name to per-cache specs from {@code cache.specs}. */",
})

doc("TbCacheTransaction.java", """\
/**
 * Unit of work for batched, atomic cache writes in {@link TbTransactionalCache}.
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@link CaffeineTbCacheTransaction} — in-process staging with commit conflict detection</li>
 *   <li>{@link RedisTbCacheTransaction} — Redis WATCH/MULTI/EXEC transaction</li>
 * </ul>
 *
 * <p>Typical lifecycle: {@link #put} stages entries, then {@link #commit()} applies them
 * or {@link #rollback()} discards them.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see TbTransactionalCache#newTransactionForKey
 */""")

doc("TbCacheValueWrapper.java", """\
/**
 * Wrapper distinguishing a cache miss from a stored {@code null} (negative cache hit).
 *
 * <p>{@link #get()} returns the cached value which may legitimately be {@code null}
 * when the wrapper represents a negative-cache entry. A {@code null} wrapper reference
 * from {@link TbTransactionalCache#get} indicates a complete cache miss.
 *
 * @param <T> cached value type
 * @see SimpleTbCacheValueWrapper
 * @see TbTransactionalCache#get
 */""", methods={
    "T get()": """\
    /**
     * Returns the cached value, which may be {@code null} for negative-cache entries.
     *
     * @return cached value or {@code null} when negatively cached
     */""",
})

doc("CaffeineTbCacheTransaction.java", """\
/**
 * In-process transaction that stages puts before atomic commit to a {@link CaffeineTbTransactionalCache}.
 *
 * <p>Pending entries accumulate in {@link #pendingPuts}. On {@link #commit()}, the parent cache
 * applies puts only if this transaction was not marked failed by a concurrent mutation on
 * a watched key. {@link #rollback()} drops the transaction without writing.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see CaffeineTbTransactionalCache#commit
 */""", methods={
    "public void put(K key, V value)": """\
    /**
     * Stages a put operation applied only on successful {@link #commit()}.
     *
     * @param key   cache key
     * @param value value to store when committed
     */""",
    "public boolean commit()": """\
    /**
     * Commits staged puts via {@link CaffeineTbTransactionalCache#commit}.
     *
     * @return {@code true} if the transaction was not invalidated before commit
     */""",
    "public void rollback()": """\
    /**
     * Aborts the transaction and releases its registry entry without writing to cache.
     */""",
}, fields={
    "private final UUID id": "/** Unique transaction identifier for registry tracking. */",
    "private final CaffeineTbTransactionalCache<K, V> cache": "/** Parent cache coordinating commit/rollback. */",
    "private final List<K> keys": "/** Keys watched by this transaction for conflict detection. */",
    "private boolean failed": "/** When {@code true}, {@link #commit()} becomes a no-op. */",
    "private final Map<K, V> pendingPuts": "/** Staged key-value pairs applied on successful commit. */",
})

doc("RedisTbCacheTransaction.java", """\
/**
 * Redis WATCH/MULTI/EXEC transaction wrapping a {@link RedisTbTransactionalCache}.
 *
 * <p>Each {@link #put} stages a SET command on the watched connection opened by
 * {@link RedisTbTransactionalCache#watch}. {@link #commit()} executes EXEC;
 * {@link #rollback()} sends DISCARD. The connection is always closed afterward.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see RedisTbTransactionalCache#newTransactionForKey
 */""", methods={
    "public void put(K key, V value)": """\
    /**
     * Stages a SET on the open MULTI connection.
     *
     * @param key   cache key
     * @param value value to store when the transaction commits
     */""",
    "public boolean commit()": """\
    /**
     * Executes the Redis transaction and closes the connection.
     *
     * @return {@code true} when EXEC returns at least one non-null result
     */""",
    "public void rollback()": """\
    /**
     * Discards staged commands and closes the connection.
     */""",
}, fields={
    "private final RedisTbTransactionalCache<K, V> cache": "/** Parent cache providing serialization and key prefixing. */",
    "private final RedisConnection connection": "/** WATCH/MULTI connection; closed on commit or rollback. */",
})

doc("SimpleTbCacheValueWrapper.java", """\
/**
 * Default {@link TbCacheValueWrapper} implementation holding a value or an explicit empty marker.
 *
 * <p>Use {@link #empty()} to represent a negative-cache hit (key present, value absent).
 * Use {@link #wrap(Object)} for normal entries. {@link #wrap(org.springframework.cache.Cache.ValueWrapper)}
 * adapts Spring Cache API results.
 *
 * @param <T> wrapped value type
 * @see TbCacheValueWrapper
 */""", methods={
    "public T get()": """\
    /**
     * {@inheritDoc}
     *
     * @return stored value, or {@code null} for {@link #empty()} wrappers
     */""",
    "public static <T> SimpleTbCacheValueWrapper<T> empty()": """\
    /**
     * Creates a wrapper representing a negative-cache entry.
     *
     * @param <T> value type
     * @return wrapper whose {@link #get()} returns {@code null}
     */""",
    "public static <T> SimpleTbCacheValueWrapper<T> wrap(T value)": """\
    /**
     * Wraps a concrete cached value.
     *
     * @param value cached value (may be {@code null})
     * @param <T>   value type
     * @return new wrapper
     */""",
    "public static <T> SimpleTbCacheValueWrapper<T> wrap(Cache.ValueWrapper source)": """\
    /**
     * Adapts a Spring {@link org.springframework.cache.Cache.ValueWrapper}.
     *
     * @param source Spring cache wrapper, or {@code null} for a miss
     * @param <T>    value type
     * @return wrapper, or {@code null} when {@code source} is {@code null}
     */""",
}, fields={
    "private final T value": "/** Cached value; {@code null} for negative-cache wrappers. */",
})

doc("TbCaffeineCacheConfiguration.java", """\
/**
 * Spring configuration registering Caffeine-backed caches from {@link CacheSpecsMap}.
 *
 * <p>Activated when {@code cache.type=caffeine} (default when property is absent).
 * Builds a {@link org.springframework.cache.support.SimpleCacheManager} with one
 * {@link org.springframework.cache.caffeine.CaffeineCache} per configured spec.
 *
 * <p>Each cache uses:
 * <ul>
 *   <li>{@link CacheSpecs#getMaxSize()} as Caffeine maximum weight</li>
 *   <li>{@link CacheSpecs#getTimeToLiveInMinutes()} for {@code expireAfterWrite} (skipped when 0)</li>
 *   <li>A collection-aware weigher so list-valued entries count by element count</li>
 * </ul>
 *
 * @see CacheSpecsMap
 * @see CaffeineTbTransactionalCache
 * @see TBRedisCacheConfiguration
 */""", methods={
    "public CacheManager cacheManager()": """\
    /**
     * Creates the Spring {@link CacheManager} with all configured Caffeine caches.
     *
     * @return initialized simple cache manager
     */""",
    "public Ticker ticker()": """\
    /**
     * Supplies the Caffeine {@link com.github.benmanes.caffeine.cache.Ticker} for TTL measurement.
     *
     * @return system nano-time ticker
     */""",
})

doc("TbRedisSerializer.java", """\
/**
 * Pluggable serializer for Redis cache values in {@link RedisTbTransactionalCache}.
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@link TbJsonRedisSerializer} — JSON for human-readable/interoperable storage</li>
 *   <li>{@link TbTypedJsonRedisSerializer} — JSON with {@link com.fasterxml.jackson.core.type.TypeReference}</li>
 *   <li>{@link TbJavaRedisSerializer} — Java native serialization</li>
 * </ul>
 *
 * @param <K> cache key type (may inform deserialization)
 * @param <T> cache value type
 * @see RedisTbTransactionalCache#getRawValue
 */""", methods={
    "byte[] serialize(@Nullable T t)": """\
    /**
     * Serializes a value to Redis bytes.
     *
     * @param t value to serialize; may be {@code null}
     * @return serialized bytes, or {@code null}
     * @throws org.springframework.data.redis.serializer.SerializationException on failure
     */""",
    "T deserialize(K key, @Nullable byte[] bytes)": """\
    /**
     * Deserializes Redis bytes to a value.
     *
     * @param key   original cache key (may guide type resolution)
     * @param bytes serialized data; may be {@code null}
     * @return deserialized value, or {@code null}
     * @throws org.springframework.data.redis.serializer.SerializationException on failure
     */""",
})

doc("TbJavaRedisSerializer.java", """\
/**
 * Java native serialization {@link TbRedisSerializer} using Spring {@link org.springframework.data.redis.serializer.RedisSerializer#java()}.
 *
 * <p>Produces compact binary payloads but requires compatible class versions across cluster nodes.
 * Prefer {@link TbJsonRedisSerializer} for entity caches unless binary compatibility is required.
 *
 * @param <K> cache key type
 * @param <V> cache value type
 * @see TbRedisSerializer
 */""", methods={
    "public byte[] serialize(V value)": """\
    /**
     * {@inheritDoc}
     *
     * @param value value to serialize
     * @return Java-serialized bytes
     * @throws org.springframework.data.redis.serializer.SerializationException on failure
     */""",
    "public V deserialize(K key, byte[] bytes)": """\
    /**
     * {@inheritDoc}
     *
     * @param key   cache key (unused)
     * @param bytes Java-serialized bytes
     * @return deserialized object
     * @throws org.springframework.data.redis.serializer.SerializationException on failure
     */""",
}, fields={
    "final RedisSerializer<Object> serializer": "/** Delegating Spring Java serializer. */",
})

doc("TbJsonRedisSerializer.java", """\
/**
 * JSON {@link TbRedisSerializer} for entity caches stored as UTF-8 JSON in Redis.
 *
 * <p>Uses {@link org.thingsboard.common.util.JacksonUtil} with unknown-property ignoring
 * on deserialize for forward-compatible schema evolution.
 *
 * @param <K> cache key type
 * @param <V> concrete value class
 * @see TbRedisSerializer
 * @see TbTypedJsonRedisSerializer
 */""", methods={
    "public TbJsonRedisSerializer(Class<V> clazz)": """\
    /**
     * Binds deserialization to a concrete value class.
     *
     * @param clazz target type for JSON deserialization
     */""",
    "public byte[] serialize(V v)": """\
    /**
     * {@inheritDoc}
     *
     * @param v value to serialize
     * @return JSON UTF-8 bytes
     */""",
    "public V deserialize(K key, byte[] bytes)": """\
    /**
     * {@inheritDoc}
     *
     * @param key   cache key (unused)
     * @param bytes JSON bytes
     * @return deserialized entity, or {@code null} when bytes are {@code null}
     * @throws org.springframework.data.redis.serializer.SerializationException when JSON is invalid
     */""",
}, fields={
    "private final Class<V> clazz": "/** Target class for Jackson deserialization. */",
})

doc("TbTypedJsonRedisSerializer.java", """\
/**
 * JSON {@link TbRedisSerializer} using Jackson {@link com.fasterxml.jackson.core.type.TypeReference}
 * for generic or complex value types.
 *
 * <p>Used when the cached type is not a simple {@link Class} (e.g. {@code List<Device>},
 * {@code Map<String, Object>}).
 *
 * @param <K> cache key type
 * @param <V> cache value type described by the type reference
 * @see TbJsonRedisSerializer
 */""", methods={
    "public TbTypedJsonRedisSerializer(TypeReference<V> valueTypeRef)": """\
    /**
     * @param valueTypeRef Jackson type reference for deserialization
     */""",
    "public byte[] serialize(V v)": """\
    /**
     * {@inheritDoc}
     *
     * @param v value to serialize
     * @return JSON bytes
     */""",
    "public V deserialize(K key, byte[] bytes)": """\
    /**
     * {@inheritDoc}
     *
     * @param key   cache key (unused)
     * @param bytes JSON bytes
     * @return deserialized value
     * @throws org.springframework.data.redis.serializer.SerializationException on parse failure
     */""",
}, fields={
    "private final TypeReference<V> valueTypeRef": "/** Jackson type token for {@code V}. */",
})

doc("VersionedCacheKey.java", """\
/**
 * Marker interface for cache keys participating in versioned optimistic concurrency.
 *
 * <p>Implemented by keys such as {@link org.thingsboard.server.cache.device.DeviceCacheKey}.
 * When {@link #isVersioned()} returns {@code true}, {@link VersionedRedisTbCache} stores
 * an 8-byte big-endian version prefix before the serialized value and uses Lua compare-and-set.
 *
 * @see VersionedTbCache
 * @see VersionedRedisTbCache
 * @see VersionedCaffeineTbCache
 */""", methods={
    "default boolean isVersioned()": """\
    /**
     * Indicates whether version-prefix storage applies to this key.
     *
     * @return {@code false} by default; override to enable versioned Redis storage
     */""",
})

doc("VersionedTbCache.java", """\
/**
 * Extension of {@link TbTransactionalCache} for entities implementing {@link org.thingsboard.server.common.data.HasVersion}.
 *
 * <p>Writes include the entity version so stale updates from out-of-order events are rejected.
 * Adds version-aware {@link #evict(K, Long)} that writes a tombstone at the given version.
 *
 * <p>Implementations: {@link VersionedCaffeineTbCache}, {@link VersionedRedisTbCache}.
 *
 * @param <K> versioned cache key
 * @param <V> versioned entity type
 * @see VersionedCacheKey
 */""", methods={
    "default V get(K key, Supplier<V> supplier)": """\
    /**
     * Cache-aside get with automatic put on miss.
     *
     * @param key      cache key
     * @param supplier database fallback
     * @return cached or loaded value
     */""",
    "default V get(K key, Supplier<V> supplier, boolean putToCache)": """\
    /**
     * Cache-aside get with optional write-through.
     *
     * @param key         cache key
     * @param supplier    database fallback
     * @param putToCache  whether to store the loaded value
     * @return cached or loaded value
     */""",
    "void evict(K key, Long version)": """\
    /**
     * Version-aware eviction writing a versioned tombstone.
     *
     * @param key     cache key
     * @param version entity version for optimistic invalidation
     */""",
    "default Long getVersion(V value)": """\
    /**
     * Extracts the version stamp from an entity for compare-and-set logic.
     *
     * @param value entity; {@code null} maps to {@code 0L}
     * @return version number, {@code 0L} for null, or {@code null} when version is unset
     */""",
})

# Entity cache docs - pattern based
ENTITY_PACKAGES = {
    "User": "user", "Customer": "customer", "Device": "device", "Edge": "edge",
    "ResourceInfo": "resourceInfo",
}

def entity_cache_doc(entity: str, key_class: str, cache_const: str, caffeine: bool = True, redis: bool = True, versioned: bool = False, data_class: str | None = None):
    base = data_class or entity
    pkg = ENTITY_PACKAGES.get(entity, "cache")
    parent_c = "VersionedCaffeineTbCache" if versioned else "CaffeineTbTransactionalCache"
    parent_r = "VersionedRedisTbCache" if versioned else "RedisTbTransactionalCache"
    bean_name = entity + "Cache"
    if caffeine:
        doc(f"{pkg}/{entity}CaffeineCache.java", (
            "/**\n"
            " * Caffeine {@link " + parent_c + "} for {@link org.thingsboard.server.common.data." + base + "} entities.\n"
            " *\n"
            " * <p>Spring bean {@code \"" + bean_name + "\"} activated when\n"
            " * {@code cache.type=caffeine} (default). Cache name: {@link org.thingsboard.server.common.data.CacheConstants#"
            + cache_const + "}.\n"
            " *\n"
            " * @see " + key_class + "\n"
            " * @see " + parent_c + "\n"
            " */"
        ), methods={
            f"public {entity}CaffeineCache(CacheManager cacheManager)": """\
    /**
     * Wires the cache from the Spring {@link org.springframework.cache.CacheManager}.
     *
     * @param cacheManager manager built by {@link TbCaffeineCacheConfiguration}
     */""",
        })
    if redis:
        doc(f"{pkg}/{entity}RedisCache.java", (
            "/**\n"
            " * Redis {@link " + parent_r + "} for {@link org.thingsboard.server.common.data." + base + "} entities.\n"
            " *\n"
            " * <p>Spring bean {@code \"" + bean_name + "\"} activated when\n"
            " * {@code cache.type=redis}. Shares cluster-wide state via {@link TBRedisCacheConfiguration}.\n"
            " * Cache name: {@link org.thingsboard.server.common.data.CacheConstants#"
            + cache_const + "}.\n"
            " *\n"
            " * @see " + key_class + "\n"
            " * @see " + parent_r + "\n"
            " */"
        ), methods={
            f"public {entity}RedisCache(": """\
    /**
     * Constructs the Redis cache with JSON serialization and TTL from {@link CacheSpecsMap}.
     *
     * @param configuration     Redis connection and evict TTL settings
     * @param cacheSpecsMap     per-cache size and TTL configuration
     * @param connectionFactory Redis connection from {@link TBRedisCacheConfiguration}
     */""",
        })

entity_cache_doc("User", "UserCacheKey", "USER_CACHE")
entity_cache_doc("Customer", "CustomerCacheKey", "CUSTOMER_CACHE")
entity_cache_doc("Edge", "EdgeCacheKey", "EDGE_CACHE")
entity_cache_doc("Device", "DeviceCacheKey", "DEVICE_CACHE", versioned=True)
entity_cache_doc("ResourceInfo", "ResourceInfoCacheKey", "RESOURCE_INFO_CACHE", data_class="TbResourceInfo")

doc("edge/RelatedEdgesCaffeineCache.java", """\
/**
 * Caffeine cache for {@link RelatedEdgesCacheValue} lists keyed by {@link RelatedEdgesCacheKey}.
 *
 * <p>Caches the set of edges related to a tenant entity to avoid repeated graph queries.
 * Bean name: {@code RelatedEdgesCache}. Cache name from {@link org.thingsboard.server.common.data.CacheConstants}.
 *
 * @see RelatedEdgesCacheKey
 * @see RelatedEdgesCacheValue
 */""")

doc("edge/RelatedEdgesRedisCache.java", """\
/**
 * Redis cache for edge relationship lists shared across cluster nodes.
 *
 * @see RelatedEdgesCaffeineCache
 * @see RelatedEdgesCacheKey
 */""", methods={
    "public RelatedEdgesRedisCache(": """\
    /**
     * @param configuration     Redis settings
     * @param cacheSpecsMap     per-cache TTL/size
     * @param connectionFactory Redis connection factory
     */""",
})

doc("usersUpdateTime/UsersSessionInvalidationCaffeineCache.java", """\
/**
 * Caffeine cache tracking last user credential update timestamps for session invalidation.
 *
 * <p>Maps user identifiers ({@link String}) to update epoch millis ({@link Long}).
 * TTL aligned with JWT refresh lifetime via {@link CacheSpecsMap#replaceTheJWTTokenRefreshExpTime()}.
 *
 * @see UsersSessionInvalidationRedisCache
 */""")

doc("usersUpdateTime/UsersSessionInvalidationRedisCache.java", """\
/**
 * Redis cache for cross-node user session invalidation timestamps.
 *
 * <p>Bean {@code UsersSessionInvalidation}. Stores {@link Long} update times as JSON.
 *
 * @see UsersSessionInvalidationCaffeineCache
 * @see CacheSpecsMap
 */""", methods={
    "public UsersSessionInvalidationRedisCache(": """\
    /**
     * @param configuration     Redis connection settings
     * @param cacheSpecsMap     cache specs including JWT-aligned TTL
     * @param connectionFactory Redis factory
     */""",
})

# Key classes
doc("customer/CustomerCacheKey.java", """\
/**
 * Composite cache key for {@link org.thingsboard.server.common.data.Customer} by tenant and title.
 *
 * <p>String form: {@code tenantUuid_title}. Used by {@link CustomerCaffeineCache}
 * and {@link CustomerRedisCache}.
 *
 * @see CustomerCacheEvictEvent
 */""", methods={
    "public String toString()": """\
    /**
     * @return {@code tenantId + "_" + title} key suffix
     */""",
}, fields={
    "private final TenantId tenantId": "/** Owning tenant; required. */",
    "private final String title": "/** Customer title used as lookup dimension. */",
})

doc("device/DeviceCacheKey.java", """\
/**
 * Composite {@link VersionedCacheKey} for {@link org.thingsboard.server.common.data.Device} lookups.
 *
 * <p>Supports three lookup shapes: by {@link org.thingsboard.server.common.data.id.DeviceId} alone,
 * by tenant+deviceId (versioned), or by tenant+deviceName (non-versioned name index).
 *
 * @see DeviceCaffeineCache
 * @see DeviceRedisCache
 * @see DeviceCacheEvictEvent
 */""", methods={
    "public DeviceCacheKey(DeviceId deviceId)": """\
    /**
     * Key for global device-id lookup (no tenant scope).
     *
     * @param deviceId device identifier
     */""",
    "public DeviceCacheKey(TenantId tenantId, DeviceId deviceId)": """\
    /**
     * Versioned tenant-scoped key by device id.
     *
     * @param tenantId tenant scope
     * @param deviceId device identifier
     */""",
    "public DeviceCacheKey(TenantId tenantId, String deviceName)": """\
    /**
     * Non-versioned tenant-scoped key by device name.
     *
     * @param tenantId   tenant scope
     * @param deviceName device name within tenant
     */""",
    "public String toString()": """\
    /**
     * @return key suffix encoding tenant, id, or name lookup variant
     */""",
    "public boolean isVersioned()": """\
    /**
     * @return {@code true} when keyed by device id (versioned storage enabled)
     */""",
})

doc("edge/EdgeCacheKey.java", """\
/**
 * Cache key for {@link org.thingsboard.server.common.data.edge.Edge} by tenant and edge name.
 *
 * @see EdgeCaffeineCache
 * @see EdgeRedisCache
 */""")

doc("edge/RelatedEdgesCacheKey.java", """\
/**
 * Cache key for edges related to a tenant-scoped {@link org.thingsboard.server.common.data.id.EntityId}.
 *
 * @see RelatedEdgesCacheValue
 * @see RelatedEdgesCaffeineCache
 */""", methods={
    "public String toString()": """\
    /**
     * @return {@code "{" + tenantId + "}" + entityId} key suffix
     */""",
})

doc("edge/RelatedEdgesCacheValue.java", """\
/**
 * Cached payload holding edge identifiers related to a {@link RelatedEdgesCacheKey}.
 *
 * <p>Serializable value type stored in {@link RelatedEdgesCaffeineCache} / {@link RelatedEdgesRedisCache}.
 */""")

doc("resourceInfo/ResourceInfoCacheKey.java", """\
/**
 * Cache key for {@link org.thingsboard.server.common.data.TbResourceInfo} by tenant and resource key.
 *
 * @see ResourceInfoCaffeineCache
 * @see ResourceInfoRedisCache
 */""")

# Evict events
for ev in ["customer/CustomerCacheEvictEvent", "device/DeviceCacheEvictEvent", "edge/EdgeCacheEvictEvent",
           "edge/RelatedEdgesEvictEvent", "resourceInfo/ResourceInfoEvictEvent", "user/UserCacheEvictEvent"]:
    entity = ev.split("/")[1].replace("CacheEvictEvent", "")
    doc(f"{ev}.java", (
        "/**\n"
        " * Cluster broadcast event evicting " + entity + " cache entries after create/update/delete.\n"
        " *\n"
        " * <p>Published to all ThingsBoard nodes so {@link " + entity + "CaffeineCache} and\n"
        " * {@link " + entity + "RedisCache} stay consistent. Handlers evict old and new key variants\n"
        " * when identifiers change (e.g. rename).\n"
        " */"
    ))

# OTA
doc("ota/OtaPackageDataCache.java", """\
/**
 * Cache for OTA firmware/package binary blobs to avoid repeated database reads during device updates.
 *
 * <p>Implementations: {@link CaffeineOtaPackageCache} (local), {@link RedisOtaPackageDataCache} (cluster).
 * Keys are OTA package IDs; values are raw {@code byte[]} firmware data.
 */""", methods={
    "byte[] get(String key)": """\
    /**
     * Returns full package data for the given key.
     *
     * @param key OTA package identifier
     * @return firmware bytes, or {@code null} on miss
     */""",
    "byte[] get(String key, int chunkSize, int chunk)": """\
    /**
     * Returns a byte range slice for chunked firmware download.
     *
     * @param key       OTA package identifier
     * @param chunkSize bytes per chunk; values {@code < 1} return full data
     * @param chunk     zero-based chunk index
     * @return slice bytes, or empty array when out of range
     */""",
    "void put(String key, byte[] value)": """\
    /**
     * Stores firmware data (typically put-if-absent semantics in implementations).
     *
     * @param key   OTA package identifier
     * @param value raw firmware bytes
     */""",
    "void evict(String key)": """\
    /**
     * Removes cached firmware data.
     *
     * @param key OTA package identifier
     */""",
    "default boolean has(String otaPackageId)": """\
    /**
     * Checks whether any data exists for the package.
     *
     * @param otaPackageId package identifier
     * @return {@code true} when the first byte chunk is non-empty
     */""",
})

doc("ota/CaffeineOtaPackageCache.java", """\
/**
 * In-process Caffeine implementation of {@link OtaPackageDataCache}.
 *
 * <p>Activated when {@code cache.type=caffeine}. Uses {@link org.thingsboard.server.common.data.CacheConstants#OTA_PACKAGE_DATA_CACHE}.
 * Chunk reads slice the in-memory {@code byte[]} without separate Redis GETRANGE.
 */""", methods={
    "public byte[] get(String key)": """\
    /** {@inheritDoc} */""",
    "public byte[] get(String key, int chunkSize, int chunk)": """\
    /** {@inheritDoc} */""",
    "public void put(String key, byte[] value)": """\
    /** {@inheritDoc} */""",
    "public void evict(String key)": """\
    /** {@inheritDoc} */""",
}, fields={
    "private final CacheManager cacheManager": "/** Spring cache manager providing the OTA data cache region. */",
})

doc("ota/RedisOtaPackageDataCache.java", """\
/**
 * Redis implementation of {@link OtaPackageDataCache} for cluster-wide firmware blob sharing.
 *
 * <p>Activated when {@code cache.type=redis}. Keys are formatted as
 * {@code OTA_PACKAGE_DATA_CACHE::<packageId>}. Chunk reads use Redis GETRANGE.
 */""", methods={
    "public byte[] get(String key)": """\
    /** {@inheritDoc} */""",
    "public byte[] get(String key, int chunkSize, int chunk)": """\
    /** {@inheritDoc} */""",
    "public void put(String key, byte[] value)": """\
    /** {@inheritDoc} */""",
    "public void evict(String key)": """\
    /** {@inheritDoc} */""",
}, fields={
    "private final RedisConnectionFactory redisConnectionFactory": "/** Redis connection from {@link TBRedisCacheConfiguration}. */",
})

# Rate limits
doc("limits/RateLimitService.java", """\
/**
 * Tenant and API rate limiting service backed by in-memory token buckets.
 *
 * <p>Implementations consult {@link TenantProfileProvider} for per-tenant limit strings
 * and track consumption in a Caffeine cache of {@link org.thingsboard.server.common.msg.tools.TbRateLimits} instances.
 *
 * @see DefaultRateLimitService
 * @see TenantProfileProvider
 */""", methods={
    "boolean checkRateLimit(LimitedApi api, TenantId tenantId)": """\
    /**
     * Checks rate limit at tenant level (level = tenant id).
     *
     * @param api      limited API identifier
     * @param tenantId tenant to check
     * @return {@code true} when the request is within limits
     */""",
    "boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level)": """\
    /**
     * Checks rate limit at a sub-tenant level (e.g. device, customer).
     *
     * @param api      limited API
     * @param tenantId owning tenant
     * @param level    limit scope object
     * @return {@code true} when within limits
     */""",
    "boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level, boolean ignoreTenantNotFound)": """\
    /**
     * Checks rate limit with optional tolerance for missing tenant profiles.
     *
     * @param api                   limited API
     * @param tenantId              tenant
     * @param level                 limit scope
     * @param ignoreTenantNotFound  when {@code true}, allows requests if profile is missing
     * @return {@code true} when within limits
     * @throws org.thingsboard.server.common.data.exception.TenantProfileNotFoundException when profile missing and not ignored
     */""",
    "boolean checkRateLimit(LimitedApi api, Object level, String rateLimitConfig)": """\
    /**
     * Checks rate limit using an explicit configuration string.
     *
     * @param api              limited API
     * @param level            limit scope
     * @param rateLimitConfig  limit definition string; empty disables limiting
     * @return {@code true} when within limits
     */""",
    "void cleanUp(LimitedApi api, Object level)": """\
    /**
     * Invalidates the cached rate-limit bucket for the given scope.
     *
     * @param api   limited API
     * @param level limit scope
     */""",
})

doc("limits/DefaultRateLimitService.java", """\
/**
 * Default {@link RateLimitService} using Caffeine-cached {@link org.thingsboard.server.common.msg.tools.TbRateLimits} buckets.
 *
 * <p>Configuration:
 * <ul>
 *   <li>{@code cache.rateLimits.timeToLiveInMinutes} — bucket cache idle TTL (default 120)</li>
 *   <li>{@code cache.rateLimits.maxSize} — max cached buckets (default 200000)</li>
 * </ul>
 *
 * <p>On limit breach, publishes a {@link org.thingsboard.server.common.data.notification.rule.trigger.RateLimitsTrigger}
 * via {@link org.thingsboard.server.common.msg.notification.NotificationRuleProcessor}.
 *
 * @see TenantProfileProvider
 */""", methods={
    "public DefaultRateLimitService(": """\
    /**
     * @param tenantProfileProvider      source of per-tenant limit configuration
     * @param notificationRuleProcessor  publishes rate-limit breach notifications
     * @param rateLimitsTtl              bucket cache TTL in minutes
     * @param rateLimitsCacheMaxSize     maximum cached buckets
     */""",
    "public boolean checkRateLimit(LimitedApi api, TenantId tenantId)": """\
    /** {@inheritDoc} */""",
    "public boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level)": """\
    /** {@inheritDoc} */""",
    "public boolean checkRateLimit(LimitedApi api, TenantId tenantId, Object level, boolean ignoreTenantNotFound)": """\
    /** {@inheritDoc} */""",
    "public boolean checkRateLimit(LimitedApi api, Object level, String rateLimitConfig)": """\
    /** {@inheritDoc} */""",
    "public void cleanUp(LimitedApi api, Object level)": """\
    /** {@inheritDoc} */""",
}, fields={
    "private final TenantProfileProvider tenantProfileProvider": "/** Resolves tenant-specific rate limit config strings. */",
    "private final NotificationRuleProcessor notificationRuleProcessor": "/** Fires notifications on limit breaches. */",
    "private final Cache<RateLimitKey, TbRateLimits> rateLimits": "/** Cached token-bucket limiters per API/level pair. */",
})

doc("limits/TenantProfileProvider.java", """\
/**
 * Supplies {@link org.thingsboard.server.common.data.TenantProfile} data for rate-limit configuration lookup.
 *
 * <p>Implemented in the application module; injected into {@link DefaultRateLimitService}.
 *
 * @see RateLimitService
 */""", methods={
    "TenantProfile get(TenantId tenantId)": """\
    /**
     * Loads the tenant profile containing API limit configuration.
     *
     * @param tenantId tenant identifier
     * @return tenant profile, or {@code null} when not found
     */""",
})

# Versioned implementations - large files need manual handling; add class + key methods
doc("VersionedCaffeineTbCache.java", """\
/**
 * Caffeine implementation of {@link VersionedTbCache} with in-process version checks.
 *
 * <p>Stores {@link org.thingsboard.server.common.data.util.TbPair} of (version, value) in the
 * underlying cache. Puts succeed only when the new version exceeds the cached version.
 *
 * @param <K> versioned cache key
 * @param <V> entity implementing {@link org.thingsboard.server.common.data.HasVersion}
 * @see VersionedTbCache
 * @see CaffeineTbTransactionalCache
 */""", methods={
    "public VersionedCaffeineTbCache(CacheManager cacheManager, String cacheName)": """\
    /**
     * @param cacheManager Spring cache manager
     * @param cacheName    configured cache region name
     */""",
    "public TbCacheValueWrapper<V> get(K key)": """\
    /**
     * Returns the value portion of the stored version-value pair.
     *
     * @param key cache key
     * @return value wrapper, or {@code null} on miss
     */""",
    "public void put(K key, V value)": """\
    /**
     * Puts only when the entity version is newer than the cached version.
     *
     * @param key   cache key
     * @param value versioned entity
     */""",
    "public void evict(K key)": """\
    /**
     * Removes the entire versioned entry.
     *
     * @param key cache key
     */""",
    "public void evict(K key, Long version)": """\
    /**
     * Writes a versioned tombstone at the given version.
     *
     * @param key     cache key
     * @param version eviction version stamp
     */""",
})

doc("VersionedRedisTbCache.java", """\
/**
 * Redis implementation of {@link VersionedTbCache} using an 8-byte version prefix and Lua compare-and-set.
 *
 * <p>Versioned keys store {@code [8-byte big-endian version][serialized value]}. The
 * {@link #SET_VERSIONED_VALUE_LUA_SCRIPT} atomically updates only when {@code newVersion > currentVersion}.
 *
 * <p>{@link #putIfAbsent} and {@link #evictOrPut} are intentionally unsupported.
 *
 * @param <K> versioned cache key
 * @param <V> versioned entity type
 * @see VersionedTbCache
 * @see RedisTbTransactionalCache
 */""", methods={
    "public VersionedRedisTbCache(String cacheName,": """\
    /**
     * @param cacheName         cache region name
     * @param cacheSpecsMap     TTL and enablement specs
     * @param connectionFactory Redis connection
     * @param configuration     Redis global config
     * @param valueSerializer   value serializer
     */""",
    "protected byte[] doGet(K key, RedisConnection connection)": """\
    /**
     * Strips the 8-byte version prefix before deserialization for versioned keys.
     *
     * @param key        cache key
     * @param connection Redis connection
     * @return serialized value bytes without version prefix
     */""",
    "public void put(K key, V value)": """\
    /**
     * Version-aware put using Lua compare-and-set.
     *
     * @param key   cache key
     * @param value versioned entity
     */""",
    "public void evict(K key, Long version)": """\
    /**
     * Writes a short-lived versioned tombstone using {@link #evictExpiration}.
     *
     * @param key     cache key
     * @param version tombstone version
     */""",
    "public void putIfAbsent(K key, V value)": """\
    /**
     * @throws org.apache.commons.lang3.NotImplementedException always — not supported for versioned caches
     */""",
    "public void evictOrPut(K key, V value)": """\
    /**
     * @throws org.apache.commons.lang3.NotImplementedException always — not supported for versioned caches
     */""",
}, fields={
    "static final byte[] SET_VERSIONED_VALUE_LUA_SCRIPT": "/** Lua script performing atomic version compare-and-set with TTL. */",
    "static final byte[] SET_VERSIONED_VALUE_SHA": "/** Expected SHA-1 of the versioned SET Lua script. */",
    "private static final int VERSION_SIZE": "/** Byte length of the big-endian version prefix stored before each value. */",
})


def process_file(path: Path, spec: dict) -> bool:
    content = path.read_text(encoding="utf-8")
    original = content
    content = fix_whitespace(content)
    if "class" in spec:
        content = replace_class_javadoc(content, spec["class"])
    for field_pat, comment in spec.get("fields", {}).items():
        if field_pat in content and comment not in content:
            content = add_field_comment(content, field_pat, comment)
    for method_prefix, javadoc in spec.get("methods", {}).items():
        if method_prefix in content:
            content = insert_method_javadoc(content, method_prefix, javadoc)
    if content != original:
        path.write_text(content, encoding="utf-8", newline="\n")
        return True
    return False


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else CACHE_ROOT
    if not root.is_absolute():
        root = Path.cwd() / root
    modified = []
    for p in sorted(root.rglob("*.java")):
        rel = p.relative_to(root).as_posix()
        if rel in DOCS:
            if process_file(p, DOCS[rel]):
                modified.append(rel)
    print(f"Modified {len(modified)} files", file=sys.stderr)
    for m in modified:
        print(m)
    return 0


if __name__ == "__main__":
    sys.exit(main())
