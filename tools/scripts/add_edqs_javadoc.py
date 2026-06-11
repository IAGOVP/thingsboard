#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to Entity Data Query Service (EDQS) sources.
Covers edqs/, common/edqs/, common/data/edqs/, queue/edqs/, and application service/edqs/.
Includes @param, @return, @throws, and HTTP endpoint paths for REST controllers.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from add_dao_javadoc import (  # noqa: E402
    DECL_LINE_RE,
    INNER_SKIP_NAMES as DAO_INNER_SKIP,
    MethodInfo,
    add_all_class_javadocs,
    class_body_bounds_at,
    dao_insert_text_at,
    dao_method_doc_start,
    detect_kind,
    find_class_methods,
    fix_unindented_method_javadocs,
    format_javadoc,
    format_return,
    humanize,
    insert_text_at,
    is_at_class_method_level,
    javadoc_block_before,
    javadoc_is_complete,
    method_description,
    method_javadoc_needs_fixup,
    method_line_indent,
    param_description,
    split_license_and_body,
    throws_description,
)
from add_service_javadoc import (  # noqa: E402
    CLASS_METHOD_RE,
    INTERFACE_METHOD_RE,
    find_insert_position,
    parse_params,
    extract_throws,
)

INNER_SKIP_NAMES = DAO_INNER_SKIP | frozenset({"main"})

EDQS_ROOTS = [
    Path("edqs/src/main/java"),
    Path("common/edqs/src/main/java"),
    Path("common/queue/src/main/java/org/thingsboard/server/queue/edqs"),
    Path("application/src/main/java/org/thingsboard/server/service/edqs"),
    Path("common/data/src/main/java/org/thingsboard/server/common/data/edqs"),
]

CLASS_REQUEST_MAPPING = re.compile(
    r'@RequestMapping\s*\(\s*(?:value\s*=\s*)?["\']([^"\']*)["\']'
)
METHOD_MAPPING = re.compile(
    r"@(Get|Post|Put|Delete|Patch)Mapping\s*\(\s*(?:value\s*=\s*)?[\"']([^\"']*)[\"']"
)

