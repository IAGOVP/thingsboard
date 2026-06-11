#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to ThingsBoard common/ module sources.
Includes @param, @return, and @throws tags. No logic changes.
"""
from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path

# Reuse core logic from service javadoc script
sys.path.insert(0, str(Path(__file__).resolve().parent))
from add_service_javadoc import (  # noqa: E402
    DECL_LINE_RE,
    INTERFACE_METHOD_RE,
    CLASS_METHOD_RE,
    SKIP_METHOD_NAMES,
    ParamInfo,
    MethodInfo,
    split_license_and_body,
    humanize,
    detect_kind,
    format_return,
    param_description,
    throws_description,
    format_javadoc,
    find_insert_position,
    javadoc_block_before,
    method_doc_needs_fixup,
    javadoc_is_complete,
    parse_params,
    extract_throws,
    class_body_bounds,
    find_method_doc_insertion_point,
    insert_text_at,
    find_methods,
    method_javadoc,
    method_description,
)

MODULE_CONTEXT: dict[str, str] = {
    "data": "shared domain model (entities, IDs, DTOs, page types, enums)",
    "transport": "device protocol adaptors, session handling, and transport API types",
    "queue": "Kafka queue messages, producers, consumers, and routing",
    "dao-api": "DAO service interfaces consumed by the application layer",
    "edqs": "Entity Data Query Service types and query API",
    "message": "internal actor and cluster message payloads",
    "cache": "Caffeine/Redis cache abstractions and transactional cache",
    "script": "TBEL and JavaScript script engine API for rule nodes",
    "util": "shared utilities (JSON, executors, TLS, protobuf helpers)",
    "actor": "ThingsBoard actor system base types and mailbox",
    "coap-server": "embedded CoAP server for transport microservices",
    "cluster-api": "cluster discovery and partition service API",
    "stats": "platform statistics collection types",
    "proto": "Protobuf-generated and helper types for gRPC",
    "version-control": "entity version control shared types",
    "edge-api": "edge synchronization protobuf/API types",
    "discovery-api": "service discovery API",
}

SUBPACKAGE_HINTS: dict[str, str] = {
    "/common/data/id/": "typed entity identifiers (UUID wrappers)",
    "/common/data/page/": "pagination request and response types",
    "/common/data/audit/": "audit log action types",
    "/common/data/alarm/": "alarm domain model",
    "/common/data/device/": "device profiles, credentials, and transport config",
    "/common/data/edge/": "edge instance domain model",
    "/common/data/oauth2/": "OAuth2 client configuration model",
    "/common/data/notification/": "notification templates, rules, and delivery",
    "/common/data/sync/": "entity import/export and version control DTOs",
    "/common/data/cf/": "calculated field configuration and types",
    "/common/data/ai/": "AI model configuration types",
    "/transport/mqtt/": "MQTT transport adaptor and topic handling",
    "/transport/http/": "HTTP device API types",
    "/transport/coap/": "CoAP transport adaptor",
    "/transport/lwm2m/": "LwM2M transport and object model",
    "/transport/snmp/": "SNMP transport session types",
    "/queue/discovery/": "queue discovery and partition info",
    "/queue/common/": "shared queue message wrappers",
    "/cache/limits/": "rate limit cache",
    "/cache/customer/": "customer-scoped cache entries",
    "/cache/device/": "device-scoped cache entries",
    "/cache/tenant/": "tenant-scoped cache entries",
    "/cache/user/": "user session and security cache",
    "/cache/oauth2/": "OAuth2 authorization request cache",
    "/cache/ota/": "OTA package metadata cache",
    "/cache/edge/": "edge-related cache",
    "/cache/ota/": "OTA firmware cache",
    "/cache/Tb": "ThingsBoard cache transaction and eviction",
}


def module_key(path: Path) -> str:
    posix = path.as_posix()
    m = re.search(r"/common/([^/]+)/src/main/java/", posix)
    if m:
        return m.group(1)
    return ""


def subpackage_hint(path: Path) -> str:
    posix = path.as_posix()
    for fragment, hint in SUBPACKAGE_HINTS.items():
        if fragment in posix:
            return hint
    return ""


def class_description(name: str, kind: str, mod: str, body: str, path: Path) -> list[str]:
    hint = subpackage_hint(path)
    ctx = MODULE_CONTEXT.get(mod, "ThingsBoard common module")
    if hint:
        ctx = f"{hint} ({ctx})"

    if mod == "data":
        if name.endswith("Id") and kind == "class":
            entity = humanize(name[:-2])
            return [
                f"Typed UUID identifier for a {entity} entity.",
                "",
                f"<p>Implements {{@link org.thingsboard.server.common.data.id.HasId}}; used across REST, DAO, and transport layers.",
            ]
        if name.endswith("Info") and kind == "class":
            return [f"Lightweight {humanize(name[:-4])} summary without heavy relations ({ctx})."]
        if name == "EntityType":
            return ["Enumerates all platform entity types (DEVICE, ASSET, TENANT, etc.)."]
        if name.endswith("Exception"):
            return [f"Domain exception for {humanize(name)} ({ctx})."]
        if name.endswith("Request") or name.endswith("Response"):
            return [f"DTO for {humanize(name)} operations ({ctx})."]
        if kind == "enum":
            return [f"Enumerates {humanize(name)} values ({ctx})."]
        if kind == "interface":
            return [f"{humanize(name)} contract ({ctx})."]
        if name in ("Device", "Asset", "Tenant", "Customer", "User", "Dashboard", "RuleChain"):
            return [
                f"Core domain entity: {name}.",
                "",
                f"<p>Persisted entity with tenant scope; serialized to JSON for REST API and stored via DAO.",
            ]

    if mod == "cache":
        if name.endswith("Cache"):
            return [f"Cache abstraction for {humanize(name.replace('Cache', ''))} ({ctx})."]
        if "Transaction" in name:
            return [f"Transactional cache batch for atomic put/commit/rollback ({ctx})."]
        if name == "TbCacheTransaction":
            return [
                "Transactional cache operation batch (put keys, then commit or rollback).",
                "",
                "<p>Used to apply multiple cache updates atomically with the backing store.",
            ]

    if mod == "queue":
        if name.endswith("Msg") or name.endswith("Message"):
            return [f"Kafka/queue message payload: {humanize(name)} ({ctx})."]
        if name.endswith("Service") and kind == "interface":
            return [f"Queue service API for {humanize(name[:-7])} ({ctx})."]

    if mod == "transport":
        if name.endswith("Adaptor"):
            return [f"Protocol adaptor translating device payloads to platform messages ({ctx})."]
        if name.endswith("Context"):
            return [f"Transport microservice Spring context and configuration ({ctx})."]
        if "TransportService" in name:
            return ["Core transport service: sessions, telemetry publish, RPC to tb-core."]

    if mod == "dao-api":
        if name.endswith("Service") and kind == "interface":
            return [
                f"DAO service contract for {humanize(name[:-7])} persistence.",
                "",
                "<p>Implemented in the dao module; called from application services and rule engine.",
            ]

    if mod == "message":
        if name.endswith("Msg") or name.endswith("Message"):
            return [f"Internal message for actor/queue processing: {humanize(name)} ({ctx})."]

    if mod == "script":
        if "ScriptEngine" in name:
            return [f"Script execution engine API ({ctx})."]
        if name.endswith("Service") and kind == "interface":
            return [f"Script invocation service API ({ctx})."]

    if name.endswith("Service") and kind == "interface":
        return [
            f"Service contract for {humanize(name[:-7])} ({ctx}).",
            "",
            "<p>Implemented by the corresponding class in this or the dao module.",
        ]
    if name.startswith("Default") and kind == "class":
        return [f"Default implementation of {humanize(name[7:])} ({ctx})."]
    if name.endswith("Exception"):
        return [f"Exception: {humanize(name)} ({ctx})."]
    if kind == "interface":
        return [f"{humanize(name)} contract ({ctx})."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values ({ctx})."]
    if "@Service" in body or "@Component" in body:
        return [f"Spring component: {humanize(name)} ({ctx})."]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def add_class_javadoc(body: str, class_name: str, kind: str, mod: str, path: Path) -> tuple[str, bool]:
    m = DECL_LINE_RE.search(body)
    if not m:
        return body, False
    insert_pos = find_insert_position(body, m.start())
    new_doc = format_javadoc(m.group("indent"), class_description(class_name, kind, mod, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        doc_text = body[existing[0] : existing[1]]
        if len(doc_text) > 80 and "contract for" not in doc_text and "Caffeine tb cache" not in doc_text:
            return body, False
    return insert_text_at(body, insert_pos, new_doc), True


def add_method_javadocs(body: str, class_name: str, kind: str) -> tuple[str, int]:
    count = 0
    class_start, _ = class_body_bounds(body, class_name)
    while True:
        pending: MethodInfo | None = None
        for method in reversed(find_methods(body, class_name, kind)):
            if method.doc_start < class_start:
                continue
            existing = javadoc_block_before(body, method.doc_start)
            if existing:
                start, end = existing
                if javadoc_is_complete(body[start:end], method):
                    continue
            pending = method
            break
        if pending is None:
            break
        pos = max(pending.doc_start, class_start + 1)
        new_doc = method_javadoc(pending)
        existing = javadoc_block_before(body, pos)
        if existing:
            start, end = existing
            body = insert_text_at(body[:start] + body[end:], start, new_doc.rstrip("\n") + "\n")
        else:
            body = insert_text_at(body, pos, new_doc)
        count += 1
    return body, count


def process_file(path: Path, dry_run: bool, force_class: bool = False) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    m = DECL_LINE_RE.search(body)
    if not m:
        return False
    class_name = m.group("name")
    kind = detect_kind(m.group(0))
    mod = module_key(path)
    changed = False
    if force_class:
        body2, cc = add_class_javadoc(body, class_name, kind, mod, path)
    else:
        insert_pos = find_insert_position(body, m.start())
        if javadoc_block_before(body, insert_pos) and not force_class:
            body2, cc = body, False
        else:
            body2, cc = add_class_javadoc(body, class_name, kind, mod, path)
    if cc:
        changed = True
        body = body2
    body, mc = add_method_javadocs(body, class_name, kind)
    if mc:
        changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def collect_java_files(base: Path) -> list[Path]:
    files: list[Path] = []
    for path in sorted(base.rglob("*.java")):
        if "src/main/java" not in path.as_posix():
            continue
        if path.name == "package-info.java":
            continue
        files.append(path)
    return files


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--base",
        type=Path,
        default=Path("common"),
    )
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--force-class", action="store_true", help="Replace thin/generic class docs")
    args = parser.parse_args()
    totals: dict[str, int] = {k: 0 for k in MODULE_CONTEXT}
    totals["other"] = 0
    for path in collect_java_files(args.base):
        if process_file(path, args.dry_run, args.force_class):
            mod = module_key(path) or "other"
            totals[mod] = totals.get(mod, 0) + 1
    total = sum(totals.values())
    for mod in sorted(totals.keys()):
        if totals[mod]:
            print(f"{mod}: {totals[mod]} files modified", file=sys.stderr)
    print(f"Done: {total} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
