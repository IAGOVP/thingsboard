#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to ThingsBoard application service packages.
Includes @param, @return, and @throws tags. No logic changes.
"""
from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path

DECL_LINE_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|sealed|non-sealed|static|final)\s+)*"
    r"(?:class|interface|enum|record)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

PACKAGE_CONTEXT: dict[str, str] = {
    "notification": "notification delivery, templates, targets, and rule-trigger processing",
    "install": "database schema installation, upgrades, and demo data loading",
    "telemetry": "telemetry subscription and WebSocket push to clients",
    "ttl": "time-to-live cleanup for alarms, events, and telemetry",
    "mail": "SMTP email sending and templating",
    "housekeeper": "background housekeeping tasks (alarm unassign, job cleanup, etc.)",
    "executors": "shared thread-pool executors for async service work",
    "transport": "tb-core to transport microservice messaging",
    "rpc": "device RPC orchestration between REST, rule engine, and transport",
    "edqs": "Entity Data Query Service integration from tb-core",
    "sms": "SMS provider abstraction and message sending",
    "ai": "AI model invocation for platform features",
    "apiusage": "tenant API usage metering and rate-limit state",
    "job": "background job scheduling and execution",
    "state": "device and entity state tracking",
    "mobile": "mobile app bundles, secrets, and deep-link support",
    "component": "rule-engine component descriptor registry",
    "device": "device claim and provisioning helpers",
    "script": "TBEL/JS script invocation from services",
    "system": "system-level configuration and info services",
    "profile": "device and asset profile resolution",
    "resource": "tenant/system resource file management",
    "session": "device session and connectivity state",
    "partition": "Kafka partition resolution for tenants and queues",
    "query": "entity query helpers for core services",
    "ota": "over-the-air firmware/software package handling",
    "lwm2m": "LwM2M bootstrap and model integration",
    "gateway_device": "gateway child-device session management",
    "rule": "rule chain metadata and helpers",
    "ruleengine": "rule engine message injection from core",
    "stats": "queue and service statistics collection",
    "update": "platform update checks and notifications",
    "action": "entity lifecycle actions, audit, and rule-engine events",
    "asset": "asset-specific service operations",
    "user": "user lockout and security helpers",
}

TARGET_PACKAGES = tuple(PACKAGE_CONTEXT.keys())

CLASS_METHOD_RE = re.compile(
    r"(?P<indent>^[ \t]*)"
    r"(?:public|protected)\s+"
    r"(?:(?:static|final|synchronized|native|abstract)\s+)*"
    r"(?:<[^>]+>\s+)?"
    r"(?P<ret>[\w.<>,\s\[\]?]+)\s+"
    r"(?P<name>\w+)\s*\(",
    re.MULTILINE,
)

INTERFACE_METHOD_RE = re.compile(
    r"(?P<indent>^[ \t]*)"
    r"(?:(?:public|protected)\s+)?"
    r"(?:(?:static|final|default)\s+)*"
    r"(?:<[^>]+>\s+)?"
    r"(?P<ret>[\w.<>,\s\[\]?]+)\s+"
    r"(?P<name>\w+)\s*\(",
    re.MULTILINE,
)

SKIP_METHOD_NAMES = frozenset(
    {
        "if", "for", "while", "switch", "catch", "return", "new", "throw",
        "equals", "hashCode", "toString",
        # common parameter names falsely matched inside signatures
        "request", "response", "callback", "ctx", "tenantId", "userId",
        "entityId", "deviceId", "customerId", "settings", "config", "msg",
    }
)


@dataclass
class ParamInfo:
    name: str
    type_hint: str


@dataclass
class MethodInfo:
    doc_start: int
    sig_start: int
    indent: str
    name: str
    return_type: str
    params: list[ParamInfo] = field(default_factory=list)
    throws: list[str] = field(default_factory=list)


def split_license_and_body(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def detect_kind(line: str) -> str:
    for k in ("class", "interface", "enum", "record"):
        if re.search(rf"\b{k}\b", line):
            return k
    return "class"


def package_key(path: Path) -> str:
    parts = path.as_posix().split("/service/")
    if len(parts) < 2:
        return ""
    return parts[1].split("/")[0]


def subpackage_hint(path: Path) -> str:
    posix = path.as_posix()
    if "/sync/vc/" in posix or posix.endswith("/sync/vc"):
        return "Git-based entity version control"
    if "/sync/ie/" in posix or posix.endswith("/sync/ie"):
        return "entity import and export"
    pkg = package_key(path)
    if pkg == "cf":
        return "calculated fields"
    if pkg == "entitiy":
        return "REST-layer entity operations"
    if pkg == "sync":
        return "entity synchronization"
    return ""


def class_description(name: str, kind: str, pkg: str, body: str, path: Path) -> list[str]:
    hint = subpackage_hint(path)
    ctx = PACKAGE_CONTEXT.get(pkg, "platform service")
    if hint:
        ctx = f"{hint} ({ctx})"

    if pkg == "sync":
        if name.endswith("Exception"):
            return [f"Exception during {hint or 'entity sync'}: {humanize(name)}."]
        if "Export" in name and name.endswith("Service"):
            entity = humanize(name.replace("ExportService", "").replace("Default", ""))
            return [
                f"Exports {entity} entities to portable JSON.",
                "",
                "<p>Used by version control and tenant migration to serialize entity graphs with dependencies.",
            ]
        if "Import" in name and name.endswith("Service"):
            entity = humanize(name.replace("ImportService", "").replace("Default", ""))
            return [
                f"Imports {entity} entities from export JSON.",
                "",
                "<p>Resolves references, applies conflict strategy, and persists through DAO services.",
            ]
        if name == "EntitiesVersionControlService":
            return [
                "Service API for Git-based entity version control.",
                "",
                "<p>Creates versions, loads historical snapshots, compares entity data, and manages repository settings per tenant.",
            ]
        if name == "EntitiesExportImportService":
            return [
                "Facade for bulk entity export and import across entity types.",
                "",
                "<p>Coordinates type-specific export/import services and applies tenant-scoped settings.",
            ]
        if "Git" in name:
            return [f"Git repository integration for {humanize(name.replace('Git', ''))}."]
        if "AutoCommit" in name:
            return ["Manages automatic version commits when entities change."]
        if name.endswith("Request") or name.endswith("Ctx"):
            return [f"Data object for {humanize(name)} used during {hint or 'sync'} operations."]

    if pkg == "cf":
        if name.endswith("Result"):
            return [f"Result of calculated-field evaluation ({humanize(name)})."]
        if name.endswith("State"):
            return [f"Runtime state for {humanize(name.replace('State', ''))} calculated fields."]
        if name.endswith("Entry"):
            return [f"Argument or aggregation entry for calculated-field state ({humanize(name)})."]
        if name == "CalculatedFieldProcessingService":
            return [
                "Service API for calculated-field argument resolution and result processing.",
                "",
                "<p>Fetches telemetry, attributes, and relations; dispatches results to the rule engine and linked fields.",
            ]
        if name == "CalculatedFieldQueueService":
            return ["Kafka queue integration for calculated-field telemetry and lifecycle messages."]
        if "ScriptEngine" in name:
            return [f"Script execution engine for {humanize(name.replace('ScriptEngine', '').replace('CalculatedField', ''))} calculated fields."]

    if pkg == "entitiy":
        if name == "AbstractTbEntityService":
            return [
                "Base class for tenant-scoped entity services in the REST layer.",
                "",
                "<p>Provides audit logging, version-control auto-commit, validation helpers, and cluster notification hooks.",
            ]
        if name == "TbLogEntityActionService":
            return ["Records entity audit log entries for user actions."]
        if name == "EntityStateSourcingListener":
            return ["Listens for entity lifecycle events to propagate state to external systems."]
        if name.startswith("Tb") and name.endswith("Service") and kind == "interface":
            domain = humanize(name[2:-7])
            return [
                f"Application-layer service API for {domain} entity operations.",
                "",
                "<p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.",
            ]
        if name.startswith("DefaultTb") and name.endswith("Service"):
            iface = "Tb" + name[9:]
            return [f"Default implementation of {{@link {iface}}}."]

    if name.endswith("Service") and kind == "interface":
        return [
            f"Service contract for {humanize(name[:-7])} operations ({ctx}).",
            "",
            "<p>Implemented by the corresponding {@code Default*} class in this package.",
        ]
    if name.startswith("Default") and kind == "class":
        return [
            f"Default Spring implementation for {humanize(name[7:])} ({ctx}).",
            "",
            "<p>Registered as a {@code @Service} or {@code @Component} bean.",
        ]
    if name.endswith("Exception"):
        return [f"Exception raised during {ctx}: {humanize(name)}."]
    if kind == "interface":
        return [f"{humanize(name)} contract for {ctx}."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values used in {ctx}."]
    if "@Service" in body or "@Component" in body:
        return [f"Spring service component for {humanize(name)} ({ctx})."]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def method_description(name: str) -> str:
    def with_rest(prefix: str, verb: str) -> str:
        rest = humanize(name[len(prefix):]) or "the requested data"
        return f"{verb} {rest}."

    rules = [
        ("saveOrUpdate", "Saves or updates"),
        ("saveDeviceWithCredentials", "Saves a device with credentials"),
        ("save", "Saves or persists"),
        ("delete", "Deletes"),
        ("remove", "Removes"),
        ("assign", "Assigns"),
        ("unassign", "Unassigns"),
        ("find", "Finds"),
        ("get", "Returns"),
        ("fetch", "Fetches"),
        ("load", "Loads"),
        ("list", "Lists"),
        ("compare", "Compares"),
        ("check", "Checks"),
        ("validate", "Validates"),
        ("process", "Processes"),
        ("export", "Exports"),
        ("import", "Imports"),
        ("commit", "Commits"),
        ("autoCommit", "Triggers auto-commit for"),
        ("push", "Pushes"),
        ("create", "Creates"),
        ("update", "Updates"),
        ("count", "Counts"),
        ("execute", "Executes"),
        ("handle", "Handles"),
        ("on", "Handles"),
    ]
    for prefix, verb in rules:
        if name.startswith(prefix):
            return with_rest(prefix, verb)
    return f"{humanize(name).capitalize()}."


def format_return(ret: str) -> str:
    ret = ret.strip()
    if not ret or ret == "void" or ret.startswith("@"):
        return "nothing"
    if ret.startswith("ListenableFuture<"):
        inner = ret[len("ListenableFuture<") : -1].strip()
        return f"future completing with {format_return(inner)}"
    if ret.startswith("Optional<"):
        inner = ret[len("Optional<") : -1].strip()
        return f"optional {format_return(inner)}, empty if not found"
    parts = ret.replace("?", "").split("<")[0].strip().split()
    if not parts:
        return "the operation result"
    simple = parts[-1]
    if simple and simple[0].isupper() and re.match(r"^[A-Z]\w*$", simple):
        return f"{{@link {simple}}}"
    if simple in ("boolean", "int", "long", "float", "double", "byte", "short", "char"):
        return f"the {simple} result"
    return f"the {ret} value"


def param_description(name: str, type_hint: str) -> str:
    hints = {
        "tenantId": "tenant that owns the entity or operation",
        "user": "authenticated user performing the action",
        "entityId": "target entity identifier",
        "deviceId": "target device identifier",
        "pageLink": "pagination and sort parameters",
        "callback": "queue callback invoked when processing completes",
        "ctx": "calculated-field execution context",
        "request": "request payload with operation parameters",
        "branch": "Git branch name",
        "versionId": "entity version identifier in the repository",
    }
    if name in hints:
        return hints[name]
    simple_parts = type_hint.replace("?", "").split("<")[0].strip().split()
    if simple_parts:
        simple = simple_parts[-1]
        if simple and simple[0].isupper() and re.match(r"^[A-Z]\w*$", simple):
            return f"{humanize(name)} ({{@link {simple}}})"
    return humanize(name)


def throws_description(exc: str) -> str:
    if exc == "ThingsboardException":
        return "if the operation fails validation, authorization, or business rules"
    if exc == "Exception":
        return "if an unexpected error occurs during processing"
    return f"if {humanize(exc)} is thrown during processing"


def format_javadoc(indent: str, lines: list[str]) -> str:
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


def javadoc_block_before(body: str, pos: int) -> tuple[int, int] | None:
    """Return javadoc block immediately before pos (whitespace only in between)."""
    trimmed = body[:pos].rstrip()
    if not trimmed.endswith("*/"):
        return None
    if body[len(trimmed) : pos].strip():
        return None
    start = trimmed.rfind("/**")
    if start < 0:
        return None
    if "Licensed under the Apache License" in body[start:pos]:
        return None
    between = body[start:pos]
    if re.search(r"(?:^|\n)\s*(?:public|protected|private|class|interface|enum|record)\b", between):
        return None
    return start, len(trimmed)


def class_javadoc_is_generic(doc: str) -> bool:
    return any(
        m in doc
        for m in (
            "Service contract for",
            "Default Spring implementation for",
            "Service implementation for",
            "Implemented by the corresponding",
        )
    )


def method_doc_needs_fixup(body: str, doc_end: int) -> bool:
    before = body[:doc_end].rstrip()
    return before.endswith("{") or before.endswith("@Override")


def javadoc_is_complete(doc: str, method: MethodInfo) -> bool:
    if "@return" not in doc or "@throws" not in doc:
        return False
    if method.return_type.strip() not in ("", "void") and "@return nothing" in doc:
        return False
    if any(x in doc for x in ("Persists .", "Returns .", "Loads .", "Saves or persists .")):
        return False
    for p in method.params:
        if f"@param {p.name}" not in doc:
            return False
    if method.throws:
        for t in method.throws:
            if f"@throws {t}" not in doc:
                return False
    return True


def strip_parameter_annotations(param: str) -> str:
    param = re.sub(r"@\w+(?:\([^)]*\))?\s+", "", param)
    return re.sub(r"final\s+", "", param).strip()


def parse_params(param_text: str) -> list[ParamInfo]:
    params: list[ParamInfo] = []
    if not param_text.strip():
        return params
    depth = 0
    parts: list[str] = []
    current: list[str] = []
    for ch in param_text:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth = max(0, depth - 1)
        elif ch == "," and depth == 0:
            parts.append("".join(current).strip())
            current = []
            continue
        current.append(ch)
    if current:
        parts.append("".join(current).strip())
    for part in parts:
        part = strip_parameter_annotations(part)
        if not part or part == "...":
            continue
        if part.endswith("..."):
            part = part[:-3].strip()
        tokens = part.split()
        if len(tokens) >= 2:
            params.append(ParamInfo(name=tokens[-1], type_hint=" ".join(tokens[:-1])))
    return params


def extract_throws(after_paren: str) -> list[str]:
    m = re.search(r"throws\s+([\w.<>,\s]+?)(?:\s*\{|\s*;|\s*//)", after_paren)
    if not m:
        return []
    return [t.strip().split(".")[-1] for t in m.group(1).split(",") if t.strip()]


def class_body_bounds(body: str, class_name: str) -> tuple[int, int]:
    class_m = re.search(
        rf"(?:class|interface|enum|record)\s+{re.escape(class_name)}[^{{]*\{{",
        body,
    )
    if not class_m:
        return 0, len(body)
    start = class_m.end()
    depth = 1
    i = start
    while i < len(body) and depth > 0:
        if body[i] == "{":
            depth += 1
        elif body[i] == "}":
            depth -= 1
            if depth == 0:
                return start, i
        i += 1
    return start, len(body)


def find_method_doc_insertion_point(body: str, sig_start: int, class_start: int) -> int:
    """Insert before the method, after any blank lines or annotations above it."""
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
        break
    return insert_at


def insert_text_at(body: str, pos: int, text: str) -> str:
    prefix = body[:pos]
    if prefix and not prefix.endswith("\n"):
        text = "\n" + text
    return prefix + text + body[pos:]


def find_methods(body: str, class_name: str, kind: str) -> list[MethodInfo]:
    methods: list[MethodInfo] = []
    class_start, class_end = class_body_bounds(body, class_name)
    region = body[class_start:class_end]
    pattern = INTERFACE_METHOD_RE if kind == "interface" else CLASS_METHOD_RE
    for m in pattern.finditer(region):
        method_name = m.group("name")
        if method_name == class_name or method_name in SKIP_METHOD_NAMES:
            continue
        ret_type = re.sub(
            r"\b(static|final|synchronized|native|abstract|default)\s+",
            "",
            m.group("ret"),
        ).strip()
        if not ret_type or ret_type.startswith("@"):
            continue
        line_start = body.rfind("\n", 0, class_start + m.start()) + 1
        if "(" in body[line_start : class_start + m.start()]:
            continue
        sig_abs = class_start + m.start()
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
        doc_start = find_method_doc_insertion_point(body, sig_abs, class_start)
        methods.append(
            MethodInfo(
                doc_start=doc_start,
                sig_start=sig_abs,
                indent=m.group("indent"),
                name=method_name,
                return_type=ret_type,
                params=parse_params(param_text),
                throws=extract_throws(after_paren),
            )
        )
    return methods


def remove_misplaced_method_javadoc(body: str, doc_start: int, sig_start: int) -> tuple[str, int]:
    """Remove javadoc attached to the signature line, not the block at doc_start."""
    removed = 0
    between = body[doc_start:sig_start].strip()
    if not between or between.startswith("/**"):
        return body, 0
    region = body[doc_start:sig_start]
    while True:
        m = re.search(r"/\*\*[\s\S]*?\*/", region)
        if not m:
            break
        abs_start = doc_start + m.start()
        abs_end = doc_start + m.end()
        body = body[:abs_start] + body[abs_end:]
        delta = abs_end - abs_start
        removed += delta
        sig_start -= delta
        region = body[doc_start:sig_start]
    return body, removed


def method_javadoc(method: MethodInfo) -> str:
    lines = [method_description(method.name), ""]
    for p in method.params:
        lines.append(f"@param {p.name} {param_description(p.name, p.type_hint)}")
    lines.append(f"@return {format_return(method.return_type)}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    else:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(method.indent, lines)


def add_class_javadoc(body: str, class_name: str, kind: str, pkg: str, path: Path) -> tuple[str, bool]:
    m = DECL_LINE_RE.search(body)
    if not m:
        return body, False
    insert_pos = find_insert_position(body, m.start())
    new_doc = format_javadoc(m.group("indent"), class_description(class_name, kind, pkg, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
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


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    m = DECL_LINE_RE.search(body)
    if not m:
        return False
    class_name = m.group("name")
    kind = detect_kind(m.group(0))
    pkg = package_key(path)
    changed = False
    body, cc = add_class_javadoc(body, class_name, kind, pkg, path)
    if cc:
        changed = True
    body, mc = add_method_javadocs(body, class_name, kind)
    if mc:
        changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--base",
        type=Path,
        default=Path("application/src/main/java/org/thingsboard/server/service"),
    )
    parser.add_argument("--packages", nargs="*", default=list(TARGET_PACKAGES))
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    totals = {pkg: 0 for pkg in args.packages}
    for path in sorted(collect_files(args.base, args.packages)):
        if path.name == "package-info.java":
            continue
        if process_file(path, args.dry_run):
            pkg = package_key(path)
            if pkg in totals:
                totals[pkg] += 1
    total = sum(totals.values())
    for pkg in args.packages:
        print(f"{pkg}: {totals.get(pkg, 0)} files modified", file=sys.stderr)
    print(f"Done: {total} files modified", file=sys.stderr)
    return 0


def collect_files(base: Path, packages: list[str]) -> list[Path]:
    files: list[Path] = []
    for pkg in packages:
        d = base / pkg
        if d.is_dir():
            files.extend(sorted(d.rglob("*.java")))
    return files


if __name__ == "__main__":
    sys.exit(main())