EDQS_CLASS_DOCS: dict[str, list[str]] = {
    "ThingsboardEdqsApplication": [
        "Spring Boot entry point for the Entity Data Query Service (EDQS) microservice.",
        "",
        "<p>Loads {@code edqs.yml}, enables Kafka queue components, and starts "
        "{@link org.thingsboard.server.edqs.processor.EdqsProcessor} consumers for in-memory entity indexing.",
    ],
    "EdqsController": [
        "HTTP readiness probe for the EDQS microservice.",
        "",
        "<p>Business entity queries are served over Kafka ({@link org.thingsboard.server.edqs.processor.EdqsProcessor}), "
        "not REST. This controller exposes {@code GET /api/edqs/ready} for Kubernetes/Docker health checks.",
    ],
    "EdqsProcessor": [
        "Core EDQS worker: consumes entity change events from Kafka and answers entity queries.",
        "",
        "<p>Updates {@link org.thingsboard.server.edqs.repo.EdqsRepository} from {@link org.thingsboard.server.common.data.edqs.EdqsEvent} "
        "messages and handles request-response {@link org.thingsboard.server.common.data.edqs.query.EdqsRequest} from tb-core.",
    ],
    "EdqsProducer": [
        "Publishes EDQS sync events from tb-core into the EDQS Kafka topic.",
        "",
        "<p>Used when entities, attributes, relations, or latest telemetry change and the SQL-based "
        "entity query path is disabled in favor of EDQS.",
    ],
    "EdqsRepository": [
        "Tenant-scoped in-memory entity index API.",
        "",
        "<p>Applies {@link org.thingsboard.server.common.data.edqs.EdqsEvent} updates and executes "
        "{@link org.thingsboard.server.common.data.query.EntityDataQuery} / "
        "{@link org.thingsboard.server.common.data.query.EntityCountQuery} without PostgreSQL.",
    ],
    "DefaultEdqsRepository": [
        "Default {@link EdqsRepository} with one {@link org.thingsboard.server.edqs.repo.TenantRepo} per tenant.",
        "",
        "<p>Evicts tenant indexes on delete events or Kafka partition reassignment; supports OOM recovery via {@link #clear()}.",
    ],
    "TenantRepo": [
        "Per-tenant in-memory entity graph and query executor.",
        "",
        "<p>Stores entities by type, relations, attributes, and latest telemetry; delegates filter evaluation to "
        "{@link org.thingsboard.server.edqs.query.processor.EntityQueryProcessorFactory}.",
    ],
    "EdqsStateService": [
        "Persists and replays EDQS Kafka partition state for crash recovery.",
        "",
        "<p>Tracks which tenants are assigned to each consumer partition and exposes {@link #isReady()} for readiness probes.",
    ],
    "LocalEdqsStateService": [
        "File-based EDQS state store for single-node or development deployments.",
    ],
    "KafkaEdqsStateService": [
        "Kafka-backed EDQS state topic consumer/producer for clustered EDQS nodes.",
    ],
    "EdqsPartitionService": [
        "Resolves Kafka partition ownership for tenants (TENANT vs NONE partitioning strategy).",
    ],
    "EntityQueryProcessorFactory": [
        "Maps {@link org.thingsboard.server.common.data.query.EntityFilter} types to concrete query processors.",
    ],
    "EntityQueryProcessor": [
        "Executes one entity filter against a {@link org.thingsboard.server.edqs.repo.TenantRepo} "
        "with {@link org.thingsboard.server.common.data.permission.QueryContext} permission checks.",
    ],
    "EdqsMapper": [
        "Serialization contract for EDQS Kafka payloads (Protobuf/JSON to {@link org.thingsboard.server.common.data.edqs.EdqsObject}).",
    ],
    "DefaultEdqsMapper": [
        "Default {@link EdqsMapper} using Jackson and Protobuf for EDQS Kafka messages.",
    ],
    "KeyDictionary": [
        "Interns attribute and telemetry key strings to integer ids to reduce memory in the EDQS index.",
    ],
    "VersionsStore": [
        "Dedupes out-of-order entity updates using monotonic version numbers from EDQS events.",
    ],
    "EdqsRocksDb": [
        "RocksDB wrapper for EDQS local persistence (state snapshots, versions, key dictionary).",
    ],
    "RepositoryUtils": [
        "Sorting, filtering, and entity-type helpers used by {@link org.thingsboard.server.edqs.repo.TenantRepo} queries.",
    ],
    "EdqsListener": [
        "Spring application listener on tb-core that forwards entity lifecycle changes to EDQS sync.",
    ],
    "EdqsSyncService": [
        "Service API for synchronizing entity changes from tb-core to EDQS (Kafka or local).",
    ],
    "DefaultEdqsService": [
        "Routes entity data/count queries to EDQS when enabled, otherwise falls back to SQL DAO.",
    ],
    "DefaultEdqsApiService": [
        "REST-layer facade that delegates entity queries to {@link DefaultEdqsService}.",
    ],
    "EdqsComponent": [
        "Spring configuration marker for EDQS queue beans (producer, consumer factory, executors).",
    ],
    "KafkaEdqsComponent": [
        "Kafka implementation of {@link EdqsComponent} for clustered EDQS deployments.",
    ],
    "EdqsQueueFactory": [
        "Factory for EDQS Kafka topics, producers, and request-response templates.",
    ],
    "EdqsConfig": [
        "EDQS Spring configuration properties ({@code queue.edqs.*}).",
    ],
    "EdqsRequest": [
        "Kafka request payload from tb-core asking EDQS to run an entity count or data query.",
    ],
    "EdqsResponse": [
        "Kafka response payload with {@link org.thingsboard.server.common.data.edqs.query.QueryResult} rows or counts.",
    ],
    "EdqsEvent": [
        "Kafka event describing create/update/delete of an indexed entity, relation, attribute, or latest TS value.",
    ],
    "QueryResult": [
        "One entity row returned by EDQS with selected fields, attributes, and latest telemetry keys.",
    ],
}

