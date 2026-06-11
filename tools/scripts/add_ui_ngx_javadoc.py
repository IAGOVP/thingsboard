#!/usr/bin/env python3
"""
Add detailed file- and method-level JSDoc to ui-ngx TypeScript sources.
Documents Angular components, HTTP services (REST `/api/...` paths), routes, guards, and models.
"""
from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path

UI_NGX_ROOT = Path("ui-ngx/src/app")

CLASS_RE = re.compile(
    r"^(?P<indent>\s*)(?:export\s+)?(?:default\s+)?(?:abstract\s+)?class\s+(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)
INTERFACE_RE = re.compile(
    r"^(?P<indent>\s*)(?:export\s+)?interface\s+(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)
ENUM_RE = re.compile(
    r"^(?P<indent>\s*)(?:export\s+)?enum\s+(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)
METHOD_RE = re.compile(
    r"^(\s*)(?:(public|protected|private)\s+)?(?:(?:async|static|override|readonly)\s+)*"
    r"(?!(?:if|for|switch|catch|while|return|new|typeof|instanceof)\b)"
    r"(\w+)\s*\(([^)]*)\)\s*(?::\s*([^{;]+))?\s*\{",
    re.MULTILINE,
)
API_RE = re.compile(r"[`'\"](/api/[^`'\"]+)[`'\"]")
API_TEMPLATE_RE = re.compile(r"`(/api/[^`]+)`")
ROUTE_PATH_RE = re.compile(r"\bpath:\s*['\"]([^'\"]+)['\"]")
SKIP_METHODS = frozenset(
    {"constructor", "ngOnChanges", "ngDoCheck", "ngAfterContentInit", "ngAfterContentChecked"}
)

SERVICE_CLASS_DOCS: dict[str, list[str]] = {
    "DeviceService": [
        "Angular HTTP service for device CRUD, credentials, claim, bulk import, and RPC.",
        "",
        "<p>Wraps ThingsBoard REST endpoints under `/api/device*`, `/api/tenant/deviceInfos`, "
        "`/api/customer/{id}/device*`, and `/api/rpc/*`.",
    ],
    "EntityService": [
        "Generic entity query and telemetry/attribute reads.",
        "",
        "<p>Uses `/api/entitiesQuery`, `/api/plugins/telemetry`, and related entity APIs.",
    ],
    "DashboardService": [
        "Dashboard CRUD, customer assignment, and import/export.",
        "",
        "<p>REST base: `/api/dashboard*`, `/api/customer/{id}/dashboard*`.",
    ],
    "RuleChainService": [
        "Rule chain and rule node metadata, save, and test script execution.",
        "",
        "<p>REST base: `/api/ruleChain*`, `/api/ruleNode*`, `/api/component/descriptor*`.",
    ],
    "AlarmService": [
        "Alarm search, acknowledge, clear, and assign.",
        "",
        "<p>REST base: `/api/alarm*`, `/api/v2/alarm*`.",
    ],
    "AuthService": [
        "JWT login, token refresh, and logout for the web UI.",
        "",
        "<p>Uses `/api/auth/login`, `/api/auth/token`, `/api/auth/logout`.",
    ],
    "UserService": [
        "User administration, activation links, and credentials.",
        "",
        "<p>REST base: `/api/user*`, `/api/users*`.",
    ],
    "TenantService": [
        "Tenant CRUD and tenant admin user management.",
        "",
        "<p>REST base: `/api/tenant*`.",
    ],
    "AttributeService": [
        "Post/delete attributes and latest telemetry values.",
        "",
        "<p>Uses `/api/plugins/telemetry` and entity attribute endpoints.",
    ],
    "GlobalHttpInterceptor": [
        "HTTP interceptor: attaches Bearer JWT, shows loading spinner, handles 401 refresh.",
        "",
        "<p>Applies to all `/api/**` requests from `@core/http` services.",
    ],
}

PARAM_HINTS: dict[str, str] = {
    "config": "optional HTTP request config (ignoreLoading, ignoreErrors, etc.)",
    "pageLink": "pagination and sort parameters",
    "entityId": "entity UUID",
    "deviceId": "device UUID",
    "customerId": "customer UUID",
    "tenantId": "tenant UUID",
    "userId": "user UUID",
    "alarmId": "alarm UUID",
    "dashboardId": "dashboard UUID",
    "ruleChainId": "rule chain UUID",
    "ctx": "Angular template or component context",
    "event": "DOM or Angular event object",
    "form": "Angular reactive form group",
    "data": "dialog or route input data",
}


@dataclass
class TsMethod:
    pos: int
    indent: str
    name: str
    params: list[tuple[str, str]]
    return_type: str


def split_license(content: str) -> tuple[str, str]:
    m = re.match(r"(///[\s\S]*?\n\n)([\s\S]*)", content)
    if m:
        return m.group(1), m.group(2)
    m2 = re.match(r"(/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m2.group(1), m2.group(2)) if m2 else ("", content)


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def ngx_area(path: Path) -> str:
    posix = path.as_posix()
    if "/core/http/" in posix:
        return "HTTP service layer"
    if "/core/auth/" in posix:
        return "authentication"
    if "/core/guards/" in posix:
        return "route guards"
    if "/core/interceptors/" in posix:
        return "HTTP interceptors"
    if "/shared/models/" in posix:
        return "shared TypeScript models"
    if "/shared/components/" in posix:
        return "shared UI components"
    if "/modules/home/pages/" in posix:
        m = re.search(r"/pages/([^/]+)/", posix)
        return f"home/{m.group(1)} pages" if m else "home pages"
    if "/modules/login/" in posix:
        return "login pages"
    if "/modules/dashboard/" in posix:
        return "dashboard module"
    if "routing" in path.name.lower():
        return "Angular routing"
    return "ThingsBoard web UI"


def format_ts_javadoc(indent: str, lines: list[str]) -> str:
    out = [f"{indent}/**"]
    for line in lines:
        if line == "":
            out.append(f"{indent} *")
        else:
            out.append(f"{indent} * {line}")
    out.append(f"{indent} */")
    return "\n".join(out) + "\n"


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


def jsdoc_block_before(body: str, pos: int) -> tuple[int, int] | None:
    chunk = body[:pos].rstrip()
    if not chunk.endswith("*/"):
        return None
    end = len(chunk)
    start = chunk.rfind("/**")
    if start < 0:
        return None
    return start, end


def strip_jsdoc_before(body: str, pos: int) -> tuple[str, int]:
    while True:
        block = jsdoc_block_before(body, pos)
        if not block:
            break
        start, end = block
        body = body[:start] + body[end:]
        pos = start
    return body, pos


def parse_params(param_str: str) -> list[tuple[str, str]]:
    params: list[tuple[str, str]] = []
    if not param_str.strip():
        return params
    depth = 0
    part = ""
    for ch in param_str + ",":
        if ch in "<([":
            depth += 1
        elif ch in ">)]":
            depth -= 1
        if ch == "," and depth == 0:
            p = part.strip()
            if p:
                m = re.match(r"(?:\.\.\.)?(\w+)\s*\??\s*:\s*([^=]+)", p)
                if m:
                    params.append((m.group(1), m.group(2).strip()))
            part = ""
        else:
            part += ch
    return params


def param_desc(name: str, type_hint: str) -> str:
    if name in PARAM_HINTS:
        return PARAM_HINTS[name]
    return f"{humanize(name)} ({type_hint.strip()})"


def extract_api_paths(chunk: str) -> list[str]:
    paths: list[str] = []
    for pat in (API_RE, API_TEMPLATE_RE):
        for m in pat.finditer(chunk):
            p = m.group(1)
            if "${" in p:
                p = p.split("${")[0].rstrip("/") + "/*"
            paths.append(p)
    return list(dict.fromkeys(paths))


def extract_routes(body: str) -> list[str]:
    return list(dict.fromkeys(ROUTE_PATH_RE.findall(body)))


def method_summary(name: str, paths: list[str], is_service: bool) -> str:
    if name.startswith("get") and paths:
        return f"GET {paths[0]} — fetch {humanize(name[3:])}."
    if name.startswith("save") or name.startswith("create") or name.startswith("add"):
        return f"POST/PUT entity — {humanize(name)}." + (f" REST: `{paths[0]}`." if paths else "")
    if name.startswith("delete") or name.startswith("remove"):
        return f"DELETE — {humanize(name)}." + (f" REST: `{paths[0]}`." if paths else "")
    if name == "ngOnInit":
        return "Angular lifecycle hook: initialize component state and subscriptions."
    if name == "ngOnDestroy":
        return "Angular lifecycle hook: unsubscribe and release resources."
    if name == "ngAfterViewInit":
        return "Angular lifecycle hook: run after the component view is initialized."
    if name.startswith("on") and len(name) > 2 and name[2].isupper():
        return f"Event handler for {humanize(name[2:])}."
    if is_service and paths:
        return f"Calls ThingsBoard REST `{paths[0]}`."
    return f"{humanize(name)}."


def class_description(name: str, path: Path, body: str) -> list[str]:
    if name in SERVICE_CLASS_DOCS:
        return SERVICE_CLASS_DOCS[name]
    area = ngx_area(path)
    routes = extract_routes(body) if "routing" in path.name.lower() or "Routes" in body[:500] else []
    if name.endswith("Service"):
        return [
            f"Angular injectable service: {humanize(name.replace('Service', ''))} ({area}).",
            "",
            "<p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.",
        ]
    if name.endswith("Component"):
        selector = re.search(r"selector:\s*['\"]([^'\"]+)['\"]", body)
        sel = f" Selector: `{selector.group(1)}`." if selector else ""
        return [
            f"Angular component: {humanize(name.replace('Component', ''))} ({area}).",
            "",
            f"<p>Template UI for the ThingsBoard web application.{sel}",
        ]
    if name.endswith("Module"):
        return [
            f"Angular NgModule bundling {humanize(name.replace('Module', ''))} ({area}).",
        ]
    if name.endswith("Guard"):
        return [f"Route guard: {humanize(name.replace('Guard', ''))} ({area})."]
    if name.endswith("Interceptor"):
        return [f"HTTP interceptor ({area})."]
    if name.endswith("Resolver"):
        return [f"Route resolver: preloads data for {humanize(name.replace('Resolver', ''))} ({area})."]
    if name.endswith("Directive"):
        return [f"Angular directive: {humanize(name.replace('Directive', ''))} ({area})."]
    if name.endswith("Pipe"):
        return [f"Angular pipe: {humanize(name.replace('Pipe', ''))} ({area})."]
    if "/models/" in path.as_posix() or name.endswith("Models"):
        return [f"TypeScript interfaces, types, and enums for {humanize(name)} ({area})."]
    if routes:
        preview = ", ".join(f"`{r}`" for r in routes[:8])
        extra = f" (+{len(routes) - 8} more)" if len(routes) > 8 else ""
        return [
            f"Angular route configuration for {humanize(name.replace('RoutingModule', ''))} ({area}).",
            "",
            f"<p>Route paths: {preview}{extra}",
        ]
    if name.endswith("RoutingModule"):
        return [f"Angular routes for {humanize(name.replace('RoutingModule', ''))} ({area})."]
    return [f"{humanize(name).capitalize()} ({area})."]


def class_doc_is_thin(doc: str, name: str) -> bool:
    if name in SERVICE_CLASS_DOCS and len(doc) < 120:
        return True
    thin = (
        "Angular component:",
        "Angular HTTP service:",
        "Angular injectable service:",
        "HTTP client for",
        "Angular NgModule:",
    )
    if any(t in doc for t in thin) and "<p>" not in doc and len(doc) < 150:
        return True
    if len(doc.strip()) < 55:
        return True
    return False


def find_methods(body: str, class_start: int) -> list[TsMethod]:
    brace = body.find("{", class_start)
    if brace < 0:
        return []
    depth = 0
    class_end = len(body)
    for i in range(brace, len(body)):
        if body[i] == "{":
            depth += 1
        elif body[i] == "}":
            depth -= 1
            if depth == 0:
                class_end = i
                break
    methods: list[TsMethod] = []
    seen: set[int] = set()
    for m in METHOD_RE.finditer(body, brace, class_end):
        if m.start() in seen:
            continue
        name = m.group(3)
        if name in SKIP_METHODS or name.startswith("_"):
            continue
        line_start = body.rfind("\n", 0, m.start()) + 1
        line_prefix = body[line_start:m.start()]
        if "=" in line_prefix:
            continue
        seen.add(m.start())
        methods.append(
            TsMethod(
                pos=m.start(),
                indent=m.group(1),
                name=name,
                params=parse_params(m.group(4) or ""),
                return_type=(m.group(5) or "void").strip(),
            )
        )
    return methods


def method_doc_is_thin(doc: str) -> bool:
    if "Calls ThingsBoard REST" in doc and "@param" not in doc:
        return True
    if "@returns" not in doc and "@return" not in doc and "void" not in doc.lower():
        if len(doc) < 120:
            return True
    if len(doc.strip()) < 40:
        return True
    return False


def build_method_javadoc(method: TsMethod, body: str, is_service: bool) -> str:
    chunk_end = body.find("\n  ", method.pos + 1)
    if chunk_end < 0:
        chunk_end = min(method.pos + 4000, len(body))
    chunk = body[method.pos:chunk_end]
    paths = extract_api_paths(chunk)
    lines = [method_summary(method.name, paths, is_service), ""]
    if paths and is_service:
        lines.append(f"REST endpoint(s): `{paths[0]}`" + (f", `{paths[1]}`" if len(paths) > 1 else ""))
        lines.append("")
    for pname, ptype in method.params:
        lines.append(f"@param {pname} {param_desc(pname, ptype)}")
    if method.return_type and method.return_type != "void":
        lines.append(f"@returns {method.return_type.strip()} observable or value")
    return format_ts_javadoc(method.indent, lines)


def add_class_javadoc(body: str, m: re.Match, path: Path, force: bool) -> tuple[str, bool]:
    name = m.group("name")
    insert = find_insert(body, m.start())
    existing = jsdoc_block_before(body, insert)
    if existing and not force and not class_doc_is_thin(body[existing[0] : existing[1]], name):
        return body, False
    if existing:
        body = body[: existing[0]] + body[existing[1] :]
        insert = existing[0]
    doc = format_ts_javadoc(m.group("indent"), class_description(name, path, body))
    return body[:insert] + doc + body[insert:], True


def process_file(path: Path, force_class: bool) -> tuple[bool, int]:
    content = path.read_text(encoding="utf-8")
    license_, body = split_license(content)
    decl = CLASS_RE.search(body) or INTERFACE_RE.search(body) or ENUM_RE.search(body)
    if not decl:
        return False, 0
    changed = False
    mc = 0
    body, cc = add_class_javadoc(body, decl, path, force_class)
    if cc:
        changed = True
    is_service = path.name.endswith(".service.ts")
    class_m = CLASS_RE.search(body)
    if class_m:
        methods = find_methods(body, class_m.start())
        to_update: list[TsMethod] = []
        for method in methods:
            if method.name == "constructor":
                continue
            block = jsdoc_block_before(body, method.pos)
            if block and not method_doc_is_thin(body[block[0] : block[1]]):
                continue
            to_update.append(method)
        for method in reversed(to_update):
            pos = method.pos
            body, pos = strip_jsdoc_before(body, pos)
            doc = build_method_javadoc(method, body, is_service)
            body = body[:pos] + doc + body[pos:]
            mc += 1
        if mc:
            changed = True
    if changed:
        path.write_text(license_ + body, encoding="utf-8", newline="\n")
    return changed, mc


def main() -> int:
    parser = argparse.ArgumentParser(description="Add detailed JSDoc to ui-ngx TypeScript")
    parser.add_argument("--root", type=Path, default=UI_NGX_ROOT)
    parser.add_argument("--force-class", action="store_true")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    files_n = methods_n = 0
    for path in sorted(args.root.rglob("*.ts")):
        if ".spec.ts" in path.name or path.name.endswith(".d.ts"):
            continue
        if args.dry_run:
            content = path.read_text(encoding="utf-8")
            _, body = split_license(content)
            if not (CLASS_RE.search(body) or INTERFACE_RE.search(body)):
                continue
            files_n += 1
            continue
        changed, mc = process_file(path, args.force_class)
        if changed:
            files_n += 1
            methods_n += mc
    print(f"files: {files_n} modified", file=sys.stderr)
    print(f"methods: {methods_n} documented", file=sys.stderr)
    print(f"Done: {files_n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
