#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to dao/src/main Java sources.
Includes @param, @return, and @throws tags. Skips tests and package-info.java.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from add_service_javadoc import (  # noqa: E402
    CLASS_METHOD_RE,
    DECL_LINE_RE,
    INTERFACE_METHOD_RE,
    SKIP_METHOD_NAMES,
    MethodInfo,
    class_body_bounds,
    detect_kind,
    extract_throws,
    find_insert_position,
    find_method_doc_insertion_point,
    find_methods,
    format_javadoc,
    format_return,
    humanize,
    insert_text_at,
    javadoc_block_before,
    javadoc_is_complete,
    method_description,
    method_javadoc,
    param_description,
    parse_params,
    split_license_and_body,
    throws_description,
)

DAO_PACKAGE_CONTEXT: dict[str, str] = {
    "alarm": "alarm persistence, comments, and alarm-type caching",
    "asset": "asset and asset-profile DAO services and caches",
    "attributes": "server-side attribute key-value storage and caching",
    "audit": "audit log persistence and log-level configuration",
    "cf": "calculated-field definitions and evaluation state",
    "component": "rule-engine component descriptor registry",
    "config": "Spring JPA/Cassandra/Timescale DAO configuration beans",
    "customer": "customer entity persistence and caching",
    "dashboard": "dashboard metadata, titles, and assignment",
    "device": "devices, credentials, profiles, and connectivity",
    "domain": "tenant domain and OAuth2 client bindings",
    "edge": "edge instances, events, sessions, and synchronization",
    "entity": "generic entity services, counts, and DAO registry",
    "event": "lifecycle and debug event persistence",
    "exception": "DAO-layer checked and runtime exceptions",
    "housekeeper": "TTL cleanup and background housekeeping tasks",
    "job": "background job persistence and scheduling metadata",
    "mobile": "mobile apps, bundles, and QR code settings",
    "model": "JPA/Cassandra entity mappings (database row models)",
    "notification": "notification templates, targets, rules, and delivery requests",
    "nosql": "Cassandra async DAO base classes",
    "oauth2": "OAuth2 client registration templates",
    "ota": "OTA firmware/software package metadata and data cache",
    "pat": "personal access tokens (API keys)",
    "relation": "entity-to-entity relation graph persistence",
    "resource": "tenant/system resources (images, JS modules, etc.)",
    "rpc": "device RPC request persistence",
    "rule": "rule chains, nodes, and node state",
    "settings": "system and tenant admin settings",
    "sql": "JPA repositories and PostgreSQL DAO implementations",
    "sqlts": "SQL/Timescale time-series key-value storage",
    "tenant": "tenants, tenant profiles, and profile caching",
    "timeseries": "Cassandra time-series DAO and latest-value caches",
    "usagerecord": "tenant API usage state and metering",
    "user": "users, credentials, and user settings",
    "util": "DAO utilities (KV conversion, rate executors, JSON mapping)",
    "widget": "widget types and widget bundles",
    "cache": "DAO-layer cache executor and helpers",
    "aspect": "database call statistics and method profiling",
    "eventsourcing": "entity lifecycle action cause tracking",
    "service": "shared DAO validators, removers, and constraints",
}

DAO_SUBPACKAGE_HINTS: dict[str, str] = {
    "/dao/sql/": "JPA/PostgreSQL persistence layer",
    "/dao/sqlts/": "time-series SQL/Timescale persistence",
    "/dao/model/sql/": "JPA entity row mappings for PostgreSQL",
    "/dao/model/sqlts/": "time-series table entity mappings",
    "/dao/timeseries/": "Cassandra telemetry and latest-value DAO",
    "/dao/nosql/": "Cassandra async DAO infrastructure",
    "/dao/service/validator/": "entity data validators invoked before save",
    "/dao/sql/query/": "dynamic entity and alarm query builders",
    "/dao/entity/count/": "entity count caching for rate limits",
    "/dao/edge/stats/": "edge message counters and statistics",
}


