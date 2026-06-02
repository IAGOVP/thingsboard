#!/usr/bin/env python3
"""
Add file-level JSDoc to ui-ngx TypeScript: classes, components, services, modules.
Inserts before @Component/@Injectable/@NgModule or export class/function.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

DECL_RE = re.compile(
    r"^(?P<indent>\s*)"
    r"(?:@\w+(?:\([^)]*\))?\s*\n\s*)*"
    r"(?:(?:export\s+)?(?:default\s+)?(?:abstract\s+)?class|interface|enum|type)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)
# Simpler: find export class Name or @Component block
CLASS_RE = re.compile(
    r"^(?P<indent>\s*)(?:export\s+)?(?:default\s+)?(?:abstract\s+)?class\s+(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)
INTERFACE_RE = re.compile(
    r"^(?P<indent>\s*)(?:export\s+)?interface\s+(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

SERVICE_DOCS = {
    "DeviceService": "HTTP client for device CRUD, credentials, claim, and RPC under `/api/.../device*`.",
    "EntityService": "Generic entity queries and telemetry/attribute reads via `/api/entitiesQuery`, timeseries APIs.",
    "DashboardService": "Dashboard CRUD, assign to customers, export/import via `/api/dashboard*`.",
    "RuleChainService": "Rule chain and rule node metadata, save/test flows via `/api/ruleChain*`.",
    "UserService": "User administration, activation links, user credentials via `/api/user*`.",
    "TenantService": "Tenant CRUD and tenant admin users via `/api/tenant*`.",
    "AlarmService": "Alarm search, acknowledge, clear via `/api/alarm*`.",
    "AttributeService": "Post/delete attributes and timeseries via `/api/plugins/telemetry` and entity APIs.",
    "AuthService": "JWT login, refresh, logout; coordinates with GlobalHttpInterceptor.",
    "GlobalHttpInterceptor": "Attaches Bearer token to `/api/**`, loading spinner, 401 refresh and error toasts.",
}


def split_license(content: str) -> tuple[str, str]:
    m = re.match(r"(///[\s\S]*?\n\n)([\s\S]*)", content)
    if m:
        return m.group(1), m.group(2)
    m2 = re.match(r"(/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m2.group(1), m2.group(2)) if m2 else ("", content)


def humanize(name: str) -> str:
    s = re.sub(r"([a-z])([A-Z])", r"\1 \2", name)
    return s.replace("_", " ").lower()


def describe(name: str, path: Path) -> str:
    if name in SERVICE_DOCS:
        return SERVICE_DOCS[name]
    posix = path.as_posix()
    if name.endswith("Service"):
        return f"Angular HTTP service: {humanize(name.replace('Service', ''))} REST wrappers (`@core/http`)."
    if name.endswith("Component"):
        return f"Angular component: {humanize(name.replace('Component', ''))} UI."
    if name.endswith("Module"):
        return f"Angular NgModule: {humanize(name.replace('Module', ''))} feature area."
    if name.endswith("Guard"):
        return f"Route guard: {humanize(name.replace('Guard', ''))}."
    if name.endswith("Interceptor"):
        return f"HTTP interceptor: {humanize(name)}."
    if name.endswith("Resolver"):
        return f"Route resolver: loads {humanize(name.replace('Resolver', ''))} before activate."
    if "/models/" in posix or name.endswith("Models") or ".models." in path.name:
        return f"TypeScript models and enums for {humanize(name)}."
    if "routing" in path.name.lower():
        return f"Angular routes for {humanize(name.replace('RoutingModule', '').replace('Routing', ''))}."
    if name.endswith("Directive"):
        return f"Angular directive: {humanize(name.replace('Directive', ''))}."
    if name.endswith("Pipe"):
        return f"Angular pipe: {humanize(name.replace('Pipe', ''))}."
    return f"{humanize(name)}."


def find_insert(body: str, decl_start: int) -> int:
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


def has_doc_before(body: str, decl_start: int) -> bool:
    insert = find_insert(body, decl_start)
    chunk = body[insert:decl_start]
    if "/**" in chunk and "*/" in chunk:
        return True
    before = body[:insert].rstrip()
    return before.endswith("*/") or before.rstrip().endswith("///")


def ts_doc(indent: str, text: str) -> str:
    return f"{indent}/**\n{indent} * {text}\n{indent} */\n"


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license(content)
    m = CLASS_RE.search(body) or INTERFACE_RE.search(body)
    if not m:
        return False
    if has_doc_before(body, m.start()):
        return False
    name = m.group("name")
    indent = m.group("indent")
    insert_at = find_insert(body, m.start())
    body = body[:insert_at] + ts_doc(indent, describe(name, path)) + body[insert_at:]
    if not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return True


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("root", nargs="?", default="ui-ngx/src/app")
    p.add_argument("--dry-run", action="store_true")
    args = p.parse_args()
    root = Path(args.root)
    n = 0
    for path in sorted(root.rglob("*.ts")):
        posix = path.as_posix()
        if ".spec.ts" in posix or path.name.endswith(".d.ts"):
            continue
        if process_file(path, args.dry_run):
            n += 1
    print(f"Updated {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
