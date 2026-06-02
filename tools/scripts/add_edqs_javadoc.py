#!/usr/bin/env python3
"""Add or improve class and public-method Javadoc for EDQS Java sources."""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

DECL_LINE_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|sealed|non-sealed|static|final)\s+)*"
    r"(?:class|interface|enum|record)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

METHOD_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected)\s+)"
    r"(?:(?:static|final|synchronized)\s+)*"
    r"(?:<[^>]+>\s+)?"
    r"(?:[\w.<>,\s\[\]]+\s+)+"
    r"(?P<name>[a-z][A-Za-z0-9_]*)\s*\(",
    re.MULTILINE,
)

WEAK_DOC = re.compile(
    r"/\*\*\s*\n\s*\*\s*(?:Processes edqs|Default edqs repository|edqs repository contract|"
    r"Tenant repo|Factory for entity query processor)\.\s*\n\s*\*/",
    re.IGNORECASE,
)


def split_license_and_body(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def edqs_class_doc(name: str, kind: str) -> str:
    if name == "ThingsboardEdqsApplication":
        return (
            "Spring Boot entry point for the Entity Data Query Service (EDQS) microservice. "
            "Loads edqs.yml, scans queue/EDQS beans, and runs Kafka consumers for entity index sync and queries."
        )
    if name == "EdqsController":
        return "HTTP readiness probe for Kubernetes/load balancers; EDQS business API is Kafka-based, not REST."
    if name == "EdqsProcessor":
        return (
            "Core EDQS worker: consumes entity change events from Kafka, updates the in-memory "
            "{@link org.thingsboard.server.edqs.repo.EdqsRepository}, and answers entity data/count "
            "queries from tb-node via request-response queue."
        )
    if name == "EdqsProducer":
        return "Publishes EDQS events from core services into the EDQS Kafka topic (tb-node side)."
    if name == "EdqsRepository":
        return (
            "Tenant-scoped in-memory index API: apply {@link org.thingsboard.server.common.data.edqs.EdqsEvent} "
            "updates and execute {@link org.thingsboard.server.common.data.query.EntityDataQuery} / "
            "{@link org.thingsboard.server.common.data.query.EntityCountQuery} without hitting SQL."
        )
    if name == "DefaultEdqsRepository":
        return (
            "Default {@link EdqsRepository}: one {@link TenantRepo} per {@link org.thingsboard.server.common.data.id.TenantId}, "
            "with stats and eviction on tenant delete or partition reassignment."
        )
    if name == "TenantRepo":
        return (
            "Per-tenant in-memory entity graph: entities by type, relations, attributes/latest TS, "
            "and query execution via {@link org.thingsboard.server.edqs.query.processor.EntityQueryProcessorFactory}."
        )
    if name == "EdqsStateService":
        return (
            "Persists and replays EDQS Kafka state topic so consumers catch up after restart; "
            "tracks partition assignment and {@link #isReady()} for health checks."
        )
    if name == "LocalEdqsStateService":
        return "File-based EDQS state store for single-node or dev deployments."
    if name == "KafkaEdqsStateService":
        return "Kafka-backed EDQS state topic consumer/producer for clustered EDQS."
    if name == "EdqsPartitionService":
        return "Resolves which Kafka partition owns a tenant (TENANT vs NONE partitioning strategy)."
    if name == "EntityQueryProcessorFactory":
        return (
            "Maps {@link org.thingsboard.server.common.data.query.EntityFilter} type to a concrete "
            "{@link EntityQueryProcessor} implementation."
        )
    if name == "EntityQueryProcessor":
        return "Executes one entity filter against a {@link TenantRepo} with permission {@link org.thingsboard.server.common.data.permission.QueryContext}."
    if name.endswith("QueryProcessor"):
        filt = humanize(name.replace("QueryProcessor", ""))
        return f"EDQS query processor for {filt} entity filters."
    if name.startswith("Abstract") and "QueryProcessor" in name:
        return f"Base class for EDQS {humanize(name[8:])} implementations."
    if name.endswith("Data") and kind == "class":
        return f"In-memory EDQS projection of {humanize(name[:-4])} entity fields and metadata."
    if name.endswith("DataPoint"):
        return f"Typed attribute/latest-TS value stored in EDQS ({humanize(name)})."
    if name == "EdqsRocksDb" or name == "TbRocksDb":
        return "RocksDB wrapper for EDQS local persistence (state, versions, key dictionary)."
    if name == "RepositoryUtils":
        return "Sorting, filtering, and entity-type helpers for {@link TenantRepo} queries."
    if name == "DefaultEdqsMapper":
        return "Protobuf/bytes to {@link org.thingsboard.server.common.data.edqs.EdqsObject} deserialization for Kafka messages."
    if name == "EdqsMapper":
        return "Serialization contract for EDQS Kafka payloads."
    if name == "KeyDictionary":
        return "Interns attribute/telemetry key strings to int ids to save memory in the index."
    if name == "VersionsStore":
        return "Dedupes out-of-order entity updates using monotonic version numbers from events."
    if name.endswith("Test"):
        return f"Unit/integration test for EDQS {humanize(name.replace('Test', ''))} query filters."
    if name.startswith("Abstract") and name.endswith("Test"):
        return "Base Spring test context for in-memory EDQS repository filter tests."
    if kind == "interface":
        return f"EDQS {humanize(name)} contract."
    return f"EDQS component: {humanize(name)}."


def method_doc(name: str) -> str:
    if name == "isReady":
        return "True when all assigned Kafka partitions have been restored from state and the index is queryable."
    if name == "processEvent":
        return "Applies create/update/delete of an entity or relation into the in-memory index."
    if name == "countEntitiesByQuery":
        return "Returns entity count for the given filter without loading full entity rows."
    if name == "findEntityDataByQuery":
        return "Returns a page of entities matching filter, sort, and key selections."
    if name == "handle":
        return "Kafka request-response handler: parses EdqsRequest JSON and returns EdqsResponse."
    if name == "create":
        return "Selects query processor implementation for the query's entity filter type."
    if name.startswith("find"):
        return f"Loads {humanize(name[4:])}."
    if name.startswith("get"):
        return f"Returns {humanize(name[3:])}."
    if name == "init":
        return "Starts consumers and wires partition/state services."
    if name == "destroy" or name == "stop":
        return "Shuts down consumers and releases resources."
    return f"{humanize(name).capitalize()}."


def indent_javadoc(indent: str, lines: list[str]) -> str:
    inner = "\n".join(f"{indent} * {line}" if line else f"{indent} *" for line in lines)
    return f"{indent}/**\n{inner}\n{indent} */\n"


def find_insert_position(body: str, decl_start: int) -> int:
    line_start = body.rfind("\n", 0, decl_start) + 1
    pos = line_start
    while pos > 0:
        prev_nl = body.rfind("\n", 0, pos - 1)
        prev_start = prev_nl + 1
        prev = body[prev_start:pos].strip()
        if not prev or prev.startswith("@") or prev.endswith(")"):
            pos = prev_start
            continue
        break
    return pos


def detect_kind(line: str) -> str:
    for k in ("class", "interface", "enum", "record"):
        if re.search(rf"\b{k}\b", line):
            return k
    return "class"


def remove_weak_class_javadoc(body: str, insert_at: int) -> tuple[str, int]:
    """Remove misplaced or weak class javadoc immediately before class decl."""
    before = body[:insert_at].rstrip()
    m = re.search(r"/\*\*[\s\S]*?\*/\s*$", before)
    if not m:
        return body, insert_at
    doc = m.group(0)
    if WEAK_DOC.search(doc) or re.search(r"@\w+", before[m.start() - 50 : m.start()] if m.start() > 50 else before[:m.start()]):
        # only strip weak docs after annotations block
        pass
    if WEAK_DOC.search(doc) or len(doc) < 120:
        new_before = before[: m.start()].rstrip() + "\n"
        delta = len(new_before) - len(body[:insert_at])
        return new_before + body[insert_at:], insert_at + delta
    return body, insert_at


def add_method_javadocs(body: str) -> tuple[str, int]:
    count = 0
    offset = 0
    for m in METHOD_RE.finditer(body):
        pos = m.start() + offset
        if has_javadoc_before(body, pos):
            continue
        line_start = body.rfind("\n", 0, pos) + 1
        line = body[line_start:pos]
        if " abstract " in line or ";" in line.split("(")[0]:
            continue
        indent = m.group("indent")
        doc = indent_javadoc(indent, [method_doc(m.group("name"))])
        body = body[:pos] + doc + body[pos:]
        offset += len(doc)
        count += 1
    return body, count


def has_javadoc_before(body: str, pos: int) -> bool:
    return body[:pos].rstrip().endswith("*/")


def process_file(path: Path, dry_run: bool, methods: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    m = DECL_LINE_RE.search(body)
    if not m:
        return False
    changed = False
    name = m.group("name")
    kind = detect_kind(m.group(0))
    indent = m.group("indent")
    insert_at = find_insert_position(body, m.start())
    body, insert_at = remove_weak_class_javadoc(body, insert_at)
    if not has_javadoc_before(body, insert_at):
        body = body[:insert_at] + indent_javadoc(indent, [edqs_class_doc(name, kind)]) + body[insert_at:]
        changed = True
    elif WEAK_DOC.search(body):
        changed = True
    if methods:
        body, mc = add_method_javadocs(body)
        if mc:
            changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("root", nargs="?", default=".")
    p.add_argument("--dry-run", action="store_true")
    p.add_argument("--no-methods", action="store_true")
    args = p.parse_args()
    root = Path(args.root)
    n = 0
    for path in sorted(root.rglob("*.java")):
        if process_file(path, args.dry_run, not args.no_methods):
            n += 1
            print(path)
    print(f"Updated {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