def dao_package_key(path: Path) -> str:
    posix = path.as_posix()
    m = re.search(r"/dao/([^/]+)/", posix)
    if m:
        return m.group(1)
    if "/dao/Dao.java" in posix or path.name == "Dao.java":
        return "core"
    if "/dao/TenantEntityDao.java" in posix:
        return "core"
    return ""


def subpackage_hint(path: Path) -> str:
    posix = path.as_posix()
    for fragment, hint in DAO_SUBPACKAGE_HINTS.items():
        if fragment in posix:
            return hint
    return ""


def class_description(name: str, kind: str, pkg: str, body: str, path: Path) -> list[str]:
    hint = subpackage_hint(path)
    ctx = DAO_PACKAGE_CONTEXT.get(pkg, "ThingsBoard DAO layer")
    if hint:
        ctx = f"{hint} ({ctx})"

    if name == "Dao":
        return [
            "Generic tenant-scoped CRUD contract for a single domain entity type.",
            "",
            "<p>Implemented by {@code Jpa*Dao} (PostgreSQL) or Cassandra DAOs for telemetry-related types.",
            "Provides synchronous and asynchronous find/exists, save, remove, and batch id scan operations.",
        ]
    if name == "TenantEntityDao":
        return [
            "DAO contract for tenant-owned entities with name uniqueness checks.",
            "",
            "<p>Extends {@link Dao} with tenant-scoped name lookup used by entity services and validators.",
        ]
    if name == "CacheExecutorService":
        return [
            "Dedicated thread pool for asynchronous cache operations in the DAO layer.",
            "",
            "<p>Pool size is configured via {@code cache.maximumPoolSize}. Used by Redis and Caffeine "
            "cache implementations to avoid blocking request threads on cache I/O.",
        ]
    if name.startswith("Base") and name.endswith("Service"):
        domain = humanize(name[4:-7])
        return [
            f"Default DAO-layer service implementation for {domain}.",
            "",
            f"<p>Coordinates validation, caching, cluster events, and {{@code *Dao}} persistence ({ctx}).",
        ]
    if name.startswith("Jpa") and name.endswith("Dao"):
        iface = name[3:]
        return [
            f"JPA/PostgreSQL implementation of {humanize(iface)}.",
            "",
            "<p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.",
        ]
    if name.endswith("Repository") and kind == "interface":
        return [
            f"Spring Data JPA repository for {humanize(name.replace('Repository', ''))} entities.",
            "",
            "<p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.",
        ]
    if name.endswith("ServiceImpl"):
        return [
            f"Spring {{@code @Service}} implementing the {humanize(name[:-11])} DAO API.",
            "",
            f"<p>Delegates to {{@code *Dao}} implementations and manages cache eviction ({ctx}).",
        ]
    if name.endswith("Dao") and kind == "interface":
        return [
            f"Persistence contract for {humanize(name[:-3])}.",
            "",
            f"<p>Implemented by {{@code Jpa*Dao}} or Cassandra DAO classes ({ctx}).",
        ]
    if name.endswith("Entity") and kind == "class":
        return [
            f"JPA/Cassandra row model for {humanize(name.replace('Entity', ''))}.",
            "",
            "<p>Maps database columns to domain objects via {@code toData()} conversion.",
        ]
    if name.endswith("CacheKey"):
        return [f"Serializable cache key for {humanize(name.replace('CacheKey', ''))} entries ({ctx})."]
    if name.endswith("EvictEvent"):
        return [
            f"Cache eviction event published when {humanize(name.replace('EvictEvent', ''))} data changes.",
            "",
            "<p>Consumed by cache listeners to invalidate stale entries cluster-wide.",
        ]
    if name.endswith("CaffeineCache") or name.endswith("RedisCache"):
        backend = "in-process Caffeine" if "Caffeine" in name else "distributed Redis"
        return [
            f"{backend} cache for {humanize(name.replace('CaffeineCache', '').replace('RedisCache', ''))} ({ctx}).",
            "",
            "<p>Implements {@link org.thingsboard.server.cache.TbTransactionalCache} with DAO-specific key types.",
        ]
    if name.endswith("DataValidator"):
        return [
            f"Validates {humanize(name.replace('DataValidator', ''))} entities before persistence.",
            "",
            "<p>Enforces constraints, uniqueness, and referential integrity at the DAO layer.",
        ]
    if name.endswith("Exception"):
        return [f"DAO exception for {humanize(name)} ({ctx})."]
    if name.endswith("Config") or name.endswith("Configuration"):
        return [
            f"Spring configuration for {humanize(name.replace('Config', '').replace('Configuration', ''))} DAO beans.",
            "",
            "<p>Registers entity managers, repositories, and datasource routing.",
        ]
    if kind == "interface":
        return [f"{humanize(name)} contract ({ctx})."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values used in {ctx}."]
    if "@Service" in body or "@Component" in body:
        return [f"Spring component for {humanize(name)} ({ctx})."]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    thin_markers = (
        "@author",
        "contract.",
        "Cache executor service.",
        "Get thread poll size.",
    )
    if any(m in doc for m in thin_markers):
        return True
    if len(doc.strip()) < 60:
        return True
    if doc.count(".") <= 1 and "<p>" not in doc:
        return True
    return False


def add_class_javadoc_at(
    body: str, decl_match: re.Match, class_name: str, kind: str, pkg: str, path: Path, force: bool
) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, pkg, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not force and not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_class_javadocs(body: str, pkg: str, path: Path, force: bool) -> tuple[str, int]:
    count = 0
    matches = list(DECL_LINE_RE.finditer(body))
    for m in reversed(matches):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, pkg, path, force)
        if changed:
            count += 1
    return body, count