EDQS_METHOD_DOCS: dict[str, str] = {
    "isReady": "Returns true when all assigned Kafka partitions have been restored and the index is queryable.",
    "processEvent": "Applies create/update/delete of an entity, relation, attribute, or latest telemetry key in the index.",
    "countEntitiesByQuery": "Returns entity count for the filter without loading full entity rows.",
    "findEntityDataByQuery": "Returns a page of entities matching filter, sort, and selected keys.",
    "handle": "Kafka request-response handler: parses EdqsRequest JSON and returns EdqsResponse.",
    "create": "Selects the query processor implementation for the query's entity filter type.",
    "clearIf": "Removes tenant repos matching the predicate (e.g. lost Kafka partitions).",
    "clear": "Clears all tenant indexes (used on OOM recovery or full resync).",
    "init": "Starts Kafka consumers and wires partition/state services.",
    "destroy": "Stops consumers and releases in-memory indexes and thread pools.",
    "stop": "Shuts down EDQS consumers and flushes pending state.",
    "onPartitionChangeEvent": "Rebalances tenant repos when Kafka partition assignment changes.",
    "sync": "Publishes an entity change event to the EDQS Kafka topic.",
    "findEntityDataByQueryInternal": "Internal query entry point with optional permission bypass for system jobs.",
}

EDQS_PARAM_HINTS: dict[str, str] = {
    "tenantId": "tenant that owns the indexed entities",
    "customerId": "customer scope for permission filtering (may be null)",
    "query": "entity count or data query with filter, sort, and key selections",
    "event": "EDQS create/update/delete event from Kafka",
    "ignorePermissionCheck": "when true, skips customer/user permission filtering (system use only)",
    "predicate": "tenant id predicate selecting which repos to evict",
    "filter": "entity filter definition (type, relations, search text, etc.)",
    "ctx": "query permission context (user, customer, authority)",
    "repo": "tenant-scoped in-memory index to query",
    "request": "EDQS Kafka request payload",
    "response": "EDQS Kafka response payload to populate",
    "msg": "Kafka queue message wrapper",
    "partition": "Kafka topic partition info",
}


def edqs_area(path: Path) -> str:
    posix = path.as_posix()
    if posix.startswith("edqs/") or "/thingsboard/edqs/" in posix.replace("\\", "/"):
        return "microservice"
    if "/common/edqs/" in posix:
        return "core"
    if "/queue/edqs/" in posix:
        return "queue"
    if "/service/edqs/" in posix:
        return "tb-core"
    if "/common/data/" in posix and "/edqs/" in posix:
        return "data"
    return "edqs"


def edqs_subpackage(path: Path) -> str:
    posix = path.as_posix()
    for fragment, hint in {
        "/edqs/processor/": "Kafka consumer and query handler",
        "/edqs/repo/": "in-memory tenant index",
        "/edqs/state/": "partition state and recovery",
        "/edqs/query/processor/": "entity filter query processors",
        "/edqs/data/": "in-memory entity projections",
        "/edqs/util/": "EDQS utilities (RocksDB, mapping, versions)",
        "/edqs/fields/": "lightweight entity field DTOs for EDQS",
    }.items():
        if fragment in posix:
            return hint
    return ""