def dao_param_description(name: str, type_hint: str) -> str:
    hints = {
        "tenantId": "tenant that owns the entity or operation",
        "entityId": "target entity identifier",
        "deviceId": "target device identifier",
        "customerId": "target customer identifier",
        "userId": "target user identifier",
        "pageLink": "pagination, sort, and text-search parameters",
        "entityType": "entity type discriminator",
        "entity": "domain entity to persist or validate",
        "id": "entity UUID primary key",
        "idOffset": "cursor for batch id scan (exclusive lower bound)",
        "limit": "maximum number of records to return",
        "name": "entity or attribute name",
        "key": "attribute or cache key",
        "scope": "attribute scope (SERVER_SCOPE, SHARED_SCOPE, etc.)",
        "startTs": "interval start timestamp (epoch ms)",
        "endTs": "interval end timestamp (epoch ms)",
        "query": "filter and sort query definition",
    }
    if name in hints:
        return hints[name]
    return param_description(name, type_hint)


def method_line_indent(body: str, sig_start: int) -> str:
    line_start = body.rfind("\n", 0, sig_start) + 1
    line = body[line_start:sig_start]
    return line[: len(line) - len(line.lstrip(" \t"))]


def method_javadoc_needs_fixup(body: str, doc_start: int, sig_start: int) -> bool:
    existing = javadoc_block_before(body, doc_start)
    if not existing:
        return False
    start, _ = existing
    method_indent = method_line_indent(body, sig_start)
    if method_indent and not body[start:].startswith(f"{method_indent}/**"):
        return True
    return False


def dao_method_javadoc(body: str, method: MethodInfo) -> str:
    indent = method_line_indent(body, method.sig_start) or method.indent or "    "
    lines = [method_description(method.name), ""]
    for p in method.params:
        lines.append(f"@param {p.name} {dao_param_description(p.name, p.type_hint)}")
    lines.append(f"@return {format_return(method.return_type)}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    else:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(indent, lines)


def class_decl_match(body: str, class_name: str, occurrence: int = 0) -> re.Match | None:
    matches = [m for m in DECL_LINE_RE.finditer(body) if m.group("name") == class_name]
    return matches[occurrence] if occurrence < len(matches) else None


def dao_method_doc_start(body: str, sig_start: int, class_start: int) -> int:
    """Insertion point immediately above the method, not above the type-level Javadoc."""
    method_line_start = body.rfind("\n", 0, sig_start) + 1
    insert_at = method_line_start
    pos = method_line_start
    min_pos = class_start + 1
    while pos > min_pos:
        prev_nl = body.rfind("\n", 0, pos - 1)
        if prev_nl < min_pos - 1:
            break
        prev_start = prev_nl + 1
        prev_line = body[prev_start:pos].strip()
        if not prev_line:
            insert_at = prev_start
            pos = prev_start
            continue
        if prev_line.startswith("@"):
            insert_at = prev_start
            pos = prev_start
            continue
        if prev_line.endswith(";") or prev_line.endswith("{") or prev_line.endswith("}"):
            break
        if re.match(r"(public|protected|private)\s+(class|interface|enum|record)\b", prev_line):
            break
        break
    return insert_at


def class_body_bounds_at(body: str, decl_match: re.Match) -> tuple[int, int, int]:
    brace = body.find("{", decl_match.end() - 1)
    if brace < 0:
        return 0, len(body), -1
    depth = 1
    i = brace + 1
    while i < len(body) and depth > 0:
        if body[i] == "{":
            depth += 1
        elif body[i] == "}":
            depth -= 1
            if depth == 0:
                return brace + 1, i, brace
        i += 1
    return brace + 1, len(body), brace


INNER_SKIP_NAMES = SKIP_METHOD_NAMES | frozenset(
    {"new", "throw", "super", "assert", "catch", "case", "instanceof"}
)


def is_at_class_method_level(body: str, abs_pos: int, class_open_brace: int, class_indent: str) -> bool:
    """Reject matches inside default-method bodies or nested blocks."""
    line_start = body.rfind("\n", 0, abs_pos) + 1
    line = body[line_start:abs_pos]
    spaces = len(line) - len(line.lstrip(" \t"))
    class_spaces = len(class_indent.expandtabs(4))
    if spaces > class_spaces + 4:
        return False
    depth = 0
    for i in range(class_open_brace, abs_pos):
        if body[i] == "{":
            depth += 1
        elif body[i] == "}":
            depth -= 1
    return depth <= 1


def dao_insert_text_at(body: str, pos: int, text: str) -> str:
    prefix = body[:pos]
    if prefix and not prefix.endswith("\n"):
        text = "\n" + text
    return prefix + text + body[pos:]


def find_class_methods(body: str, class_name: str, kind: str, decl_match: re.Match) -> list[MethodInfo]:
    methods: list[MethodInfo] = []
    class_start, class_end, class_open = class_body_bounds_at(body, decl_match)
    region = body[class_start:class_end]
    pattern = INTERFACE_METHOD_RE if kind == "interface" else CLASS_METHOD_RE
    class_indent = decl_match.group("indent")

    for m in pattern.finditer(region):
        method_name = m.group("name")
        if method_name == class_name or method_name in INNER_SKIP_NAMES:
            continue
        ret_type = re.sub(
            r"\b(static|final|synchronized|native|abstract|default)\s+",
            "",
            m.group("ret"),
        ).strip()
        if not ret_type or ret_type.startswith("@"):
            continue
        if ret_type in ("throw", "return", "case"):
            continue
        name_idx = region.find(f"{method_name}(", m.start())
        if name_idx < 0:
            continue
        sig_start = class_start + name_idx
        sig_line_start = body.rfind("\n", 0, sig_start) + 1
        if class_open >= 0 and not is_at_class_method_level(body, sig_line_start, class_open, class_indent):
            continue
        if "(" in body[sig_line_start:sig_start]:
            continue
        paren_start = class_start + m.end() - 1
        depth = 0
        j = paren_start
        while j < class_end:
            if body[j] == "(":
                depth += 1
            elif body[j] == ")":
                depth -= 1
                if depth == 0:
                    break
            j += 1
        if j >= class_end:
            continue
        param_text = body[paren_start + 1 : j]
        after_paren = body[j + 1 : j + 200]
        doc_start = dao_method_doc_start(body, sig_line_start, class_start)
        methods.append(
            MethodInfo(
                doc_start=doc_start,
                sig_start=sig_line_start,
                indent=m.group("indent"),
                name=method_name,
                return_type=ret_type,
                params=parse_params(param_text),
                throws=extract_throws(after_paren),
            )
        )
    return methods


def add_dao_method_javadocs(body: str, decl_match: re.Match, class_name: str, kind: str) -> tuple[str, int]:
    count = 0
    class_start, _, _ = class_body_bounds_at(body, decl_match)
    while True:
        pending: MethodInfo | None = None
        for method in reversed(find_class_methods(body, class_name, kind, decl_match)):
            if method.doc_start < class_start:
                continue
            existing = javadoc_block_before(body, method.doc_start)
            if existing:
                start, end = existing
                if javadoc_is_complete(body[start:end], method) and not method_javadoc_needs_fixup(
                    body, method.doc_start, method.sig_start
                ):
                    continue
            pending = method
            break
        if pending is None:
            break
        pos = max(pending.doc_start, class_start + 1)
        new_doc = dao_method_javadoc(body, pending)
        existing = javadoc_block_before(body, pos)
        if existing:
            start, end = existing
            body = dao_insert_text_at(body[:start] + body[end:], start, new_doc.rstrip("\n") + "\n")
        else:
            body = dao_insert_text_at(body, pos, new_doc)
        count += 1
    return body, count


def fix_unindented_method_javadocs(body: str) -> tuple[str, bool]:
    """Indent method Javadoc blocks placed at column 0 after a method signature line."""

    def indent_block(match: re.Match) -> str:
        prefix = match.group(1)
        block = match.group(2)
        if block.startswith("    /**"):
            return prefix + block + "\n"
        indented = "\n".join(("    " + line) if line else line for line in block.split("\n"))
        return prefix + indented + "\n"

    updated, n = re.subn(
        r"(?ms)^(    \S[^\n]*;\n)(/\*\*.*?^ \*/)\n",
        indent_block,
        body,
    )
    return updated, n > 0


def process_file(path: Path, dry_run: bool, force_class: bool) -> bool:
    if path.name == "package-info.java":
        return False
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    if not DECL_LINE_RE.search(body):
        return False
    pkg = dao_package_key(path)
    changed = False
    body, cc = add_all_class_javadocs(body, pkg, path, force_class)
    if cc:
        changed = True
    decl_count = len(list(DECL_LINE_RE.finditer(body)))
    for ordinal in range(decl_count):
        decls = list(DECL_LINE_RE.finditer(body))
        if ordinal >= len(decls):
            break
        decl_match = decls[ordinal]
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_dao_method_javadocs(body, decl_match, class_name, kind)
        if mc:
            changed = True
    body, fc = fix_unindented_method_javadocs(body)
    if fc:
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
    parser = argparse.ArgumentParser(description="Add detailed Javadoc to dao module sources")
    parser.add_argument(
        "--base",
        type=Path,
        default=Path("dao/src/main/java"),
    )
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument(
        "--force-class",
        action="store_true",
        help="Replace thin or @author-only class Javadoc",
    )
    args = parser.parse_args()
    totals: dict[str, int] = {k: 0 for k in DAO_PACKAGE_CONTEXT}
    totals["core"] = 0
    totals["other"] = 0
    for path in collect_java_files(args.base):
        if process_file(path, args.dry_run, args.force_class):
            pkg = dao_package_key(path) or "other"
            totals[pkg] = totals.get(pkg, 0) + 1
    total = sum(totals.values())
    for pkg in sorted(totals.keys()):
        if totals[pkg]:
            print(f"{pkg}: {totals[pkg]} files modified", file=sys.stderr)
    print(f"Done: {total} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