def class_description(name: str, kind: str, area: str, body: str, path: Path) -> list[str]:
    if name in EDQS_CLASS_DOCS:
        return EDQS_CLASS_DOCS[name]
    hint = edqs_subpackage(path)
    ctx = f"EDQS {area}" + (f" — {hint}" if hint else "")

    if name.endswith("QueryProcessor"):
        filt = humanize(name.replace("QueryProcessor", ""))
        return [
            f"EDQS query processor for {filt} entity filters.",
            "",
            "<p>Evaluates {@link org.thingsboard.server.common.data.query.EntityFilter} against a "
            f"{{@link org.thingsboard.server.edqs.repo.TenantRepo}} ({ctx}).",
        ]
    if name.startswith("Abstract") and "QueryProcessor" in name:
        return [f"Base class for EDQS {humanize(name[8:])} query processors ({ctx})."]
    if name.endswith("Data") and kind == "class" and "/edqs/data/" in path.as_posix():
        return [
            f"In-memory EDQS projection of {humanize(name[:-4])} entity fields.",
            "",
            "<p>Updated from {@link org.thingsboard.server.common.data.edqs.EdqsEvent} and used during query execution.",
        ]
    if name.endswith("DataPoint"):
        return [f"Typed attribute or latest-TS value stored in the EDQS index ({humanize(name)})."]
    if name.endswith("Fields") and kind in ("class", "interface"):
        return [f"Lightweight {humanize(name[:-6])} field selection for EDQS queries and events ({ctx})."]
    if name.endswith("Service") and kind == "interface":
        return [f"EDQS service contract for {humanize(name[:-7])} ({ctx})."]
    if name.startswith("Default") and kind == "class":
        return [f"Default EDQS implementation of {humanize(name[7:])} ({ctx})."]
    if name.endswith("Test"):
        return [f"Unit test for EDQS {humanize(name.replace('Test', ''))} ({ctx})."]
    if kind == "interface":
        return [f"{humanize(name)} contract ({ctx})."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values used by EDQS ({ctx})."]
    if "@RestController" in body:
        return [f"REST controller for EDQS {humanize(name)} ({ctx})."]
    if "@Service" in body or "@Component" in body:
        return [f"Spring component for EDQS {humanize(name)} ({ctx})."]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    if class_name in EDQS_CLASS_DOCS:
        return len(doc) < 80
    thin = (
        "EDQS component:",
        "contract (EDQS",
        "Processes edqs",
        "Tenant-scoped in-memory index API: apply",
        "without hitting SQL.",
    )
    if any(t in doc for t in thin) and "<p>" not in doc and "@param" not in doc:
        return True
    if len(doc.strip()) < 70:
        return True
    return False


def add_class_javadoc_at(
    body: str, decl_match: re.Match, class_name: str, kind: str, area: str, path: Path, force: bool
) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, area, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not force and not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_edqs_class_javadocs(body: str, area: str, path: Path, force: bool) -> tuple[str, int]:
    count = 0
    for m in reversed(list(DECL_LINE_RE.finditer(body))):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, area, path, force)
        if changed:
            count += 1
    return body, count


def http_endpoint_for_method(body: str, sig_start: int, class_base: str) -> str | None:
    region_start = max(0, sig_start - 800)
    region = body[region_start:sig_start]
    m = METHOD_MAPPING.search(region)
    if not m:
        return None
    verb, sub = m.group(1).upper(), m.group(2) or ""
    base = class_base.rstrip("/")
    sub = sub if sub.startswith("/") else ("/" + sub if sub else "")
    path = re.sub(r"/+", "/", f"{base}{sub}") if base else sub or "/"
    return f"{verb} {{@code {path}}}"


def class_request_mapping(body: str) -> str:
    m = CLASS_REQUEST_MAPPING.search(body)
    return m.group(1) if m else ""


def edqs_param_description(name: str, type_hint: str) -> str:
    if name in EDQS_PARAM_HINTS:
        return EDQS_PARAM_HINTS[name]
    return param_description(name, type_hint)


def strip_all_javadocs_before(body: str, pos: int) -> tuple[str, int]:
    while True:
        existing = javadoc_block_before(body, pos)
        if not existing:
            break
        start, end = existing
        body = body[:start] + body[end:]
        pos = start
    return body, pos


def edqs_method_javadoc(body: str, method: MethodInfo, class_base: str, is_controller: bool) -> str:
    raw = method_line_indent(body, method.sig_start) or method.indent or "    "
    indent = "    " if len(raw.replace("\t", "    ")) > 4 else raw
    desc = EDQS_METHOD_DOCS.get(method.name, method_description(method.name))
    lines = [desc, ""]
    if is_controller:
        endpoint = http_endpoint_for_method(body, method.sig_start, class_base)
        if endpoint:
            lines.append(f"<p>HTTP: {endpoint}")
            lines.append("")
    for p in method.params:
        lines.append(f"@param {p.name} {edqs_param_description(p.name, p.type_hint)}")
    lines.append(f"@return {format_return(method.return_type)}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    else:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(indent, lines)


def method_javadoc_is_thin(doc: str) -> bool:
    if "@return" not in doc or "@throws" not in doc:
        return True
    for thin in ("Process event.", "Count entities by query.", "Find entity data by query.", "Clear if.", "Clear."):
        if thin in doc:
            return True
    return False


def add_edqs_method_javadocs(body: str, decl_match: re.Match, class_name: str, kind: str) -> tuple[str, int]:
    count = 0
    class_start, _, _ = class_body_bounds_at(body, decl_match)
    class_base = class_request_mapping(body)
    is_controller = "@RestController" in body
    while True:
        pending: MethodInfo | None = None
        for method in reversed(find_class_methods(body, class_name, kind, decl_match)):
            if method.doc_start < class_start:
                continue
            existing = javadoc_block_before(body, method.doc_start)
            if existing:
                start, end = existing
                doc = body[start:end]
                if (
                    javadoc_is_complete(doc, method)
                    and not method_javadoc_needs_fixup(body, method.doc_start, method.sig_start)
                    and not method_javadoc_is_thin(doc)
                    and (not is_controller or "HTTP:" in doc)
                ):
                    continue
            pending = method
            break
        if pending is None:
            break
        pos = max(pending.doc_start, class_start + 1)
        body, pos = strip_all_javadocs_before(body, pos)
        new_doc = edqs_method_javadoc(body, pending, class_base, is_controller)
        body = dao_insert_text_at(body, pos, new_doc)
        count += 1
    return body, count


def process_file(path: Path, dry_run: bool, force_class: bool) -> bool:
    if path.name == "package-info.java":
        return False
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    if not DECL_LINE_RE.search(body):
        return False
    area = edqs_area(path)
    changed = False
    body, cc = add_all_edqs_class_javadocs(body, area, path, force_class)
    if cc:
        changed = True
    for ordinal, decl_match in enumerate(DECL_LINE_RE.finditer(body)):
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_edqs_method_javadocs(body, decl_match, class_name, kind)
        if mc:
            changed = True
    body, fc = fix_unindented_method_javadocs(body)
    if fc:
        changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def collect_files(roots: list[Path]) -> list[Path]:
    files: list[Path] = []
    seen: set[str] = set()
    for root in roots:
        if not root.exists():
            continue
        for path in sorted(root.rglob("*.java")):
            if "src/main/java" not in path.as_posix():
                continue
            if path.name == "package-info.java":
                continue
            key = str(path.resolve())
            if key not in seen:
                seen.add(key)
                files.append(path)
    return files


def main() -> int:
    parser = argparse.ArgumentParser(description="Add detailed Javadoc to EDQS sources")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--force-class", action="store_true", help="Replace thin class Javadoc")
    args = parser.parse_args()
    totals: dict[str, int] = {k: 0 for k in ("microservice", "core", "queue", "tb-core", "data")}
    for path in collect_files(EDQS_ROOTS):
        if process_file(path, args.dry_run, args.force_class):
            area = edqs_area(path)
            totals[area] = totals.get(area, 0) + 1
    total = sum(totals.values())
    for area in sorted(totals.keys()):
        if totals[area]:
            print(f"{area}: {totals[area]} files modified", file=sys.stderr)
    print(f"Done: {total} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
